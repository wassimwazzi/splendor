import random

import pygame

import utils
from board import Board
from flyweight import Flyweight


@Flyweight
class City:
    x_MarginToBoardSizeRatio = 0.05
    y_MarginToBoardSizeRatio = 0.07
    x_DistanceBetweenCardsToBoardWidthRatio = 1 / 30
    x_ratio = 1 / 4.5  # ratio of card width to board width 
    y_ratio = 1 / 8  # ratio of card height to board height

    def __init__(self, id: int):
        self._id = id  # 1 -> 3
        self._image = pygame.image.load('../sprites/cities/{}.png'.format(id))
        self.slot = len(City.flyweights)  # The slot position of the city
        print("city slot: ", self.slot)
        self.pos = self._default_position()
        self.isOnDisplay = True

    @staticmethod
    def initialize(cities):
        """get ids from json server"""
        
        # Create n cities with the chosen ids
        for city in cities:
            City.instance(id=city)

    @staticmethod
    def display_all(screen):
        for city in City.flyweights.values():
            if city.isOnDisplay:
                city.draw(screen, *city._default_position())

    @staticmethod
    def get_clicked_city(mouse_pos):
        """
        Returns the city that is clicked. Returns None if no city is clicked.
        """
        for city in City.flyweights.values():
            if city.isOnDisplay and city.is_clicked(mouse_pos):
                return city
        return None

    def draw(self, screen, x, y):
        screen.blit(pygame.transform.scale(self._image, City.get_card_size()), (x, y))

    def draw_for_sidebar(self, screen, x, y):
        width, height = City.get_card_size()
        image = pygame.transform.scale(self._image, (
        int(width) * utils.SIDEBAR_IMAGE_SCALE, int(height) * utils.SIDEBAR_IMAGE_SCALE))
        screen.blit(image, (x, y))
        self.pos = (x, y)

    def get_rect(self):
        return self._image.get_rect()

    def get_id(self):
        return self._id

    @staticmethod
    def update_all(city_ids):
        for city in City.flyweights.values():
            if city._id not in city_ids:
                city.isOnDisplay = False
            

    @staticmethod
    def get_card_size():
        board = Board.instance()
        width = board.get_width() * City.x_ratio
        height = board.get_height() * City.y_ratio
        return width, height

    @staticmethod
    def get_distance_between_cards(board):
        return board.get_width() * City.x_DistanceBetweenCardsToBoardWidthRatio + City.get_card_size()[0]

    def _default_position(self):
        board = Board.instance()
        x = board.get_width() * self.x_MarginToBoardSizeRatio + self.slot * City.get_distance_between_cards(board)
        x += board.get_x()
        y = board.get_height() * self.y_MarginToBoardSizeRatio
        y += board.get_y()
        return x, y

