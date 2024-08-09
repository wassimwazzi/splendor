from random import shuffle
from typing import Dict
from xmlrpc.client import boolean

import pygame

from board import Board
from card import Card
from color import Color
from sidebar import Sidebar
from singleton import Singleton
from utils import outlined_text


class Deck:
    x_DistanceBetweenCardsToBoardWidthRatio = 1 / 30  # The ratio of the distance between cards on the x-axis to the width of the board
    y_DistanceBetweenCardsToBoardHeightRatio = 1 / 22  # The ratio of the distance between cards on the y-axis to the height of the board
    x_MarginToBoardWidthRatio = 1 / 60  # The margin between the left side of the board and the left side of the deck cover
    y_MarginToBoardHeightRatio = 0.25  # First deck starts at 0.44 * board height y-coordinate
    _ID_START = 0
    _NUMBER_OF_CARDS = 0

    def __init__(self, level: int, color: Color, card_slots: int = 4):
        """
        Initializes the deck
        """
        self.card_slots = card_slots
        self.level = level
        self.color = color
        self.image = self._get_image()
        self.empty_slot_image = self._get_empty_slot_image()
        self.rect = self.image.get_rect()
        self.cardsOnDisplay: Dict[int, Card] = {}  # If no card is in a slot, the value is None
        self.num_remaining_cards = 0
        self.board = Board.instance()

    @staticmethod
    def get_deck_display_size(board):
        """
        Returns the size of the deck cover based on the size of the board
        """
        return Card.get_card_size(board)

    def update(self, decks_json):
        """
        Updates the deck based on the json data

        :param: decks_json: The json data of the decks
        """
        deck_json = self.find_deck_by_color_and_level(decks_json)
        self.num_remaining_cards = deck_json["numRemainingCards"]
        for i, card in enumerate(deck_json["faceUpCards"], start=1):
            if card is None:
              # skip loop if card is None
              continue
            self.cardsOnDisplay[i] = Card.instance(id=card["cardId"], color=self.get_color())

    def is_empty(self):
        """
        Returns whether the deck is empty
        """
        return self.num_remaining_cards == 0

    def display(self, screen):
        """
        Displays the cards on the board, and the deck cover
        """
        self._display_deck_cover(screen, amount=self.num_remaining_cards)
        for slot_pos, card in self.cardsOnDisplay.items():
            if card is not None:
                self._draw_card_to_board(screen, card, slot_pos)
            else:
                self.draw_empty_slot(screen, slot_pos)

    def get_clicked_card(self, mouse_pos):
        """
        Returns the card that was clicked on, if any

        :param: mousePos: The position of the mouse click
        :return: The card that was clicked on, if any
        """
        # FIXME: improve algorithm by not checking all cards.
        for card in self.cardsOnDisplay.values():
            if card is not None and card.is_clicked(mouse_pos):
                return card
        return None

    def _display_deck_cover(self, screen, amount=0):
        """
        Displays the deck cover on the board surface
        """
        if amount == 0:
            return
        width, height = Deck.get_deck_display_size(self.board)
        x = self._get_deck_x()
        if self.color == Color.RED:
            x -= width
        y = self._get_deck_y()
        image = pygame.transform.scale(self.image, (int(width), int(height)))
        outlined_text(image, str(amount), font_size=30, center=(width / 2, height / 4))
        screen.blit(image, (x, y))

    def _draw_card_to_board(self, screen, card, slot_pos):
        """
        Draws a card on the board surface

        :param: screen: The screen to draw the card on
        :param: card: The card to draw
        :param: slot_pos: The position of the card slot to draw the card to
        """
        card.draw(screen, *self._get_card_coords(slot_pos))

    def _get_card_coords(self, slot_pos):
        """
        Returns the position of the next card to be drawn
        """
        if self.color == Color.RED:
            x = self._get_deck_x() - slot_pos * self._x_distance_between_card_start_to_next_start() - \
                Card.get_card_size(self.board)[0]
        else:
            x = self._get_deck_x() + slot_pos * self._x_distance_between_card_start_to_next_start()
        y = self._get_deck_y()
        return (x, y)

    def _get_deck_pos(self):
        """
        Returns the position of the deck cover based on the size of the board
        """
        return (self._get_deck_x(), self._get_deck_y())

    def _get_deck_x(self):
        """
        Returns the x-coordinate of the side of the deck nearest to the board based on the size of the board
        """
        board = self.board
        if self.color == Color.RED:
            return board.get_x() + board.get_width() * (
                    1 - self.x_MarginToBoardWidthRatio)  # Red deck is on the right side of the board

        return board.get_x() + board.get_width() * self.x_MarginToBoardWidthRatio

    def _get_deck_y(self):
        """
        Returns the y-coordinate of the deck cover based on the size of the board
        """
        board = self.board
        distance_from_top = (3 - self.level) * self._y_distance_between_card_start_to_next_start()  # based on the deck level
        return board.get_y() + (board.get_height() * self.y_MarginToBoardHeightRatio) + distance_from_top

    def _get_image(self):
        """
        Returns the image of the deck cover
        """
        if hasattr(self, 'image'):
            return self.image
        if self.color == Color.RED:
            return pygame.image.load('../sprites/cards/red{}/back.png'.format(str(self.level)))
        else:
            return pygame.image.load('../sprites/cards/{}/back.png'.format(self.color.name.lower()))

    def _get_empty_slot_image(self):
        """
        Returns the image of the empty slot
        """
        if hasattr(self, 'empty_slot_image'):
            return self.empty_slot_image
        if self.color == Color.RED:
            return pygame.image.load('../sprites/cards/red{}/empty_slot.png'.format(str(self.level)))
        else:
            return pygame.image.load('../sprites/cards/{}/empty_slot.png'.format(self.color.name.lower()))

    def _x_distance_between_cards(self):
        """
        Returns the distance between the end of the first card, and start of second card on the x-axis
        """
        return self.board.get_width() * self.x_DistanceBetweenCardsToBoardWidthRatio

    def _y_distance_between_cards(self):
        """
        Returns the distance between the end of the first card, and start of second card on the y-axis
        """
        return self.board.get_height() * self.y_DistanceBetweenCardsToBoardHeightRatio

    def _x_distance_between_card_start_to_next_start(self):
        """
        Returns the distance between the start of the first card, and start of second card on the x-axis
        """
        return self._x_distance_between_cards() + Card.get_card_size(self.board)[0]

    def _y_distance_between_card_start_to_next_start(self):
        """
        Returns the distance between the start of the first card, and start of second card on the y-axis
        """
        return self._y_distance_between_cards() + Card.get_card_size(self.board)[1]

    def get_color(self):
        return self.color

    def add_card_to_sidebar(self, card):
        sidebar = Sidebar.instance()
        sidebar.add_card(card)

    def add_reserved_to_sidebar(self, card):
        sidebar = Sidebar.instance()
        sidebar.reserve_card(card)

    def draw_empty_slot(self, screen, slot_pos):
        """
        Draws an empty slot on the board surface

        :param: screen: The screen to draw the card on
        :param: slot_pos: The position of the card slot to draw the card to
        """
        x, y = self._get_card_coords(slot_pos)
        image = pygame.transform.scale(self.empty_slot_image,
                                       (int(Card.get_card_size(self.board)[0]), int(Card.get_card_size(self.board)[1])))
        screen.blit(image, (x, y))

    def find_deck_by_color_and_level(self, decks_json):
        for deck in decks_json:
            if deck['color'] == self.color.name.upper() and deck['level'] == self.level:
                return deck


@Singleton
class BlueDeck(Deck):
    _NUMBER_OF_CARDS = 20
    _ID_START = 71  # 71-90

    def __init__(self):
        super().__init__(level=3, color=Color.BLUE)


@Singleton
class GreenDeck(Deck):
    _NUMBER_OF_CARDS = 40
    _ID_START = 1  # 1- 40

    def __init__(self):
        super().__init__(level=1, color=Color.GREEN)


@Singleton
class YellowDeck(Deck):
    _NUMBER_OF_CARDS = 30
    _ID_START = 41  # 41-70

    def __init__(self):
        super().__init__(level=2, color=Color.YELLOW)


@Singleton
class RedDeck1(Deck):
    _NUMBER_OF_CARDS = 10
    _ID_START = 91

    def __init__(self):
        super().__init__(level=1, color=Color.RED, card_slots=2)


@Singleton
class RedDeck2(Deck):
    _NUMBER_OF_CARDS = 10
    _ID_START = 101

    def __init__(self):
        super().__init__(level=2, color=Color.RED, card_slots=2)


@Singleton
class RedDeck3(Deck):
    _NUMBER_OF_CARDS = 10
    _ID_START = 111

    def __init__(self):
        super().__init__(level=3, color=Color.RED, card_slots=2)
