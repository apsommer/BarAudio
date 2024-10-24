from firebase_functions import https_fn
from firebase_admin import initialize_app
from flask import request, json

# initialize
initialize_app()

# define function endpoint
@https_fn.on_request()
def baraudio(request: https_fn.Request) -> https_fn.Response:
    requestJson = request.json
    jsonString = json.dumps(requestJson, default = str)
    response = jsonString
    return https_fn.Response(response)