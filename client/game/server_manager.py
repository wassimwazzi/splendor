import requests
from authenticator import Authenticator
from config import config


def get_board(authenticator: Authenticator, game_id: str):
  """
  Returns the board of the game with the given id.
  :param authenticator: The authenticator of the user.
  :param game_id: The id of the game.
  :return: The board of the game.
  """
  url = config.SERVER_URL + '/api/games/' + game_id + '/board'
  data = {
      "username": authenticator.username,
      "access_token": authenticator.get_token()
  }
  response = requests.get(url, params=data)
  if response.status_code == 200:
      return response.json()
  raise Exception("Could not get board")


def get_actions(authenticator: Authenticator, game_id: str, player_name: str):
  """
  Returns the actions of the game with the given id.
  :param player_name: the name of the player performing the action
  :param authenticator: The authenticator of the user.
  :param game_id: The id of the game.
  :return: The actions of the game.
  """
  url = config.SERVER_URL + '/api/games/' + game_id + '/players/' + player_name + '/actions'
  data = {
      "username": authenticator.username,
      "access_token": authenticator.get_token()
  }
  response = requests.get(url, params=data)
  if response.status_code == 200:
      return response.json()
  elif response.status_code == 401:
      return []
  print(f"{response.status_code}")
  raise Exception("Could not get actions")


def perform_action(authenticator: Authenticator, game_id: str, player_name: str, action_id: int):
  """
  Performs the action with the given id.
  :param authenticator: The authenticator of the user.
  :param game_id: The id of the game.
  :param player_name: the name of the player performing the action
  :param action_id: The id of the action.
  :return: The actions of the game.
  """
  print("Performing action with id {}".format(action_id))
  url = config.SERVER_URL + '/api/games/' + game_id + '/players/' + player_name + '/actions/' \
        + str(action_id)
  data = {
      "username": authenticator.username,
      "access_token": authenticator.get_token(),
  }
  headers = {"Content-Type": "application/json; charset=utf-8"}
  response = requests.post(url, params=data, headers=headers, json={})
  if response.status_code == 200:
      return
  raise Exception("Could not perform action")

def save_game(authenticator: Authenticator, game_id: str):
  """
  Saves the game on the server
  :param authenticator: authenticator
  :param game_id: The id of the game
  """
  print("Saving game with id {}".format(game_id))
  url = config.SERVER_URL + '/api/games/' + game_id + '/save'
  data = {
      "username": authenticator.username,
      "access_token": authenticator.get_token(),
  }
  headers = {"Content-Type": "application/json; charset=utf-8"}
  response = requests.post(url, params=data, headers=headers, json={})
  if response.status_code == 200:
      return
  raise Exception("Could not save game")




