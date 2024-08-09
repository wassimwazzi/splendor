import pygame
from singleton import Singleton


@Singleton
class Board:
    def __init__(self, screen_width, screen_height):
        self.height_offset = 150
        self.width_offset = 300
        self.boardImage = pygame.image.load('../sprites/board.jpg')
        self.width = screen_width - self.width_offset
        self.height = screen_height - self.height_offset
        self.boardRect = pygame.Rect(0, 0, self.width, self.height)
        self.boardRect.center = (screen_width / 2 + self.height_offset,
                                 screen_height / 2 - int(self.width_offset / 2.5))

    def display(self, screen):
        image = pygame.transform.scale(self.boardImage, (self.width, self.height))
        screen.blit(image, self.boardRect)

        # draw black background
        # pygame.draw.rect(screen, (0, 0, 0), self.boardRect)

    def get_rect(self):
        return self.boardRect

    def get_x(self):
        return self.boardRect.x

    def get_y(self):
        return self.boardRect.y

    def get_width(self):
        return self.width

    def get_height(self):
        return self.height

    def get_center(self):
        return self.boardRect.center

    def is_clicked(self, mouse_pos):
        return self.boardRect.collidepoint(mouse_pos)
