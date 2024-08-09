from action import Action
from board import Board
from bonus import Bonus
from color import Color
from cost import Cost
from flyweight import Flyweight
from utils import *


def draw_reserve_button(surface: pygame.Surface):
    """
    The x and y coordinates returned are relative to the surface.
    :param surface:
    :return:
    """
    reserve_button = button('Reserve', width=surface.get_width() / 6, height=surface.get_height() / 8, color=RED)
    x = surface.get_width() / 2 + 10
    y = surface.get_height() - reserve_button.get_height() * 1.5
    surface.blit(reserve_button, (x, y))
    button_rect = reserve_button.get_rect()
    button_rect.x = x
    button_rect.y = y
    return button_rect


def draw_buy_button(surface: pygame.Surface):
    """
    The x and y coordinates returned are relative to the surface.
    :param surface:
    :return:
    """
    buy_button = button('Buy', width=surface.get_width() / 6, height=surface.get_height() / 8, color=GREEN)
    x = surface.get_width() / 2 - buy_button.get_width() * SIDEBAR_IMAGE_SCALE - 10
    y = surface.get_height() - buy_button.get_height() * SIDEBAR_IMAGE_SCALE
    surface.blit(buy_button, (x, y))
    button_rect = buy_button.get_rect()
    button_rect.x = x
    button_rect.y = y
    return button_rect

def draw_take_button(surface: pygame.Surface):
    """
    The x and y coordinates returned are relative to the surface.
    :param surface:
    :return:
    """
    take_button = button('Unlock', width=surface.get_width() / 6, height=surface.get_height() / 8, color=GREEN)
    x = surface.get_width() / 2 - take_button.get_width() * SIDEBAR_IMAGE_SCALE - 10
    y = surface.get_height() - take_button.get_height() * SIDEBAR_IMAGE_SCALE
    surface.blit(take_button, (x, y))
    button_rect = take_button.get_rect()
    button_rect.x = x
    button_rect.y = y
    return button_rect

def draw_cancel_button(surface: pygame.Surface):
    """
    The x and y coordinates returned are relative to the surface.
    :param surface:
    :return:
    """
    cancel_button = button('Cancel', width=surface.get_width() / 6, height=surface.get_height() / 8, color=RED)
    x = surface.get_width() / 2 + 10
    y = surface.get_height() - cancel_button.get_height() * SIDEBAR_IMAGE_SCALE
    surface.blit(cancel_button, (x, y))
    button_rect = cancel_button.get_rect()
    button_rect.x = x
    button_rect.y = y
    return button_rect


@Flyweight
class Card:
    x_ratio = 0.09  # ratio of card width to board width
    y_ratio = 0.18  # ratio of card height to board height

    def __init__(self, id: int, color, prestige_points=1, cost=Cost(1, 1, 1, 1, 1),
                 bonus=Bonus(1, 1, 1, 1, 1)):
        self._id = id
        self._prestige_points = prestige_points
        self._cost = cost
        self._bonus = bonus
        self._color = color
        # self.deck = deck
        self._image = self._get_image()
        self.pos = None

    @staticmethod
    def get_card_size(board=None):
        if board is None:
            board = Board.instance()
        width = board.get_width() * Card.x_ratio
        height = board.get_height() * Card.y_ratio
        return width, height

    def draw(self, screen, x, y, width=None, height=None):
        board = Board.instance()
        if width is None:
            width = Card.get_card_size(board)[0]
        if height is None:
            height = Card.get_card_size(board)[1]
        image = pygame.transform.scale(self._image, (int(width), int(height)))
        screen.blit(image, (x, y))
        self.pos = (x, y)

    def draw_for_sidebar(self, screen, x, y, width_mult=1.5, height_mult=1.5):
        board = Board.instance()
        width, height = Card.get_card_size(board)
        image = pygame.transform.scale(self._image, (int(width) *width_mult, int(height) *height_mult))
        screen.blit(image, (x, y))
        self.pos = (x, y)

    def is_clicked(self, mousePos):
        """
        Returns True if the card is clicked.
        :pre: self.pos is not None. This means that draw has to be called before this method.
        """
        x_start = self.pos[0]
        y_start = self.pos[1]
        x_end = x_start + Card.get_card_size(Board.instance())[0]
        y_end = y_start + Card.get_card_size(Board.instance())[1]
        return x_start <= mousePos[0] <= x_end and y_start <= mousePos[1] <= y_end

    def get_rect(self):
        return self._image.get_rect()

    def get_color(self):
        return self._color

    def get_prestige_points(self):
        return self._prestige_points

    def get_id(self):
        return self._id

    def get_bonus(self):
        return self._bonus

    def set_pos(self, x, y):
        self.pos = (x, y)
        self._image.get_rect().center = self.pos

    def _get_image(self):
        if self._color == Color.RED:
            # Look inside all the red card folders
            for i in range(1, 4):
                try:
                    return pygame.image.load('../sprites/cards/red{}/{}.png'.format(i, self._id))
                except:
                    pass

        return pygame.image.load('../sprites/cards/{}/{}.png'.format(self._color.name.lower(), self._id))

    def get_user_selection(self, screen) -> Action:
        """
        Shows a box to the user with all the card's information.
        Allows user to choose whether to buy or reserve the card.
        """
        selection_box, selection_box_rect = get_selection_box(screen)

        # draw the reserve button
        reserve_button = draw_reserve_button(selection_box)
        # get the true position of the button
        reserve_button.x += selection_box_rect.x
        reserve_button.y += selection_box_rect.y
        # draw the buy button
        buy_button = draw_buy_button(selection_box)
        # get the true position of the button
        buy_button.x += selection_box_rect.x
        buy_button.y += selection_box_rect.y

        card_width, card_height = Card.get_card_size(Board.instance())
        card_width *= 2  # make the card wider
        card_height = buy_button.y - selection_box_rect.y - 20  # height of card is from top of box to buy button
        self.draw(selection_box,
                  selection_box_rect.width / 2 - card_width / 2,
                  10,
                  width=card_width, height=card_height)

        screen.blit(selection_box, selection_box_rect)
        pygame.display.update()
        # wait for user to click on a button
        while True:
            for event in pygame.event.get():
                if event.type == pygame.MOUSEBUTTONDOWN:
                    if reserve_button.collidepoint(event.pos):
                        return Action.RESERVE
                    elif buy_button.collidepoint(event.pos):
                        return Action.BUY
                    else:
                        return Action.CANCEL
                if event.type == pygame.KEYDOWN:
                    if event.key == pygame.K_ESCAPE:
                        return Action.CANCEL
                elif event.type == pygame.QUIT:
                    pygame.quit()
                    quit()

    def get_user_cascade_selection(self, screen) -> Action:
        """
        Shows a box to the user with all the card's information.
        Allows user to choose if they want to take the card for free, or cancel
        """
        selection_box, selection_box_rect = get_selection_box(screen)

        # draw the reserve button
        take_button = draw_take_button(selection_box)
        # get the true position of the button
        take_button.x += selection_box_rect.x
        take_button.y += selection_box_rect.y
        # draw the buy button
        cancel_button = draw_cancel_button(selection_box)
        # get the true position of the button
        cancel_button.x += selection_box_rect.x
        cancel_button.y += selection_box_rect.y

        card_width, card_height = Card.get_card_size(Board.instance())
        card_width *= 2  # make the card wider
        card_height = take_button.y - selection_box_rect.y - 20  # height of card is from top of box to buy button
        self.draw(selection_box,
                  selection_box_rect.width / 2 - card_width / 2,
                  10,
                  width=card_width, height=card_height)

        screen.blit(selection_box, selection_box_rect)
        pygame.display.update()
        # wait for user to click on a button
        while True:
            for event in pygame.event.get():
                if event.type == pygame.MOUSEBUTTONDOWN:
                    if cancel_button.collidepoint(event.pos):
                        return Action.CANCEL
                    elif take_button.collidepoint(event.pos):
                        return Action.CASCADE
                    else:
                        return Action.CANCEL
                if event.type == pygame.KEYDOWN:
                    if event.key == pygame.K_ESCAPE:
                        return Action.CANCEL
                elif event.type == pygame.QUIT:
                    pygame.quit()
                    quit()      

    def __str__(self):
        return "Color: {}, id: {}".format(self._color, self._id)
