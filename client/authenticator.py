import requests

from config import config
from time import time

# This class is responsible for authenticating the user, and storing the token and refresh token
class Authenticator:
    def __init__(self):
        self.username = None
        self.token = None
        self.refresh_token = None
        # Time since last refresh
        self.last_refresh = time()

    def authenticate(self, username, password):
        url = config.LOBBY_SERVICE_URL + '/oauth/token'
        data = {
            'grant_type': 'password',
            'username': username, #username,
            'password': password, #password
        }
        response = requests.post(url, data=data, auth=('bgp-client-name', 'bgp-client-pw'))
        if response.status_code == 200:
            self.username = username
            self.token = response.json()['access_token']
            self.refresh_token = response.json()['refresh_token']
            print("Successfully authenticated")
            return True
        return False

    def refresh(self):
        url = config.LOBBY_SERVICE_URL + '/oauth/token'
        data = {
            'grant_type': 'refresh_token',
            'refresh_token': self.refresh_token
        }
        response = requests.post(url, data=data, auth=('bgp-client-name', 'bgp-client-pw'))
        if response.status_code == 200:
            self.token = response.json()['access_token']
            self.refresh_token = response.json()['refresh_token']
            return True
        return False

    def get_token(self, escape=False):
        # Refresh the token if it is older than 5 minutes
        if time() - self.last_refresh > 300:
            self.refresh()
            self.last_refresh = time()
        if escape:
            return self.token.replace('+', "%2B")

        return self.token
