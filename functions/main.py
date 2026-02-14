from firebase_admin import initialize_app, credentials, db, messaging
from firebase_admin.exceptions import FirebaseError
from firebase_admin.messaging import UnregisteredError
from firebase_functions import https_fn
import time, json

# topics
topics = {'NQ'} # todo change to hash in production
# ...

# initialize admin sdk
app = initialize_app(
    credential = credentials.Certificate('admin.json'),
    options = { 'databaseURL': 'https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/' })

# https://us-central1-com-sommerengineering-baraudio.cloudfunctions.net/baraudio?uid=...
@https_fn.on_request()
def baraudio(req: https_fn.Request) -> https_fn.Response:

    # parse request
    topic = req.args.get(key='broadcast', type=str) # query param
    uid = req.args.get(key='uid', type=str) # query param
    timestamp = str(round(time.time() * 1000)) # calculate timestamp from local system
    message = req.get_data(as_text = True) # message as plain/text from body
    origin = str(req.headers.get('X-Forwarded-For')) # extract origin from header

    # catch malformed request
    if req.method != 'POST':
        return https_fn.Response('Request must be POST and include uid as query parameter')

    # catch empty message
    if message is None or len(message) == 0:
        return https_fn.Response('The message is empty')

    # broadcast to topic subscribers
    if topic:

        # prevent unauthorized broadcasts
        if topic not in topics:
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

    # set priority to high
    config = messaging.AndroidConfig(
        priority='high',  # "normal" is default, "high" attempts to wake device in doze mode
        ttl=0)  # ttl is "time to live", 0 means "now or never" and fcm discards if can't be delivered immediately

    # construct notification
    broadcast = messaging.Message(
        data={
            'broadcast': topic,
            'timestamp': timestamp,
            'message': message,
            'origin': origin
        },
        android=config,
        topic=topic)

    try:

        # broadcast to topic subscribers
        messaging.send(broadcast)

        users = db.reference('users').get() or {}
        whitelist = db.reference('whitelist').get() or {}

        for uid in users:

            # check whitelist
            if uid in whitelist and not whitelist[uid]:
                continue

            write_to_database(uid, timestamp, message, origin)

    # catch error
    except FirebaseError as error:
        print(f"Broadcast to: {topic}, failed with error: {error}")

def send_message_to_single_device(uid, device_token, timestamp, message, origin):

    # set priority to high
    config = messaging.AndroidConfig(
        priority='high',  # "normal" is default, "high" attempts to wake device in doze mode
        ttl=0)  # ttl is "time to live", 0 means "now or never" and fcm discards if can't be delivered immediately

    # construct notification
    notification = messaging.Message(
        data={
            'uid': uid,
            'timestamp': timestamp,
            'message': message,
            'origin': origin},
        android=config,
        token=device_token)

    try:

        # send notification to device
        messaging.send(notification)
        write_to_database(uid, timestamp, message, origin)

    # catch error
    except FirebaseError as error:
        print(f"Send to single device uid: {uid}, failed with error: {error}")

def write_to_database(uid, timestamp, message, origin):

    group_key = db.reference('messages')
    group_key.child(uid).child(timestamp).set('{ "message": "' + message + '", "origin": "' + origin + '" }')

# view logs
# https://console.cloud.google.com/run/detail/us-central1/baraudio/observability/logs?inv=1&invt=AbhuYw&project=com-sommerengineering-baraudio
