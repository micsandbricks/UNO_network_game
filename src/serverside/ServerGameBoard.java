package serverside;

import java.util.LinkedList;
import java.util.Random;

import userside.Deck;
import userside.User;

public class ServerGameBoard {
	LinkedList<User> users;
	Deck deck;
	int playerTurn;
	boolean clockwise;
	char chosenColour;
	
	public ServerGameBoard(LinkedList<User> users) {
		this.users = users;
	}
	
	public void setupGame() {
		deck = new Deck();
		// Delar ut 7 kort till spelarna
		for (int i = 0; i < 7 ; i++) {
			for (int j = 0; j < users.size(); j++) {
				users.get(j).addCard(deck.draw());
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
	}

}
