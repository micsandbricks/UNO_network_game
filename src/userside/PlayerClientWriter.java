package userside;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Hashtable;

public class PlayerClientWriter extends Thread {

	private PlayerOutputMonitor pom;
	private PrintWriter printWriter;

	public PlayerClientWriter(PlayerOutputMonitor pmm, Socket playerSocket) {
		this.pom = pmm;
		try {
			addWriter(playerSocket);
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

	 private void writeOutput() {
	 String message = pom.fetchMessage();
	 if (!message.isEmpty()) {
	   printWriter.println(message);
	   printWriter.flush();
	 } else {
		 System.out.println("dropped");
	 }
	
	 }

	private void addWriter(Socket playerSocket) throws IOException {
		OutputStream out = playerSocket.getOutputStream();
		Writer w = new OutputStreamWriter(out);
		BufferedWriter bw = new BufferedWriter(w);
		this.printWriter = new PrintWriter(bw);
		printWriter.flush();
	}
}
