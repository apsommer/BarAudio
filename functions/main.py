from firebase_admin import initialize_app, credentials, db, messaging
from firebase_functions import https_fn, logger
import time

# initialize admin sdk
app = initialize_app(
    credential = credentials.Certificate('admin.json'),
    options = { 'databaseURL': 'https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/' }
)

@https_fn.on_request()
def baraudio(req: https_fn.Request) -> https_fn.Response:

    # extract attributes from request
    device_token = req.args.get(key='id', type=str) # uid as query param
    message = req.get_data(as_text = True) # message as plain/text from body

    # process webhook when request is properly formed
    if req.method == 'POST' and device_token is not None and len(message) > 0:

        # extract origin of webhook: tradingview, trendspider, ...
        agent = req.user_agent.string
        origin = str(req.headers.get('X-Forwarded-For'))

        # todo dev
        if "insomnia" in agent:
            origin = "insomnia"

        timestamp = str(round(time.time() * 1000))
        write_to_database(device_token, timestamp, message, origin)
        send_fcm(device_token, timestamp, message)

    # respond with simple message
    return https_fn.Response('Thank you for using BarAudio! :)')

def write_to_database(device_token: str, timestamp: str, message: str, origin: str):

    group_key = db.reference('messages')
    group_key.child(device_token).child(timestamp).set('{ "message": "' + message + '", "origin": "' + origin + '" }')

def send_fcm(device_token: str, timestamp: str, message: str):

    # get device token
    # group_key = db.reference('users')
    # device_token = group_key.get()[device_token] # todo must catch bad uid, users will for sure do this, send "are you sure that's the uid?" response

    # set priority to high
    config = messaging.AndroidConfig(
        priority = "high", # "normal" is default, "high" attempts to wake device in doze mode
        ttl = 0 # ttl is "time to live", 0 means "now or never" and fcm discards if can't be delivered immediately
    )

    # construct notification
    remote_message = messaging.Message(
        data = {
            'timestamp': timestamp,
            'message': message },
        android = config,
        token = device_token)

    # send notification to device
    messaging.send(remote_message)