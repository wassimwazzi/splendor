from enum import Enum


class TokenAction(Enum):

    # WHITEPLUS = 'WHITEPLUS'
    # WHITEMINUS = 'WHITEMINUS'
    # BLUEPLUS = 'BLUEPLUS'
    # BLUEMINUS = 'BLUEMINUS'
    # GREENPLUS = 'GREENPLUS'
    # GREENMINUS = 'GREENMINUS'
    # REDPLUS = 'REDPLUS'
    # REDMINUS = 'REDMINUS'
    # BLACKPLUS = 'BLACKPLUS'
    # BLACKMINUS = 'BLACKMINUS'
    # GOLDPLUS = 'GOLDPLUS'
    # GOLDMINUS = 'GOLDMINUS'
    INCREMENT = 'INCREMENT'
    DECREMENT = 'DECREMENT'
    CANCEL = 'CANCEL'
    BUY = 'BUY'

    def __eq__(self, other):
        return self.value == other.value
