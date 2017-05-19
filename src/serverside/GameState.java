package serverside;

import java.util.LinkedList;

import userside.Card;
import userside.User;

public class GameState extends Thread {

	private MailboxMonitor mm;
	private LinkedList<User> users = new LinkedList<User>();
	private LinkedList<String> info = new LinkedList<String>();
	private ServerGameBoard serverGameBoard;
	private boolean running = false;

	public GameState(MailboxMonitor mm) {
		this.mm = mm;
	}

	/* Uppdaterar ändringar i spelet. */
	@Override
	public void run() {
		while (!interrupted()) {
			getMessageAndCompute();
			getGameBoardInfo();
		}
	}

	/* Receive message from ServerGameBoard and adds to info-list. */
	public void addtoInfo(String message) {
		info.add(message);
	}

	/* Parse the message in the info-list. */
	private void getGameBoardInfo() {
		if (!info.isEmpty()) {
			String message = info.remove();
			System.out.println(message);
			switch (message.substring(0, 1)) {
			/* Notify all players about playerturn. */
			case ("T"):
				mm.addToOutMailbox("A " + message);
				break;
			/* Updates the last played card for all players. */
			case ("L"):
				mm.addToOutMailbox("A " + message);
				break;
			/* Send card to a specified player. */
			case ("D"):
				mm.addToOutMailbox("P" + message.substring(1, 2) + "D " + message.substring(3));
				break;
			/* Sends to a player that the cards can be played. */
			case ("P"):
				mm.addToOutMailbox("P" + message.substring(1, 2) + "P ");
				break;
			/* Show the color textfield to all players. */
			case ("J"):
				mm.addToOutMailbox("A " + message);
				break;
			/* Hide the color textfield to all players. */
			case ("K"):
				mm.addToOutMailbox("A " + message);
				break;
			/* Notify all players about the winner. */
			case ("W"):
				mm.addToOutMailbox("A " + message);
				break;
			default:
				break;
			}
		}
	}

	private void getMessageAndCompute() {
		String message = mm.fetchMessageFromInMailbox();
		if (message != "") {
			int playerInt;
			switch (message.substring(3, 4)) {
			/* Send chatmessage to all players. */
			case ("C"):
				playerInt = Integer.parseInt(message.substring(1, 2));
				mm.addToOutMailbox("A C " + users.get(playerInt - 1).getName() + ": " + message.substring(4));
				break;
			/* Notify all players that a player has UNO. */
			case ("U"):
				playerInt = Integer.parseInt(message.substring(1, 2));
				mm.addToOutMailbox("A U" + users.get(playerInt - 1).getName());
				serverGameBoard.setUno(playerInt - 1, true);
				break;
			/* Add a new player. */
			case ("N"):
				users.add(new User(message.substring(5)));
				break;
			/* Setup the game. */
			case ("S"):
				int numberPlayers = Integer.parseInt(message.substring(4, 5));
				if (users.size() == numberPlayers) {
					this.serverGameBoard = new ServerGameBoard(users, this);
					serverGameBoard.setupGame();
					running = true;
				}
				break;
			/* When a player wants to play cards, and send them if it ok. */
			case ("P"):
				playerInt = Integer.parseInt(message.substring(1, 2));
				System.out.println(message);
				String[] stringCards = message.substring(5).split(" ");
				LinkedList<Card> cards = new LinkedList<Card>();
				for (int i = 0; i < stringCards.length; i++) {
					cards.add(new Card(stringCards[i]));
				}
				if (serverGameBoard.checkCards(cards)) {
					// Play the cards
					serverGameBoard.playCards(cards);
				} else {
					// Notify that the cards invalid
					mm.addToOutMailbox("P" + playerInt + "PF");
				}

				break;
			/*
			 * If a +2 or +4 card is played. Sends cards to the next player and
			 * switches playerturn.
			 */
			case ("G"):
				Card temp = serverGameBoard.getDeck().draw();
				String card = temp.toString();
				serverGameBoard.users.get(serverGameBoard.playerTurn).addCard(temp);
				mm.addToOutMailbox(message.substring(0, 2) + "D " + card);
				serverGameBoard.setPlayerTurn(serverGameBoard.nextPlayer(1));
				mm.addToOutMailbox("A T " + serverGameBoard.getCurrentUser().getName());
				break;
			/* Change the chosen color. */
			case ("F"):
				serverGameBoard.setColor(message.substring(5));
				break;
			default:
				break;
			}
		}
	}

	public boolean running() {
		return running;
	}

	public boolean allPlayersAdded(int requestedNumberPlayers) {
		return users.size() == requestedNumberPlayers;
	}
}
