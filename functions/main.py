import time, json
from datetime import datetime, timedelta
from zoneinfo import ZoneInfo

from firebase_admin import initialize_app, credentials, db, messaging
from firebase_admin.exceptions import FirebaseError
from firebase_admin.messaging import UnregisteredError
from firebase_functions import https_fn

# initialize admin sdk
APP = initialize_app(
    credential = credentials.Certificate('admin.json'),
    options = {'databaseURL': 'https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/'})

# streams
STREAMS = {'NQ', 'GC'}

# configure notification
BASE_CONFIG = messaging.AndroidConfig(
    priority = 'high',  # 'normal' is default, 'high' attempts to wake device in doze mode
    ttl = 86400)  # ttl is 'time to live', 0 = 'now or never', '43200' = 12h, 86400 = 24h

# time adjustments
NYC = ZoneInfo('America/New_York')
DAY_MILLIS = 86400000

# database
USERS_NODE = db.reference('users')
STREAMS_NODE = db.reference('streams')
TOKENS_NODE = db.reference('tokens')

def get_session_start(timestamp: int) -> int:

    nyc_time = datetime.fromtimestamp(timestamp / 1000, NYC) # covert to NYC timezone
    session_start = nyc_time.replace(hour = 18, minute = 0, second = 0, microsecond = 0) # market close for today
    if session_start > nyc_time: session_start -= timedelta(days = 1) # before close, session start started yesterday
    return int(session_start.timestamp() * 1000) # convert to UTC

def write_message_to_database(node, timestamp, message, origin):

    # purge old message, if needed
    purge_node(node, timestamp)

    # write message
    node.child(str(timestamp)).set({
        'message': message,
        'origin': origin})

def purge_node(node, timestamp):

    # calculate session start of last two trading days
    current_session_start = get_session_start(timestamp)
    previous_session_start = current_session_start - DAY_MILLIS

    # query old messages
    old_messages = node.order_by_key().end_at(str(previous_session_start - 1)).get()
    if not old_messages: return

    # batch delete
    old_messages = { key: None for key in old_messages.keys() }
    node.update(old_messages)

# https://us-central1-com-sommerengineering-baraudio.cloudfunctions.net/baraudio?uid=...
@https_fn.on_request()
def baraudio(req: https_fn.Request) -> https_fn.Response:

    # parse request
    stream = req.args.get(key = 'broadcast', type = str) # query param
    uid = req.args.get(key = 'uid', type = str) # query param
    message = req.get_data(as_text = True) # message as plain/text from body
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

    # broadcast to stream subscribers
    if stream:

        # prevent unauthorized broadcasts
        if stream not in STREAMS:
            return https_fn.Response(f'Stream {stream} does not exist')

        broadcast_to_stream(stream, timestamp, message, origin)

        node = STREAMS_NODE.child(stream)
        write_message_to_database(node, timestamp, message, origin)

        return https_fn.Response(f'Broadcasted to stream: {stream}')

    # send message to single device
    if uid:

        # ensure user is authenticated
        device_token = TOKENS_NODE.child(uid).get()
        if device_token is None:
            return https_fn.Response(f'Sign-in to hear message')

        send_message_to_single_device(uid, device_token, timestamp, message, origin)

        node = USERS_NODE.child(uid)
        write_message_to_database(node, timestamp, message, origin)

        return https_fn.Response(f'Message sent to uid: {uid}')

    # respond with simple generic message, should never happen
    return https_fn.Response('Thank you for using BarAudio! :)')

def broadcast_to_stream(stream, timestamp, message, origin):

    # construct notification
    broadcast = messaging.Message(
        data = {
            'broadcast': stream,
            'timestamp': str(timestamp),
            'message': message,
            'origin': stream}, # TODO remove, redundant
        android = BASE_CONFIG,
        topic = stream)

    # broadcast to stream subscribers
    try: messaging.send(broadcast)
    except FirebaseError as error: print(f'Broadcast to stream: {stream}, error: {error}')

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
    except FirebaseError as error: print(f'Send to uid: {uid}, error: {error}')

def delete_token_from_database(uid):
    TOKENS_NODE.child(uid).delete()

# view logs
# https://console.cloud.google.com/run/detail/us-central1/baraudio/observability/logs?inv=1&invt=AbhuYw&project=com-sommerengineering-baraudio
