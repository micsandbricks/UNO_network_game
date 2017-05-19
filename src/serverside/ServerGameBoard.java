package serverside;

import java.util.LinkedList;
import java.util.Random;

import userside.Card;
import userside.Deck;
import userside.User;

public class ServerGameBoard {
	LinkedList<User> users;
	Deck deck;
	int playerTurn;
	boolean clockwise;
	char chosenColor;
	GameState gamestate;

	public ServerGameBoard(LinkedList<User> users, GameState gamestate) {
		this.users = users;
		this.gamestate = gamestate;
	}

	public Deck getDeck() {
		return deck;
	}

	public void setPlayerTurn(int player) {
		playerTurn = player;
	}

	public User getCurrentUser() {
		return users.get(playerTurn);
	}

	public void setColor(String color) {
		char c = color.charAt(0);
		chosenColor = c;
	}

	public void setUno(int i, boolean b) {
		users.get(i).setUno(b);
	}

	public boolean hasUno(int i) {
		return users.get(i).hasUno();
	}

	/* Setup the game and hand out 7 cards. */
	public void setupGame() {
		deck = new Deck();
		Card card;
		LinkedList<StringBuilder> sb = new LinkedList<StringBuilder>();
		for (int i = 0; i < users.size(); i++) {
			sb.add(new StringBuilder());
			sb.get(i).append("D" + (i + 1) + " ");
		}
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < users.size(); j++) {
				card = deck.draw();
				users.get(j).addCard(card);
				sb.get(j).append(card.toString() + " ");
			}
		}

		deck.play(deck.draw());
		/* Set a "start card", must be a number. */
		while (deck.getLastPlayed().getValue() > 9) {
			deck.play(deck.draw());
		}

		clockwise = true;
		/* Set a random player to start. */
		Random rand = new Random();
		playerTurn = rand.nextInt(users.size());
		sendToGameState("L " + deck.getLastPlayed().toString());
		sendToGameState("T " + users.get(playerTurn).getName());
		/* Send the cards to each player. */
		for (int i = 0; i < sb.size(); i++) {
			sendToGameState(sb.get(i).toString());
		}

	}

	/* Send the message to gamestate. */
	private void sendToGameState(String message) {
		gamestate.addtoInfo(message);
	}

	/* Check if the cards are valid. */
	public boolean checkCards(LinkedList<Card> cards) {
		if (cards.isEmpty()) {
			return false;
		}
		/* All numbers are equal. */
		for (int i = 0; i < cards.size() - 1; i++) {
			if (cards.get(i).getValue() != cards.get(i + 1).getValue()) {
				return false;
			}
		}

		/* First card is black -> always ok */
		if (cards.get(0).getColour() == 's') {
			return true;
		}

		/*
		 * Black card is the last played -> only the chosen color is ok to play.
		 */
		if (deck.getLastPlayed().getColour() == 's') {
			if (cards.get(0).getColour() == chosenColor) {
				return true;
			} else {
				return false;
			}
		}

		/*
		 * Last played value or color is invalid (but not a black card, it is
		 * already checked(above).
		 */
		if (cards.get(0).getColour() != deck.getLastPlayed().getColour()
				&& cards.get(0).getValue() != deck.getLastPlayed().getValue()) {
			return false;
		}

		return true;
	}

	/* Play the selected cards. */
	public void playCards(LinkedList<Card> cards) {
		int currentPlayer = playerTurn;
		Card temp;
		/* Cards is played and the turn is passed on. */
		if (cards.get(0).getValue() < 10) {
			/* Makes the color textfield invisible. */
			sendToGameState("K");
			playerTurn = nextPlayer(1);

		} else if (cards.get(0).getValue() == 10) { // skip a player
			sendToGameState("K");
			playerTurn = nextPlayer(cards.size() + 1);

		} else if (cards.get(0).getValue() == 11) { // next player draws n*2
													// cards and skip this
													// player
			sendToGameState("K");
			int next = uglyNextPlayer(1);
			StringBuilder card = new StringBuilder();
			for (int i = 0; i < cards.size() * 2; i++) {
				temp = deck.draw();
				card.append(temp.toString() + " ");
				users.get(next).addCard(temp);
			}
			sendToGameState("D" + (next + 1) + " " + card.toString());

			playerTurn = nextPlayer(2);

		} else if (cards.get(0).getValue() == 12) { // change direction
			sendToGameState("K");
			if (cards.size() % 2 == 1) {
				clockwise = !clockwise;
			}
			playerTurn = nextPlayer(1);

		} else if (cards.get(0).getValue() == 13) { // change color
			sendToGameState("J " + chosenColor);
			playerTurn = nextPlayer(1);

		} else if (cards.get(0).getValue() == 14) { // next player draws n*4
													// cards and skip this
													// player
			sendToGameState("J " + chosenColor);
			int next = uglyNextPlayer(1);
			StringBuilder card = new StringBuilder();
			for (int i = 0; i < cards.size() * 4; i++) {
				temp = deck.draw();
				card.append(temp.toString() + " ");
				users.get(next).addCard(temp);
			}
			sendToGameState("D" + (next + 1) + " " + card.toString());

			playerTurn = nextPlayer(2);
		}

		/* Update lastplayed and remove from the playerhand. */
		Card c;
		while (!cards.isEmpty()) {
			c = cards.remove();
			deck.play(c);
		}

		/* Check if the player plays the last card (and have said Uno). */
		if (users.get(currentPlayer).getHand().isEmpty()) {
			if (users.get(currentPlayer).hasUno()) {
				/* The currentplayer wins */
				sendToGameState("W " + users.get(currentPlayer).getName());
				return;
			} else {
				/*
				 * Current player played the last card, but forgot to say Uno..
				 * The player gets 3 cards.
				 */
				StringBuilder card = new StringBuilder();
				for (int i = 0; i < 3; i++) {
					card.append(deck.draw().toString() + " ");
				}
				sendToGameState("D" + (currentPlayer + 1) + " " + card.toString());
			}
		}
		/* Send player command, last played card, and the player turn. */
		sendToGameState("P" + (currentPlayer + 1));
		sendToGameState("L " + deck.getLastPlayed().toString());
		sendToGameState("T " + users.get(playerTurn).getName());

	}

	/* Returns the next player, "i" represent the number of players to skip. */
	public int nextPlayer(int i) {
		int temp = playerTurn;

		for (int j = 0; j < i; j++) {
			if (clockwise) {
				temp++;
				if (temp >= users.size()) {
					temp = 0;
				}
			} else {
				temp--;
				if (temp < 0) {
					temp = users.size() - 1;
				}
			}
		}

		return temp;

	}

	public int uglyNextPlayer(int i) {
		int temp = playerTurn;

		for (int j = 0; j < i; j++) {
			if (clockwise) {
				temp++;
				if (temp >= users.size()) {
					temp = 0;
				}
			} else {
				temp--;
				if (temp < 0) {
					temp = users.size() - 1;
				}
			}
		}

		return temp;

	}
}
