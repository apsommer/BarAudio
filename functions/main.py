from flask import Flask, json, request, make_response, Response
from werkzeug.routing import Rule
from firebase_admin import initialize_app, credentials, db

# required firebase functions?
# from firebase_functions import https_fn
# from firebase_admin import initialize_app
# initialize_app()
# @https_fn.on_request()

# firebase admin sdk
cred = credentials.Certificate('admin.json')
options = {
    'databaseURL': 'https://com-sommerengineering-baraudio.firebaseio.com/'
}
default_app = initialize_app(cred, options)
ref = db.reference('/')
print(ref.get())

# initialize
app = Flask(__name__)
app.url_map.add(Rule('/', endpoint='/'))

# define function endpoint
@app.endpoint("/")
def baraudio() -> Response:

    if request.method == "POST" and request.is_json:
        bodyJson = request.get_json()
        json_string = json.dumps(bodyJson)
        message = "POST request received with json: " + json_string

    else:
        message = "Thank you for using BarAudio! :)"

    return make_response(message)
