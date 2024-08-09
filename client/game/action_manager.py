from game import server_manager
from game.action import Action
from game.card import Card
from game.noble import Noble
from game.splendorToken import Token
from typing import Dict, List


class ActionManager:
    def __init__(self, game_id, authenticator):
        self.game_id = game_id
        self.authenticator = authenticator
        self.actions = []
        self.last_updated_player = None

    def update(self, player_name):
        if self.last_updated_player == player_name and self.last_updated_player is not None:
            # To avoid unnecessary requests
            return
        self.last_updated_player = player_name
        self.actions = server_manager.get_actions(self.authenticator, self.game_id, player_name)
    
    def force_update(self, player_name):
        """
        Needed when we do cascade, so we can get the new action list
        """
        self.last_updated_player = player_name
        self.actions = server_manager.get_actions(self.authenticator, self.game_id, player_name)

    def get_card_action_id(self, card: Card, player_name: str, action_type: Action) -> int:
        

        if action_type == Action.CANCEL:
            return 0
        if player_name != self.last_updated_player:
            # Not player's turn
            return -1
        if action_type == Action.CASCADE:
            # Cascade is special
            return self.get_cascade_action_id(card)
        elif action_type == Action.TAKE_NOBLE:
            # Reserve is special
            return self.get_reserve_noble_action_id(card)
        elif action_type == Action.DISCARD:
            # Discard is special
            return self.get_discard_action_id(card)
        elif action_type == Action.CLONE_CARD:
            # Clone is special
            return self.get_clone_action_id(card)
        elif action_type == Action.BUY_RESERVED_CARD:
            # Buy reserved card is special
            return self.get_buy_reserved_card_action_id(card)

        for action in self.actions:
            if "card" in action and "cardId" in action["card"] and action["card"]["cardId"] == card.get_id()\
                and action["actionType"] == action_type.value:
                return action["actionId"]
        return -1


    def get_strip_card_action_id(self, card: Card, payment_cards: List[int]) -> int:
        for action in self.actions:
            if "card" in action and "cardId" in action["card"] and action["card"]["cardId"] == card.get_id()\
                and action["actionType"] == Action.BUY.value:
                correct_action = True
                if "cardPayment" in action:
                    for payment_card in action["cardPayment"]:
                        if payment_card["cardId"] in payment_cards:
                            pass
                        else:
                            correct_action = False
                
                if correct_action:
                    return action["actionId"]
        return -1
    
    def get_reserve_noble_action_id(self, noble : Noble) -> int:
        """ find which action id is for reserving a noble"""
        print("Getting reserve noble action id for noble: " + str(noble.get_id()))
        for action in self.actions:
            if "card" in action and "cardId" in action["card"] and action["card"]["cardId"] == noble.get_id()\
                and action["actionType"] == Action.TAKE_NOBLE.value:
                print("Found reserve noble action id: " + str(action["actionId"]))
                return action["actionId"]
        print("Action not found")
        return -1
    
    def get_buy_reserved_card_action_id(self, card: Card) -> int:
        """ find which action id is for buying a reserved card"""
        print("Getting buy reserved card action id for card: " + str(card.get_id()))
        for action in self.actions:
            if "card" in action and "cardId" in action["card"] and action["card"]["cardId"] == card.get_id()\
                and action["actionType"] == Action.BUY_RESERVED_CARD.value:
                print("Found buy reserved card action id: " + str(action["actionId"]))
                return action["actionId"]
        print("Action not found")
        return -1

    def get_discard_action_id(self, card: Card) -> int:
        """ find which action id is for discarding a card"""
        print("Getting discard action id for card: " + str(card.get_id()))
        for action in self.actions:
            if "card" in action and "cardId" in action["card"] and action["card"]["cardId"] == card.get_id()\
                and action["actionType"] == Action.DISCARD.value:
                print("Found discard action id: " + str(action["actionId"]))
                return action["actionId"]
        print("Action not found")
        return -1
    
    def get_clone_action_id(self, card: Card) -> int:
        """ find which action id is for cloning a card"""
        print("Getting clone action id for card: " + str(card.get_id()))
        for action in self.actions:
            if "card" in action and "cardId" in action["card"] and action["card"]["cardId"] == card.get_id()\
                and action["actionType"] == Action.CLONE_CARD.value:
                print("Found clone action id: " + str(action["actionId"]))
                return action["actionId"]
        print("Action not found")
        return -1

    def get_cascade_action_id(self, card: Card) -> int:
        """
         we need to find the action id with the given card, and the action type TAKE_CARD_1 or TAKE_CARD_2
         
        """
        print("Getting cascade action id for card: " + str(card.get_id()))
        for action in self.actions:
            if "card" in action and "cardId" in action["card"] and action["card"]["cardId"] == card.get_id()\
                and (action["actionType"] == Action.TAKE_CARD_1.value or action["actionType"] == Action.TAKE_CARD_2.value):
                print("Found cascade action id: " + str(action["actionId"]))
                return action["actionId"]
        print("Action not found")
        return -1

    def has_unlocked_reserve_noble(self, player_name: str) -> bool:
        print("Checking reserve")
        if player_name != self.last_updated_player:
            # Not player's turn
            return False
        for action in self.actions:
            if "actionType" in action and action["actionType"] == Action.TAKE_NOBLE.value:
                print("has unlocked reserve")
                return True
        return False
    
    def has_unlocked_discard(self, player_name: str) -> bool:
        print("Checking discard")
        if player_name != self.last_updated_player:
            # Not player's turn
            return False
        for action in self.actions:
            if "actionType" in action and action["actionType"] == Action.DISCARD.value:
                print("has unlocked discard")
                return True
        return False
    
    def has_unlocked_clone(self, player_name: str) -> bool:
        print("Checking clone")
        if player_name != self.last_updated_player:
            # Not player's turn
            return False
        for action in self.actions:
            if "actionType" in action and action["actionType"] == Action.CLONE_CARD.value:
                print("has unlocked clone")
                return True
        return False
    
    def has_unlocked_return_token(self, player_name: str) -> bool:
        print("Checking return token")
        if player_name != self.last_updated_player:
            # Not player's turn
            return False
        for action in self.actions:
            if "actionType" in action and action["actionType"] == Action.RETURN_TOKENS.value:
                print("has unlocked return tokens")
                return True
        return False

    def has_unlocked_cascade(self, player_name: str) -> bool:
        print("Checking cascade")
        if player_name != self.last_updated_player:
            # Not player's turn
            return False
        for action in self.actions:
            if "actionType" in action and action["actionType"] == Action.TAKE_CARD_1.value or action["actionType"] == Action.TAKE_CARD_2.value:
                print("has unlocked cascade")
                return True
        return False

    # Given a Dict[Token,int], return the action id for taking tokens / returning tokens
    def get_token_action_id(self, token_selection: Dict[Token,int], player_name: str, action_type: Action) -> int:
        
        if action_type == Action.CANCEL:
            return 0
        if player_name != self.last_updated_player:
            # Not player's turn
            return -1
        
        # Craft a new Dict[str,int] without non-zeroes
        color_key_dict = {}
        for token in token_selection:
            if token_selection[token] != 0:
                color_key_dict[token.get_color().name] = token_selection[token]

        # Find the action in the action list
        for action in self.actions:
            if "tokens" in action and action["tokens"] == color_key_dict\
                and action["actionType"] == action_type.value:
                return action["actionId"]
            if "token" in action and action["token"] == color_key_dict\
                and action["actionType"] == Action.TAKE_ONE_TOKEN.value:
                return action["actionId"]
        return -2
    
    # get a list of all unique actions
    def get_unique_actions(self):
        action_names_list = []
        for action in self.actions:
            if "actionType" in action and (not (action["actionType"] in action_names_list)):
                action_names_list.append(action["actionType"])
        return action_names_list
    
    def get_actions_json(self):
        print(self.actions)

    def perform_action(self, action_id: int):
        server_manager.perform_action(self.authenticator, self.game_id, self.last_updated_player,
                                      action_id)


