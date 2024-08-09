import math
import os
import sys
import threading

from pygame.locals import *
from win32api import GetSystemMetrics
from typing import List, Callable, Tuple
from action import Action
from token_action import TokenAction
from game import server_manager
from game.action_manager import ActionManager
from deck import *
from sidebar import *
from trade_route import * 
from city import * 
from splendorToken import Token
from color import Color
from enum import Enum
from menu import Menu

os.chdir(os.path.dirname(
    os.path.abspath(__file__)))  # to make image imports start from current directory
WIDTH, HEIGHT = GetSystemMetrics(0), GetSystemMetrics(1)
FPS = 60
FPSCLOCK = pygame.time.Clock()
pygame.display.set_caption('Splendor')
fullScreen = True
DECKS = [BlueDeck, RedDeck3, YellowDeck, RedDeck2, GreenDeck, RedDeck1]
FLASH_MESSAGE = None
FLASH_TIMER = 0
FLASH_START = 0
FLASH_COLOR = GREEN
PERSISTENT_MESSAGE = None
NUM_PLAYERS = 4  # For now
CURR_PLAYER = 0
action_manager = None
has_initialized = False
cascade = False
TRADING_POST_ENABLED = False 
CITIES_ENABLED = False
global EXIT
EXIT = False
DISPLAYSURF = pygame.display.set_mode((WIDTH, HEIGHT), pygame.FULLSCREEN)
MINIMIZED = False

current_action_list = []

class WIN_TYPE(Enum):
    WIN = 1
    TIE = 2
    LOSE = 3
    NOTHING = 0
    def __eq__(self, other):
        return self.value == other.value

IS_WON = WIN_TYPE.NOTHING

class IndividualTokenSelection:
    def __init__(self, token: Token, x_pos: int, y_pos: int) -> None:
        self.x_pos = x_pos
        self.y_pos = y_pos
        self.token = token

        self.amount = 0 # how many tokens are selected
        # TODO: make these buttons automatically based on position of button

        def incrementEvent():
            if self.amount < 3:
                self.amount += 1
                self.display()

        def decrementEvent():
            if self.amount > 0:
                self.amount -= 1
                self.display()
        X_SHIFT = 46
        Y_SHIFT = 60
        BUTTON_WIDTH = 90
        BUTTON_HEIGHT = 55
        green_rect = pygame.Rect(x_pos-X_SHIFT,y_pos+Y_SHIFT,BUTTON_WIDTH,BUTTON_HEIGHT)
        red_rect = pygame.Rect(x_pos+X_SHIFT,y_pos+Y_SHIFT,BUTTON_WIDTH,BUTTON_HEIGHT)
        self.incrementButton = Button(green_rect,incrementEvent,GREEN)
        self.decrementButton = Button(red_rect,decrementEvent,RED)
    
    def display(self):
        self.token.draw(DISPLAYSURF,self.x_pos,self.y_pos,amount=self.amount)
        self.incrementButton.display(DISPLAYSURF)
        self.decrementButton.display(DISPLAYSURF)

def initialize_game_type(board_json):
    global TRADING_POST_ENABLED
    global CITIES_ENABLED
    if board_json['gameType'] == 'TRADEROUTES':
        TRADING_POST_ENABLED = True
    elif board_json['gameType'] == 'CITIES':
        CITIES_ENABLED = True

def initialize_game(board_json):
    initialize_game_type(board_json)
    pygame.display.set_mode((WIDTH, HEIGHT), pygame.FULLSCREEN)
    initialize_board()
    initialize_cards()
    initialize_tokens()
    initialize_menu()
    
    initialize_players(board_json)
    if TRADING_POST_ENABLED:
        initialize_trade_routes(board_json)
        initialize_nobles(board_json)
    elif CITIES_ENABLED:
        # no nobles
        initialize_cities(board_json)
    else: # normal game
        initialize_nobles(board_json)
    initialize_sidebar()

# Trade routes visual will be able to be accessed via a button
# Not yet implemented
def initialize_trade_routes(board_json):
    #trade_routes = board_json['tradeRoutes']
    TradeRoute.instance()

def initialize_cities(board_json):
    ids = [city['cardId'] for city in board_json['cityDeck']['cities']  if city is not None]
    City.initialize(ids)


def initialize_players(board_json):
    global NUM_PLAYERS
    players = board_json['players']
    NUM_PLAYERS = len(players)
    for i in range(0, NUM_PLAYERS):
        player = Player.instance(id=i, name=players[i]['name'])


def initialize_board():
    Board.instance(WIDTH, HEIGHT)


def initialize_sidebar():
    Sidebar.instance(WIDTH, HEIGHT)

def initialize_menu():
    Menu.instance(WIDTH, HEIGHT)

def initialize_cards():
    BlueDeck.instance()
    RedDeck3.instance()
    YellowDeck.instance()
    RedDeck2.instance()
    GreenDeck.instance()
    RedDeck1.instance()


def initialize_tokens():
    Token.initialize()


def initialize_nobles(board_json):
    ids = [noble['cardId'] for noble in board_json['nobleDeck']['nobles']  if noble is not None]
    Noble.initialize(ids)


def show_flash_message():
    global FLASH_TIMER, FLASH_MESSAGE, FLASH_START
    time_diff = (pygame.time.get_ticks() - FLASH_START) / 1000
    if FLASH_MESSAGE is None or time_diff > FLASH_TIMER:
        return
    flash_message(DISPLAYSURF, FLASH_MESSAGE,
                  color=FLASH_COLOR, opacity=255 * (1 - time_diff / FLASH_TIMER))

def show_persistent_message(color=GREEN):
    if PERSISTENT_MESSAGE is None:
        return flash_right_side(DISPLAYSURF, "", color=GREEN, opacity=0)

    flash_right_side(DISPLAYSURF, PERSISTENT_MESSAGE, color=color, opacity=255)

def check_if_won(board_json,username):
    # checks checkGameEnd in Board
    global IS_WON
    if "winners" in board_json:
        print("found winners")
        lst = board_json["winners"]

        if username in lst and len(lst) == 1:
            print("player won")
            IS_WON = WIN_TYPE.WIN

        elif username in lst and len(lst) > 1:
            IS_WON = WIN_TYPE.TIE
        elif len(lst) > 0:
                
            IS_WON = WIN_TYPE.LOSE
        else:
            print("win type nothing")
            IS_WON = WIN_TYPE.NOTHING
    else:
        IS_WON = WIN_TYPE.NOTHING

def set_flash_message(text, color=GREEN, timer=5):
    global FLASH_MESSAGE, FLASH_TIMER, FLASH_START, FLASH_COLOR
    FLASH_MESSAGE, FLASH_TIMER, FLASH_START = text, timer, pygame.time.get_ticks()
    FLASH_COLOR = color

def update(authenticator, game_id):
    global has_initialized
    global action_manager
    board_json = server_manager.get_board(authenticator=authenticator, game_id=game_id)
    if not has_initialized:
        has_initialized = True
        initialize_game(board_json)
    global action_manager
    check_if_won(board_json,authenticator.username)
    action_manager.update(Player.instance(id=CURR_PLAYER).name)
    print(action_manager.actions)
    #print(action_manager.actions)
    # TODO: add cascading buy for cards]
    # if we need to cascade, we don't chance players

    check_cascade()
    check_clone()
    if not CITIES_ENABLED:
        check_reserve_noble()
    check_discard()
    update_turn_player(board_json)
    update_players(board_json)
    update_decks(board_json)
    update_tokens(board_json)

    if TRADING_POST_ENABLED:
        TradeRoute.instance().update(board_json)
        update_nobles(board_json)
    if CITIES_ENABLED:
        update_cities(board_json)
    else:
        update_nobles(board_json)

    global current_action_list
    new_unique_action_list = action_manager.get_unique_actions()
    if current_action_list != new_unique_action_list:
        current_action_list = new_unique_action_list
        if current_action_list != []:
            set_flash_message("Your unique actions: " + ", ".join(action_manager.get_unique_actions()))

def check_clone():
    """checks if the card has a clone effect. if so, display card menu with clone action so player can choose what to clone"""
    global action_manager, PERSISTENT_MESSAGE
    if action_manager.has_unlocked_clone(Player.instance(id=CURR_PLAYER).name):
        PERSISTENT_MESSAGE = "You Unlocked a Clone! Choose a card in your sidebar to clone!"
        return True
    else:
        PERSISTENT_MESSAGE = None
        return False

def check_return_tokens():
    """Check if player is forced to return tokens"""
    global action_manager, PERSISTENT_MESSAGE
    if action_manager.has_unlocked_return_token(Player.instance(id=CURR_PLAYER).name):
        PERSISTENT_MESSAGE = "You have too many tokens! Return until you have 10."
        return True
    else:
        PERSISTENT_MESSAGE = None
        return False

def check_reserve_noble():
    """checks if the player has unlocked the reserve noble action. if so, display the reserve noble button"""
    global action_manager, PERSISTENT_MESSAGE
    if action_manager.has_unlocked_reserve_noble(Player.instance(id=CURR_PLAYER).name):
        PERSISTENT_MESSAGE = "You Unlocked a Reserve Noble! Choose a noble to reserve!"
        return True
    else:
        PERSISTENT_MESSAGE = None
        return False

def check_discard():
    """checks if the player has unlocked the discard action. if so, display the discard button"""
    global action_manager, PERSISTENT_MESSAGE
    if action_manager.has_unlocked_discard(Player.instance(id=CURR_PLAYER).name):
        PERSISTENT_MESSAGE = "You Unlocked a Discard! Choose a card to discard!"
        return True
    else:
        PERSISTENT_MESSAGE = None
        return False

def check_cascade():
    """Checks if we need to cascade a card purchase.
      If so, let the next card bought be bought for free"""
    global action_manager, cascade, PERSISTENT_MESSAGE

    if action_manager.has_unlocked_cascade(Player.instance(id=CURR_PLAYER).name):
      cascade = True
      PERSISTENT_MESSAGE = "You Unlocked a Cascade! Choose a card to buy for free!"
    else:
        cascade = False
        PERSISTENT_MESSAGE = None

def update_nobles(board_json):
    ids = [noble['cardId'] for noble in board_json['nobleDeck']['nobles'] if noble is not None]
    Noble.update_all(ids)

def update_cities(board_json):
    ids = [city['cardId'] for city in board_json['cityDeck']['cities']  if city is not None]
    City.update_all(ids)

def update_tokens(board_json):
    Token.update_all(board_json['bank']['tokens'])

def update_decks(board_json):
    GreenDeck.instance().update(board_json['decks'])
    BlueDeck.instance().update(board_json['decks'])
    RedDeck3.instance().update(board_json['decks'])
    YellowDeck.instance().update(board_json['decks'])
    RedDeck2.instance().update(board_json['decks'])
    RedDeck1.instance().update(board_json['decks'])


def update_turn_player(board_json):
    global CURR_PLAYER
    CURR_PLAYER = board_json['currentTurn']
    print("Current player is: " + str(Player.instance(id=CURR_PLAYER).name))


def update_players(board_json):
    global NUM_PLAYERS
    players = board_json['players']
    NUM_PLAYERS = len(players)
    for i in range(0, NUM_PLAYERS):
        player = Player.instance(id=i, name=players[i]['name'])
        player.update_player_inventory(players[i])


def display_everything(current_user):
    global MINIMIZED
    if MINIMIZED:
        return
    # reset the display and re-display everything
    DISPLAYSURF.fill((0, 0, 0))
    display_sidebar()
    display_players(current_user)
    display_board()
    display_decks()
    display_tokens()
    display_menu()
    if CITIES_ENABLED:
        display_cities()
    else:
        display_nobles()
    if TRADING_POST_ENABLED:
        display_trade_routes()

    show_flash_message()  # last so it's on top
    show_persistent_message()
    pygame.display.update()

def display_menu():
  Menu.instance().display(DISPLAYSURF)

def display_trade_routes():
    if TRADING_POST_ENABLED:
        TradeRoute.instance().display(DISPLAYSURF)

def display_board():
    Board.instance().display(DISPLAYSURF)


def display_sidebar():
    # 0 = card, 1 = noble, 2 = reserve
    Sidebar.instance().display(DISPLAYSURF)


def display_decks():
    BlueDeck.instance().display(DISPLAYSURF)
    RedDeck3.instance().display(DISPLAYSURF)
    YellowDeck.instance().display(DISPLAYSURF)
    RedDeck2.instance().display(DISPLAYSURF)
    GreenDeck.instance().display(DISPLAYSURF)
    RedDeck1.instance().display(DISPLAYSURF)


def display_tokens():
    Token.display_all(DISPLAYSURF)


def display_nobles():
    Noble.display_all(DISPLAYSURF)

def display_cities():
    City.display_all(DISPLAYSURF)


def display_players(logged_in_player_username):
    for i in range(NUM_PLAYERS):
        highlight = i == CURR_PLAYER
        Player.instance(id=i).display(DISPLAYSURF, NUM_PLAYERS, logged_in_player_username, highlighted=highlight)


def get_clicked_object(pos):
    board = Board.instance()
    sidebar = Sidebar.instance()
    if Menu.instance().is_clicked(pos):
        return Menu.instance()
    for i in range(NUM_PLAYERS):
        temp_player = Player.instance(id=i)
        if temp_player.is_clicked(pos, WIDTH, HEIGHT, NUM_PLAYERS):
            return temp_player
    if not board.is_clicked(pos):
        return None
    for deck in DECKS:
        card = deck.instance().get_clicked_card(pos)
        if card is not None:
            return card
    token = Token.get_clicked_token(pos)
    if token is not None:
        return token
    if not CITIES_ENABLED:
        noble = Noble.get_clicked_noble(pos)
        if noble is not None:
            return noble
    return None


def get_user_card_selection(card :Card):
    """
    Allow user to choose whether to buy or reserve the card
    :param card:
    :return:
    """
    dim_screen(DISPLAYSURF)
    action = card.get_user_selection(DISPLAYSURF)
    global FLASH_MESSAGE, FLASH_TIMER, CURR_PLAYER, action_manager

    # special case for non-token buys; change the flow to the card menu ui
    STRIP_CARD_IDS = [115,116,117,119,120]
    if card.get_id() in STRIP_CARD_IDS and action == Action.BUY:
        print("found stripping")
        card_menu = CardMenu(list(Player.instance(id=CURR_PLAYER).cards_bought.keys()), CardMenuAction.DISCARD, card)
        card_menu.display()
        return
    
    server_action_id = action_manager.get_card_action_id(card, Player.instance(id=CURR_PLAYER).name,
                                                            action)
    if server_action_id == 0:
        return
    if server_action_id <= -1:
        set_flash_message('Invalid action', color=RED)
        return

    action_manager.perform_action(server_action_id)
    if action == Action.RESERVE:
        set_flash_message('Reserved a card')
    else:
        set_flash_message('Bought a card')
    action_manager.force_update(Player.instance(id=CURR_PLAYER).name)

def get_user_cascade_selection(card :Card):
    """
    Allow user to choose whether to get a card for free or not
    :param card:
    :return:
    """
    dim_screen(DISPLAYSURF)
    action = card.get_user_cascade_selection(DISPLAYSURF)
    global FLASH_MESSAGE, FLASH_TIMER, CURR_PLAYER, action_manager, cascade
    server_action_id = action_manager.get_card_action_id(card, Player.instance(id=CURR_PLAYER).name,
                                                         action)
    if server_action_id == 0:
        return
    if server_action_id <= -1:
        set_flash_message('Invalid action', color=RED)
        return
        
    action_manager.perform_action(server_action_id)
    set_flash_message('You got a free card!')
    global PERMANENT_MESSAGE
    PERMANENT_MESSAGE = None
    action_manager.force_update(Player.instance(id=CURR_PLAYER).name)

def check_sidebar_reserve(user, position):
    global CURR_PLAYER
    global action_manager
    if user == Player.instance(id=CURR_PLAYER).name and Sidebar.instance().current_player.name == user:
        action_manager.update(Player.instance(id=CURR_PLAYER).name)
        if Sidebar.instance().is_clicked_reserve(position):
        # check if clicked on a reserved card to buy it 
            # opens the cardmeny
            #print("checking if clicked reserved")
            print(list(Player.instance(id=CURR_PLAYER).reserved_cards.keys()))
            card_menu = CardMenu(list(Player.instance(id=CURR_PLAYER).reserved_cards.keys()), CardMenuAction.RESERVED)
            card_menu.display()

def check_sidebar_clone(user, position):
    global CURR_PLAYER
    global action_manager
    if user == Player.instance(id=CURR_PLAYER).name and Sidebar.instance().current_player.name == user:
        action_manager.update(Player.instance(id=CURR_PLAYER).name)
        if Sidebar.instance().is_clicked_owned_cards(position):
            print("=== clicked bought cards")
            if check_clone():
                print("=== preparing to clone")
                print(list(Player.instance(id=CURR_PLAYER).cards_bought.keys()))
                card_menu = CardMenu(list(Player.instance(id=CURR_PLAYER).cards_bought.keys()), CardMenuAction.CLONE)
                card_menu.display()

def perform_action(obj, user, position, game_id, authenticator):
  if obj is None:
      return
  global CURR_PLAYER
  global action_manager

  # Check menuand sidebar  not matter the turn
  if isinstance(obj, Menu):
    selection = Menu.instance().get_menu_selection(DISPLAYSURF)
    if selection == "save":
      server_manager.save_game(authenticator, game_id)
      set_flash_message('Game saved')
    elif selection == "lobby":
      print("going to lobby")
      global EXIT, has_initialized
      EXIT = True
      has_initialized = False
      City.delete_all()
      Noble.delete_all()
    return
  if isinstance(obj, Player):
      Sidebar.instance().switch_player(obj)
      return
  # make sure it's the current user's turn, otherwise cannot take cards
  if user == Player.instance(id=CURR_PLAYER).name:
    action_manager.update(Player.instance(id=CURR_PLAYER).name)
    
    if isinstance(obj, Card):
        global cascade
        if cascade:
            get_user_cascade_selection(obj)
        else:
            get_user_card_selection(obj)
    elif isinstance(obj, Token):
        # opens token selection menu
        get_token_selection()
        
    elif isinstance(obj, Noble):
        # check if the player is currently in the reserve noble phase
        
        if action_manager.has_unlocked_reserve_noble(Player.instance(id=CURR_PLAYER).name):
            print("=-=-=-=preparing to reserve a noble")
            #  find noble in the json
            for action in action_manager.actions:
                if "card" in action and "cardId" in action["card"] and action["card"]["cardId"] == obj.get_id()\
                    and action["actionType"] == Action.TAKE_NOBLE.value:

                    reserve_id = action_manager.get_reserve_noble_action_id(obj)
                    action_manager.perform_action(reserve_id)
  else:
    set_flash_message('Not your turn', color=RED)

class CardMenuAction(Enum):
    CLONE = 1
    RESERVED = 2
    DISCARD = 3
class CardMenu:
    """generic menu that displays all the cards that a player owns or reserved, for cloning, discarding and buying"""
    def __init__(self, cards : List[Card], action : CardMenuAction, card_to_buy: Card = None):
        global action_manager
        # action could be buy a reserved, clone, discard functions
        selection_box, selection_box_rect = get_selection_box(DISPLAYSURF, 1, 0.6)
        self.selection_box = selection_box
        self.selection_box_rect = selection_box_rect
        self.highlighted_box = (None, None, None, None) #(x, y, Card), for drawing a highlight around the card selected
        self.highlighted_box2 = (None, None, None, None) # this is the second highlighted box, for cloning
        self.menu = pygame.Surface((WIDTH, HEIGHT))
        self.menu.fill((50, 50, 50))
        #self.menu.set_alpha(200)
        self.menu_rect = self.menu.get_rect()
        self.menu_rect.center = (WIDTH / 2, HEIGHT / 2)
        self.confirm = Button(pygame.Rect(WIDTH/2,HEIGHT*7/10,90,55), None, text="Confirm")
        self.next_page = Button(pygame.Rect(WIDTH*3/4,HEIGHT*7/10,90,55), None, text="Next")
        self.prev_page = Button(pygame.Rect(WIDTH/4,HEIGHT*7/10,90,55), None, text="Prev")
        self.cards = cards # the cards that the menu will display, either owned or reserved depending on context
        self.action = action
        self.send_action = None
        
        self.current_page = 0 # the page that the menu is currently displaying
        self.current_card_mapping = {} # maps the card to the coords that is clicked on it
        self.card_selected = None # the card that the user has selected
        self.card_selected2 = None # the second card that the user has selected
        self.card_to_buy = card_to_buy # in case of stripping, the card we want to buy

    def create_send_action(self, card, payment_cards=None):
        def reserved_action(card):
            self.send_action = action_manager.perform_action(action_manager.get_buy_reserved_card_action_id(card))
        def discard_action(card, payment_cards):
            self.send_action = action_manager.perform_action(action_manager.get_strip_card_action_id(card,payment_cards))
        def clone_action(card):
            self.send_action = action_manager.perform_action(action_manager.get_clone_action_id(card))

        if self.action == CardMenuAction.CLONE:
            return clone_action(card)
        elif self.action == CardMenuAction.RESERVED:
            return reserved_action(card)
        elif self.action == CardMenuAction.DISCARD:
            return discard_action(card, payment_cards)


    def display(self):
        self.selection_box.blit(self.menu, self.menu_rect)
        dim_screen(DISPLAYSURF)
        DISPLAYSURF.blit(self.selection_box, self.selection_box_rect)
        # draw the buttons
        self.confirm.display(DISPLAYSURF)
        self.next_page.display(DISPLAYSURF)
        self.prev_page.display(DISPLAYSURF)

        if self.action == CardMenuAction.RESERVED:
            write_on(DISPLAYSURF,"Choose a Reserved Card to buy",center=(WIDTH/2,HEIGHT/20))
        elif self.action == CardMenuAction.CLONE:
            write_on(DISPLAYSURF,"Choose a card to clone its bonus",center=(WIDTH/2,HEIGHT/20))
        elif self.action == CardMenuAction.DISCARD:
            write_on(DISPLAYSURF,"Choose two cards of the correct color to strip",center=(WIDTH/2,HEIGHT/20))
        write_on(DISPLAYSURF, self.confirm.text, center=self.confirm.rectangle.center,color=WHITE)
        write_on(DISPLAYSURF, self.next_page.text, center=self.next_page.rectangle.center,color=WHITE)
        write_on(DISPLAYSURF, self.prev_page.text, center=self.prev_page.rectangle.center,color=WHITE)
        #write_on(DISPLAYSURF, "Page " + str(self.current_page + 1) + "/" + str(math.ceil(len(self.cards) / 5)), WIDTH/2, HEIGHT*3/10 - 20, size=30)
        # draw the cards, we will draw them the same size as on the board
        if len(self.cards) == 0:
            # if there are no cards, abort
            return 
        card_width, card_height = self.cards[0].get_card_size(Board.instance())

        # wait for user to click on something or leave
        while True:
            if self.card_selected is not None:
                self.draw_border_to_card(self.highlighted_box[3])
            if self.card_selected2 is not None:
                self.draw_border_to_card2(self.highlighted_box2[3])
            for i in range(self.current_page * 6, min(len(self.cards), (self.current_page + 1) * 6)):
                # draw_for_sidebar(self, screen, x, y):
                self.cards[i].draw_for_sidebar(DISPLAYSURF,WIDTH/7 + i*(card_width+55),HEIGHT*3/10 )
                self.current_card_mapping[self.cards[i]] = (WIDTH/7 + i*(card_width+55), HEIGHT*3/10, i)
            pygame.display.update()
            for event in pygame.event.get():
                if event.type == QUIT:
                    pygame.quit()
                    sys.exit()
                elif event.type == MOUSEBUTTONUP:
                    card = self.check_if_clicked_card(pygame.mouse.get_pos())
                    if card:
                        if card == self.card_selected:
                            self.card_selected = None
                            self.remove_border_to_card()
                            continue
                        elif self.card_selected is not None:
                            self.remove_border_to_card()
                            self.card_selected = card
                            pygame.display.update()
                            self.add_border_to_card(card)
                            continue
                        else:
                            self.add_border_to_card(card) # visually indicate this card is chosen
                            self.card_selected = card
                        if self.action == CardMenuAction.DISCARD and self.card_selected is not None:
                            # if the first card is selected, then the second card is selected
                            if card == self.card_selected2:
                                # deselect the second card 
                                self.card_selected2 = None
                                self.remove_border_to_card2()
                                continue
                            elif self.card_selected2 is not None:
                                # there's already such a card selected but it's different
                                self.remove_border_to_card2() # remove prev card
                                self.card_selected2 = card
                                pygame.display.update()
                                self.add_border_to_card2(card) # add new card 
                                continue
                            else: 
                                self.card_selected2 = card
                                self.add_border_to_card2(card)
                                continue
                    
                    elif self.confirm.rectangle.collidepoint(pygame.mouse.get_pos()):
                        if self.card_selected is None:
                            return # if the user clicks confirm without selecting a card, just close the menu

                        if self.action == CardMenuAction.DISCARD:
                            self.create_send_action(self.card_to_buy,[self.card_selected.get_id(),self.card_selected2.get_id()])
                        else:
                            self.create_send_action(self.card_selected)
                        return 
                    elif self.next_page.rectangle.collidepoint(pygame.mouse.get_pos()):
                        # increments current page up to the max page
                        self.current_page = min(self.current_page + 1, len(self.cards) // 6)
                    elif self.prev_page.rectangle.collidepoint(pygame.mouse.get_pos()):
                        # decrements current page down to 0
                        self.current_page = max(self.current_page - 1, 0)
                    else:
                        if self.action == CardMenuAction.CLONE:
                            self.card_selected = None # deselect the card but doesn't close since cloning and stripping is forced
                        elif self.selection_box_rect.collidepoint(pygame.mouse.get_pos()):
                            continue # do nothing if the user clicks inside the menu
                        else: # reserve cards can be closed
                            self.card_selected = None
                            return # if the user clicks outside the menu, just close it
            pygame.display.update()
            FPSCLOCK.tick(FPS)
    def remove_border_to_card(self):
        if not self.highlighted_box[0] and not self.highlighted_box[1]:
            return
        card_width, card_height = self.cards[0].get_card_size(Board.instance())
        x_start = self.highlighted_box[0]
        y_start = self.highlighted_box[1]
        self.highlighted_box = (None, None, None, None)
        
        pygame.draw.rect(DISPLAYSURF, (50, 50, 50), (x_start-10, y_start-10, card_width+55+20, card_height+55+20))
    def remove_border_to_card2(self):
        
        if not self.highlighted_box[0] and not self.highlighted_box[1]:
            return
        card_width, card_height = self.cards[0].get_card_size(Board.instance())
        x_start = self.highlighted_box[0]
        y_start = self.highlighted_box[1]
        self.highlighted_box2 = (None, None, None, None)
        
        pygame.draw.rect(DISPLAYSURF, (50, 50, 50), (x_start-10, y_start-10, card_width+55+20, card_height+55+20))
    def add_border_to_card2(self, card):

        self.highlighted_box2 = (self.current_card_mapping[card][0], self.current_card_mapping[card][1], self.current_card_mapping[card][2], card)

    def add_border_to_card(self, card):
        """visually highlights this card"""
        
        self.highlighted_box = (self.current_card_mapping[card][0], self.current_card_mapping[card][1], self.current_card_mapping[card][2], card)

    def draw_border_to_card(self, card : Card):
        if not self.highlighted_box[0] and not self.highlighted_box[1]:
            return
        card_width, card_height = self.cards[0].get_card_size(Board.instance())
        x_start = self.highlighted_box[0]
        y_start = self.highlighted_box[1]
        #card = 
        pygame.draw.rect(DISPLAYSURF, RED, (x_start-10, y_start-10, card_width+55+20, card_height+55+20))
        #card_index = self.current_card_mapping[card][2]
        #card.draw_for_sidebar(DISPLAYSURF,WIDTH/7 + card_index*(card_width+55),HEIGHT*3/10 ) # card is on top of the border
        #pygame.display.update()

    def draw_border_to_card2(self, card : Card):
        if not self.highlighted_box2[0] or not self.highlighted_box2[1]:
            return
        card_width, card_height = self.cards[0].get_card_size(Board.instance())
        x_start = self.highlighted_box2[0]
        y_start = self.highlighted_box2[1]
        #card = 
        pygame.draw.rect(DISPLAYSURF, RED, (x_start-10, y_start-10, card_width+55+20, card_height+55+20))
        #card_index = self.current_card_mapping[card][2]
        #card.draw_for_sidebar(DISPLAYSURF,WIDTH/7 + card_index*(card_width+55),HEIGHT*3/10 ) # card is on top of the border
        #pygame.display.update()

    def check_if_clicked_card(self, mouse_pos):
        for card in self.current_card_mapping:
            x_start = self.current_card_mapping[card][0]
            y_start = self.current_card_mapping[card][1]
            x_end = x_start + Card.get_card_size(Board.instance())[0] * 1.5
            y_end = y_start + Card.get_card_size(Board.instance())[1] * 1.5
            if x_start <= mouse_pos[0] <= x_end and y_start <= mouse_pos[1] <= y_end:
                return card
        return False
    
class TokenMenu:
    """generates all the buttons, remembers which tokens user picked, checks if legal"""
    def __init__(self):
        selection_box, selection_box_rect = get_selection_box(DISPLAYSURF, 1, 0.6)
        self.selection_box = selection_box
        self.selection_box_rect = selection_box_rect
        self.selection_box_rect.center = (WIDTH / 2, HEIGHT / 4)

        self.menu = pygame.Surface((WIDTH, HEIGHT/ 4))
        self.menu.fill((0, 0, 0))
        self.menu.set_alpha(200)
        self.menu_rect = self.menu.get_rect()
        self.menu_rect.center = (WIDTH / 2, HEIGHT / 4)

        self.token_selection_list :List[IndividualTokenSelection] = [] 
        # button for confirming token selection
        self.confirm_take_button = Button(pygame.Rect(WIDTH/2-110,HEIGHT*4/10,100,55), self.confirm_take_token, text="Take Token")
        self.confirm_return_button = Button(pygame.Rect(WIDTH/2 + 30,HEIGHT*4/10,100,55), self.confirm_return_token, text="Return Token")

        
    def generate_selection_and_buttons(self) -> Tuple[List[IndividualTokenSelection],List[Button]]:
        # generate a list of buttons for the token menu   
        self.token_selection_list = []     
        button_list = []
        for index,token in enumerate(Token.get_all_token_colors()):
            tokenSelection = IndividualTokenSelection(token,WIDTH/10.5+index*200,HEIGHT/6)
            tokenSelection.display()
            self.token_selection_list.append(tokenSelection)
            button_list.append(tokenSelection.incrementButton)
            button_list.append(tokenSelection.decrementButton)
        return self.token_selection_list,button_list

    def display(self):
        self.selection_box.blit(self.menu, self.menu_rect)
        
    
    def confirm_take_token(self) -> None:
        """ checks if the input corresponds to a valid token selection
        returns the token selection if valid, None if not valid """
        global FLASH_MESSAGE, FLASH_TIMER, CURR_PLAYER, action_manager

        valid_selection = True

        
        # Logic to validate tokens on client
        total_tokens = 0
        same_color_chosen = False
        user_selection: Dict[Token,int] = {}
        for token_selection in self.token_selection_list:
            
            current_token = token_selection.token
            current_count = token_selection.amount

            print(current_token)
            print(current_count)

            user_selection[current_token] = current_count
            if current_count == 2:
                same_color_chosen = True
            if current_count > 2:
                valid_selection = False
            total_tokens += current_count
        
        # Can only take 2 tokens total if taken from the same color
        if total_tokens > 2 and same_color_chosen:
            valid_selection = False
        
        # Can take up to 3 colors
        if total_tokens > 3:
            valid_selection = False

        # Return to the flow if invalid
        if not valid_selection:
            set_flash_message('Invalid selection', color=RED)
            return

        # Find the action id and perform it if it's valid
        take_token_action_id: int = action_manager.get_token_action_id(user_selection,Player.instance(id=CURR_PLAYER).name,Action.TAKE_TOKENS)

        # Return to the flow if invalid
        if take_token_action_id < 0:
            set_flash_message('Illegal selection', color=RED)
            return
        
        action_manager.perform_action(take_token_action_id)

        action_manager.force_update(Player.instance(id=CURR_PLAYER).name)
        check_return_tokens()

        return
    
    def confirm_return_token(self) -> None:
        """ checks if the input corresponds to a valid token selection for RETURNING
        returns the token selection if valid, None if not valid """
        global FLASH_MESSAGE, FLASH_TIMER, CURR_PLAYER, action_manager
        
        # Logic to validate tokens on client
        user_selection: Dict[Token,int] = {}
        for token_selection in self.token_selection_list:
            
            current_token = token_selection.token
            current_count = token_selection.amount

            user_selection[current_token] = current_count

        # Find the action id and perform it if it's valid
        take_token_action_id: int = action_manager.get_token_action_id(user_selection,Player.instance(id=CURR_PLAYER).name,Action.RETURN_TOKENS)

        # Return to the flow if invalid
        if take_token_action_id < 0:
            set_flash_message('Illegal selection', color=RED)
            return
        
        action_manager.perform_action(take_token_action_id)

        return

    def get_user_token_selection(self) -> Action:
            dim_screen(DISPLAYSURF)
            DISPLAYSURF.blit(self.selection_box, self.selection_box_rect)
            components_generated: Tuple[List[IndividualTokenSelection],List[Button]] = self.generate_selection_and_buttons()
            individual_token_list: List[IndividualTokenSelection] = components_generated[0]
            button_list: List[Button] = components_generated[1]
            self.display()

            self.confirm_take_button.display(DISPLAYSURF)
            self.confirm_return_button.display(DISPLAYSURF)

            write_on(DISPLAYSURF,self.confirm_take_button.text,center=self.confirm_take_button.rectangle.center, color=WHITE)
            write_on(DISPLAYSURF,self.confirm_return_button.text,center=self.confirm_return_button.rectangle.center, color=WHITE)
            write_on(DISPLAYSURF,"Take 2 identical tokens or 3 different colored tokens",center=(WIDTH/2,HEIGHT/20))
            write_on(DISPLAYSURF,"You must return tokens over 10",center=(WIDTH/2,HEIGHT/12))

            pygame.display.update()
            for token_selection in button_list:
                pygame.draw.rect(self.selection_box,token_selection.color,token_selection.rectangle)
                #self.selection_box.blit(button.rectangle)
                #button.display()
            
            # Check the list of actions when we click the tokens
            action_manager.get_actions_json()

            while True:
                for event in pygame.event.get():
                    if event.type == pygame.KEYDOWN:
                        if event.key == pygame.K_ESCAPE:
                            return Action.CANCEL
                    elif event.type == pygame.QUIT:
                        pygame.quit()
                        quit()

                    elif event.type == MOUSEBUTTONDOWN:
                        clicked_position = pygame.mouse.get_pos()
                        # button is individual token selection class
                        if self.confirm_take_button.rectangle.collidepoint(clicked_position):
                            print("confirm take")
                            return self.confirm_take_button.activation()
                        if self.confirm_return_button.rectangle.collidepoint(clicked_position):
                            print("confirm return")
                            return self.confirm_return_button.activation()
                        for token_selection in individual_token_list:
                            if token_selection.incrementButton.rectangle.collidepoint(clicked_position):
                                print("increment")
                                token_selection.incrementButton.activation()
                            elif token_selection.decrementButton.rectangle.collidepoint(clicked_position):
                                print("decrement")
                                token_selection.decrementButton.activation()
                        if not TokenMenu().selection_box_rect.collidepoint(clicked_position):
                            print("clicked out")
                            return Action.CANCEL
                        
                        pygame.display.update()

def get_token_selection():
    """RETURNS WHAT TOKENS PLAYER CHOSE"""

    # draw the 7 buttons 
    TokenMenu().get_user_token_selection()
    pygame.display.update()
    # wait for user to click on a button

def check_toggle(mouse_pos):
    sidebar = Sidebar.instance()
    page_num = sidebar.is_clicked_toggle(mouse_pos)
    sidebar.toggle(page_num)

def play(authenticator, game_id, screen):
    """Main game loop"""
    DISPLAYSURF = screen
    last_update = pygame.time.get_ticks() # force update on first loop
    global action_manager, MINIMIZED
    global EXIT
    global PERSISTENT_MESSAGE
    global IS_WON, TRADING_POST_ENABLED, CITIES_ENABLED, has_initialized
    EXIT = False
    action_manager = ActionManager(authenticator=authenticator, game_id=game_id)
    update(authenticator, game_id)
    logged_in_user = authenticator.username
    display_everything(logged_in_user)
    while True:
        if IS_WON != WIN_TYPE.NOTHING:
            
            if IS_WON == WIN_TYPE.WIN:
                dim_screen(DISPLAYSURF)

                PERSISTENT_MESSAGE = "You won!"
                show_persistent_message()
                pygame.display.update()
                while True:
                    for event in pygame.event.get():
                        if event.type == MOUSEBUTTONDOWN or event.type == KEYDOWN:
                            has_initialized = False
                            TRADING_POST_ENABLED = False 
                            CITIES_ENABLED = False
                            IS_WON == WIN_TYPE.NOTHING
                            return
            elif IS_WON == WIN_TYPE.TIE:
                dim_screen(DISPLAYSURF)
                PERSISTENT_MESSAGE = "You tied!"
                show_persistent_message()
                pygame.display.update()
                while True:
                    for event in pygame.event.get():
                        if event.type == MOUSEBUTTONDOWN or event.type == KEYDOWN:
                            has_initialized = False
                            TRADING_POST_ENABLED = False 
                            CITIES_ENABLED = False
                            IS_WON == WIN_TYPE.NOTHING
                            return
            elif IS_WON == WIN_TYPE.LOSE:
                dim_screen(DISPLAYSURF)
                PERSISTENT_MESSAGE = "You lost!"
                show_persistent_message()
                pygame.display.update()
                while True:
                    for event in pygame.event.get():
                        if event.type == MOUSEBUTTONDOWN or event.type == KEYDOWN:
                            has_initialized = False
                            TRADING_POST_ENABLED = False 
                            CITIES_ENABLED = False
                            IS_WON == WIN_TYPE.NOTHING
                            return

        if pygame.time.get_ticks() - last_update > 2000:
            last_update = pygame.time.get_ticks()
            # await async_update(authenticator, game_id)
            # start a new thread
            with threading.Lock():
                threading.Thread(target=update, args=(authenticator, game_id)).start()
        for event in pygame.event.get():
            if event.type == QUIT:
                pygame.quit()
                sys.exit()
            
            elif event.type == pygame.VIDEORESIZE:   
                print("MAXIMIZED")          
                MINIMIZED = False
            elif event.type == KEYDOWN:
                if event.key == K_ESCAPE:
                    pygame.quit()
                    sys.exit()
                # if event.key == K_m:
                #     # minimize the window
                #     # FIXME: Is there a better way to do this?
                #     MINIMIZED = True
                #     pygame.display.iconify()
                if event.key == K_UP:
                    Sidebar.instance().scroll_sidebar(50)
                if event.key == K_DOWN:
                    Sidebar.instance().scroll_sidebar(-50)
            elif event.type == MOUSEBUTTONDOWN:
                if event.button == 4:
                    Sidebar.instance().scroll_sidebar(50)
                elif event.button == 5:
                    Sidebar.instance().scroll_sidebar(-50)
                else:
                    # check if it's the sidebar toggle
                    position = pygame.mouse.get_pos()
                    check_toggle(position)
                    if TRADING_POST_ENABLED:
                        TradeRoute.instance().check_click(position,DISPLAYSURF)
                    check_sidebar_reserve(logged_in_user, position)
                    check_sidebar_clone(logged_in_user, position)
                    obj = get_clicked_object(position)
                    perform_action(obj, logged_in_user, position, game_id, authenticator)
                    if EXIT:
                        has_initialized = False
                        TRADING_POST_ENABLED = False 
                        CITIES_ENABLED = False
                        IS_WON == WIN_TYPE.NOTHING
                        return
                    with threading.Lock():
                        threading.Thread(target=update, args=(authenticator, game_id)).start()

        display_everything(logged_in_user)
        pygame.display.update()
        FPSCLOCK.tick(FPS)
