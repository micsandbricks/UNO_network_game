package userside;

public class Card {
	char colour;
	int value;

	public Card(char colour, int value) {
		this.colour = colour;
		this.value = value;			
	}

	public char getColour() {
		return colour;
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		if (value < 10) {
			return this.colour + '0' + Integer.toString(value);
		}
		return this.colour + Integer.toString(value);
	}

	public String getImgLink() {
		if (value < 10) {
			return "/pictures/" + this.colour + '0' + Integer.toString(value) + ".png";
		}
		return "/pictures/" + this.colour + Integer.toString(value) + ".png";
	}
}
