class Cost:
    def __init__(self, red: int, green: int, blue: int, white: int, black: int):
        self._red = red
        self._green = green
        self._blue = blue
        self._white = white
        self._black = black

    def get_red(self):
        return self._red

    def get_green(self):
        return self._green

    def get_blue(self):
        return self._blue

    def get_white(self):
        return self._white

    def get_black(self):
        return self._black
