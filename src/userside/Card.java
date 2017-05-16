package userside;

public class Card {
	char colour;
	int value;

	public Card(char colour, int value) {
		this.colour = colour;
		this.value = value;
	}
	
	public Card(String str){
		this.colour = str.charAt(0);
		this.value = Integer.parseInt(str.substring(1, 3));
		
	}

	public char getColour() {
		return colour;
	}

	public int getValue() {
		return value;
	}
 
	public String toString() {
		if (value < 10) {
			return String.valueOf(this.colour) + String.valueOf('0' + Integer.toString(value));
		}
		return String.valueOf(this.colour) + String.valueOf(Integer.toString(value));
	}

	public String getImgLink() {
		if (value < 10) {
			return "/pictures/" + this.colour + '0' + Integer.toString(value) + ".png";
		}
		return "/pictures/" + this.colour + Integer.toString(value) + ".png";
	}
}
