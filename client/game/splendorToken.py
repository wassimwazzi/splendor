import pygame

from board import Board
from color import Color
from flyweight import Flyweight
from utils import outlined_text


@Flyweight
class Token:
    positions = {
        Color.WHITE: 0,
        Color.BLUE: 1,
        Color.GREEN: 2,
        Color.RED: 3,
        Color.BROWN: 4,
        Color.GOLD: 5,
    }

    multiplicities = {
        Color.WHITE: 15,  # 7,
        Color.BLUE: 15,  # 7,
        Color.GREEN: 15,  # 7,
        Color.RED: 15,  # 7,
        Color.BROWN: 15,  # 7,
        Color.GOLD: 13,  # 5,
    }

    yMargin = 30 / 33
    xMargin = 1 / 10
    xRatio = 1 / 15
    yRatio = 2 / 33
    xSeparationRatio = 1 / 20

    @staticmethod
    def get_all_token_colors():
        unique_token_list = []

        color_list = Color.get_token_colors()
        for token in Token.flyweights.values():
            for token in Token.flyweights.values():
                token_color = token.get_color()
                if token_color in color_list:
                    color_list.remove(token_color)
                    unique_token_list.append(token)
        
        return unique_token_list

    @staticmethod
    def get_token(color: Color):
        for token in Token.flyweights.values():
            if token.get_color() == color:
                return token

    @staticmethod
    def get_token_from_board(color):
        """
        Returns the first token of the given color from the board
        """
        for token in Token.flyweights.values():
            if token.get_color() == color and token.isOnDisplay:
                return token

    def __init__(self, color: Color, id: int):
        self._color = color
        self.image = pygame.image.load('../sprites/tokens/{}.png'.format(color.name.lower()))
        self._id = id  # Separates tokens with same color
        self.isOnDisplay = True
        self.pos = self._default_position()

    @staticmethod
    def get_x_start():
        board = Board.instance()
        return board.get_x() + board.get_width() * Token.xMargin

    @staticmethod
    def get_y_start():
        board = Board.instance()
        return board.get_y() + board.get_height() * Token.yMargin

    @staticmethod
    def initialize():
        id = 1
        for color in Token.positions.keys():  # for token color
            color_id = 1
            for _ in range(Token.multiplicities[color]):  # for number of tokens
                token = Token.instance(color=color, id=id)
                id += 1
                # This is a hack for the demo!!!!!!!!
                if color_id > 5 and color == Color.GOLD:
                    token.isOnDisplay = False
                elif color_id > 7:
                    token.isOnDisplay = False
                color_id += 1

    @staticmethod
    def is_within_range(mouse_pos):
        """
        Returns true if the mouse is within the range of the token display
        """
        r = Token.tokens_range()
        return r[0][0] <= mouse_pos[0] <= r[1][0] and r[0][1] <= mouse_pos[1] <= r[1][1]

    @staticmethod
    def tokens_range():
        """
        Returns the range of tokens
        """
        x_start = Token.get_x_start()
        y_start = Token.get_y_start()
        x_end = x_start + len(Token.positions) * Token.distance_between_tokens()
        y_end = y_start + Token.get_size()[1]
        return (x_start, y_start), (x_end, y_end)

    @staticmethod
    def distance_between_tokens():
        """
        Returns the distance between the start of one token and the start of the next token
        """
        board = Board.instance()
        width, height = Token.get_size()
        return width + board.get_width() * Token.xSeparationRatio

    @staticmethod
    def update_all(tokens_json):
        for color in Color:
            number_remaining = tokens_json.get(str(color).split('.')[1])
            for token in Token.flyweights.values():
                if token.get_color() != color:
                    continue
                if number_remaining > 0:
                    token.isOnDisplay = True
                    number_remaining -= 1
                else:
                    token.isOnDisplay = False

    @staticmethod
    def display_all(screen):
        """
        Draws all tokens on display to the screen
        :param screen:
        :return:
        """
        for color in Color:
            tokens = Token.tokens_on_display(color)
            if len(tokens) > 0:
                token_to_draw = tokens[0]
                token_to_draw.draw(screen, *token_to_draw.pos, amount=len(tokens))

    @staticmethod
    def get_clicked_token(mouse_pos):
        if not Token.is_within_range(mouse_pos):
            return None
        # FIXME: Only look at tokens that are on display, and one of each color
        for token in Token.flyweights.values():
            if token.isOnDisplay and token.is_clicked(mouse_pos):
                return token

    @staticmethod
    def get_size():
        board = Board.instance()
        width = board.get_width() * Token.xRatio
        height = board.get_height() * Token.yRatio
        return width, height

    @staticmethod
    def tokens_on_display(color: Color):
        return [token for token in Token.flyweights.values() if
                token.isOnDisplay and token.get_color() == color]

    def take_token(self, player):
        """
        Takes a token from the display
        """
        self.isOnDisplay = False
        player.add_token(self)

    def draw(self, screen, x, y, amount=0, size=None):
        if size is None:
            size = Token.get_size()
        image = pygame.transform.scale(self.image, size)
        outlined_text(image, str(amount), font_size=30)
        screen.blit(image, (x, y))

    def get_color(self):
        return self._color

    def get_rect(self):
        return self.image.get_rect()

    def is_clicked(self, mouse_pos):
        """
        Returns true if the mouse is within the range of the token
        """
        x, y = self.pos
        width, height = Token.get_size()
        return x <= mouse_pos[0] <= x + width and y <= mouse_pos[1] <= y + height

    def _default_position(self):
        x = self.get_x_start() + self.positions[self._color] * Token.distance_between_tokens()
        y = self.get_y_start()
        return x, y
