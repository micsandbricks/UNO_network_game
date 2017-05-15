package userside;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

public class PlayerClientReader extends Thread {

	private PlayerInputMonitor pim;
	private BufferedReader br;

	public PlayerClientReader(PlayerInputMonitor pim, Socket playerSocket) {
		this.pim = pim;
		try {
			addReader(playerSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				readFromServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void readFromServer() throws IOException {
		String message = "";
		// We use readLine, not sure if we want players to be able to send
		// multiple messages at once.
		message = br.readLine();
		if (message != "") {
			System.out.println("In PlayerClientReader, message: " + message);
			this.pim.addToMailbox(message);
		}
	}

	private void addReader(Socket playerSocket) throws IOException {
		InputStream in = playerSocket.getInputStream();
		Reader r = new InputStreamReader(in);
		this.br = new BufferedReader(r);
	}
}
