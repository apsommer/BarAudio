import time, json
from datetime import datetime
from zoneinfo import ZoneInfo

from firebase_admin import initialize_app, credentials, db, messaging
from firebase_admin.exceptions import FirebaseError
from firebase_admin.messaging import UnregisteredError
from firebase_functions import https_fn

# initialize admin sdk
APP = initialize_app(
    credential = credentials.Certificate('admin.json'),
    options = { 'databaseURL': 'https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/' })

# topics
TOPICS = {'NQ', 'GC'} # TODO change to hash in production
# ...

# configure notification
BASE_CONFIG = messaging.AndroidConfig(
    priority = 'high',  # "normal" is default, "high" attempts to wake device in doze mode
    ttl = 86400)  # ttl is "time to live", 0 = "now or never", "43200" = 12h, 86400 = 24h

NYC = ZoneInfo("America/New_York")
UTC = ZoneInfo("UTC")

def get_cutoff_timestamp(timestamp: int) -> int:

    nyc_time = datetime.fromtimestamp(timestamp / 1000, NYC) # covert to NYC timezone
    session_start = nyc_time.replace(hour = 18, minute = 0, second = 0, microsecond = 0) # market close for today
    if session_start > nyc_time: session_start -= timedelta(days = 1) # before close, session start started yesterday
    return int(session_start.timestamp() * 1000) # convert to UTC

def write_message_to_database(stream, uid, timestamp, message, origin):

    # determine reference node
    if stream: ref = db.reference(f'streams/{stream}')
    else if uid: ref = db.reference(f'users/{uid}/alerts')
    else: return

    # write message to database
    ref.child(str(timestamp)).set({
        'message': message,
        'origin': origin})

    # purge old messages
    cutoff_timestamp = get_cutoff_timestamp(timestamp)
    old_messages = ref.order_by_key().start_at(str(cutoff_timestamp)).get()
    if old_messages:
        for key in old_messages.keys():
            ref.child(key).delete()

# https://us-central1-com-sommerengineering-baraudio.cloudfunctions.net/baraudio?uid=...
@https_fn.on_request()
def baraudio(req: https_fn.Request) -> https_fn.Response:

    # parse request
    topic = req.args.get(key='broadcast', type=str) # query param
    uid = req.args.get(key='uid', type=str) # query param
    message = req.get_data(as_text=True) # message as plain/text from body
    origin = req.headers.get('X-Forwarded-For') # extract origin from header

    # clean raw params
    message = message.strip()[:200] if message else '' # keep messages short for client display
    origin = origin if origin else 'unknown' # fallback if origin is empty

    # calculate raw utc timestamp from system (millis)
    timestamp = int(time.time() * 1000)

    # catch malformed request
    if req.method != 'POST':
        return https_fn.Response('Request must be POST and include uid as query parameter')

    # catch empty message
    if len(message) == 0:
        return https_fn.Response('The message is empty')

    # broadcast to topic subscribers
    if topic:

        # prevent unauthorized broadcasts
        if topic not in TOPICS:
            return https_fn.Response(f"Topic '{topic}' does not exist")

        broadcast_to_topic(topic, timestamp, message, origin)
        return https_fn.Response(f"Broadcasted to topic: {topic}")

    # send message to single device
    if uid:

        # ensure user is authenticated
        device_token = db.reference('users').child(uid).get()
        if device_token is None:
            return https_fn.Response(f"Sign-in to hear message")

        send_message_to_single_device(uid, device_token, timestamp, message, origin)
        return https_fn.Response(f"Message sent to uid: {uid}")

    # respond with simple generic message, should never happen
    return https_fn.Response('Thank you for using BarAudio! :)')

def broadcast_to_topic(topic, timestamp, message, origin):

    # construct notification
    broadcast = messaging.Message(
        data = {
            'broadcast': topic,
            'timestamp': str(timestamp),
            'message': message,
            'origin': origin},
        android = BASE_CONFIG,
        topic = topic)

    # broadcast to topic subscribers
    try: messaging.send(broadcast)
    except FirebaseError as error: print(f"Broadcast to topic: {topic}, error: {error}")

def send_message_to_single_device(uid, device_token, timestamp, message, origin):

    # construct notification
    notification = messaging.Message(
        data = {
            'uid': uid,
            'timestamp': str(timestamp),
            'message': message,
            'origin': origin},
        android = BASE_CONFIG,
        token = device_token)

    # send notification to single device
    try: messaging.send(notification)
    except UnregisteredError: delete_token_from_database(uid)
    except FirebaseError as error: print(f"Send to uid: {uid}, error: {error}")

def delete_token_from_database(uid):
    db.reference('users').child(uid).delete()

# view logs
# https://console.cloud.google.com/run/detail/us-central1/baraudio/observability/logs?inv=1&invt=AbhuYw&project=com-sommerengineering-baraudio
