# WebCheckers

Communication API with game server:
- REST create game by pl1 (with name/pass) returns new game table with 1 player and game id.
- WS message with game id by pl1 registers player for server messages.
- REST join game by pl2 (also name/pass) returns same field with same id, adding pl2 to it.
- WS message with game id by pl2 registers player for server messages.
- WS message from server calls players for game and selects active player.
- Active player sends by REST clicked field.
- Server validates move (if first click could be made and is valid), if yes it's remembered, return accept or not.
- Active player sends next click (if first was invalid, it's first).
- Validate if player can move to this field, returns next or finished state
- If finished, checks if game completed, if yes message players that finished, if no message that next move.
- After message players REST last move and put it in local game field.
- Message also contains info about new active player.

Domain classes:
Class Game: contains board, players and meta data used i.e. for recognizing draw.
Class Board: contains 2d array of checkers.
Class Checker: contains player, position and promoted flag.
Class Player: contains active flag, color and WS session.