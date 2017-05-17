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

	@Override
	public void run() {
		while (!interrupted()) {
			getMessageAndCompute();
			getGameBoardInfo();
		}
	}
	public void addtoInfo(String message){
		info.add(message);
	}
	private void getGameBoardInfo(){
		if(!info.isEmpty()){
			String message = info.remove();
			System.out.println(message);
			switch(message.substring(0, 1)){
			case ("T"):
				mm.addToOutMailbox("A " + message);
				break;
			case ("L"):
				mm.addToOutMailbox("A " + message);
				break;
			case ("D"):
				System.out.println("in gameState: P"+ message.substring(1, 2) + " " + message.substring(3));
				mm.addToOutMailbox("P" + message.substring(1, 2) + "D " + message.substring(3));	
			break;
			default:
				break;
			}
		}
	}

	private void getMessageAndCompute() {
		String message = mm.fetchMessageFromInMailbox();
		if (message != "") {
			switch (message.substring(3,4)){
			case ("C"):
				int playerInt = Integer.parseInt(message.substring(1,2));
			mm.addToOutMailbox("A " + users.get(playerInt-1).getName() + ": " + message.substring(3));
			break;
			case ("U"):
				mm.addToOutMailbox(message);
			break;
			case ("N"):
				users.add(new User(message.substring(5)));
				System.out.println((users.toString()));
				break;
			case ("S"):
				int numberPlayers = Integer.parseInt(message.substring(4,5));
			if(users.size() == numberPlayers){
				this.serverGameBoard = new ServerGameBoard(users, this);
				serverGameBoard.setupGame();
				running = true;
			}
				break;
			case ("P"):
				System.out.println(message);
				String[] stringCards = message.substring(5).split(" ");
				LinkedList<Card> cards = new LinkedList<Card>();
				for(int i = 0; i < stringCards.length; i++){
					cards.add(new Card(stringCards[i]));
					System.out.println(cards.get(i).toString());
				}
				System.out.println("check cards = " + serverGameBoard.checkCards(cards));
				break;
			case ("G"):
				String card = serverGameBoard.getDeck().draw().toString();
				mm.addToOutMailbox(message.substring(0,2) + "D " + card);
			default:
				break;
			}
		}
	}
	
	public boolean running(){
		return running;
	}

	public boolean allPlayersAdded(int requestedNumberPlayers) {
		return users.size() == requestedNumberPlayers;
	}
}
