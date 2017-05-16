package userside;
import java.util.LinkedList;
import java.util.Random;

public class GameBoard {
	LinkedList<User> users;
	Deck deck;
	int playerTurn;
	boolean clockwise;
	char chosenColour;

	public GameBoard(LinkedList<User> users) {
		this.users = users;
		playerTurn = 0;
	}

	public void addUser(User user) {
		users.add(user);
	}

	public User getUser(int i) {
		return users.get(i);
	}

	public User getUser(String n) {
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getName().equals(n)) {
				return users.get(i);
			}
		}		
		return null;
	}

	public int getPlayerTurn() {
		return playerTurn;
	}

	public User getActiveUser() {
		return users.get(playerTurn);
	}

	public LinkedList<User> getAllUsers() {
		return users;
	}

	public Deck getDeck() {
		return deck;
	}
	
	//välj färg (skal ske om svart kort blivit lagt
	public void setColour(char c) {
		this.chosenColour = c;
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
		while (deck.getLastPlayed().value > 9) {
			deck.play(deck.draw());
		}

		clockwise = true;

		Random rand = new Random();
		playerTurn = rand.nextInt(users.size());
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

	public String playCards(LinkedList<Card> cards) {

		StringBuilder toSend = new StringBuilder();

		playerTurn = 0; //den här ska bort när vi är klara sen...

		if (checkCards(cards)) {
			if (cards.get(0).getValue() < 10) {
				//(korten läggs och) turen går vidare
				playerTurn = nextPlayer(1);

			} else if (cards.get(0).getValue() == 10) {
				//hoppa över spelare
				playerTurn = nextPlayer(cards.size()+1);

			} else if (cards.get(0).getValue() == 11) {
				//nästa drar n*2 kort				
				int next = nextPlayer(1);
				if (cards.size()*2 < 10) {
					toSend.append("D:" + next + "0" + cards.size()*2 + '\n');
				} else {
					toSend.append("D:" + next + cards.size()*2 + '\n');
				}
							
				playerTurn = nextPlayer(2);

			} else if (cards.get(0).getValue() == 12) {
				//byt håll
				if (cards.size() % 2 == 1) {
					clockwise = !clockwise;
				}
				playerTurn = nextPlayer(1);
			} else if (cards.get(0).getValue() == 13) {
				//byt färg
				playerTurn = nextPlayer(1);

			} else if (cards.get(0).getValue() == 14) {
				//nästa drar n*4 nya
				int next = nextPlayer(1);
				
				if (cards.size()*4 < 10) {
					toSend.append("D:" + next + "0" + cards.size()*2 + '\n');
				} else {
					toSend.append("D:" + next + cards.size()*2 + '\n');
				}
								
				playerTurn = nextPlayer(2);				
				//byt färg

			}

			//uppdatera lastPlayed och tar bort från spelarens hand

			Card c;

			while (!cards.isEmpty()) {
				c = cards.remove();
				System.out.println(playerTurn);
				users.get(playerTurn).removeCard(c);
				deck.play(c);
			}

			//Kollar om spelaren lägger sitt sista kort (och har sagt uno)
			if (users.get(playerTurn).getHand().isEmpty()) {
				if (users.get(playerTurn).hasUno()) {
					//Spelaren vinner! Yay :) Meddela servern
				} else {
					//Spelaren la sitt sista kort men har glömt att säga uno...
						toSend.append("D:" + playerTurn + "03\n");
						users.get(playerTurn).addCard(deck.draw());
				}

			}

		}
		
		toSend.append("P:" + playerTurn + deck.getLastPlayed().toString()+'\n');
		return toSend.toString();
	}

	private int nextPlayer(int i) { //Den här metoden verkar itet funka som den ska
		int temp = playerTurn;

		if (clockwise) {
			temp = temp + i;
			while (temp > users.size()) {
				temp = temp - users.size();
			}
		} else {
			temp = temp - i;
			while (temp < 0) {
				temp = users.size() + temp;
			}
		}
		return temp;

	}



}
