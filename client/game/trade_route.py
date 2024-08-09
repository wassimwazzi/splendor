import pygame

from pygame.locals import *
from win32api import GetSystemMetrics
from board import Board
from card import Card
from color import Color
from sidebar import Sidebar
from singleton import Singleton
from utils import outlined_text
from typing import List, Dict

@Singleton
class TradeRoute:
    def __init__(self):
        WIDTH, HEIGHT = GetSystemMetrics(0), GetSystemMetrics(1)
        self.screen_width = WIDTH
        self.screen_height = HEIGHT
        self.card_size = Card.get_card_size()
        self.board = Board.instance()
        self.board_width = self.board.get_rect().width
        self.board_height = self.board.get_rect().height
        # rect: (x, y, width, height)
        self.trade_route_button = pygame.Rect(self.board_width * 1.1, self.board_height * 17/20,
                                self.board_width / 8, self.card_size[1] / 4)
        
        self.trade_route_menu: pygame.Surface = self._get_trade_routes_image()
        self.coat_of_arms_list = []
        for player in range(0,4):
            self.coat_of_arms_list.append(self._get_coat_image(player))

        self.menu_x_pos = (self.screen_width - self.trade_route_menu.get_width())//2
        self.menu_y_pos = (self.board_height - self.trade_route_menu.get_height())//2

        # Pixel shifting
        self.initial_coat_x = self.menu_x_pos + 58
        self.initial_coat_y = self.menu_y_pos + 22

        self.coat_shift_x = 98
        self.coat_shift_y = 110
        self.route_shift_x = 267

        # Info storage
        # Key are the player_pos, 0-indexed
        self.owned_trade_routes_by_player: Dict[int,List[int]] = {}
        self.test_trade_routes_by_player: Dict[int,List[int]] = {
            0: [1,2,3,4,5],
            1: [1,2,3,4,5],
            2: [1,2,3,4,5],
            3: [1,2,3,4,5]
        }

    def check_click(self, pos, screen):
        """checks if the trade route button is clicked"""
        if self.trade_route_button.collidepoint(pos):
            self.open_trade_route_menu(screen)
    
    def update(self, board_json) -> None:
        """Update the routes owned by each player internally"""
        players = board_json['players']
        for index, player in enumerate(players):
            owned_routes = []

            coat_of_arms = player['coatOfArms']
            for coat in coat_of_arms:
                owned_routes.append(coat["id"])
            
            owned_routes.sort()
            self.owned_trade_routes_by_player[index] = owned_routes
    
    def display_coat_of_arms(self, screen: pygame.Surface) -> None:
        # Iterate over the player ids
        for player_id in self.owned_trade_routes_by_player:
            # Iterate over their owned trade routes
            for trade_route_id in self.owned_trade_routes_by_player[player_id]:
                # Calculate where to place the coat
                x_pos = self.initial_coat_x + (trade_route_id-1) * self.route_shift_x + (player_id) * self.coat_shift_x
                y_pos = self.initial_coat_y
                # third and fourth players go on the row below
                # shift x back and shift y down
                if player_id > 1:
                    x_pos -= self.coat_shift_x*2
                    y_pos += self.coat_shift_y
                # STUPID Edge cases because the mat for the coat placement
                # doesn't have the same distance everywhere
                if trade_route_id == 3:
                    x_pos += 3
                elif trade_route_id == 4:
                    x_pos += 12
                screen.blit(self.coat_of_arms_list[player_id], dest=(x_pos,y_pos))

    def display(self, screen):
        """displays the trade route button"""
        pygame.draw.rect(screen, (0,0,0,0), self.trade_route_button)
        outlined_text(screen, text="Trade Routes", center=self.trade_route_button.center)

    def open_trade_route_menu(self, screen: pygame.Surface):
        """opens the trade route menu after clicking on button"""
        while True:
            screen.blit(self.trade_route_menu, dest=(self.menu_x_pos,self.menu_y_pos))

            # Logic for displaying the coat of arms
            self.display_coat_of_arms(screen)

            pygame.display.update()

            for event in pygame.event.get():
                if event.type == pygame.KEYDOWN:
                    return
                elif event.type == pygame.QUIT:
                    pygame.quit()
                    quit()

                elif event.type == MOUSEBUTTONDOWN:
                    return

    def _get_trade_routes_image(self) -> pygame.Surface:
        """
        Returns the coat with coat_id [0,3]
        """
        return pygame.image.load(f'../sprites/trade_route/board_extension.png')

    def _get_coat_image(self, coat_id: int) -> pygame.Surface:
        """
        Returns the coat with coat_id [0,3]
        """
        return pygame.image.load(f'../sprites/trade_route/coat{coat_id}.png')