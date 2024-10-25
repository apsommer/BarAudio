from firebase_functions import https_fn
# from firebase_admin import initialize_app
from flask import Flask, json, request

# initialize
# initialize_app()

# initialize
app = Flask(__name__)

# define function endpoint
# @https_fn.on_request()

# define function endpoint
@app.route("/", methods=["GET", "POST"])
# def baraudio(req: https_fn.Request) -> https_fn.Response:
def baraudio() -> https_fn.Response:

    response_message = ""

    if request.method == "GET":
        response_message = "GET request received"

    if request.method == "POST":
        response_message = "POST request received"

    response = https_fn.Response(response_message)
    return response