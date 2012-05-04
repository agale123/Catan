package gamelogic;

import java.util.*;
import java.util.Map.Entry;

import catanai.*;
import catanui.*;

public class PublicGameBoard {
	
	private ArrayList<Vertex> _vertices;
	private ArrayList<Hex> _hexes;
	private ArrayList<Edge> _edges;
	private ArrayList<Player> _players;
	private ArrayList<catanai.AIPlayer> _ais;
	private int _longestRd = 4;
	private int _longestRd_Owner = -1;
	private int _largestArmy = 2;
	private int _largestArmy_Owner = -1;
	private server.Server _server;
	private HashMap<CoordPair, Integer> _coordMap;
	private HashMap<Pair, Integer> _edgeMap;
	
	
	public PublicGameBoard(server.Server server, int numPlayers) {
		_server = server;
		_hexes = new ArrayList<Hex>();
		_players = new ArrayList<Player>();
		_ais = new ArrayList<catanai.AIPlayer>();
		_coordMap = new HashMap<CoordPair, Integer>();
		_edgeMap = new HashMap<Pair, Integer>();
		_vertices = new ArrayList<Vertex>();
		_edges = new ArrayList<Edge>();
		
		for (int i = 0; i<numPlayers; i++) {
			_players.add(new Player(i));
		}
		setUpBoard(numPlayers);
	}
	
	public PublicGameBoard(server.Server s, Object a, Object b, Object c, Object d) {
		new PublicGameBoard(null, 4);
	}
	
	public void setUpBoard(int numPlayers) {
		//make hexes
		ArrayList<Integer> colSizes = null;
		ArrayList<Integer> startY = null;
		ArrayList<Integer> numbers = null;
		int numHexes = 0;
		if (numPlayers <= 4) {
		colSizes = new ArrayList<Integer>(Arrays.asList(3,4,5,4,3));
		startY = new ArrayList<Integer>(Arrays.asList(3,2,1,2,3));
		numHexes = 19;
		numbers = new ArrayList<Integer>(Arrays.asList(8,4,11,10,11,3,12,5,9,2,6,9,2,4,5,10,6,3,8));
		} else if (numPlayers == 5 || numPlayers == 6) {
		colSizes = new ArrayList<Integer>(Arrays.asList(4,5,6,7,6,5,4));
		startY = new ArrayList<Integer>(Arrays.asList(4,3,2,1,2,3,4));
		numHexes = 37;
		numbers = new ArrayList<Integer>(Arrays.asList(9,8,11,11,3,4,10,5,10,6,12,3,2,4,6,4,11,12,6,9,3,5,5,10,9,5,8,9,2,3,8,4,2,6,10,12,8));
		} 
		
		ArrayList<catanui.BoardObject.type> resources = new ArrayList<catanui.BoardObject.type>();
		catanui.BoardObject.type[] types = {catanui.BoardObject.type.WHEAT, catanui.BoardObject.type.WOOD, 
		catanui.BoardObject.type.SHEEP,catanui.BoardObject.type.BRICK, catanui.BoardObject.type.ORE};
		for (int r = 0; r<numHexes; r++) {
		resources.add(types[r%5]);
		}
		
		double currx = -0.5;
		double curry;
		int hexCount = 0;
		for (int i=0; i<colSizes.size(); i++) {
		currx += 2;
		curry = startY.get(i);
		for (int x=0; x<colSizes.get(i); x++) {
			Hex hex = new Hex(hexCount, currx, curry);
			int rand = (int) (Math.random() * resources.size());
			hex.setResource(resources.get(rand));
			hex.setRollNum(numbers.get(hexCount));
			resources.remove(rand);
			_hexes.add(hex);
			hexCount++;
			
			ArrayList<Vertex> vertices = new ArrayList<Vertex>();
			if(_vertices.contains(new Vertex((int)(currx-1.5), (int)(curry)))) {
			vertices.add(_vertices.get(_coordMap.get(new CoordPair((int)(currx-1.5), (int)(curry)))));
			} else {
			vertices.add(new Vertex((int)(currx-1.5), (int)(curry)));
			} if(_vertices.contains(new Vertex((int)(currx-.5), (int)(curry-1)))) {
			vertices.add(_vertices.get(_coordMap.get(new CoordPair((int)(currx-.5), (int)(curry-1)))));
			} else {
			vertices.add(new Vertex((int)(currx-.5), (int)(curry-1)));
			} if(_vertices.contains(new Vertex((int)(currx+.5), (int)(curry-1)))) {
			vertices.add(_vertices.get(_coordMap.get(new CoordPair((int)(currx+.5), (int)(curry-1)))));
			} else {
			vertices.add(new Vertex((int)(currx+.5), (int)(curry-1)));
			} if(_vertices.contains(new Vertex((int)(currx+1.5), (int)(curry)))) {
			vertices.add(_vertices.get(_coordMap.get(new CoordPair((int)(currx+1.5), (int)(curry)))));
			} else {
			vertices.add(new Vertex((int)(currx+1.5), (int)(curry)));
			} if(_vertices.contains(new Vertex((int)(currx+.5), (int)(curry+1)))) {
			vertices.add(_vertices.get(_coordMap.get(new CoordPair((int)(currx+.5), (int)(curry+1)))));
			} else {
			vertices.add(new Vertex((int)(currx+.5), (int)(curry+1)));
			} if(_vertices.contains(new Vertex((int)(currx-.5), (int)(curry+1)))) {
			vertices.add(_vertices.get(_coordMap.get(new CoordPair((int)(currx-.5), (int)(curry+1)))));
			} else {
			vertices.add(new Vertex((int)(currx-.5), (int)(curry+1)));
			}
			hex.setVertices(vertices);
			
			for (int z=0; z<(vertices.size()); z++) {
			if (!_vertices.contains(vertices.get(z))) {
				_vertices.add(vertices.get(z));
				_coordMap.put(new CoordPair(vertices.get(z).getX(), vertices.get(z).getY()), 
							new Integer(_vertices.indexOf(vertices.get(z))));
			}
			if (z == 5) {
				Edge edge = new Edge(vertices.get(z), vertices.get(0));
				if (!_edges.contains(edge)) {
				_edges.add(edge);
				_edgeMap.put(new Pair(new CoordPair(edge.getStartV().getX(), edge.getStartV().getY()), new CoordPair(edge.getEndV().getX(), edge.getEndV().getY())), 
							new Integer(_edges.indexOf(edge)));
				_edgeMap.put(new Pair(new CoordPair(edge.getEndV().getX(), edge.getEndV().getY()), new CoordPair(edge.getStartV().getX(), edge.getStartV().getY())), 
							new Integer(_edges.indexOf(edge)));
				}
			} else {
				Edge edge = new Edge(vertices.get(z), vertices.get(z+1));
				if (!_edges.contains(edge)) {
				_edges.add(edge);
				_edgeMap.put(new Pair(new CoordPair(edge.getEndV().getX(), edge.getEndV().getY()), new CoordPair(edge.getStartV().getX(), edge.getStartV().getY())), 
							new Integer(_edges.indexOf(edge)));
				_edgeMap.put(new Pair(new CoordPair(edge.getStartV().getX(), edge.getStartV().getY()), new CoordPair(edge.getEndV().getX(), edge.getEndV().getY())), 
							new Integer(_edges.indexOf(edge)));
				}
			}
			} 
			curry += 2;
		}
		}
	}
	
	public synchronized boolean canBuySettlement(int p) {
		if (_players.get(p).getHand().contains(BoardObject.type.WOOD) &&
			_players.get(p).getHand().contains(BoardObject.type.BRICK) &&
			_players.get(p).getHand().contains(BoardObject.type.SHEEP) && 
			_players.get(p).getHand().contains(BoardObject.type.WHEAT)) {
			return true;
		}
		return false;
	}
	
	public synchronized boolean canBuildSettlement(int p, int vx, int vy) { 
		if (!_server.everyonesReady()) {
		return false;
		}
		int v = _coordMap.get(new CoordPair(vx, vy));
		
		
		//check if vertex at least 2 away from other object
		Integer v1 = _coordMap.get(new CoordPair(vx+1, vy+1));
		if (v1 != null) {
		if (_vertices.get(v1).getObject() != 0) {
			return false; 
		}
		}Integer v2 = _coordMap.get(new CoordPair(vx-1, vy-1));
		if (v2 != null) {
		if (_vertices.get(v2).getObject() != 0) {
			return false;
		}
		}Integer v3 = _coordMap.get(new CoordPair(vx+1, vy-1));
		if (v3 != null) {
		if (_vertices.get(v3).getObject() != 0) {
			return false;
		}
		}Integer v4 = _coordMap.get(new CoordPair(vx-1, vy+1));
		if (v4 != null) {
		if (_vertices.get(v4).getObject() != 0) {
			return false;
		}
		}Integer v5 = _coordMap.get(new CoordPair(vx+1, vy));
		if (v5 != null) {
		if (_vertices.get(v5).getObject() != 0) {
			return false;
		}
		}Integer v6 = _coordMap.get(new CoordPair(vx-1, vy));
		if (v6 != null) {
		if (_vertices.get(v6).getObject() != 0) {
			return false;
		}
		}
		
		if ((_vertices.get(v).getObject() != 0)) { //if point already full
		return false;
		}
	
		if (_players.get(p).getSettlements().size() < 2) { //if first round
		buildSettlement(p, vx, vy);
		return true;
		}
		for (Edge e : _players.get(p).getRoads()) {
		if (e.getStartV() == _vertices.get(v) || e.getEndV() == _vertices.get(v)) { 
		//if player has road connected
			buildSettlement(p, vx, vy);
			return true;
		}
		}
		return false;
	}
	
	public void buildSettlement(int p, int vx, int vy) {
		int x = _coordMap.get(new CoordPair(vx, vy));
		Vertex v = _vertices.get(x);
		_players.get(p).addSettlement(v);
		v.setObject(1);
		v.setOwner(p);
		
		if(_players.get(p).getSettlements().size() == 2) {
			catanui.BoardObject.type[] ar = new catanui.BoardObject.type[3];
			int found = 0;
			for(Hex h : _hexes) {
				if(h.containsVertex(v)) {
					ar[found] = h.getResource();
					_players.get(p).addCard(h.getResource());
					found++;
				}
			}
			if (p < _players.size() - _ais.size()) _server.sendFreeCards(p, ar);
		} else if (_players.get(p).getSettlements().size() > 2) {
		    _players.get(p).removeCard(catanui.BoardObject.type.WOOD);
		    _players.get(p).removeCard(catanui.BoardObject.type.BRICK);
		    _players.get(p).removeCard(catanui.BoardObject.type.WHEAT);
		    _players.get(p).removeCard(catanui.BoardObject.type.SHEEP);
		}
		catanai.Player mover;
		catanai.Vertex target;
		for (AIPlayer ai : _ais) {
			mover = ai.getPlayer(Integer.toString(p));
			target = ai.getVertexFromBoard(x);
			if (_players.get(p).getSettlements().size() > 2) ai.registerMove(new BuildSettlement(mover, target));
			else ai.registerInitialSettlement(new BuildSettlement(mover, target));
		}
	}
	
	public synchronized boolean canBuyRoad(int p) {
		if (_players.get(p).getHand().contains(BoardObject.type.WOOD) 
			&& _players.get(p).getHand().contains(BoardObject.type.BRICK)) {
			return true;
		}
		return false;
	}
	
	public synchronized boolean canBuildRoad(int p, int vx1, int vy1, int vx2, int vy2) {
		int e;
		try {
			e = _edgeMap.get(new Pair(new CoordPair(vx1, vy1), new CoordPair(vx2, vy2)));
		} catch(Exception e0) {
			return false;
		} 
		if (_edges.get(e).hasRoad()) {//if edge already has road 
			return false;	
		}
		if (_players.get(p).getnumRds() < 2) { //if first round
			for (Edge i : _players.get(p).getRoads()) {
			if (i.getStartV() == _edges.get(e).getStartV() || i.getStartV() == _edges.get(e).getEndV() ||
						i.getEndV() == _edges.get(e).getStartV() || i.getEndV() == _edges.get(e).getEndV()) {
				//if new road connected to old road
				if (_edges.get(e).getStartV().getOwner() == p) {
					if (i.getStartV() == _edges.get(e).getEndV() || i.getEndV() == _edges.get(e).getEndV()) {
					break;
					}
				} else if (_edges.get(e).getEndV().getOwner() == p) {
					if (i.getStartV() == _edges.get(e).getStartV() || i.getEndV() == _edges.get(e).getStartV()) {
					break;
					}
				}
				return false;
			}
			}
			if (_edges.get(e).getStartV().getOwner() == p || 
					_edges.get(e).getEndV().getOwner() == p) {
			buildRoad(p, e);
			checkFirstRoundOver();
			return true;
			}
		}
		else { //if not first round
			for (Edge i : _players.get(p).getRoads()) {
			if (i.getStartV() == _edges.get(e).getStartV() || i.getStartV() == _edges.get(e).getEndV() ||
					i.getEndV() == _edges.get(e).getStartV() || i.getEndV() == _edges.get(e).getEndV()) {
				//if new road connected to old road
				buildRoad(p, e);
				return true;
			}
			}
		}
		return false;
	}
	
	public void buildRoad(int p, int e) {
		_players.get(p).addRoad(_edges.get(e));
		_edges.get(e).setRoad();
		if (_players.get(p).getnumRds() > 2) {
			_players.get(p).removeCard(catanui.BoardObject.type.WOOD);
			_players.get(p).removeCard(catanui.BoardObject.type.BRICK);
		}
		catanai.Player mover;
		catanai.Edge target;
		Pair pr = null;
		for (Entry<Pair, Integer> ent : _edgeMap.entrySet()) {
			if (ent.getValue() == e) {
				pr = ent.getKey();
				break;
			}
		}
		int v_i = _coordMap.get(pr.getA());
		int v_j = _coordMap.get(pr.getB());
		for (AIPlayer ai : _ais) {
			mover = ai.getPlayer(Integer.toString(p));
			target = ai.getEdgeFromBoard(v_i, v_j);
			if (_players.get(p).getnumRds() > 2) ai.registerMove(new BuildRoad(mover, target));
			else ai.registerInitialRoad(new BuildRoad(mover, target));
		}
		updateLongestRd(p);
	}
	
	public synchronized boolean canBuyCity(int p) {
		int numOre = 0;
		int numWheat = 0;
		for (BoardObject.type resource: _players.get(p).getHand()) {
		if (resource == BoardObject.type.ORE) {
			numOre++;
		} else if (resource == BoardObject.type.WHEAT) {
			numWheat++;
		}
		}
		if (numOre >= 3 && numWheat >= 2) {
		return true;
		}
		return false;
	}
	
	public synchronized boolean canBuildCity(int p, int vx, int vy) {
		int v = _coordMap.get(new CoordPair(vx, vy));
		if (_vertices.get(v).getObject() != 1 || //if no settlement on vertex
				!_players.get(p).hasSettlement(_vertices.get(v))) { //if settlement belongs to player
		return false;
		}
		buildCity(p, vx, vy);
		return true;
	}
	
	public void buildCity(int p, int vx, int vy) {
		int v = _coordMap.get(new CoordPair(vx, vy));
		_players.get(p).addCity(_vertices.get(v));
		_vertices.get(v).setObject(2);
		_players.get(p).removeCard(catanui.BoardObject.type.ORE);
		_players.get(p).removeCard(catanui.BoardObject.type.ORE);
		_players.get(p).removeCard(catanui.BoardObject.type.ORE);
		_players.get(p).removeCard(catanui.BoardObject.type.WHEAT);
		_players.get(p).removeCard(catanui.BoardObject.type.WHEAT);
		catanai.Player mover;
		catanai.Vertex target;
		for (AIPlayer ai : _ais) {
			mover = ai.getPlayer(Integer.toString(p));
			target = ai.getVertexFromBoard(v);
			ai.registerMove(new BuildCity(mover, target));
		}
	}
	
	public synchronized boolean canBuyDev(int p) {
		if (_players.get(p).getHand().contains(BoardObject.type.ORE) && 					
				_players.get(p).getHand().contains(BoardObject.type.SHEEP) &&_players.get(p).getHand().contains(BoardObject.type.WHEAT)) {
		_players.get(p).removeCard(BoardObject.type.SHEEP);
		_players.get(p).removeCard(BoardObject.type.WHEAT);
		_players.get(p).removeCard(BoardObject.type.ORE);
		return true;
		}
		return false;
	}
	
	public int playDevCard(int p) {
	    int d = (int) (Math.random() * 5);
	    if (d == 1) {
		_players.get(p).addPoint();
	    }
	    return d;
	}
	
	public synchronized boolean canTrade(int p1, int p2, Trade t) {
		catanui.BoardObject.type[] ins = t.getIns();
		catanui.BoardObject.type[] outs = t.getOuts();
		
		
		// player 1 has ins and player 2 has outs
		for(int i=0; i<ins.length; i++) {
			if(ins[i] != null && !_players.get(p1).getHand().contains(ins[i])) {
				return false;
			}
		}
		for(int i=0; i<outs.length; i++) {
			if(outs[i] != null && !_players.get(p2).getHand().contains(outs[i])) {
				return false;
			}
		}
		makeTrade(p1,p2, ins, outs);
		return true;
	}
	
	public void makeTrade(int p1, int p2, catanui.BoardObject.type[] ins, 
						catanui.BoardObject.type[] outs) {
		for(int i=0; i<ins.length; i++) {
			_players.get(p1).removeCard(ins[i]);
			_players.get(p2).addCard(ins[i]);
		}
		for(int i=0; i<outs.length; i++) {
			_players.get(p2).removeCard(outs[i]);
			_players.get(p1).addCard(outs[i]);
		}
	}
	
	public void diceRolled(int roll) {
		for (Hex h : _hexes) {
			if (h.getRollNum() == roll) {
				for (Vertex vertex : h.getVertices()) {
					int p = vertex.getOwner();
					if (p != -1) {
						_players.get(p).addCard(h.getResource());
						if (vertex.getObject() == 2)  { //if city
							_players.get(p).addCard(h.getResource());
						}
					}
				}
			}
		}
		for (AIPlayer ai : _ais) ai.registerDieRoll(roll);
	}
	
	public void updateLongestRd(int p) {
		if (_players.get(p).getnumRds() > _longestRd) {
			if (_longestRd_Owner != -1) {
				_players.get(_longestRd_Owner).updateLongestRd(-2);
			}
			_players.get(p).updateLongestRd(2);
			_longestRd_Owner = p;
			_longestRd = _players.get(p).getnumRds();
		}
	}
	
	public String getState() {
		String toReturn = "";
		for(Hex h : _hexes) {
			toReturn += h.getResource().toString() + ",";
		}
		return toReturn;
	}

	public void addAIPlayer(catanai.AIPlayer play, int i) {
		_ais.add(play);
		System.out.println("AI player being added with ID " + Integer.toString(i) + "."); // TODO: Debug line
	}
	
	public List<catanui.BoardObject.type> resData() {
		ArrayList<catanui.BoardObject.type> data = new ArrayList<catanui.BoardObject.type>();
		for (int i = 0; i < _hexes.size(); i++) data.add(_hexes.get(i).getResource());
		return data;
	}
	
	public List<Integer> rollData() {
		ArrayList<Integer> data = new ArrayList<Integer>();
		for (int i = 0; i < _hexes.size(); i++) data.add(_hexes.get(i).getRollNum());
		return data;
	}
	
	public CoordPair getCoordsFromInt(int v) {
		if (! _coordMap.values().contains(v)) return null;
		for (Entry<CoordPair, Integer> ent : _coordMap.entrySet()) if (ent.getValue() == v) return ent.getKey();
		return null;
	}
	
	public void checkFirstRoundOver() {
		for (Player p : _players) {
		    if (!p.isLostConnection() && (p.getSettlements().size() < 2 || p.getnumRds() < 2)) {
				return;
			}
		}
		/*for (Player p : _players) {
		    for (Hex h : _hexes) {
			for (Vertex vertex : h.getVertices()) {
			    if (p.getSettlements().get(1) == vertex) {
				_players.get(p).addCard(h.getResource());
			    }
			}
		    }
		}*/
		_server.beginTimer();
	}
	
	public void lostPlayer(int i) {
		_players.get(i).setLostConnection(true);
	}
	
	public void addCard(int p, BoardObject.type card) {
	    _players.get(p).addCard(card);
	}
	
	public int monopoly(int p, BoardObject.type cardType) {
	    int numCards = 0;
	    for (Player player : _players) {
		if (player != _players.get(p)) {
		    Iterator iterator = player.getHand().iterator();
		    while (iterator.hasNext()) {
			BoardObject.type o = (BoardObject.type) iterator.next();
			if (o == cardType) {
			    _players.get(p).addCard(o);
			    numCards++;
			    iterator.remove();
			}
		    }
		}
	    }
	    return numCards;
	}
	
	public void promptInitRoundAI() {
		for (AIPlayer ai : _ais) ai.playFirstRound();
	}
}
