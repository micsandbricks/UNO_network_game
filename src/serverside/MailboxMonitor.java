package serverside;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

/*
 * This monitor handles the PlayerReader and GameState threads.
 * It handles all operations that handle input from players.
 * PlayerReader reads from the players' input streams and puts messages in
 * a FIFO-queue called in_mailbox.
 * GameState reads messages from the in_mailbox and uses them to handle changes
 * to the game game.
 */
public class MailboxMonitor {
	
	private static int numberPlayers = 0;

	// in_mailbox works as a FIFO queue, being the drop-off point for message
	// from PlayerReader Thread
	private LinkedList<String> in_mailbox = new LinkedList<String>();
	// out_mailboxes is a list containing the out_mailbox of each of the PlayerWriters
	private LinkedList<LinkedList<String>> out_mailboxes = new LinkedList<LinkedList<String>>();
	// Reads messages from the in_mailbox queue:
	private GameState gs;

	public MailboxMonitor() {
		this.gs = new GameState(this);
		gs.start();
	}

	// This method adds players to the PlayerReader-object.
	public synchronized void addPlayer(Socket playerSocket) throws IOException {
		(new PlayerReader(this, playerSocket, numberPlayers + 1)).start();
		(new PlayerWriter(this, playerSocket, numberPlayers + 1)).start();
		out_mailboxes.add(new LinkedList<String>());
		numberPlayers++;
	}

	/*
	 * fetchMessage tries to fetch a message from the in_mailbox. An empty
	 * string is returned if nothing was found.
	 */
	public synchronized String fetchMessageFromInMailbox() {
		String message = "";
		if (in_mailbox.isEmpty()) {
			return message;
		}
		message = this.in_mailbox.remove();
		notifyAll();
		return message;
	}

	/*
	 * fetchMessage tries to fetch a message from the out_mailbox. An empty
	 * string is returned if nothing was found.
	 */
	public synchronized String fetchMessageFromOutMailbox(int playerInt) {
		String message = "";
		LinkedList<String> out_mailbox = this.out_mailboxes.get(playerInt - 1);
		while (out_mailbox.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		message = out_mailbox.remove();
		notifyAll();
		return message;
	}

	/*
	 * addToin_mailbox takes the argument string and attempts to add it to the
	 * in_mailbox.
	 */
	public synchronized void addToInMailbox(String message) {
		this.in_mailbox.add(message);
		notifyAll();
	}

	/*
	 * add_to_out_mailbox takes the argument string and attempts to add it to
	 * the in_mailbox.
	 */
	public synchronized void addToOutMailbox(String message) {
		switch (message.substring(0,1)){
		case ("A"):
			for (LinkedList<String> out_mailbox : out_mailboxes){
				out_mailbox.add(message.substring(2));
			}
			break;
		case ("P"):
			int playerInt = Integer.parseInt(message.substring(1,2));
			out_mailboxes.get(playerInt-1).add(message.substring(2));
		default:
			break;
		}
		notifyAll();
	}

	public boolean gameStateRunning() {
		return gs.running();
	}

	public boolean allPlayersAdded(int requestedNumberPlayers) {
		return gs.allPlayersAdded(requestedNumberPlayers);
	}
}
