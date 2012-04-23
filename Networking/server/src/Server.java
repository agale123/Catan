import java.io.*;
import java.net.*;

/**
 * A chat server, listening for incoming connections and passing them
 * off to {@link ClientHandler}s.
 */
public class Server extends Thread {

	private int _port;
	private ServerSocket _socket;
	private ClientPool _clients;
	private boolean _running;

	/**
	 * Initialize a server on the given port. This server will not listen until
	 * it is launched with the start() method.
	 * 
	 * @param port
	 * @throws IOException
	 */
	public Server(int port) throws IOException {
		if (port <= 1024) {
			throw new IllegalArgumentException("Ports under 1024 are reserved!");
		}
		
		_port = port;
		_clients = new ClientPool();
		_socket = new ServerSocket(_port);
	}

	/**
	 * Wait for and handle connections indefinitely.
	 */
	public void run() {
		try {
			while(true) {
				Socket clientConnection = _socket.accept();
				ClientHandler ch = new ClientHandler(_clients, clientConnection);
				_clients.add(ch);
				ch.start();
			}
		} catch(IOException e) {
		
		}
	}
	
	/**
	 * Stop waiting for connections, close all connected clients, and close
	 * this server's {@link ServerSocket}.
	 * 
	 * @throws IOException if any socket is invalid.
	 */
	public void kill() throws IOException {
		_running = false;
		_clients.killall();
		_socket.close();
	}
}

