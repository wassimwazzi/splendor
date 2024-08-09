import requests

# some_file.py
import sys

# caution: path[0] is reserved for script path (or '' in REPL)
sys.path.insert(1, 'client\config')
from config.config import LOBBY_SERVICE_URL


def delete_session(access_token, session):
    url = f"{LOBBY_SERVICE_URL}/api/sessions/{session}?access_token={access_token}"
    response = requests.delete(url)
    return response


def remove_player(access_token, session, username):
    url = f"{LOBBY_SERVICE_URL}/api/sessions/{session}/players/{username}?access_token={access_token}"
    response = requests.delete(url)
    # print(response.text)
    return response


# session number can be either 3165409446827231019 or "3165409446827231019"
# delete_session("J%2BF98Sf9oVbnCX6RuHlR72vzscs=", 4017376585323182585)