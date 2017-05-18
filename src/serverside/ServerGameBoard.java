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
	public void setUno(int i, boolean b){
		users.get(i).setUno(b);
	}
	public boolean hasUno(int i){
		return users.get(i).hasUno();
	}

	public void setupGame() {
		deck = new Deck();
		Card card;
		LinkedList<StringBuilder> sb = new LinkedList<StringBuilder>();
		for (int i = 0; i < users.size(); i++) {
			sb.add(new StringBuilder());
			sb.get(i).append("D" + (i + 1) + " ");
		}
		// Delar ut 7 kort till spelarna
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < users.size(); j++) {
				card = deck.draw();
				users.get(j).addCard(card);
				sb.get(j).append(card.toString() + " ");
			}
		}

		deck.play(deck.draw());
		while (deck.getLastPlayed().getValue() > 9) {
			deck.play(deck.draw());
		}

		clockwise = true;

		Random rand = new Random();
		playerTurn = rand.nextInt(users.size());
		System.out.println("user size: " + users.size());
		System.out.println("playerturn: " + playerTurn);
		sendToGameState("L " + deck.getLastPlayed().toString());
		System.out.println("In sgb, last played: " + deck.getLastPlayed().toString());
		sendToGameState("T " + users.get(playerTurn).getName());
		System.out.println("in ServerGameBoard, playerTurn: " + users.get(playerTurn).getName());
		for (int i = 0; i < sb.size(); i++) {
			sendToGameState(sb.get(i).toString());
			System.out.println("I ServerGameBoard, kort delas ut: " + sb.get(i).toString());
		}
	}

	private void sendToGameState(String message) {
		gamestate.addtoInfo(message);
	}

	// Kontrollera att korten får läggas
	public boolean checkCards(LinkedList<Card> cards) {
		if (cards.isEmpty()) {
			return false;
		}
		// Alla siffror är samma
		for (int i = 0; i < cards.size() - 1; i++) {
			if (cards.get(i).getValue() != cards.get(i + 1).getValue()) {
				return false;
			}
		}

		// Första kortet är svart -> Kan alltid läggas
		if (cards.get(0).getColour() == 's') {
			return true;
		}

		// Svart kort är lagt -> Endast vald färg får läggas.
		if (deck.getLastPlayed().getColour() == 's') {
			char temp = cards.get(0).getColour();
			if (cards.get(0).getColour() == chosenColor) {
				return true;
			} else {
				return false;
			}
		}

		// Första siffran eller första färgen är fel (men inte svart, redan
		// tittat på ovan)
		if (cards.get(0).getColour() != deck.getLastPlayed().getColour()
				&& cards.get(0).getValue() != deck.getLastPlayed().getValue()) {
			return false;
		}

		return true;
	}

	public void playCards(LinkedList<Card> cards) {
		int currentPlayer = playerTurn;

		if (cards.get(0).getValue() < 10) {
			// Gör färgtextruta osynlig
			sendToGameState("K");
			// (korten läggs och) turen går vidare
			playerTurn = nextPlayer(1);

		} else if (cards.get(0).getValue() == 10) { // hoppa över spelare
			sendToGameState("K");
			playerTurn = nextPlayer(cards.size() + 1);

		} else if (cards.get(0).getValue() == 11) { // nästa drar n*2 kort
			sendToGameState("K");
			int next = uglyNextPlayer(1);
			StringBuilder card = new StringBuilder();
			for (int i = 0; i < cards.size() * 2; i++) {
				card.append(deck.draw().toString() + " ");
			}
			sendToGameState("D" + (next + 1) + " " + card.toString());

			playerTurn = nextPlayer(2);

		} else if (cards.get(0).getValue() == 12) { // byt håll
			sendToGameState("K");
			if (cards.size() % 2 == 1) {
				clockwise = !clockwise;
			}
			playerTurn = nextPlayer(1);

		} else if (cards.get(0).getValue() == 13) {
			sendToGameState("J " + chosenColor);
			playerTurn = nextPlayer(1);
		
		} else if (cards.get(0).getValue() == 14) {
			// nästa drar n*4 nya int next = nextPlayer(1);
			sendToGameState("J " + chosenColor);
			int next = uglyNextPlayer(1);
			
			StringBuilder card = new StringBuilder();
			
			for (int i = 0; i < cards.size() * 4; i++) {
				card.append(deck.draw().toString() + " ");
			}
			sendToGameState("D" + (next + 1) + " " + card.toString());

			playerTurn = nextPlayer(2); 

		}

		// uppdatera lastPlayed och tar bort från spelarens hand
		Card c;
		while (!cards.isEmpty()) {
			c = cards.remove();
			System.out.println("The card wa removed: " + users.get(currentPlayer).removeCard(c));
			deck.play(c);
			System.out.println(users.get(currentPlayer).getName() + " " + users.get(currentPlayer).getHand().size());
		}

		// Kollar om spelaren lägger sitt sista kort (och har sagt uno)
		if (users.get(currentPlayer).getHand().isEmpty()) {
			if (users.get(currentPlayer).hasUno()) {
				// Spelaren vinner! Yay :) Meddela servern
				sendToGameState("W " + users.get(currentPlayer).getName());
				return;
			} else {
				// Spelaren la sitt sista kort men har glömt att säga uno...
				// Spelaren får 3 kort;
				StringBuilder card = new StringBuilder();
				for (int i = 0; i < 3; i++) {
					card.append(deck.draw().toString() + " ");
				}
				sendToGameState("D" + (currentPlayer+1) + " " + card.toString());
			}
		}

		sendToGameState("P" + (currentPlayer + 1));
		sendToGameState("L " + deck.getLastPlayed().toString());
		sendToGameState("T " + users.get(playerTurn).getName());

	}

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
