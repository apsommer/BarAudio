from firebase_admin import initialize_app, credentials, db, messaging
from firebase_admin.exceptions import FirebaseError
from firebase_admin.messaging import UnregisteredError
from firebase_functions import https_fn
import time, json

# initialize admin sdk
app = initialize_app(
    credential = credentials.Certificate('admin.json'),
    options = { 'databaseURL': 'https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/' })

# drew@baraud.io
uid_admin = 'GxZTktT079Rf3vwDWLdSUFnUBs52'

# https://us-central1-com-sommerengineering-baraudio.cloudfunctions.net/baraudio?uid=...
@https_fn.on_request()
def baraudio(req: https_fn.Request) -> https_fn.Response:

    # extract attributes from request
    uid = req.args.get(key='uid', type=str) # uid as query param
    message = req.get_data(as_text = True) # message as plain/text from body

    # process webhook when request is properly formed
    if req.method == 'POST' and uid is not None:

        # catch empty message
        if len(message) == 0: return

        # parse request
        timestamp = str(round(time.time() * 1000))
        origin = str(req.headers.get('X-Forwarded-For'))

        # check if message originates from drew@baraudio
        if uid == uid_admin:
            send_message_to_all_devices(timestamp, message, origin)
            return https_fn.Response('drew@baraud.io sent message to all devices ...')
        else:

            send_message_to_single_device(uid, timestamp, message, origin)
            https_fn.Response('Thank you for using BarAudio! :)')

    # respond with simple error message
    return https_fn.Response('Request must be POST and include uid as query parameter. Thank you for using BarAudio! :)')

def send_message_to_all_devices(timestamp, message, origin):

    users = db.reference('users').get(shallow=True)

    for uid in users.keys():
        send_message_to_single_device(uid, timestamp, message, origin)

def send_message_to_single_device(uid, timestamp, message, origin):

    # write to database
    group_key = db.reference('messages')
    group_key.child(uid).child(timestamp).set('{ "message": "' + message + '", "origin": "' + origin + '" }')

    # get device token
    try: device_token = db.reference('users').get()[uid] # todo increase perf, pass device token to only call once
    except TypeError: return https_fn.Response('Thank you for using BarAudio, sign-in to hear message! :)')

    # set priority to high
    config = messaging.AndroidConfig(
        priority="high",  # "normal" is default, "high" attempts to wake device in doze mode
        ttl=0)  # ttl is "time to live", 0 means "now or never" and fcm discards if can't be delivered immediately

    # construct notification
    remote_message = messaging.Message(
        data={
            'uid': uid,
            'timestamp': timestamp,
            'message': message},
        android=config,
        token=device_token)

    # send notification to device
    # https://console.cloud.google.com/run/detail/us-central1/baraudio/observability/logs?inv=1&invt=AbhuYw&project=com-sommerengineering-baraudio
    try:
        messaging.send(remote_message)
    except (FirebaseError, UnregisteredError) as error:
        print(uid)
        print(error)
        # todo remove user from database, these are generated test accounts from automated systems
