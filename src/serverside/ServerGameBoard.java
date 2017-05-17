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
	char chosenColour;
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
	
	public void setupGame() {
		deck = new Deck();
		Card card;
		LinkedList<StringBuilder> sb = new LinkedList<StringBuilder>();
		for (int i = 0; i < users.size(); i++) {
			sb.add(new StringBuilder());
			sb.get(i).append("D" + (i+1) + " ");			
		}
		// Delar ut 7 kort till spelarna
		for (int i = 0; i < 7 ; i++) {
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
	
	private void sendToGameState(String message){
		gamestate.addtoInfo(message);
	}
	//Kontrollera att korten får läggas
	public boolean checkCards(LinkedList<Card> cards) {
		if (cards.isEmpty()) {
			return false;
		}
		//Alla siffror är samma
		for (int i = 0; i < cards.size()-1; i++) {
			if (cards.get(i).getValue() != cards.get(i+1).getValue()) {
				return false;
			}
		}		
		
		//Första kortet är svart -> Kan alltid läggas
		if (cards.get(0).getColour() == 's') {
			return true;
		}

		//Svart kort är lagt -> Endast vald färg får läggas.
		if (deck.getLastPlayed().getColour() == 's') {
			char temp = cards.get(0).getColour();
			if (cards.get(0).getColour() == chosenColour) {
				return true;				
			} else {
				return false;
			}
		}

		//Första siffran eller första färgen är fel (men inte svart, redan tittat på ovan)
		if (cards.get(0).getColour() != deck.getLastPlayed().getColour() && cards.get(0).getValue() != deck.getLastPlayed().getValue()) {
			return false;
		}

		return true;		
	}
	
	public int nextPlayer(int i) {
		int temp = playerTurn;

		for (int j = 0; j < i; j++ ) {
			if (clockwise) {
				temp++;
				if (temp >= users.size()) {
					temp = 0;
				}
			} else {
				temp--;
				if (temp < 0) {
					temp = users.size()-1;
				}
			}
		}
		
		return temp;

	}

}
