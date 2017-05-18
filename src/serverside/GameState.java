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
			case ("P"):
				mm.addToOutMailbox("P" + message.substring(1,2) + "P ");
			break;
			case("J"):
				mm.addToOutMailbox("A " + message);
			break;
			case("K"):
				mm.addToOutMailbox("A " + message);
				break;
			case("W"):
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
			switch (message.substring(3,4)){
			case ("C"):
				playerInt = Integer.parseInt(message.substring(1,2));
				mm.addToOutMailbox("A C " + users.get(playerInt-1).getName() + ": " + message.substring(4));
			break;
			case ("U"):
				playerInt = Integer.parseInt(message.substring(1,2));
				mm.addToOutMailbox("A U" + users.get(playerInt-1).getName());
				serverGameBoard.setUno(playerInt-1,true);
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
				playerInt = Integer.parseInt(message.substring(1,2));
				System.out.println(message);
				String[] stringCards = message.substring(5).split(" ");
				LinkedList<Card> cards = new LinkedList<Card>();
				for(int i = 0; i < stringCards.length; i++){
					cards.add(new Card(stringCards[i]));
					System.out.println(cards.get(i).toString());
				}
				if (serverGameBoard.checkCards(cards)) {
					//Spela korten
					
					serverGameBoard.playCards(cards);
				} else {
					//Får inte spela dessa kort
					mm.addToOutMailbox("P" + playerInt + "PF");
				}
				
				System.out.println("check cards = " + serverGameBoard.checkCards(cards));
				break;
			case ("G"):
				Card temp = serverGameBoard.getDeck().draw();
				String card = temp.toString();
				serverGameBoard.users.get(serverGameBoard.playerTurn).addCard(temp);
				mm.addToOutMailbox(message.substring(0,2) + "D " + card);
				serverGameBoard.setPlayerTurn(serverGameBoard.nextPlayer(1));
				mm.addToOutMailbox("A T " + serverGameBoard.getCurrentUser().getName());
				break;
			case("F"):
				serverGameBoard.setColor(message.substring(5));
			break;
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
