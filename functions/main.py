from firebase_admin import initialize_app, credentials, db, messaging
from firebase_functions import https_fn, logger
import time

# initialize admin sdk
app = initialize_app(
    credential = credentials.Certificate('admin.json'),
    options = { 'databaseURL': 'https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/' })

@https_fn.on_request()
def baraudio(req: https_fn.Request) -> https_fn.Response:

    # extract attributes from request
    uid = req.args.get(key='uid', type=str) # uid as query param
    message = req.get_data(as_text = True) # message as plain/text from body

    # process webhook when request is properly formed
    if req.method == 'POST' and uid is not None and len(message) > 0:

        timestamp = str(round(time.time() * 1000))
        origin = get_origin(req)

        write_to_database(uid, timestamp, message, origin)
        send_fcm(uid, timestamp, message)

    # respond with simple message
    return https_fn.Response('Thank you for using BarAudio! :)')

def get_origin(req: https_fn.Request) -> str:

    # extract origin of webhook: tradingview, trendspider, ...
    origin = str(req.headers.get('X-Forwarded-For'))

    # todo detect insomnia calls, can remove in production
    agent = req.user_agent.string
    if "insomnia" in agent:
        origin = "insomnia"

    return origin

def write_to_database(uid: str, timestamp: str, message: str, origin: str):

    group_key = db.reference('messages')
    group_key.child(uid).child(timestamp).set('{ "message": "' + message + '", "origin": "' + origin + '" }')

def send_fcm(uid: str, timestamp: str, message: str):

    # get device token
    device_token = db.reference('users').get()[uid]
    # todo must catch bad token, throws 500 here ... instead response with "Please sign-in ..."
    #  500 also if correct token exits, but user has never signed-in before (fresh install)

    # set priority to high
    config = messaging.AndroidConfig(
        priority = "high", # "normal" is default, "high" attempts to wake device in doze mode
        ttl = 0 # ttl is "time to live", 0 means "now or never" and fcm discards if can't be delivered immediately
    )

    # construct notification
    remote_message = messaging.Message(
        data = {
            'uid': uid,
            'timestamp': timestamp,
            'message': message },
        android = config,
        token = device_token)

    # send notification to device
    messaging.send(remote_message)