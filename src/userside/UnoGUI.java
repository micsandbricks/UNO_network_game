package userside;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import userside.PlayerInputMonitor;
import userside.PlayerOutputMonitor;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class UnoGUI extends Application {

	private LinkedList<User> users = new LinkedList<User>();
	private Button unob;
	private TextArea ta;
	private TextField tf;
	private PlayerOutputMonitor pom;
	private PlayerInputMonitor pim;
	private Stage primaryStage;
	private boolean runGame = false;
	private Card lastPlayed = new Card('r', 00);
	private Button playb;
	private User user;
	private ObservableList<ToggleButton> obsTb;
	private LinkedList<ToggleButton> tb;
	private LinkedList<Card> cardsToPlay;
	private FlowPane flow;
	private String currentPlayer;
	private TextField tfp;
	private TextField tfc;
	private boolean newCard = false;
	private boolean click = false;

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.primaryStage = primaryStage;

		// Ask for the user name
		String name = (String) JOptionPane.showInputDialog(null, "What is your name?", JOptionPane.PLAIN_MESSAGE);

		// Adds the user
		this.user = (new User(name));

		users.add(new User(name));

		primaryStage.setTitle("UNO game");

		Pane pane = new Pane();

		Scene scene = new Scene(pane, 1400, 800);
		scene.getStylesheets().add(UnoGUI.class.getResource("GUIStyle.css").toExternalForm());

		ScrollPane scroll = new ScrollPane();
		scroll.setLayoutX(40);
		scroll.setLayoutY(500);
		scroll.setPrefSize(1200, 300);
		scroll.getStylesheets().add(UnoGUI.class.getResource("Scroll.css").toExternalForm());

		this.flow = new FlowPane();
		flow.setVgap(6);
		flow.setHgap(2);
		flow.setPrefWrapLength(1180);
		scroll.setContent(flow);

		FlowPane colourFlow = new FlowPane();
		colourFlow.setLayoutX(1000);
		colourFlow.setLayoutY(400);
		colourFlow.setVgap(4);
		colourFlow.setHgap(4);
		colourFlow.setPrefWrapLength(110);

		/* Creates objects, buttons, windows etc. */
		this.unob = new Button();

		Image unopic = new Image(getClass().getResource("/pictures/suno.png").toExternalForm(), 179, 97, true, true);
		ImageView unoimv = new ImageView();
		unoimv.setImage(unopic);
		unob.getStylesheets().add(UnoGUI.class.getResource("B_Style.css").toExternalForm());
		unob.setLayoutX(1000);
		unob.setLayoutY(300);
		unob.setId("uno");
		unob.setGraphic(unoimv);

		Button drawb = new Button();

		final Image imageD = new Image(getClass().getResource("/pictures/uno.png").toExternalForm(), 180, 270, true,
				true);
		ImageView imvD = new ImageView();
		imvD.setImage(imageD);
		drawb.getStylesheets().add(UnoGUI.class.getResource("B_Style.css").toExternalForm());
		drawb.setLayoutX(700);
		drawb.setLayoutY(200);
		drawb.setId("draw");
		drawb.setGraphic(imvD);

		this.playb = new Button();

		Image imageP = new Image(getClass().getResource(lastPlayed.getImgLink()).toExternalForm(), 180, 270, true,
				true);
		ImageView imvP = new ImageView();
		imvP.setImage(imageP);
		playb.getStylesheets().add(UnoGUI.class.getResource("B_Style.css").toExternalForm());
		playb.setLayoutX(500);
		playb.setLayoutY(200);
		playb.setId("play");
		playb.setGraphic(imvP);

		Image chat = new Image(getClass().getResource("/pictures/chat2.png").toExternalForm(), 148, 74, false, false);
		ImageView chatimv = new ImageView();
		chatimv.setLayoutX(25);
		chatimv.setLayoutY(10);
		chatimv.setImage(chat);

		this.ta = new TextArea();

		this.ta.setPrefSize(225, 350);
		this.ta.setLayoutX(30);
		this.ta.setLayoutY(70);
		this.ta.setWrapText(true);
		ta.setId("ta");
		ta.getStylesheets().add(UnoGUI.class.getResource("B_Style.css").toExternalForm());
		this.ta.setEditable(false);

		this.tf = new TextField("");

		this.tf.setLayoutX(30);
		this.tf.setLayoutY(430);
		this.tf.setPrefSize(225, 25);

		this.tfp = new TextField("");
		this.tfp.setLayoutX(1000);
		this.tfp.setLayoutY(30);
		this.tfp.setEditable(false);
		this.tfp.setBackground(null);
		this.tfp.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		this.tfp.setPrefSize(800, 50);

		this.tfc = new TextField("HEJ");
		this.tfc.setEditable(false);
		this.tfc.setBackground(null);
		this.tfc.setLayoutX(550);
		this.tfc.setLayoutY(175);
		this.tfc.setVisible(false);
		this.tfc.setPrefSize(500, 30);
		this.tfc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

		/* Creates 4 colored buttons. Displays when the black card is played */
		Button[] setColourButtons = new Button[4];
		for (int i = 0; i < setColourButtons.length; i++) {
			setColourButtons[i] = new Button();
			setColourButtons[i].getStylesheets().add(UnoGUI.class.getResource("B_Style.css").toExternalForm());
			setColourButtons[i].setPrefSize(50, 50);
		}

		setColourButtons[0].setId("r");
		setColourButtons[1].setId("b");
		setColourButtons[2].setId("y");
		setColourButtons[3].setId("g");

		for (int i = 0; i < setColourButtons.length; i++) {
			colourFlow.getChildren().add(setColourButtons[i]);
		}
		colourFlow.setVisible(false);

		/* Displays the hand */
		this.tb = new LinkedList<ToggleButton>();
		this.cardsToPlay = new LinkedList<Card>();

		/* Creates the ObsList */
		this.obsTb = FXCollections.observableList(tb);
		
		/* Creates all eventhandlers */
		/* Creates a new togglebutton for each card in the hand */
		for (int i = 0; i < user.getHand().size(); i++) {
			ToggleButton newTb = new ToggleButton();
			obsTb.add(newTb);
			newTb.setOnAction(a -> {
				int x = obsTb.indexOf(newTb);
				if (newTb.isSelected()) {
					cardsToPlay.add(user.getHand().get(x));
					ta.appendText(user.getHand().get(x).getColour() + Integer.toString(user.getHand().get(x).getValue())
							+ " is selected \n");
					tb.get(x).setText(Integer.toString(cardsToPlay.size()));
				} else {
					System.out.println(tb.size() + " " + x);
					cardsToPlay.remove(user.getHand().get(x));
					ta.appendText(user.getHand().get(x).toString() + " is deselected \n");
					tb.get(x).setText("");
					for (int k = 0; k < tb.size(); k++) {
						Card userCard = user.getHand().get(k);
						if (cardsToPlay.indexOf(userCard) >= 0) {
							tb.get(k).setText(Integer.toString(cardsToPlay.indexOf(user.getHand().get(k)) + 1));
						}
					}
				}
			});
			tb.getLast().setGraphic(
					new ImageView(new Image(getClass().getResourceAsStream(user.getHand().get(i).getImgLink()))));
			tb.getLast().getStylesheets().add(UnoGUI.class.getResource("ToggleB_Hand.css").toExternalForm());
		}

		/* Chatevent send message */
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

		/* Unoevent to say UNO! */
		unob.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (user.sayUno()) {
					ta.appendText("You have UNO!");
					pom.addToMailbox("U");
				} else {
					ta.appendText("You don't have UNO");
				}
			}
		});

		/* Drawevent to pull card from the deck */
		drawb.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (currentPlayer.equals(user.getName())) {
					ta.appendText("draw new card \n");
					pom.addToMailbox("G");
				} else {
					ta.appendText("Please wait for your turn \n");
				}

			}
		});

		/* Colorevent for the black cards */
		for (int i = 0; i < setColourButtons.length; i++) {
			final int j = i;
			setColourButtons[j].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					// Send chosen color to ServerGameBoard */
					pom.addToMailbox("F " + setColourButtons[j].getId().charAt(0));

					colourFlow.setVisible(false);

					ta.appendText("Yes, it is your turn to play! \n");
					StringBuilder sb = new StringBuilder();
					sb.append("P ");
					for (Card c : cardsToPlay) {
						sb.append(c.toString() + " ");
					}
					pom.addToMailbox(sb.toString());
					ta.appendText(setColourButtons[j].getId());
				}
			});
		}

		/* Playevent to play card(s) */
		playb.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (cardsToPlay.isEmpty()) {
					ta.appendText("Chose cards to play or draw a new card" + '\n');
				} else if (cardsToPlay.get(0).getColour() == 's' && currentPlayer.equals(user.getName())) {
					System.out.println("In UnoGUI: Har läst in svart kort");
					colourFlow.setVisible(true);
				} else if (cardsToPlay.get(0).getColour() != 's' && currentPlayer.equals(user.getName())) {
					ta.appendText("Yes, it is your turn to play! \n");
					StringBuilder sb = new StringBuilder();
					sb.append("P ");
					for (Card c : cardsToPlay) {
						sb.append(c.toString() + " ");
					}
					pom.addToMailbox(sb.toString());
				} else {
					ta.appendText("Please wait for your turn \n");
				}
			}
		});

		/* Adds children to parents */
		for (int i = 0; i < tb.size(); i++) {
			flow.getChildren().add(tb.get(i));
		}
		pane.getChildren().addAll(tf, ta, scroll, unob, drawb, playb, colourFlow, tfp, chatimv, tfc);

		primaryStage.setScene(scene);
		primaryStage.setResizable(false);

		Socket playerSocket = null;
		// Try connecting to the server
		try {

			JTextField host = new JTextField();
			JTextField port = new JTextField();
			Object[] message = { "Machine:", host, "Port:", port };

			int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
			if (option == JOptionPane.OK_OPTION) {
				playerSocket = new Socket(host.getText(), Integer.parseInt(port.getText()));
			} else {

			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * Sets up the monitors that govern writing to and reading from the
		 * server
		 */
		this.pom = new PlayerOutputMonitor(playerSocket);
		this.pim = new PlayerInputMonitor(playerSocket, this);

		pom.addToMailbox("N " + name);

		while (!runGame) {
			System.out.println(runGame);
		}
		System.out.println(runGame);
		primaryStage.show();

	}

	/* Remove selected cards from flow */
	private void removeCards() {
		int selectedCards = 0;
		int removedCards = 0;

		for (int i = 0; i < tb.size(); i++) {
			if (tb.get(i).isSelected() == true) {
				selectedCards++;
			}
		}

		int index = 0;
		while (removedCards != selectedCards) {
			if (index < tb.size()) {
				if (tb.get(index).isSelected()) {
					flow.getChildren().remove(tb.get(index));
					obsTb.remove(index);
					removedCards++;
				}
				index++;
			} else {
				index = 0;

			}
		}

		for (Card c : cardsToPlay) {
			user.removeCard(c);
		}

		while (!cardsToPlay.isEmpty()) {
			cardsToPlay.remove();
		}
	}

	/* Adds a togglebutton and an event for the pulled card */
	private void drawCard() {
		ToggleButton newTb = new ToggleButton();
		obsTb.add(newTb);
		newTb.setOnAction(a -> {
			int x = obsTb.indexOf(newTb);
			if (newTb.isSelected()) {
				cardsToPlay.add(user.getHand().get(x));
				ta.appendText(user.getHand().get(x).getColour() + Integer.toString(user.getHand().get(x).getValue())
						+ " is selected \n");
				tb.get(x).setText(Integer.toString(cardsToPlay.size()));
			} else {
				cardsToPlay.remove(user.getHand().get(x));
				ta.appendText(user.getHand().get(x).toString() + " is deselected \n");
				tb.get(x).setText("");
				for (int k = 0; k < tb.size(); k++) {
					Card userCard = user.getHand().get(k);
					if (cardsToPlay.indexOf(userCard) >= 0) {
						tb.get(k).setText(Integer.toString(cardsToPlay.indexOf(user.getHand().get(k)) + 1));
					}
				}
			}
		});

		tb.getLast().setGraphic(
				new ImageView(new Image(getClass().getResourceAsStream(user.getHand().getLast().getImgLink()))));
		tb.getLast().getStylesheets().add(UnoGUI.class.getResource("ToggleB_Hand.css").toExternalForm());
		this.flow.getChildren().add(tb.getLast());
	};

	/* Updates the GUI based on the message from the server. */
	public void update() {
		String message = pim.readFromMailbox();
		System.out.println("In unogui update: " + message);
		switch (message.substring(0, 1)) {
		/* Run the game */
		case ("R"):
			runGame = true;
			break;
		/* Display chatmessage */
		case ("C"):
			ta.appendText(message.substring(2) + '\n');
			break;
		/* Display playerturn */
		case ("T"):
			Platform.runLater(() -> {
				try {
					currentPlayer = message.substring(2);
					this.ta.appendText("It is turn for " + currentPlayer + '\n');
					if (currentPlayer.equals(user.getName())) {
						this.tfp.setText("Your turn");
					} else {
						this.tfp.setText(currentPlayer + "'s turn");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			break;
		/* Update last played card */
		case ("L"):
			Platform.runLater(() -> {
				try {
					lastPlayed = new Card(message.substring(2));
					System.out.println("Last played: " + lastPlayed.toString());
					Image imageP = new Image(getClass().getResource(lastPlayed.getImgLink()).toExternalForm(), 180, 270,
							true, true);
					ImageView imvP = new ImageView();
					imvP.setImage(imageP);
					this.playb.setGraphic(imvP);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			break;
		/* Draw cards */
		case ("D"):
			String[] cardsIn = message.substring(2).split(" ");
			Platform.runLater(() -> {
				try {
					for (String str : cardsIn) {
						user.addCard(new Card(str));
						drawCard();
						System.out.println("in Gui draw card: " + str);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			break;
		/* Response from server when player play cards. */
		case ("P"):
			/* If unvalid */
			if (message.substring(1, 2).equals("F")) {
				Platform.runLater(() -> {
					try {
						ta.appendText("You can't play those cards");
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				break;
			}
			/* If valid. Removes from hand. */
			Platform.runLater(() -> {
				try {
					removeCards();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			break;
		/* Display the chosen color when a black card is played. */
		case ("J"):
			Platform.runLater(() -> {
				try {
					tfc.setVisible(true);
					tfc.setText(message.substring(2));

				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			break;
		/* Removes the colored textfield above the played card. */
		case ("K"):
			Platform.runLater(() -> {
				try {
					tfc.setVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			break;
		/* Notifies the winner and losers. */
		case ("W"):
			if (message.substring(2).equals(user.getName())) {
				JOptionPane.showMessageDialog(null, "You won!", "GAME OVER", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, message.substring(2) + " won!", "GAME OVER",
						JOptionPane.INFORMATION_MESSAGE);
			}
			break;
		default:
			break;
		}
	}

}
