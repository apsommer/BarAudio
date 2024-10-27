from firebase_admin import initialize_app, credentials, db, messaging
from firebase_functions import https_fn
import time

# initialize admin sdk
app = initialize_app(
    credential = credentials.Certificate('admin.json'),
    options = { 'databaseURL': 'https://com-sommerengineering-baraudio.firebaseio.com/' }
)

@https_fn.on_request()
def baraudio(req: https_fn.Request) -> https_fn.Response:

    # extract attributes from request
    uid = req.args.get(key='uid', type=str) # uid as query param
    message = req.get_data(as_text = True) # message as plain/text from body

    # write message to database when request is properly formed
    if req.method == 'POST' and uid is not None and len(message) > 0:
        write_to_database(uid, message)
        send_fcm(uid, message)

    # respond with simple message
    return https_fn.Response('Thank you for using BarAudio! :)')

def write_to_database(uid: str, message: str):

    # timestamp message to database
    groupKey = db.reference('messages')
    timestamp = str(round(time.time() * 1000))
    groupKey.child(uid).child(timestamp).set(message)

def send_fcm(uid: str, message: str):

    # get device token
    groupKey = db.reference('users')
    device_token = groupKey.get()[uid]

    # construct notification
    notification = messaging.Message(
        data = { 'message': message },
        token = device_token)

    # send notification
    messaging.send(notification)