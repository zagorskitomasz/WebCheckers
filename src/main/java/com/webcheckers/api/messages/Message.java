package com.webcheckers.api.messages;

import java.io.Serializable;
import java.security.InvalidParameterException;

import com.webcheckers.api.domain.enums.MsgCode;
import com.webcheckers.api.service.GameID;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	public final MsgCode CODE;
	public final GameID gameID;
	public final String[] ARGS;

	public Message(MsgCode code, GameID gameID, String... args) {
		this.CODE = code;
		this.gameID = gameID;
		this.ARGS = args;
	}

	public Message(MsgCode code) {
		this.CODE = code;
		this.gameID = null;
		this.ARGS = new String[0];
	}

	public String serialize() {

		StringBuilder stringBuilder = new StringBuilder();

		appendGameID(stringBuilder);
		appendCode(stringBuilder);
		appendArgs(stringBuilder);

		return stringBuilder.toString().trim();
	}

	private void appendGameID(StringBuilder stringBuilder) {

		if (gameID == null)
			stringBuilder.append("00 00 ");
		else
			stringBuilder.append(gameID.NAME + " " + gameID.PASSWORD + " ");
	}

	private void appendCode(StringBuilder stringBuilder) {

		stringBuilder.append(CODE.getCode() + " ");
	}

	private void appendArgs(StringBuilder stringBuilder) {

		if (ARGS != null)
			for (int i = 0; i < ARGS.length; i++)
				stringBuilder.append(ARGS[i] + " ");
	}

	public static Message deserialize(String stringMessage) {

		try {
			String[] splittedStrings = stringMessage.split(" ");
			
			if(splittedStrings.length < 3)
				return null;

			GameID gameID = readID(splittedStrings);
			MsgCode code = readCode(splittedStrings);
			String[] args = readArgs(splittedStrings);

			Message message = new Message(code, gameID, args);

			return message;
		} catch (Exception ex) {
			throw new InvalidParameterException("Deserialization failed");
		}
	}

	private static GameID readID(String[] splittedStrings) {

		GameID gameID;

		if ("00".equals(splittedStrings[0]))
			gameID = null;
		else
			gameID = new GameID(splittedStrings[0], splittedStrings[1]);

		return gameID;
	}

	private static MsgCode readCode(String[] splittedStrings) {

		MsgCode code = MsgCode.getByCode(splittedStrings[2]);

		return code;
	}

	private static String[] readArgs(String[] splittedStrings) {

		String[] args = new String[splittedStrings.length - 3];

		for (int i = 3; i < splittedStrings.length; i++)
			args[i - 3] = splittedStrings[i];

		return args;
	}
}
