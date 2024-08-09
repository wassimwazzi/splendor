import requests

# some_file.py
import sys

# caution: path[0] is reserved for script path (or '' in REPL)
sys.path.insert(1, 'client\config')
from config.config import LOBBY_SERVICE_URL


def add_player(access_token, session, username):
    url = f"{LOBBY_SERVICE_URL}/api/sessions/{session}/players/{username}?access_token={access_token}"
    response = requests.put(url)
    # print(response.json())
    return response

# add_player("m%2BL/mY/70yMWI4uE5wLaudtDpHs=", 636302944309790167, "maex")
