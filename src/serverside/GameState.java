package serverside;

import java.util.LinkedList;

import userside.User;

public class GameState extends Thread {

	private MailboxMonitor mm;
	private LinkedList<User> users = new LinkedList<User>();

	public GameState(MailboxMonitor mm) {
		this.mm = mm;
	}

	@Override
	public void run() {
		while (!interrupted()) {
			getMessageAndCompute();
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
				users.add(new User(message.substring(2)));
				System.out.println((users.toString()));
				break;
			default:
				break;
			}
		}
	}
}
