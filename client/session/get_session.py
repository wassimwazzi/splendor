from config import *
import requests

# some_file.py
import sys

# caution: path[0] is reserved for script path (or '' in REPL)
sys.path.insert(1, 'client\config')
LOBBY_SERVICE_URL = config.get_url()
GAME_TYPES = ["Splendor", "SplendorCities", "SplendorTraderoutes"]


def get_all_sessions(authenticator):
  url = f"{LOBBY_SERVICE_URL}/api/sessions"
  sessions = requests.get(url).json()["sessions"]
  saved_sessions = get_saved_sessions(authenticator)
  for saved_session in saved_sessions:
    if exists(sessions, saved_session["savegameid"]):
      continue
    # change the saved session json to match what we expect from the lobby service
    saved_session["gameParameters"] = {}
    saved_session["gameParameters"]["maxSessionPlayers"] = len(saved_session["players"])
    saved_session["gameParameters"]["minSessionPlayers"] = len(saved_session["players"])
    saved_session["gameParameters"]["name"] = saved_session["gamename"]
    saved_session["launched"] = False
    saved_session["creator"] = authenticator.username # so it can be launched
    saved_session["created"] = False
    # make the key the savegameid
    sessions[saved_session["savegameid"]] = saved_session
  # remove session if it has a savegameid and the player is not in it
  to_remove = [key for key, value in sessions.items() if value["savegameid"] != "" and not player_in_saved_session(authenticator, value, saved_sessions)]
  # remove session if it is launched, and the player is not in it
  to_remove += [key for key, value in sessions.items() if value["launched"] and authenticator.username not in value["players"]]
  # remove duplicate keys
  to_remove = list(set(to_remove))
  for key in to_remove:
    del sessions[key]
  return sessions

def exists(sessions, savegameid):
  for session_id, session_json in sessions.items():
    if session_json["savegameid"] == savegameid:
      return True
  return False

def player_in_saved_session(authenticator, session, saved_sessions):
  """
  For each saved session, check if the player is in it.
  Only call if session has a savegameid
  """
  for saved_session in saved_sessions:
    if session["savegameid"] == saved_session["savegameid"]:
      return authenticator.username in saved_session["players"]
  return False

def get_all_sessions_long_polling(hash_code):
  url = f"{LOBBY_SERVICE_URL}/api/sessions?hash={hash_code}"
  response = requests.get(url)
  # print(response.json())
  return response


def get_session(session):
  url = f'{LOBBY_SERVICE_URL}/api/sessions/{session}'
  response = requests.get(url)
  # print(response.json())
  return response


def get_saved_sessions(authenticator):
  """
  Get all saved sessions for the current user
  """
  sessions = []
  for game_type in GAME_TYPES:
      url = f"{LOBBY_SERVICE_URL}/api/gameservices/{game_type}/savegames?access_token={authenticator.get_token(escape=True)}"
      username = authenticator.username
      response = requests.get(url)
      if response.status_code != 200:
        continue
      for session in response.json():
        if username in session["players"]:
          sessions.append(session)

  return sessions

# get_all_sessions()
# get_session(4989443599829874511)

"""
{
   "sessions":{
      "6552631076119715766":{
         "gameParameters":{
            "name":"Splendor",
            "displayName":"Splendor",
            "location":"http://server:8000",
            "maxSessionPlayers":4,
            "minSessionPlayers":2,
            "webSupport":"false"
         },
         "creator":"wassim",
         "players":[
            "wassim",
            "maex"
         ],
         "launched":true,
         "savegameid":"",
         "playerLocations":{
            
         }
      },
      "1151894549914272833":{
         "gameParameters":{
            "name":"Splendor",
            "displayName":"Splendor",
            "location":"http://server:8000",
            "maxSessionPlayers":4,
            "minSessionPlayers":2,
            "webSupport":"false"
         },
         "creator":"wassim",
         "players":[
            "wassim"
         ],
         "launched":false,
         "savegameid":"",
         "playerLocations":{
            
         }
      },
    }
}

"""
