package server;

import java.io.IOException;
import java.util.*;
import gamelogic.Trade;
/**
 * A group of {@link ClientHandler}s representing a "chat room".
 */
public class ClientPool {
	private static final int ID_BOUND = 4194304;
	private ArrayList<ClientHandler> _clients;
	private gamelogic.PublicGameBoard _board;
	private int _numCon;
	private Server _serv;
	private HashMap<Integer, Integer> _tradeIDs;
	private HashMap<String, Integer> _names;
	/**
	 * Initialize a new {@link ClientPool}.
	 */
	public ClientPool(int num, Server s) {
		_clients = new ArrayList<ClientHandler>();
		_numCon = num;
		_serv = s;
		_tradeIDs = new HashMap<Integer, Integer>();
		_names = new HashMap<String, Integer>();
	}

	/**
	 * Add a new client to the chat room.
	 * 
	 * @param client to add
	 */
	public synchronized void add(ClientHandler client, int index) {
		_clients.add(index, client);
	}

	/**
	 * Remove a client from the pool. Only do this if you intend to clean up
	 * that client later.
	 * 
	 * @param client to remove
	 * @return true if the client was removed, false if they were not there.
	 */
	public synchronized boolean remove(ClientHandler client) {
		return _clients.remove(client);
	}
	
	public void addName(String n, Integer i) {
		_names.put(n, i);
	}
	
	public int getName(String n) {
		return _names.get(n);
	}
	/**
	 * Send a message to clients in the pool, but the sender.
	 * 
	 * @param message to send
	 * @param sender the client _not_ to s _goal.isLegal(this)end the message to (send to everyone
	 *          if null)
	 */

	public synchronized void broadcastMe(Object message, ClientHandler sender) {
		for (ClientHandler client : _clients) {
			if (sender != null && sender != client) {
				continue;
			}

			client.send(message);
		}
	}

	public synchronized void broadcast(Object e, ClientHandler sender) {
		if (e instanceof Trade) {
			Trade tr = (Trade) e;
			if (tr.isPropose() && ! _tradeIDs.containsKey(tr.getTradeID()) && ! tr.isBuild())
				addTrade(tr.getTradeID(), sender.getIndex());
			if (! tr.isBuild() && (tr.isPropose() || tr.isComplete())) _board.notifyAITrade(tr);
		}
		for (ClientHandler client : _clients) {
			if (sender != null && sender == client) {
				continue;
			}

			client.send(e);
		}
	}

	public synchronized void broadcastTo(Object e, int id) {
		_clients.get(id).send(e);
	}

	public synchronized void initMessage(ClientHandler client) {
		client.send(client.getIndex() + "," + _numCon);
		client.send(_board.getState());
		client.send(_board.getPorts());
		client.send("7/free");
	}

	/**
	 * Close all {@link ClientHandler}s and empty the pool
	 */
	public synchronized void killall() {
		try {
			for(ClientHandler c : _clients) {
				c.kill();
			}
		} catch (Exception e) {

		}
		_clients.clear();
	}

	public void addBoard(gamelogic.PublicGameBoard board) {
		_board = board;
	}

	public synchronized gamelogic.PublicGameBoard getBoard() {
		return _board;
	}

	public void addTrade(int id, int player) {
		System.out.println("Adding ID pairing (" + Integer.toString(id) + ", " + Integer.toString(player) + ")"); // TODO: Debug line
		synchronized (_tradeIDs) {
			_tradeIDs.put(new Integer(id), new Integer(player));
		}
	}

	public void removeTrade(int id) {
		synchronized (_tradeIDs) {
			_tradeIDs.remove(new Integer(id));
		}
	}

	public int getPlayerFromTrade(int id) {
		if (_tradeIDs == null) System.out.println("_tradeIDs is null!"); // TODO: Debug line
		synchronized (_tradeIDs) {
			if (! _tradeIDs.containsKey(id)) System.out.println("Could not find trade ID " + Integer.toString(id) + "!"); // TODO: Debug line
			return _tradeIDs.get(new Integer(id));
		}
	}

	public void lostConnection(int i) {
		_board.lostPlayer(i);
	}

	public int nextTradeID(int p) {
		synchronized (_tradeIDs) {
			Random rand = new Random();
			int ret;
			do {ret = rand.nextInt(ID_BOUND) + 101;}
			while (_tradeIDs.containsKey(ret));
			addTrade(ret, p);
			return ret;
		}
	}
}

