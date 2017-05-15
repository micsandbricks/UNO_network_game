package serverside;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UNOserver {

	private ServerSocket s;
	
	public UNOserver(ServerSocket s){
		this.s = s;
	}	
	public Socket accept() throws IOException{
		return s.accept();
	}
	
}
