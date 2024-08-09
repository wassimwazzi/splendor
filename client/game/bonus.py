import pygame

from color import Color
from utils import outlined_text


class Bonus:
    def __init__(self, red: int, green: int, blue: int, white: int, brown: int):
        self.bonus = {
            Color.BLUE: blue,
            Color.BROWN: brown,
            Color.GREEN: green,
            Color.RED: red,
            Color.WHITE: white,
        }

    def get_red(self):
        return self.bonus[Color.RED]

    def get_green(self):
        return self.bonus[Color.GREEN]

    def get_blue(self):
        return self.bonus[Color.BLUE]

    def get_white(self):
        return self.bonus[Color.WHITE]

    def get_black(self):
        return self.bonus[Color.BROWN]

    def get_all(self):
        return self.bonus

    def draw(self, surface, num_cards_reserved, reserved_color):
        """
        Draws the bonus on the surface. Bonuses are evenly spaced out to fill the surface
        :param surface: The surface to draw on
        :return: None
        """
        width = surface.get_width() / (len(self.bonus)+1)
        x = 0
        for color in self.bonus.keys():
            bonus = pygame.Surface((width, surface.get_height()))
            bonus.fill(color.value)
            outlined_text(bonus, "+ " + str(self.bonus[color]))
            surface.blit(bonus, (x, 0))
            x += width
        bonus = pygame.Surface((width, surface.get_height()))
        bonus.fill(reserved_color.value)
        outlined_text(bonus, str(num_cards_reserved))
        surface.blit(bonus, (x, 0))

    def __add__(self, other):
        for color in self.bonus.keys():
            self.bonus[color] += other.bonus[color]
        return self

    def __sub__(self, other):
        for color in self.bonus.keys():
            self.bonus[color] -= other.bonus[color]
        return self
