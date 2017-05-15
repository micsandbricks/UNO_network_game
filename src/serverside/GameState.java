package serverside;

public class GameState extends Thread {

	private MailboxMonitor mm;

	public GameState(MailboxMonitor mm) {
		this.mm = mm;
	}

	@Override
	public void run() {
		while (!interrupted()) {
			getMessageAndCompute();
		}
	}

	private void getMessageAndCompute() {
		String message = mm.fetchMessageFromInMailbox();
		if (message != "") {
			switch (message.substring(3,4)){
			case ("C"):
				mm.addToOutMailbox("A " + message.substring(3));
				break;
			case ("U"):
				mm.addToOutMailbox(message);
				break;
			default:
				break;
			}
		}
	}
}
