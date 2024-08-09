import pygame
from singleton import Singleton
from color import Color
from utils import write_on, dim_screen, get_selection_box, button

@Singleton
class Menu:
  """
  Menu class. Has button to save game, or go back to lobby.
  Menu is in the top-right corner of the screen. There should be no space between the top of the screen and the menu.
  """
  def __init__(self, screen_width, screen_height):
    self.width = 200
    self.height = 75
    self.menuRect = pygame.Rect(0, 0, self.width, self.height)
    self.menuRect.center = (screen_width - self.width / 2, self.height / 2)

  def display(self, screen):
    # display grey box
    menu = pygame.Surface((self.width, self.height))
    menu.fill(Color.GREY.value)
    write_on(menu, "Menu", font_size=30, color=Color.WHITE.value)
    screen.blit(menu, self.menuRect)

  def get_menu_selection(self, screen):
    """
    Pop up the menu, and get the user selection.
    User can select to save the game, or go back to the lobby.
    """
    print("get_menu_selection")
    # display grey box
    dim_screen(screen)
    menu_surface, menu_rect = get_selection_box(screen)

    button_width = 200
    button_height = 75

    # add the two buttons
    save_button = button("Save Game", button_width, button_height, color=Color.GREEN.value)
    lobby_button = button("Return To Lobby", button_width, button_height, color=Color.GREY.value)
    menu_surface.blit(save_button, (menu_surface.get_width() - button_width - 20, menu_surface.get_height() - button_height - 20))
    menu_surface.blit(lobby_button, (20, menu_surface.get_height() - button_height - 20))
    save_button = save_button.get_rect()
    lobby_button = lobby_button.get_rect()
    save_button.x += menu_rect.x + menu_surface.get_width() - button_width - 20
    save_button.y += menu_rect.y + menu_surface.get_height() - button_height - 20
    lobby_button.x += menu_rect.x + 20
    lobby_button.y += menu_rect.y + menu_surface.get_height() - button_height - 20
    
    screen.blit(menu_surface, menu_rect)
    pygame.display.update()

    # wait for user input
    while True:
      for event in pygame.event.get():
        if event.type == pygame.QUIT:
          pygame.quit()
          quit()
        if event.type == pygame.MOUSEBUTTONDOWN:
          if save_button.collidepoint(event.pos):
            return "save"
          if lobby_button.collidepoint(event.pos):
            return "lobby"
          if not menu_rect.collidepoint(event.pos):
            return None

  def get_rect(self):
    return self.menuRect

  def get_x(self):
    return self.menuRect.x

  def get_y(self):
    return self.menuRect.y

  def get_width(self):
    return self.width

  def get_height(self):
    return self.height

  def get_center(self):
    return self.menuRect.center

  def is_clicked(self, mouse_pos):
    return self.menuRect.collidepoint(mouse_pos)
