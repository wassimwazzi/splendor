import random

import pygame

import utils
from board import Board
from flyweight import Flyweight


@Flyweight
class Noble:
    x_MarginToBoardSizeRatio = 0.2
    y_MarginToBoardSizeRatio = 0.07
    x_DistanceBetweenCardsToBoardWidthRatio = 1 / 30
    x_ratio = 1 / 10  # ratio of card width to board width 
    y_ratio = 1 / 7  # ratio of card height to board height

    def __init__(self, id: int):
        self._id = id  # 1 -> 4
        self._image = pygame.image.load('../sprites/nobles/{}.png'.format(id))
        print("Nobles flyweights: ", Noble.flyweights)
        self.slot = len(Noble.flyweights)  # The slot position of the noble
        print("noble slot: ", self.slot)
        self.pos = self._default_position()
        self.isOnDisplay = True

    @staticmethod
    def initialize(nobles):
        """get ids from json server"""
        
        # Create n nobles with the chosen ids
        for noble in nobles:
            Noble.instance(id=noble)

    @staticmethod
    def display_all(screen):
        for noble in Noble.flyweights.values():
            if noble.isOnDisplay:
                noble.draw(screen, *noble._default_position())

    @staticmethod
    def get_clicked_noble(mouse_pos):
        """
        Returns the noble that is clicked. Returns None if no noble is clicked.
        """
        for noble in Noble.flyweights.values():
            if noble.isOnDisplay and noble.is_clicked(mouse_pos):
                return noble
        return None

    def draw(self, screen, x, y):
        screen.blit(pygame.transform.scale(self._image, Noble.get_card_size()), (x, y))

    def draw_for_sidebar(self, screen, x, y):
        width, height = Noble.get_card_size()
        image = pygame.transform.scale(self._image, (
        int(width) * utils.SIDEBAR_IMAGE_SCALE, int(height) * utils.SIDEBAR_IMAGE_SCALE))
        screen.blit(image, (x, y))
        self.pos = (x, y)

    def get_rect(self):
        return self._image.get_rect()

    def get_id(self):
        return self._id

    @staticmethod
    def update_all(noble_ids):
        for noble in Noble.flyweights.values():
            if noble._id not in noble_ids:
                noble.isOnDisplay = False
            

    @staticmethod
    def get_card_size():
        board = Board.instance()
        width = board.get_width() * Noble.x_ratio
        height = board.get_height() * Noble.y_ratio
        return width, height

    @staticmethod
    def get_distance_between_cards(board):
        return board.get_width() * Noble.x_DistanceBetweenCardsToBoardWidthRatio + Noble.get_card_size()[0]

    def _default_position(self):
        board = Board.instance()
        x = board.get_width() * self.x_MarginToBoardSizeRatio + self.slot * Noble.get_distance_between_cards(board)
        x += board.get_x()
        y = board.get_height() * self.y_MarginToBoardSizeRatio
        y += board.get_y()
        return x, y

    def is_clicked(self, mouse_pos):
        """
        Returns True if the noble is clicked.
        :pre: self.pos is not None. This means that draw has to be called before this method.
        """
        x_start = self.pos[0]
        y_start = self.pos[1]
        x_end = x_start + Noble.get_card_size()[0]
        y_end = y_start + Noble.get_card_size()[1]
        return x_start <= mouse_pos[0] <= x_end and y_start <= mouse_pos[1] <= y_end

    def get_prestige_points(self):
        # Just to make player add noble work
        return 5
