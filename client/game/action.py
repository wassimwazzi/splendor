from enum import Enum


class Action(Enum):
    RESERVE = 'RESERVE'
    BUY = 'BUY'
    CANCEL = 'CANCEL'
    TAKE_TOKENS = 'TAKE_TOKENS'
    RETURN_TOKENS = 'RETURN_TOKENS'
    TAKE_CARD_1 = 'TAKE_CARD_1'
    TAKE_CARD_2 = 'TAKE_CARD_2'
    CASCADE = 'CASCADE'
    CLONE_CARD = 'CLONE_CARD'
    TAKE_NOBLE = 'TAKE_NOBLE'
    RESERVE_NOBLE = 'RESERVE_NOBLE'
    DISCARD = 'DISCARD'
    BUY_RESERVED_CARD = 'BUY_RESERVED_CARD'
    TAKE_ONE_TOKEN = 'TAKE_ONE_TOKEN'

    def __eq__(self, other):
        return self.value == other.value
