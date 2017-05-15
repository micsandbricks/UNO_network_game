package serverside;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import serverside.MailboxMonitor;
import serverside.UNOserver;

public class UnoMain {
	
	private static int PORT = 30000;
	
	public static void main(String[] args){
		
		UNOserver us = null;
		MailboxMonitor mailMonitor = new MailboxMonitor();
		
		try {
			ServerSocket serversocket = new ServerSocket(PORT);
			us = new UNOserver(serversocket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// The accept loop
		while(true){
			try {
				Socket playerSocket = us.accept();
				mailMonitor.addPlayer(playerSocket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
