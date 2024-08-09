import os
import pygame
import sys
from typing import List, Callable, Tuple

currentdir = os.path.dirname(os.path.realpath(__file__))
parentdir = os.path.dirname(currentdir)
sys.path.append(parentdir)
from authenticator import *

from session import get_session, post_session, delete_session, put_session

HEIGHT = 750
WIDTH = 900
GREY = (57, 57, 57)
WHITE = (255, 255, 255)
LIGHT_GREY = (99, 99, 99)
LIGHT_BLUE = "lightskyblue3"
GREEN = (0, 204, 0)
RED = (255, 0, 0)
FPS = 15

os.chdir(os.path.dirname(os.path.abspath(__file__)))
pygame.init()
pygame.display.set_caption('Splendor')
base_font = pygame.font.Font(None, 28)  # font, size
clock = pygame.time.Clock()
screen = pygame.Surface((WIDTH, HEIGHT))

splendor_text = pygame.image.load('../sprites/splendor-title.png')
splendor_text = pygame.transform.scale(splendor_text, (500, 200))

color_active = pygame.Color(LIGHT_BLUE)
color_passive = pygame.Color(LIGHT_GREY)
color_error = pygame.Color(RED)

'''ALL FUNCTIONS HERE HAVE TO BE CHANGED'''

def new_text(text, color, x, y):
    # rect = pygame.Rect(rectx, recty, rectwidth, rectheight)
    # pygame.draw.rect(screen, rectcolor, rect)
    text_surface = base_font.render(text, True, color)
    text_rect = text_surface.get_rect()
    text_rect.topleft = (x, y)
    screen.blit(text_surface, text_rect)

def new_button(rectx, recty, rectwidth, rectheight, rectcolor) -> pygame.Rect:
    rect = pygame.Rect(rectx, recty, rectwidth, rectheight)
    pygame.draw.rect(screen, rectcolor, rect)
    return rect

def get_games(sessions):
    # gets games currently stored in memory
    return list(sessions.keys())

# constants for game_rect, the box(es) that shows session ids and usernames
GAME_RECT_INIT_X = 150
GAME_RECT_INIT_Y = 250
GAME_RECT_INCR_Y = 100 # How much to increment Y per listing
GAME_RECT_SIZE = (400,55)

# constants for del_rect, the box associated with game_rect for deleting or leaving
DEL_RECT_INIT_X = GAME_RECT_INIT_X + 405
DEL_RECT_INIT_Y = GAME_RECT_INIT_Y
DEL_RECT_INCR_Y = GAME_RECT_INCR_Y
DEL_RECT_SIZE = (90,55)

# constants for launch_rect, the box associated with game_rect for joining, launching, starting
LAUNCH_RECT_INIT_X = GAME_RECT_INIT_X + 505
LAUNCH_RECT_INIT_Y = GAME_RECT_INIT_Y
LAUNCH_RECT_INCR_Y = GAME_RECT_INCR_Y
LAUNCH_RECT_SIZE = (90,55)

# max sessions before putting more on the next page
MAX_SESSIONS_PER_PAGE = 4
current_page = 0
#current_type = "splendor"



class Button:
    def __init__(self,rectangle : pygame.Rect, on_click_event : Callable[[None], None], color: Tuple[int,int,int] = LIGHT_GREY, text: str = "") -> None:
        self.rectangle = rectangle
        self.activation = on_click_event
        self.color = color
        self.text = text

    def set_text(self, text):
        self.text = text

    def get_rectangle(self, screen, full_screen):
        difference_width = (full_screen.get_width() - screen.get_width()) / 2
        difference_height = (full_screen.get_height() - screen.get_height()) / 2
        return pygame.Rect(self.rectangle.x + difference_width, self.rectangle.y + difference_height, self.rectangle.width, self.rectangle.height)

class ToggleButton (Button):
    def __init__(self, rectangle : pygame.Rect, on_click_event : Callable[[None], None], color: Tuple[int,int,int] = RED, text: str = "") -> None:
        super().__init__(rectangle, on_click_event, color, text)
        self.active = False
        # on click event should always be the toggle func
        # for use with the togglebuttons
    def toggle(self):
        self.active = not self.active
        if self.active:
            self.color = GREEN
        else:
            self.color = RED
        

# action when the back button is pressed
def back_button_event() -> None:
    screen.fill(GREY)
    exit()



# Class for a session listing. A session listing is the game info and interaction buttons
# associated with an existing session in the session list
class SessionListing:
    def __init__(self, authenticator, session_id : str, session_info, index : int, full_screen) -> None:
        self.session_id = session_id
        # Access relevant information about the session
        self.creator = session_info["creator"] # str playername
        self.min_plr = session_info["gameParameters"]["minSessionPlayers"] # int
        self.max_plr = session_info["gameParameters"]["maxSessionPlayers"] # int
        self.launched = session_info["launched"] # boolean
        self.plr_list = session_info["players"] # list of str playernames
        self.savegame = session_info["savegameid"] # str
        self.game_type = session_info["gameParameters"]["name"] # str
        if "created" in session_info.keys():
            self.created = session_info["created"]
        else:
            # If it's not there, then we got the session ftom the created ones
            self.created = True
        self.authenticator = authenticator
        self.current_user = authenticator.username
        
        # index_order and page_number start from 0
        # index order is the order of this listing on the page it is in
        self.index_order = index % MAX_SESSIONS_PER_PAGE
        # page_number is the page this listing is in
        self.page_number = index // MAX_SESSIONS_PER_PAGE
        self.full_screen = full_screen

        # Rects associated with this session listing in the session list
        # TODO: Generate here and link to click events
        game_info_rect = pygame.Rect((GAME_RECT_INIT_X,GAME_RECT_INIT_Y+GAME_RECT_INCR_Y*self.index_order),GAME_RECT_SIZE)
        self.game_info = game_info_rect
        # TODO: use button class
        self.red_button :Button = None
        self.green_button :Button = None
    
    # get a string of the game description, to be blitted in the box later
    def get_game_info(self) -> str:
        return f"{self.creator} / {','.join(self.plr_list[1:])} [{len(self.plr_list)}/{self.min_plr}-{self.max_plr}]"
    def get_game_info_game(self) -> str:
        if self.savegame != "":
            if self.game_type == "SplendorTraderoutes":
                return f"S-{self.savegame} / SplendorTrade"
            else:
                return f"S-{self.savegame} / {self.game_type}"
        else: #not a savedgame
            if self.game_type == "SplendorTraderoutes":
                return f"{self.session_id} / SplendorTrade"
            else:
                return f"{self.session_id} / {self.game_type}"
    def assign_buttons(self) -> None:
        red_rect_position = (DEL_RECT_INIT_X,DEL_RECT_INIT_Y+DEL_RECT_INCR_Y*self.index_order)
        red_rect = pygame.Rect(red_rect_position,DEL_RECT_SIZE)
        self.red_button = Button(red_rect,self.redButtonEvent,RED)

        green_rect_position = (LAUNCH_RECT_INIT_X,LAUNCH_RECT_INIT_Y+LAUNCH_RECT_INCR_Y*self.index_order)
        green_rect = pygame.Rect(green_rect_position,LAUNCH_RECT_SIZE)
        self.green_button = Button(green_rect,self.greenButtonEvent,GREEN)

    # generate the list of button so they can be added to the button list in the main loop
    def get_button_list(self) -> List[Button]:
        return [self.red_button,self.green_button]
    
    def display(self) -> None:
        game_info = self.get_game_info()
        game_info_game = self.get_game_info_game()
        pygame.draw.rect(screen, LIGHT_GREY, self.game_info)
        new_text(game_info, WHITE, GAME_RECT_INIT_X, GAME_RECT_INIT_Y+30+GAME_RECT_INCR_Y*self.index_order)
        new_text(game_info_game, WHITE, GAME_RECT_INIT_X, GAME_RECT_INIT_Y+GAME_RECT_INCR_Y*self.index_order)
        # set text for buttons
        if self.current_user == self.creator and not self.launched:
            self.red_button.set_text("Delete")
        elif self.current_user in self.plr_list and not self.launched:
            self.red_button.set_text("Leave")
        if not self.created and self.current_user == self.creator:
            self.green_button.set_text("Load")
        elif not self.launched:
            if self.current_user == self.creator and len(self.plr_list) >= self.min_plr:
                self.green_button.set_text("Launch")
            elif self.current_user not in self.plr_list and len(self.plr_list) < self.max_plr:
                self.green_button.set_text("Join")
        else:
            self.green_button.set_text("Play")
            
    def redButtonEvent(self) -> None:
        # game is not yet launched; creator can delete, others can leave
        if self.current_user == self.creator and not self.launched:
            self.del_sess()
        elif self.current_user in self.plr_list and not self.launched:
            self.leave_sess()
    
    def greenButtonEvent(self):
        if not self.created and self.current_user == self.creator:
            self.create_saved_game()
        elif not self.launched:
            # game is not yet launched; creator can launch, others can join
            if self.current_user == self.creator:
                self.launch_sess()
            elif self.current_user not in self.plr_list:
                self.join_sess()
        elif self.launched:
            return self.play_sess()

    def create_saved_game(self) -> None:
        print("Creating saved game ", self.savegame)
        post_session.create_session(self.authenticator.username, self.authenticator.get_token(escape=True), self.game_type, self.savegame)

    # logged-in user is the creator and deletes the session
    def del_sess(self) -> None:
        delete_session.delete_session(self.authenticator.get_token(escape=True), self.session_id)
        session(self.authenticator, self.full_screen)

    # logged-in user is the creator and launches the session if there are enough players
    def launch_sess(self) -> None:
        post_session.launch_session(self.authenticator.get_token(escape=True), self.session_id)

    # logged-in user is not the creator and joins the session
    def join_sess(self) -> None:
            
        put_session.add_player(self.authenticator.get_token(escape=True), self.session_id, self.authenticator.username)

        session(self.authenticator, self.full_screen)

    # logged-in user starts playing in the session
    def play_sess(self) -> None:
        #post_session.play_session(self.authenticator.get_token(), self.session_id)
        return self.session_id

    # logged-in user leaves the session
    def leave_sess(self) -> None:
        delete_session.remove_player(self.authenticator.get_token(escape=True), self.session_id, self.authenticator.username)
        session(self.authenticator, self.full_screen)

# Takes sessions json and outputs a list of pygame objects to be blitted
def generate_session_list_buttons(authenticator,sessions_json, full_screen) -> List[SessionListing]:
    if len(get_games(sessions_json)) == 0:
        # if there are no sessions return empty list
        return []
    
    session_list = []
    # for each existing session, create a SessionListing object
    # it will handle the buttons, information, and events
    for index,session in enumerate(sessions_json):
        # enumerate keeps track of the index, to be used for positioning and paging
        new_session = SessionListing(authenticator,session,sessions_json[session],index, full_screen)
        session_list.append(new_session)
    
    return session_list

def session(authenticator :Authenticator, full_screen: pygame.Surface) -> int:
    # set up the screen  
    full_screen.fill(GREY)
    # center the screen on the full screen
    screen_rect = screen.get_rect()
    full_screen_rect = full_screen.get_rect()
    screen_rect.center = full_screen_rect.center
    full_screen.blit(screen, screen_rect)

    # current_page: 0-indexed
    # functions for the pagination buttons
    def previous_button_event() -> None:
        global current_page
        current_page = max(0, current_page-1)

    def next_button_event() -> None:
        global current_page
        current_page = min(current_page+1, max(0,(len(session_list)-1) // MAX_SESSIONS_PER_PAGE))

    def create_button_event() -> None:
        post_session.create_session(authenticator.username, authenticator.get_token(escape=True), parse_type())


    # These buttons are always visible regardless of page
    back_rect = Button(pygame.Rect((50, 100, 150, 70)), back_button_event, RED)
    previous_rect = Button(pygame.Rect((150, 660, 150, 70)), previous_button_event, LIGHT_BLUE)
    next_rect = Button(pygame.Rect((600, 660, 150, 70)), next_button_event, LIGHT_BLUE)
    # Back is the logout button, not to be confused with previous
    create_rect = Button(pygame.Rect((600, 100, 150, 70)), create_button_event, LIGHT_BLUE)
    trade_toggle = ToggleButton(pygame.Rect((440, 100, 150, 70)), None, RED, "Trade Route")
    cities_toggle = ToggleButton(pygame.Rect((280, 100, 150, 70)), None, RED, "Cities")

    trade_toggle.activation = trade_toggle.toggle
    cities_toggle.activation = cities_toggle.toggle

    while True:
        screen.fill(GREY)

        def parse_type() -> str:
            game_type = "Splendor"
            if trade_toggle.active:
                game_type += "Traderoutes"
            if cities_toggle.active:
                game_type += "Cities"
            return game_type

        sessions_json = get_session.get_all_sessions(authenticator)
        session_list = generate_session_list_buttons(authenticator,sessions_json, full_screen)
        # This is the list of buttons that should be visible. We need to draw them.
        clickable_buttons :List[Button] = []


        clickable_buttons.append(back_rect)
        clickable_buttons.append(next_rect)
        clickable_buttons.append(previous_rect)
        clickable_buttons.append(create_rect)
        clickable_buttons.append(trade_toggle)
        clickable_buttons.append(cities_toggle)

        # display page number: 1-indexed
        new_text(f"{current_page+1} / {max(1,((len(session_list)-1) // MAX_SESSIONS_PER_PAGE)+1)}", WHITE, 385, 665)

        for session_listing in session_list:
            # Display all the listings in our current page
            if session_listing.page_number == current_page:
                # init the buttons with their functions
                session_listing.assign_buttons()
                # display the game info string
                session_listing.display()
                # This adds this visible session's buttons to the list of clickable buttons
                clickable_buttons += session_listing.get_button_list()

        # draw all the buttons on the screen
        for button in clickable_buttons:
            pygame.draw.rect(screen,button.color,button.rectangle)
            new_text(button.text, WHITE, button.rectangle.x + 10, button.rectangle.y + 10)

        # writing text on buttons
        new_text("Exit", WHITE, 85, 125)
        new_text("Next", WHITE, 625, 665)
        new_text("Previous", WHITE, 150, 665)
        new_text("Create", WHITE, 625, 125)

        for event in pygame.event.get():
            # when the user clicks or types anything
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()
            if event.type == pygame.MOUSEBUTTONUP:
                clicked_position = pygame.mouse.get_pos()

                for button in clickable_buttons:
                    if button.get_rectangle(screen, full_screen).collidepoint(clicked_position):
                        button_return = button.activation()
                        if not (button_return is None):
                            return button_return

        full_screen.blit(screen, screen_rect)
        pygame.display.flip()
        clock.tick(FPS)