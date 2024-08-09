from authenticator import Authenticator
from game import splendor
from login import login
from session import session
import pygame
from pygame.locals import *
from game.noble import Noble
from game.city import City

if __name__ == '__main__':
    pygame.init()
    screen = pygame.display.set_mode((0, 0), pygame.RESIZABLE)
    # fill screen with grey
    screen.fill((57, 57, 57))
    authenticator = Authenticator()
    login.login(authenticator, screen)
    while True:
      game_id = session.session(authenticator, screen)
      splendor.play(authenticator=authenticator, game_id=game_id, screen=screen)
