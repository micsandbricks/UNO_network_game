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
	public void setUno(boolean b){
		hasUno = b;
	}

	public boolean sayUno() {
		hasUno = true;
		int value = hand.get(0).getValue();
		for(Card c : hand){
			if(c.getColour() == 's'){
				hasUno = false;
				break;
			}
			if( c.getValue() != value){
				hasUno = false;
				break;
			}
		}
		return hasUno;
	}

	public boolean hasUno() {
		return hasUno;
	}

}
