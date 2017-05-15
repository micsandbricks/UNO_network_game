package userside;

import java.net.Socket;
import java.util.LinkedList;

public class PlayerOutputMonitor {

	private PlayerClientWriter pcw;
	private LinkedList<String> out_mailbox = new LinkedList<String>();

	public PlayerOutputMonitor(Socket playerSocket) {
		pcw = new PlayerClientWriter(this, playerSocket);
		pcw.start();
	}

	// Adds messages to the out_mailbox
	public synchronized void addToMailbox(String message) {
		out_mailbox.add(message);
		notifyAll();
	}

	/*
	 * fetchMessage tries to fetch a message from the mailbox. An empty string
	 * is returned if nothing was found.
	 */
	public synchronized String fetchMessage() {
		String message = "";
		while (this.out_mailbox.isEmpty())
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		message = this.out_mailbox.remove();
		return message;

	}
}
