from firebase_functions import https_fn
from firebase_admin import initialize_app

# initialize
initialize_app()

# define function endpoint
@https_fn.on_request()
def baraudio(req: https_fn.Request) -> https_fn.Response:
    response = "Python is better than JavaScript"
    return https_fn.Response(response)