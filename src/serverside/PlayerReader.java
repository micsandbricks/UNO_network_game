package serverside;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class PlayerReader extends Thread {

	MailboxMonitor mailboxMonitor;
	BufferedReader buffRead;
	private int playerInt;

	public PlayerReader(MailboxMonitor mailboxMonitor, Socket playerSocket, int playerInt) {
		this.mailboxMonitor = mailboxMonitor;
		this.playerInt = playerInt;
		try {
			setUpPlayerReader(playerSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				readPlayer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void readPlayer() throws IOException {
		String message = "";
		message = buffRead.readLine();
		this.mailboxMonitor.addToInMailbox("P" + playerInt + " " + message);
	}

	public void setUpPlayerReader(Socket playerSocket) throws IOException {
		InputStream in = playerSocket.getInputStream();
		Reader r = new InputStreamReader(in);
		this.buffRead = new BufferedReader(r);
	}
}
