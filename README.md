# WebCheckers

WebSocket game server API message cycle:
1. Creating instance of Message class
	a) contains enum code from MsgCode class,
	b) if needed contains arguments, etc. added/removed checkers as X Y coords.
2. Serializing to string with custom serialize implementation.
---transfer---
3. Recreating (deserializing) received string to Message instance.
	
