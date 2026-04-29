import time
from datetime import datetime, timedelta
from zoneinfo import ZoneInfo

from firebase_admin import initialize_app, credentials, db, messaging
from firebase_admin.exceptions import FirebaseError
from firebase_admin.messaging import UnregisteredError
from firebase_functions import https_fn

# view logs
# ...

# todo production notes
# credential only required for local environment, can be removed for cloud only production
# min-instances=0 by default, change to 1 for to keep container running at all times, reduce latency by 500ms, cost $2-4/month

# initialize admin sdk
APP = initialize_app(
    credential = credentials.Certificate('admin.json'),
    options = {'databaseURL': 'https://signalvoice-api-default-rtdb.firebaseio.com/'})

# streams
STREAMS = frozenset({'ZN', 'NQ', 'BTC', 'ES', 'GC', 'SI'})

# user sources
TRADINGVIEW = {'52.89.214.238', '34.212.75.30', '54.218.53.128', '52.32.178.7'}
TRENDSPIDER = '3.12.143.24'
# todo MT5

# configure notification
BASE_CONFIG = messaging.AndroidConfig(
    priority = 'high',  # 'normal' default, 'high' attempts to wake device in doze mode
    ttl = 0)  # ttl is 'time to live', 0 = 'now or never', '43200' = 12h, 86400 = 24h

# time adjustments
NYC = ZoneInfo('America/New_York')
DAY_MILLIS = 86400000
WEEK_MILLIS = 7 * DAY_MILLIS

# database
USERS_NODE = db.reference('users')
STREAMS_NODE = db.reference('streams')
TOKENS_NODE = db.reference('tokens')

# ...
@https_fn.on_request()
def signal(req: https_fn.Request) -> https_fn.Response:

    # parse request
    stream = req.args.get(key = 'broadcast', type = str) # query param
    uid = req.args.get(key = 'uid', type = str) # query param
    message = req.get_data(as_text = True) # message as plain/text from body
    source_ip = req.headers.get('X-Forwarded-For') # extract source ip from header
    source_override = req.headers.get('dev-source') # catch dev environment: postman, insomnia, ...

    # clean raw message
    message = message.strip()[:200] if message else '' # keep messages short for client display

    # calculate raw utc timestamp from system (millis)
    timestamp = time.time_ns() // 1_000_000 # // floor division discards remainder after ms

    # catch malformed request
    if req.method != 'POST':
        return https_fn.Response('Request must be POST and include stream or uid as query parameter')
    if stream and uid:
        return https_fn.Response('Request must not include both stream and uid query parameters')
    if not stream and not uid:
        return https_fn.Response('Request must include either stream or uid query parameters')

    # catch empty message
    if len(message) == 0:
        return https_fn.Response('The message is empty')

    # broadcast to stream subscribers
    if stream:

        # prevent unauthorized broadcasts
        if stream not in STREAMS:
            return https_fn.Response(f'Stream {stream} does not exist')

        broadcast_to_stream(stream, timestamp, message)
        write_stream_message_to_database(stream, timestamp, message)

        return https_fn.Response(f'Broadcasted to stream: {stream}')

    # send message to single device
    if uid:

        # ensure user is authenticated todo this is O(n) reverse lookup, refactor to O(1) with extra node
        tokens = TOKENS_NODE.get() or {}
        device_token = next((t for t, u in tokens.items() if u == uid), None)
        if device_token is None:
            return https_fn.Response(f'Sign-in to hear message')

        # get source from ip
        if source_override: source = source_override # dev environment
        else: source = resolve_source_from_ip(source_ip) # user signal

        send_message_to_single_device(uid, device_token, timestamp, message, source)
        write_user_message_to_database(uid, timestamp, message, source)

        return https_fn.Response(f'Message sent to uid: {uid}')

    # respond with simple generic message, should never happen
    return https_fn.Response('Thank you for using BarAudio! :)')

def broadcast_to_stream(stream, timestamp, message):

    # construct notification
    broadcast = messaging.Message(
        data = {
            'stream': stream,
            'timestamp': str(timestamp),
            'message': message},
        android = BASE_CONFIG,
        topic = stream)

    # broadcast to stream subscribers
    try: messaging.send(broadcast)
    except FirebaseError as error: print(f'Broadcast to stream: {stream}, error: {error}')

def send_message_to_single_device(uid, device_token, timestamp, message, source):

    # construct notification
    notification = messaging.Message(
        data = {
            'uid': uid,
            'timestamp': str(timestamp),
            'message': message,
            'source': source},
        android = BASE_CONFIG,
        token = device_token)

    # send notification to single device
    try: messaging.send(notification)
    except UnregisteredError: TOKENS_NODE.child(device_token).delete() # delete token if unregistered (google test accounts)
    except FirebaseError as error: print(f'Send to uid: {uid}, error: {error}')

def write_stream_message_to_database(stream, timestamp, message):

    node = STREAMS_NODE.child(stream)

    # purge old message, if needed
    purge_node(node, timestamp)

    # write message
    node.child(str(timestamp)).set({
        'message': message })

def write_user_message_to_database(uid, timestamp, message, source):

    node = USERS_NODE.child(uid)

    # purge old message, if needed
    purge_node(node, timestamp)

    # write message
    node.child(str(timestamp)).set({
        'message': message,
        'source': source })

def purge_node(node, timestamp):

    # calculate session start of last two trading days
    current_session_start = get_session_start(timestamp)
    previous_session_start = get_session_start(current_session_start - DAY_MILLIS)

    # query old messages
    old_messages = node.order_by_key().end_at(str(previous_session_start - 1)).get()
    if not old_messages: return

    # batch delete
    old_messages = { key: None for key in old_messages.keys() }
    node.update(old_messages)

def get_session_start(timestamp: int) -> int:

    # time of market close for this day, in NYC timezone
    nyc_time = datetime.fromtimestamp(timestamp / 1000, NYC)
    session_start = nyc_time.replace(hour = 18, minute = 0, second = 0, microsecond = 0)

    # now is before close, trading session started yesterday
    if session_start > nyc_time: session_start -= timedelta(days = 1)

    # market is closed on weekends
    weekday = session_start.weekday()
    if weekday == 5: session_start -= timedelta(days = 2) # saturday -> thursday

    return int(session_start.timestamp() * 1000) # convert to UTC

def resolve_source_from_ip(source_ip: str) -> str:

    # catch empty ip list
    if not source_ip: return 'unknown'

    # take first instance of IPv4 address
    ips = [ip.strip() for ip in source_ip.split(',')]
    ip = next((ip for ip in ips if '.' in ip), None)

    # catch empty ip
    if not ip: return 'unknown'

    # clean ip
    ip = ip.strip()
    if len(ip) > 45: return 'unknown' # IPv6, localhost, ...

    if ip in TRADINGVIEW: return 'tradingview'
    if ip == TRENDSPIDER: return 'trendspider'
    # todo MT5

    return 'unknown'
