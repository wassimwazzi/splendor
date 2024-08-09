import pygame
from typing import List, Callable, Tuple

GREEN = (0, 255, 0)
RED = (255, 0, 0)
WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
GREY = (57, 57, 57)
LIGHT_GREY = (99, 99, 99)
LIGHT_BLUE = (141, 182, 205)
# BACKGROUND_COLOR = (158, 58, 64)
BACKGROUND_COLOR = (113, 155, 158)

SIDEBAR_IMAGE_SCALE = 1.5  # make cards and nobles 1.5x bigger


def dim_screen(screen, color=(0, 0, 0), alpha=128):
    """
    Dim the screen with a color and alpha value
    """
    dim = pygame.Surface(screen.get_size())
    dim.fill(color)
    dim.set_alpha(alpha)
    screen.blit(dim, (0, 0))


def get_selection_box(screen: pygame.Surface, width=0.5, height=0.5, color=BACKGROUND_COLOR):
    """
    Creates a selection box on the screen. Centered by default.
    :param screen: used for relative positioning
    :param width: the width of the box relative to the screen size
    :param height: the height of the box relative to the screen size
    :param color: the color of the box
    :return: The surface of the selection box, and the rect of the selection box
    """
    x = screen.get_width() * (1 - width) / 2
    y = screen.get_height() * (1 - height) / 2
    width = screen.get_width() * width
    height = screen.get_height() * height
    box = pygame.Surface((width, height))
    box.fill(color)
    rect = pygame.Rect(x, y, width, height)
    return box, rect


def button(text, width, height, color, border_radius=10):
    """
    Create a green button with the given text.
    :param text: the text to display on the button
    :param width: the width of the button
    :param height: the height of the button
    :param color: the color of the button
    :param border_radius: the border radius of the button
    :return: the button
    """
    # create a transparent surface
    button = pygame.Surface((width, height), pygame.SRCALPHA)
    pygame.draw.rect(button, color, (0, 0, width, height), border_radius=border_radius)
    write_on(button, text)
    return button


def flash_message(screen, text, color=GREEN, opacity=255):
    """
    Display a message
    :param color: the color of the box
    :param screen: the screen to display the message on
    :param text: the text to display
    :param: color: the color of the text
    :param opacity: the opacity of the box
    """
    box = pygame.Surface((screen.get_width() / 2, screen.get_height() / 10))
    box.set_alpha(opacity)
    box.fill(color)
    write_on(box, text)
    screen.blit(box, (screen.get_width() / 2 - box.get_width() / 2, 0))

def flash_right_side(screen, text, color=GREEN, opacity=255, font_size=20):
    """
    Display a message in the top right corner
    :param color: the color of the box
    :param screen: the screen to display the message on
    :param text: the text to display
    :param: color: the color of the text
    :param opacity: the opacity of the box
    :param font_size: the size of the font
    """
    font = pygame.font.Font(None, font_size)
    text_surface = font.render(text, True, color)
    box_width = text_surface.get_rect().width + 10
    box_height = screen.get_height() / 10
    box = pygame.Surface((box_width, box_height))
    box.set_alpha(opacity)
    box.fill(color)
    write_on(box, text, font_size=font_size)
    screen.blit(box, (screen.get_width() - box.get_width() - 30, 0))


def write_on(surface, text, color=BLACK, font='Arial', font_size=20, center=None):
    """
    Write text to a surface
    :param center: center the text on the surface
    :param text: the text to write
    :param surface: the rect to write to
    :param color: the color of the text
    :param font: the font of the text
    :param font_size: the size of the font
    """
    font = pygame.font.SysFont(font, font_size)
    text = font.render(text, True, color)
    text_rect = text.get_rect()
    if center is None:
        text_rect.center = (surface.get_width() / 2, surface.get_height() / 2)
    else:
        text_rect.center = center
    surface.blit(text, text_rect)


def outlined_text(surface, text, outline_color=BLACK, color=WHITE, font='Arial', font_size=20, center=None):
    """
    Write text to a surface
    :param outline_color: the color of the outline
    :param center: center the text on the surface
    :param text: the text to write
    :param surface: the rect to write to
    :param color: the color of the text
    :param font: the font of the text
    :param font_size: the size of the font
    """
    if center is None:
        center = (surface.get_width() / 2, surface.get_height() / 2)
    # top left
    write_on(surface, text, outline_color, font, font_size, (center[0] - 1, center[1] - 1))
    # top right
    write_on(surface, text, outline_color, font, font_size, (center[0] + 1, center[1] - 1))
    # btm left
    write_on(surface, text, outline_color, font, font_size, (center[0] - 1, center[1] + 1))
    # btm right
    write_on(surface, text, outline_color, font, font_size, (center[0] + 1, center[1] + 1))

    # TEXT FILL

    write_on(surface, text, color, font, font_size, center)

class Button:
    def __init__(self,rectangle : pygame.Rect, on_click_event : Callable[[None], None], color: Tuple[int,int,int] = LIGHT_GREY, text: str = "") -> None:
        self.rectangle = rectangle
        self.activation = on_click_event
        self.color = color
        self.text = text

    def set_text(self, text):
        self.text = text
    
    def display(self, screen):
        pygame.draw.rect(screen,self.color,self.rectangle)