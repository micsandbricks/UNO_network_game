package userside;
import java.util.LinkedList;

public class User {
	String name;
	LinkedList<Card> hand;
	boolean hasUno;

	public User(String name) {
		this.name = name;
		hand = new LinkedList<Card>();
		hasUno = false;
	}

	public String getName() {
		return name;
	}

	public LinkedList<Card> getHand() {
		return hand;
	}

	public void addCard(Card card) {
		hand.add(card);
		if (hasUno) {
			hasUno = !hasUno;
		}
	}

	public void removeCard(Card card) {
		hand.remove(card);
	}

	public boolean sayUno() {
		boolean temp = true;

		for (int i = 0; i < hand.size()-1; i++) {
			if (hand.get(i).getValue() != hand.get(i+1).getValue()) {
				temp = false;
				break;
			}
		}

		if (hand.size() == 1 || temp) {
			hasUno = true;
		}
		
		return hasUno;
	}
	
	public boolean hasUno() {
		return hasUno;
	}


}
