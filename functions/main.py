from firebase_admin import initialize_app, credentials, db, messaging
from firebase_functions import https_fn
import time

# initialize admin sdk
app = initialize_app(
    credential = credentials.Certificate('admin.json'),
    options = { 'databaseURL': 'https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/' })

# https://us-central1-com-sommerengineering-baraudio.cloudfunctions.net/baraudio?uid=...
@https_fn.on_request()
def baraudio(req: https_fn.Request) -> https_fn.Response:

    # extract attributes from request
    uid = req.args.get(key='uid', type=str) # uid as query param
    message = req.get_data(as_text = True) # message as plain/text from body

    # process webhook when request is properly formed
    if req.method == 'POST' and uid is not None:

        # catch empty message
        if len(message) == 0:
            return https_fn.Response('Received empty message ...')

        

        timestamp = str(round(time.time() * 1000))
        origin = str(req.headers.get('X-Forwarded-For'))

        # write to database
        group_key = db.reference('messages')
        group_key.child(uid).child(timestamp).set('{ "message": "' + message + '", "origin": "' + origin + '" }')

        # get device token
        try:
            device_token = db.reference('users').get()[uid]
        except TypeError:
            return https_fn.Response('Thank you for using BarAudio, sign-in to hear message! :)')

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
        messaging.send(remote_message)

    # respond with simple message
    return https_fn.Response('Thank you for using BarAudio! :)')
