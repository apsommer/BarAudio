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
    if req.method != 'POST' or text == "":
        return https_fn.Response("Thank you for using BarAudio! :)")

    ####################################################################################################################

    # todo database
    ref = db.reference('/test')

    key = round(time.time() * 1000)
    ref.set({
        key: text
    })

    # respond with simple message
    message = "POST request received with plain/text body: \n\n" + text
    return https_fn.Response(message)
