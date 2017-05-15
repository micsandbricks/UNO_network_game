package userside;

import java.net.Socket;
import java.util.LinkedList;

public class PlayerInputMonitor {

	private PlayerClientReader pcr;
	private UnoGUI unogui;
	private LinkedList<String> in_mailbox = new LinkedList<String>();

	public PlayerInputMonitor(Socket playerSocket, UnoGUI unogui) {
		this.unogui = unogui;
		this.pcr = new PlayerClientReader(this, playerSocket);
		this.pcr.start();
	}

	/*
	 * addToMailbox takes the argument string and attempts to add it to the
	 * mailbox.
	 */
	public synchronized void addToMailbox(String message) {
		System.out.println("In pim, message: " + message);
		this.in_mailbox.add(message);
		unogui.update();
	}

	/*
	 * readFromMailbox allows PlayerGUI to get messages from the mailbox
	 */
	public synchronized String readFromMailbox() {
		String message = this.in_mailbox.remove();
		return message;
	}

}
