package userside;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.stage.Stage;

public class GameClient {

	public static void main(String args[]) {

		// Sets up the b
		try {
			Application.launch(UnoGUI.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}