package userside;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import userside.PlayerInputMonitor;
import userside.PlayerOutputMonitor;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class UnoGUI extends Application {

	private LinkedList<User> users = new LinkedList<User>();
	private User user;
	private Button unob;
	private TextArea ta;
	private TextField tf;
	private PlayerOutputMonitor pom;
	private PlayerInputMonitor pim;
	private boolean isLast = false;

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Ask for the user name
		String name = (String) JOptionPane.showInputDialog(null, "What is your name?", JOptionPane.PLAIN_MESSAGE);

		Socket playerSocket = null;
		// Try connecting to the server
		try {
			playerSocket = new Socket("localhost", 30000);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Lägg till själva användaren
		users.add(new User(name));

		GameBoard gb = new GameBoard(users);
		User user = gb.getUser(0);
		gb.setupGame();
		// Sets up the monitors that govern writing to and reading from the
		// server
		this.pom = new PlayerOutputMonitor(playerSocket);
		this.pim = new PlayerInputMonitor(playerSocket, this);

		pom.addToMailbox("N " + name);

		// String currentPlayer = gb.getActiveUser().getName(); //Kan endast
		// spela om deta är "rätt"

		primaryStage.setTitle("UNO game");

		Pane pane = new Pane();

		Scene scene = new Scene(pane, 1400, 800);
		scene.getStylesheets().add(UnoGUI.class.getResource("GUIStyle.css").toExternalForm());

		ScrollPane scroll = new ScrollPane();
		scroll.setLayoutX(40);
		scroll.setLayoutY(500);
		scroll.setPrefSize(1130, 300);

		FlowPane flow = new FlowPane();
		flow.setVgap(6);
		flow.setHgap(2);
		flow.setPrefWrapLength(1110); // preferred width = 300
		scroll.setContent(flow);

		FlowPane colourFlow = new FlowPane();
		colourFlow.setLayoutX(1000);
		colourFlow.setLayoutY(400);
		colourFlow.setVgap(4);
		colourFlow.setHgap(4);
		colourFlow.setPrefWrapLength(110); // preferred width = 300
		// Create a UNO-button

		/* Skapa objekt (knappar,bildf�nster och textrutor) */
		this.unob = new Button();

		Image unopic = new Image(getClass().getResource("/pictures/unob.png").toExternalForm(), 179, 97, true, true);
		ImageView unoimv = new ImageView();
		unoimv.setImage(unopic);
		unob.getStylesheets().add(UnoGUI.class.getResource("B_Style.css").toExternalForm());
		unob.setLayoutX(1175);
		unob.setLayoutY(625);
		unob.setId("uno");
		unob.setGraphic(unoimv);

		Button drawb = new Button();

		final Image imageD = new Image(getClass().getResource("/pictures/uno.png").toExternalForm(), 180, 270, true,
				true);
		ImageView imvD = new ImageView();
		imvD.setImage(imageD);
		drawb.getStylesheets().add(UnoGUI.class.getResource("B_Style.css").toExternalForm());
		drawb.setLayoutX(800);
		drawb.setLayoutY(200);
		drawb.setId("draw");
		drawb.setGraphic(imvD);

		Button playb = new Button();

		Image imageP = new Image(getClass().getResource(gb.getDeck().getLastPlayed().getImgLink()).toExternalForm(),
				180, 270, true, true);
		ImageView imvP = new ImageView();
		imvP.setImage(imageP);
		playb.getStylesheets().add(UnoGUI.class.getResource("B_Style.css").toExternalForm());
		playb.setLayoutX(600);
		playb.setLayoutY(200);
		playb.setId("play");
		playb.setGraphic(imvP);

		// Create a text-area
		this.ta = new TextArea();

		this.ta.setPrefSize(250, 300);
		this.ta.setLayoutX(30);
		this.ta.setLayoutY(30);
		this.ta.setWrapText(true);
		this.ta.setEditable(false);

		this.tf = new TextField("");

		this.tf.setLayoutX(30);
		this.tf.setLayoutY(340);
		this.tf.setPrefSize(250, 25);

		// Skapa fyra knappar för val av färg
		Button[] setColourButtons = new Button[4];
		for (int i = 0; i < setColourButtons.length; i++) {
			setColourButtons[i] = new Button();
			setColourButtons[i].getStylesheets().add(UnoGUI.class.getResource("B_Style.css").toExternalForm());
			setColourButtons[i].setPrefSize(50, 50);
			// setColourButtons[i].setVisible(false);
		}

		setColourButtons[0].setId("r");
		setColourButtons[1].setId("b");
		setColourButtons[2].setId("y");
		setColourButtons[3].setId("g");

		for (int i = 0; i < setColourButtons.length; i++) {
			colourFlow.getChildren().add(setColourButtons[i]);
		}
		colourFlow.setVisible(false);

		// För att visa korten på hand:
		LinkedList<ToggleButton> tb = new LinkedList<ToggleButton>(); // Denna
																		// lista
																		// ska
																		// vara
																		// så
																		// stor
																		// som
																		// handen
																		// har
																		// kort
		LinkedList<Card> cardsToPlay = new LinkedList<Card>();

		// Skapa en obslist som h�ller koll p� f�r�ndringar i listan
		ObservableList<ToggleButton> obsTb = FXCollections.observableList(tb);
		obsTb.addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Object> change) {
				int index;
				while (change.next()) {
					if (change.wasAdded()) {
						index = obsTb.size() - 1;
						final int i = index;
						System.out.println("added a card");
						// h�nder om ett kort blivit tillagt - setOnAction
						obsTb.get(index).setOnAction(a -> {
							if (tb.get(i).isSelected()) {
								cardsToPlay.add(user.getHand().get(i));
								ta.appendText(user.getHand().get(i).getColour()
										+ Integer.toString(user.getHand().get(i).getValue()) + " is selected \n");
								tb.get(i).setText(Integer.toString(cardsToPlay.size()));
							} else {
								cardsToPlay.remove(user.getHand().get(i));
								ta.appendText(user.getHand().get(i).toString() + " is deselected \n");
								tb.get(i).setText("");
								for (int k = 0; k < tb.size(); k++) {
									if (cardsToPlay.indexOf(user.getHand().get(k)) >= 0) {
										tb.get(k).setText(
												Integer.toString(cardsToPlay.indexOf(user.getHand().get(k)) + 1));
									}
								}

							}
						});
					} else {
						// händer annars
						System.out.println("card was removed?");
					}
				}
			}
		});

		// Skapar en ny
		for (int i = 0; i < user.getHand().size(); i++) {
			obsTb.add(new ToggleButton());
			obsTb.get(i).setGraphic(
					new ImageView(new Image(getClass().getResourceAsStream(user.getHand().get(i).getImgLink()))));
			obsTb.get(i).getStylesheets().add(UnoGUI.class.getResource("ToggleB_Hand.css").toExternalForm());
		}

		/* Event f�r objekt */

		// Skriver ut meddelandet fr�n textfield i textarea n�r Enter trycks
		// ner, inget h�nder om textfield �r tomt
		// tf.setOnKeyPressed(new EventHandler<KeyEvent>() {
		// @Override
		// public void handle(KeyEvent event) {
		// if (event.getCode() == KeyCode.ENTER && !tf.getText().equals("")) {
		// ta.appendText(user.getName() + ": " + tf.getText() + '\n');
		// tf.setText("");
		// }
		// }
		// });

		tf.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER && !tf.getText().equals("")) {
					String message = tf.getText();
					pom.addToMailbox("C " + message);
					tf.setText("");
				}
			}
		});

		// unob.setOnAction(new EventHandler<ActionEvent>() {
		// @Override
		// public void handle(ActionEvent e) {
		// if (user.sayUno()) {
		// // Meddela övriga spelare att den här spelare har uno
		// ta.appendText("You said UNO! \n");
		// } else {
		// ta.appendText("You do not have UNO! \n");
		// }
		//
		// }
		// });

		unob.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				pom.addToMailbox("U");
			}
		});

		drawb.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				user.addCard(gb.getDeck().draw());
				ta.appendText("draw new card \n");

				// Lägger till ny knapp när nytt kort dras
				obsTb.add(new ToggleButton());
				tb.getLast().setGraphic(new ImageView(
						new Image(getClass().getResourceAsStream(user.getHand().getLast().getImgLink()))));
				tb.getLast().getStylesheets().add(UnoGUI.class.getResource("ToggleB_Hand.css").toExternalForm());
				flow.getChildren().add(tb.getLast());

			}
		});

		for (int i = 0; i < setColourButtons.length; i++) {
			final int j = i;
			setColourButtons[j].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					String msg = "C:" + setColourButtons[j].getId() + '\n';
					gb.setColour(setColourButtons[j].getId().charAt(0));
					colourFlow.setVisible(false);
					ta.appendText(setColourButtons[j].getId());
				}
			});
		}

		playb.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				// kolla att cardsToPlay inte är tom
				if (gb.checkCards(cardsToPlay)) {
					// om första kortet är svart -> välj färg och skicka
					// till server
					if (cardsToPlay.get(0).getColour() == 's') {
						colourFlow.setVisible(true);
					}
					gb.playCards(cardsToPlay);
					ta.appendText("cards played \n");

					ta.appendText("Last played " + gb.getDeck().getLastPlayed().toString() + "\n");

					// Raderar de markerade korten - de lagda korten - fr�n
					// flow
					for (int i = 0; i < user.getHand().size(); i++) {
						if (tb.get(i).isSelected() == true) {
							flow.getChildren().remove(tb.get(i));
						}
					}

					// Sätt alla togglebuttons till icke-valda
					for (int i = 0; i < user.getHand().size(); i++) {
						tb.get(i).setSelected(false);
						tb.get(i).setText("");
					}

					// Byt översta kortet till det senast spelade
					Image imageP = new Image(
							getClass().getResource(gb.getDeck().getLastPlayed().getImgLink()).toExternalForm(), 180,
							270, true, true);
					ImageView imvP = new ImageView();
					imvP.setImage(imageP);
					playb.setGraphic(imvP);

				} else {
					ta.appendText("no cards chosen \n");
					for (int i = 0; i < user.getHand().size(); i++) {
						tb.get(i).setSelected(false);
						tb.get(i).setText("");
					}
				}

				while (!cardsToPlay.isEmpty()) {
					cardsToPlay.remove();
				}
			}
		});

		/* L�gger till children till parents */
		for (int i = 0; i < tb.size(); i++) {
			flow.getChildren().add(tb.get(i));
		}
		pane.getChildren().addAll(ta, tf, scroll, unob, drawb, playb, colourFlow);

		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
		if (this.isLast) 
			pom.addToMailbox("S");
	}

	private void addUser(String userName) {
		this.users.add(new User(userName));
	}

	public void update() {
		String message = pim.readFromMailbox();
		if (message.substring(0, 1) == "R")
			this.isLast = true;
		this.ta.appendText(message + '\n');
	}

}
