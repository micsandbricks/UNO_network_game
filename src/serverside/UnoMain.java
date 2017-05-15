package serverside;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import serverside.MailboxMonitor;
import serverside.UNOserver;

public class UnoMain {

	private static int PORT = 30000;
	private static int numberPlayers = 0;

	public static void main(String[] args) {

		boolean runGame = false;
		
		UNOserver us = null;
		MailboxMonitor mailMonitor = new MailboxMonitor();

		try {
			ServerSocket serversocket = new ServerSocket(PORT);
			us = new UNOserver(serversocket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Integer num = Integer.parseInt(
				JOptionPane.showInputDialog(null, "How many players do you want?", JOptionPane.PLAIN_MESSAGE));

		// The accept loop
		while (!runGame) {
			Socket playerSocket = null;
			try {
				playerSocket = us.accept();
				mailMonitor.addPlayer(playerSocket);
				numberPlayers++;
				if (numberPlayers == num)
					runGame =true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		mailMonitor.addToOutMailbox("A R");
		// The game loop
		while (runGame){
			
		}
	}
}
