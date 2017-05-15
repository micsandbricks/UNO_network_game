package serverside;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Hashtable;
import java.util.LinkedList;

public class PlayerWriter extends Thread {

	private MailboxMonitor mm;
	private PrintWriter printWrite;
	private int playerInt;

	public PlayerWriter(MailboxMonitor mailboxMonitor, Socket playerSocket, int playerInt) {
		this.mm = mailboxMonitor;
		this.playerInt = playerInt;
		try {
			setupPlayerWriter(playerSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!interrupted()) {
			writeOutput();
		}
	}

	/*
	 * This method writes output to the players
	 */
	public void writeOutput() {
		String message = mm.fetchMessageFromOutMailbox(this.playerInt);
		if (!message.isEmpty()) {
			System.out.println("In PlayerWriter, message: " + message);
			printWrite.println(message);
			printWrite.flush();
		}
	}
	
	public void setupPlayerWriter(Socket playerSocket) throws IOException {
		OutputStream out = playerSocket.getOutputStream();
		Writer w = new OutputStreamWriter(out);
		BufferedWriter bw = new BufferedWriter(w);
		this.printWrite = new PrintWriter(bw);
	}
}