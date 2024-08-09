from enum import Enum


class Color(Enum):
    BLUE = (0, 0, 255)
    GREEN = (0, 255, 0)
    RED = (255, 0, 0)
    YELLOW = (255, 255, 0)
    BROWN = (139, 69, 19)
    WHITE = (255, 255, 255)
    GOLD = (255, 215, 0)
    BLACK = (0, 0, 0)
    GREY = (128, 128, 128)

    @classmethod
    def get_token_colors(cls):
        return [cls.WHITE,cls.BLUE,cls.GREEN,cls.RED,cls.BROWN,cls.GOLD]