from firebase_admin import initialize_app, credentials, db
from firebase_functions import https_fn
import time

# initialize admin sdk
app = initialize_app(
    credential = credentials.Certificate('admin.json'),
    options = { 'databaseURL': 'https://com-sommerengineering-baraudio.firebaseio.com/' }
)

@https_fn.on_request()
def baraudio(req: https_fn.Request) -> https_fn.Response:

    # extract plain/text from body
    text = req.get_data(as_text = True)

    # catch malformed request, respond with simple message
    if req.method != 'POST' or text == '':
        return https_fn.Response('Thank you for using BarAudio! :)')

    write_to_database(text)

    # respond with simple message
    message = 'POST request received with plain/text body: \n\n' + text
    return https_fn.Response(message)

def write_to_database(message: str):

    # write new message to database
    messagesKey = db.reference('messages')
    timestamp = str(round(time.time() * 1000))

    uid = 'YMYIKSbKNVb6AgZ3zbd6StNp6NL2' # todo temp hardcode
    messagesKey.child(uid).child(timestamp).set(message)