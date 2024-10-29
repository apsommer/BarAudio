from firebase_admin import initialize_app, credentials, db, messaging
from firebase_functions import https_fn
import time

# initialize admin sdk
app = initialize_app(
    credential = credentials.Certificate('admin.json'),
    # options = { 'databaseURL': 'https://com-sommerengineering-baraudio.firebaseio.com/' }
)

@https_fn.on_request()
def baraudio(req: https_fn.Request) -> https_fn.Response:

    # extract attributes from request
    uid = req.args.get(key='uid', type=str) # uid as query param
    message = req.get_data(as_text = True) # message as plain/text from body

    # process webhook when request is properly formed
    if req.method == 'POST' and uid is not None and len(message) > 0:
        timestamp = str(round(time.time() * 1000))
        write_to_database(uid, timestamp, message)
        send_fcm(uid, timestamp, message)

    # respond with simple message
    return https_fn.Response('Thank you for using BarAudio! :)')

def write_to_database(uid: str, timestamp: str, message: str):

    group_key = db.reference('messages')
    group_key.child(uid).child(timestamp).set(message)

def send_fcm(uid: str, timestamp: str, message: str):

    # get device token
    group_key = db.reference('users')
    device_token = group_key.get()[uid]

    # visible notification in status bar
    notification = messaging.Notification(
        title = timestamp,
        body = message,
        # image = "https://lh3.googleusercontent.com/a/ACg8ocLh9EGppTdyl_UJMT4bpA_RucEb6TET0JtG1YEM_tquXhCPmesnOQ=s96-c"
    )

    # set priority to high todo are these configs needed, default may be sufficient?
    config = messaging.AndroidConfig(
        priority = "high", # "normal" is default, "high" attempts to wake device in doze mode
        ttl = 0 # ttl is "time to live", 0 means "now or never" and fcm discards if can't be delivered immediately
    )

    # construct notification
    remote_message = messaging.Message(
        # notification = notification, # todo omit for silent data payload only, no visible notification, handle notification display in app?
        data = { 'message': message },
        android = config,
        token = device_token)

    # send notification
    messaging.send(remote_message)