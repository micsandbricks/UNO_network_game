package userside;

import java.util.LinkedList;
import java.util.Random;

public class Deck {
	LinkedList<Card> cards;
	LinkedList<Card> played;

	public Deck() {
		cards = new LinkedList<Card>();
		played = new LinkedList<Card>();
		
		LinkedList<Card> sortedCards = new LinkedList<Card>();
		
		//Skapar kort 0-12, två av varje färg		
		for (int i = 0; i <= 12; i++) {
			sortedCards.add(new Card('r', i));
			sortedCards.add(new Card('r', i));
			sortedCards.add(new Card('g', i));
			sortedCards.add(new Card('g', i));
			sortedCards.add(new Card('b', i));
			sortedCards.add(new Card('b', i));
			sortedCards.add(new Card('y', i));
			sortedCards.add(new Card('y', i));
		}
		
		//Skapar kort 13 och 14, 4 stycken
		for (int i = 0; i < 4; i++) {
			sortedCards.add(new Card('s', 13));
			sortedCards.add(new Card('s', 14));
		}
		
		int index = 0;
		Random rand = new Random();
		while (!sortedCards.isEmpty()) {
			index = rand.nextInt(sortedCards.size());
			cards.add(sortedCards.remove(index));
		}
	}

	//Blandar de slängda korten och lägger in sist i kortleken igen
	public void reShuffle() {
		int index = 0;
		Random rand = new Random();
		Card temp = played.getLast();
		while (!played.isEmpty()) {
			index = rand.nextInt(played.size() + 1);
			cards.add(played.remove(index));
		}
		played.add(temp);
	}

	//Drar det översta kortet
	public Card draw() {
		if (cards.size() != 0) {
			return cards.poll();
		} else {
			this.reShuffle();
			return cards.poll();
		}
	}
	
	//visar det senast spelade kortet
	public Card getLastPlayed() {
		return played.getLast();
	}
	
	//Lägger ett kort i högen med spelade kort
	public void play(Card card) {
		played.add(card);
	}
	
	//Testar om det finns kort kvar i kortleken
	public boolean isEmpty() {
		return cards.isEmpty();
	}
	
	//Testar om det finns kort i slänghögen	
	public boolean playedIsEmpty() {
		return played.isEmpty();
	}

}
