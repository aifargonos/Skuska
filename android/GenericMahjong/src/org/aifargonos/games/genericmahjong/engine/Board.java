package org.aifargonos.games.genericmahjong.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.aifargonos.games.genericmahjong.data.Coordinates;


/**
 * TODO
 * 
 * zmenit random tak, aby to nerozhadzovalo az tak pravidelne ...
 * 
 * @author aifargonos
 */
public class Board {
//	
//	
//	
//	public static final String XML_ENCODING = "UTF-8";
//	public static final String XML_VERSION = "1.0";
//	public static final String XML_ROOT_ELEMENT = "board";
	
	
	
//	private TreeMap<Coordinates, Stone> board;
	private Map<Coordinates, Stone> board;
	
	
	
	public Board() {
//		this.board = new TreeMap<Coordinates, Stone>();
		this.board = new HashMap<Coordinates, Stone>();
	}
	
	
	
	public boolean isEmpty() {
		return board.isEmpty();
	}
	
	public boolean isFree(Coordinates c) {
		c = new Coordinates(c);
		if(board.containsKey(c)) return false;
		c.x++;
		if(board.containsKey(c)) return false;
		c.y++;
		if(board.containsKey(c)) return false;
		c.x--;
		if(board.containsKey(c)) return false;
		c.y--;
		return true;
	}
	
	public boolean put(Stone stone) {
		Coordinates c = stone.getPosition();
		if(!isFree(c)) return false;
		
		board.put(c, stone);
		board.put(c.getTranslatedCopy(1, 0, 0), stone);
		board.put(c.getTranslatedCopy(1, 1, 0), stone);
		board.put(c.getTranslatedCopy(0, 1, 0), stone);
		
		return true;
	}
	
	public Stone get(Coordinates c) {
		return board.get(c);
	}
	
	public boolean remove(Stone stone) {
		Coordinates c = stone.getPosition();
		if(!board.containsKey(c)) return false;
		
		board.remove(c);
		c.x++;
		board.remove(c);
		c.y++;
		board.remove(c);
		c.x--;
		board.remove(c);
		
		return true;
	}
	
	public boolean remove(Coordinates c) {
		if(!board.containsKey(c)) return false;
		return remove(board.get(c));
	}
	
	public void clear() {
		board.clear();
	}
	
	public int size() {
		return board.size();
	}
	
	public Stone[] getStones() {
		Stone[] ret = new Stone[board.size()];
		return board.values().toArray(ret);
	}
//	
//	
//	
//	public void save(OutputStream xmlOut) throws XMLStreamException, JAXBException {
//        
//		XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(xmlOut, XML_ENCODING);
//        
//        writer.writeStartDocument(XML_ENCODING, XML_VERSION);
//        writer.writeCharacters("\n");
//        writer.writeStartElement(XML_ROOT_ELEMENT);
//        writer.writeCharacters("\n");
//        
//		JAXBContext context = JAXBContext.newInstance(Coordinates.class);
//		Marshaller marshaller = context.createMarshaller();
//		marshaller.setProperty("jaxb.formatted.output", true);// nefacha ...
//		marshaller.setProperty("jaxb.fragment", true);
//		
//		for(Stone stone : board.values()) {
//			marshaller.marshal(stone.getPosition(), writer);
//            writer.writeCharacters("\n");
//		}
//		
//		writer.writeEndElement();
//        writer.writeCharacters("\n");
//		writer.writeEndDocument();
//		writer.close();
//		
//	}
//	
//	public void load(InputStream xmlIn) throws XMLStreamException, JAXBException {
//		
//		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(xmlIn, XML_ENCODING);
//		
//		JAXBContext context = JAXBContext.newInstance(Coordinates.class);
//		Unmarshaller unmarshaller = context.createUnmarshaller();
//		
//		boolean inRoot = false;
//		while(reader.hasNext()) {
//			
//			if(reader.isStartElement()) {
//				if(reader.getLocalName().equals(XML_ROOT_ELEMENT)) {
//					if(inRoot) {
//						throw new XMLStreamException("root element in root element", reader.getLocation());
//					} else {
//						inRoot = true;
//					}
//				} else {
//					if(inRoot) {
//						
//						Object o = unmarshaller.unmarshal(reader);
//						if(o instanceof Coordinates) {
//							put(new Stone((Coordinates)o));
//						}
//						
//					} else {
//						throw new XMLStreamException("something out of root element", reader.getLocation());
//					}
//				}
//			} else if(reader.isEndElement()) {
//				if(reader.getLocalName().equals(XML_ROOT_ELEMENT)) {
//					if(inRoot) {
//						inRoot = false;
//					} else {
//						throw new XMLStreamException("end of root element out of root element", reader.getLocation());
//					}
//				}
//			}
//			
//			reader.next();
//		}
//		
//		reader.close();
//		
//	}
	
	
	
	private Stone getNeighbour(final Coordinates c, final Coordinates d) {
		return board.get(c.translate(d));
	}
	
	private Stone getNeighbour(final Stone stone, final Coordinates d) {
		return getNeighbour(stone.getPosition(), d);
	}
	
	
	public boolean hasNeighbour(final Coordinates c, final Coordinates d) {
		return board.containsKey(c.translate(d));
	}
	
	public boolean hasNeighbour(final Stone stone, final Coordinates d) {
		return hasNeighbour(stone.getPosition(), d);
	}
	
	
	
	/**
	 * POZOR !!! iba vertikalne blokovanie !!! neoveruje to horizontalne blokovanie !!!
	 * TODO !!! aj tam je stone, co uz ma content, tak ho to neblokuje !!!
	 * 
	 * @param stone
	 * @return
	 */
	private boolean blocksVertically(Stone stone) {
		/* 
		 * ak je pod kockou nieco bez obsahu
		 */
		Stone neighbour = getNeighbour(stone, Coordinates.UNE);
		if(neighbour != null && neighbour.getContent() == null) return true;
		neighbour = getNeighbour(stone, Coordinates.UNW);
		if(neighbour != null && neighbour.getContent() == null) return true;
		neighbour = getNeighbour(stone, Coordinates.USE);
		if(neighbour != null && neighbour.getContent() == null) return true;
		neighbour = getNeighbour(stone, Coordinates.USW);
		if(neighbour != null && neighbour.getContent() == null) return true;
		return false;
	}
	
	private boolean isContentToLeft(Set<Stone> stones) {
		Set<Stone> newStones = new HashSet<Stone>();
		Set<Stone> tmp = null;
		for(Stone stone : stones) {
			Stone neighbour = getNeighbour(stone, Coordinates.WN);
			if(neighbour != null) newStones.add(neighbour);
			neighbour = getNeighbour(stone, Coordinates.WS);
			if(neighbour != null) newStones.add(neighbour);
		}
		stones.clear();
		while(!newStones.isEmpty()) {
			for(Stone stone : newStones) {
				if(stone.getContent() != null) return true;
				Stone neighbour = getNeighbour(stone, Coordinates.WN);
				if(neighbour != null) stones.add(neighbour);
				neighbour = getNeighbour(stone, Coordinates.WS);
				if(neighbour != null) stones.add(neighbour);
			}
			newStones.clear();
			tmp = newStones;
			newStones = stones;
			stones = tmp;
		}
		return false;
	}
	private boolean blocksLeft(Stone stone) {
		/*
		 * ak je od laveho suseda, ktory nema obsah, vlavo nieco s obsahom
		 */
		Set<Stone> stones = new HashSet<Stone>();
		Stone neighbour = getNeighbour(stone, Coordinates.WN);
		if(neighbour != null && neighbour.getContent() == null) stones.add(neighbour);
		neighbour = getNeighbour(stone, Coordinates.WS);
		if(neighbour != null && neighbour.getContent() == null) stones.add(neighbour);
		return isContentToLeft(stones);
	}
	
	private boolean isContentToRight(Set<Stone> stones) {
		Set<Stone> newStones = new HashSet<Stone>();
		Set<Stone> tmp = null;
		for(Stone stone : stones) {
			Stone neighbour = getNeighbour(stone, Coordinates.EN);
			if(neighbour != null) newStones.add(neighbour);
			neighbour = getNeighbour(stone, Coordinates.ES);
			if(neighbour != null) newStones.add(neighbour);
		}
		stones.clear();
		while(!newStones.isEmpty()) {
			for(Stone stone : newStones) {
				if(stone.getContent() != null) return true;
				Stone neighbour = getNeighbour(stone, Coordinates.EN);
				if(neighbour != null) stones.add(neighbour);
				neighbour = getNeighbour(stone, Coordinates.ES);
				if(neighbour != null) stones.add(neighbour);
			}
			newStones.clear();
			tmp = newStones;
			newStones = stones;
			stones = tmp;
		}
		return false;
	}
	private boolean blocksRight(Stone stone) {
		/*
		 * ak je od praveho suseda, ktory nema obsah, vpravo nieco s obsahom
		 */
		Set<Stone> stones = new HashSet<Stone>();
		Stone neighbour = getNeighbour(stone, Coordinates.EN);
		if(neighbour != null && neighbour.getContent() == null) stones.add(neighbour);
		neighbour = getNeighbour(stone, Coordinates.ES);
		if(neighbour != null && neighbour.getContent() == null) stones.add(neighbour);
		return isContentToRight(stones);
	}
	
	
	
	/**
	 * TODO nie vzdy treba ist aj vertikalne, ale keby som to spravil iba horizontalne,
	 *      malo by to vacsiu reziu
	 * 
	 * @param stone
	 * @param blockedBySet
	 */
	private void addToBlockedBySetToLeft(Stone stone, Set<Stone> blockedBySet) {
		Stone neighbour = getNeighbour(stone, Coordinates.WN);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
//			addToBlockedBySetToLeft(neighbour, blockedBySet);
			addToBlockedBySet(neighbour, blockedBySet);
		}
		neighbour = getNeighbour(stone, Coordinates.WS);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
//			addToBlockedBySetToLeft(neighbour, blockedBySet);
			addToBlockedBySet(neighbour, blockedBySet);
		}
	}
	/**
	 * TODO nie vzdy treba ist aj vertikalne, ale keby som to spravil iba horizontalne,
	 *      malo by to vacsiu reziu
	 * 
	 * @param stone
	 * @param blockedBySet
	 */
	private void addToBlockedBySetToRight(Stone stone, Set<Stone> blockedBySet) {
		Stone neighbour = getNeighbour(stone, Coordinates.EN);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
//			addToBlockedBySetToRight(neighbour, blockedBySet);
			addToBlockedBySet(neighbour, blockedBySet);
		}
		neighbour = getNeighbour(stone, Coordinates.ES);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
//			addToBlockedBySetToRight(neighbour, blockedBySet);
			addToBlockedBySet(neighbour, blockedBySet);
		}
	}
	private void addToBlockedBySet(Stone stone, Set<Stone> blockedBySet) {
		// horizontally
		HashSet<Stone> stones = new HashSet<Stone>();
		stones.add(stone);
		if(isContentToLeft(stones)) {
			addToBlockedBySetToRight(stone, blockedBySet);
		}
		stones.clear();
		stones.add(stone);
		if(isContentToRight(stones)) {
			addToBlockedBySetToLeft(stone, blockedBySet);
		}
		// vertically
		Stone neighbour = getNeighbour(stone, Coordinates.ANE);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
			addToBlockedBySet(neighbour, blockedBySet);
		}
		neighbour = getNeighbour(stone, Coordinates.ANW);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
			addToBlockedBySet(neighbour, blockedBySet);
		}
		neighbour = getNeighbour(stone, Coordinates.ASE);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
			addToBlockedBySet(neighbour, blockedBySet);
		}
		neighbour = getNeighbour(stone, Coordinates.ASW);
		if(neighbour != null && !blockedBySet.contains(neighbour)) {
			blockedBySet.add(neighbour);
			addToBlockedBySet(neighbour, blockedBySet);
		}
	}
	private int getBlockedByCount(Stone stone) {
		HashSet<Stone> blockedBySet = new HashSet<Stone>();
		addToBlockedBySet(stone, blockedBySet);
		return blockedBySet.size();
	}
	
	
	
	private Stone getRandomStone(Set<Stone> stones) {
		
		HashMap<Stone, Integer> blockedByResults = new HashMap<Stone, Integer>(stones.size());
		for(Stone stone : stones) {
			blockedByResults.put(stone, getBlockedByCount(stone));
		}
		
		Vector<Stone> weightedStones = new Vector<Stone>();
		for(Stone stone : blockedByResults.keySet()) {
			for(int i = 0; i < blockedByResults.get(stone) + 1; i++) {
				weightedStones.add(stone);
			}
		}
		
		return weightedStones.get((int)(Math.random() * weightedStones.size()));
	}
	
	
	
	public void generate(List<StoneContent> stoneContents) {
		
		
		// random associated pairs
		
		int oldEnd = stoneContents.size();
		
		while(oldEnd > 0) {
			
			// first
			StoneContent first = stoneContents.remove((int)(Math.random() * oldEnd));
			oldEnd--;
			if(oldEnd == 0) break;
			
			// second
			int start = (int)(Math.random() * oldEnd);
			StoneContent second = null;
			int i = start;
			do {
				if(stoneContents.get(i).isAssociatedWith(first)) {
					second = stoneContents.remove(i);
					oldEnd--;
					break;
				}
				i++;
				if(i >= oldEnd) {
					i = 0;
				}
			} while(i != start);
			
			// pair
			if(second != null) {
				stoneContents.add(first);
				stoneContents.add(second);
			}
			
		}
		
		
		/* 
		 * a teraz vlastne generovani .:
		 * budem zaradom obsadzovat tie stones-y, co nic neblokuju a nastavovat blokovanie ... 
		 * 
		 * spravim zoznam stones-ov, ktore nic neblokuju
		 * loop az kym nevyprazdnim stoneContents
		 * spomedzi tych, co su v zozname a su najviac blokovane, vyberem nahodny.
		 * ten dostane prvy content z associated paru a vyberem ho zo zoznamu.
		 * zo zoznamu vyberem take stones-y, ktore kvoli tomuto priradeniu nieco zacali blokovat.
		 * spomedzi tych, co su v zozname a su najviac blokovane, vyberem nahodny.
		 * ten dostane druhy content z associated paru a vyberem ho zo zoznamu.
		 * zo zoznamu vyberem take stones-y, ktore kvoli tomuto priradeniu nieco zacali blokovat.
		 *   do zoznamu pridam take stones-y, ktore vdaka obom priradeniam uz nic neblokuju.
		 */
		
		// clear
		
		for(Stone stone : board.values()) {
			stone.setContent(null);
		}
		
		// nonBlockingStones
		
		HashSet<Stone> nonBlockingStones = new HashSet<Stone>(board.values());
		for(Iterator<Stone> it = nonBlockingStones.iterator(); it.hasNext();) {
			Stone stone = it.next();
			if(stone.getContent() != null || blocksVertically(stone) || blocksLeft(stone) || blocksRight(stone)) {
				it.remove();
				continue;
			}
		}
		
		// generate
		
//		HashMap<Stone, Integer> blockedByResults = new HashMap<Stone, Integer>(nonBlockingStones.size());
//		Vector<Stone> maxStones = new Vector<Stone>();
		HashSet<Stone> stones = new HashSet<Stone>();
		
		Iterator<StoneContent> stoneContentsIterator = stoneContents.iterator();
		while(stoneContentsIterator.hasNext() && !nonBlockingStones.isEmpty()) {
			
//			System.out.println("nonBlockingStones: " + nonBlockingStones);// TODO DEBUG
//			if(nonBlockingStones.isEmpty()) {
//				System.err.println("!!! pruser .: nonBlockingStones.isEmpty()");
//				return;// TODO dat sem exception
//			}
//			
			// counting BlockedByCount
			
//			blockedByResults.clear();
//			for(Stone stone : nonBlockingStones) {
//				blockedByResults.put(stone, getBlockedByCount(stone));
//			}
//			System.out.println("blockedByResults: " + blockedByResults);// TODO DEBUG
//			Integer[] results = new Integer[blockedByResults.size()];// TODO DEBUG
//			results = blockedByResults.values().toArray(results);// TODO DEBUG
//			Arrays.sort(results);// TODO DEBUG
//			System.out.println("results: " + Arrays.toString(results));// TODO DEBUG
			
			// finding most blocked stones
			
//			int max = 0;
//			for(int i : blockedByResults.values()) {
//				if(i > max) max = i;
//			}
//			System.out.println("max: " + max);// TODO DEBUG
//			maxStones.clear();
//			for(Stone stone : blockedByResults.keySet()) {
//				if(blockedByResults.get(stone) == max) maxStones.add(stone);
//			}
//			System.out.println("maxStones: " + maxStones);// TODO DEBUG
			
			// random
			
//			Stone first = maxStones.get((int)(Math.random() * maxStones.size()));
			Stone first = getRandomStone(nonBlockingStones);
//			System.out.println("first: " + first);// TODO DEBUG
			
			// setting first content
			
			first.setContent(stoneContentsIterator.next());
			if(!stoneContentsIterator.hasNext()) {
				System.err.println("!!! pruser .: v stoneContents nebol parny pocet .: chyba niekde vyzsie v algoitme");
				// TODO .: proper exception !!!
			}
			
			// blocking stones
			
			nonBlockingStones.remove(first);
			
			stones.clear();
			Stone neighbour = getNeighbour(first, Coordinates.WN);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToLeft(neighbour, stones);// TODO ide to aj horizontalne
			neighbour = getNeighbour(first, Coordinates.WS);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToLeft(neighbour, stones);// TODO ide to aj horizontalne
			neighbour = getNeighbour(first, Coordinates.EN);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToRight(neighbour, stones);// TODO ide to aj horizontalne
			neighbour = getNeighbour(first, Coordinates.ES);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToRight(neighbour, stones);// TODO ide to aj horizontalne
			nonBlockingStones.removeAll(stones);
//			System.out.println("nonBlockingStones: " + nonBlockingStones);// TODO DEBUG
			
			
			// counting BlockedByCount
			
//			blockedByResults.clear();
//			for(Stone stone : nonBlockingStones) {
//				blockedByResults.put(stone, getBlockedByCount(stone));
//			}
//			System.out.println("blockedByResults: " + blockedByResults);// TODO DEBUG
//			results = new Integer[blockedByResults.size()];// TODO DEBUG
//			results = blockedByResults.values().toArray(results);// TODO DEBUG
//			Arrays.sort(results);// TODO DEBUG
//			System.out.println("results: " + Arrays.toString(results));// TODO DEBUG
			
			// finding most blocked stones
			
//			max = 0;
//			for(int i : blockedByResults.values()) {
//				if(i > max) max = i;
//			}
//			System.out.println("max: " + max);// TODO DEBUG
//			maxStones.clear();
//			for(Stone stone : blockedByResults.keySet()) {
//				if(blockedByResults.get(stone) == max) maxStones.add(stone);
//			}
//			System.out.println("maxStones: " + maxStones);// TODO DEBUG
			
			// random
			
//			Stone second = maxStones.get((int)(Math.random() * maxStones.size()));
			Stone second = getRandomStone(nonBlockingStones);
//			System.out.println("second: " + second);// TODO DEBUG
			
			// setting second content
			
			second.setContent(stoneContentsIterator.next());
			
			// blocking stones
			
			nonBlockingStones.remove(second);
			
			stones.clear();
			neighbour = getNeighbour(second, Coordinates.WN);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToLeft(neighbour, stones);// TODO ide to aj horizontalne TODO staci " && neighbour.getContent() == null" iba tu ??
			neighbour = getNeighbour(second, Coordinates.WS);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToLeft(neighbour, stones);// TODO ide to aj horizontalne TODO staci " && neighbour.getContent() == null" iba tu ??
			neighbour = getNeighbour(second, Coordinates.EN);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToRight(neighbour, stones);// TODO ide to aj horizontalne TODO staci " && neighbour.getContent() == null" iba tu ??
			neighbour = getNeighbour(second, Coordinates.ES);
			if(neighbour != null && neighbour.getContent() == null) addToBlockedBySetToRight(neighbour, stones);// TODO ide to aj horizontalne TODO staci " && neighbour.getContent() == null" iba tu ??
			nonBlockingStones.removeAll(stones);
//			System.out.println("nonBlockingStones: " + nonBlockingStones);// TODO DEBUG
			
			/* 
			 * do mnoziny dam tie stones-y, u ktorych sa mohlo zmenit blokovanie
			 * teda: pravych, lavych, hornych susedov, co nie su v nonBlockingStones a nemaju content
			 * toto blokovanie overim, a podla toho ich pridam do nonBlockingStones
			 */
			
			// unblocking stones
			
			stones.clear();
			neighbour = getNeighbour(first, Coordinates.ANE);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.ANW);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.ASE);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.ASW);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.WN);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.WS);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.EN);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(first, Coordinates.ES);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.ANE);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.ANW);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.ASE);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.ASW);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.WN);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.WS);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.EN);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			neighbour = getNeighbour(second, Coordinates.ES);
			if(neighbour != null && neighbour.getContent() == null
					&& !nonBlockingStones.contains(neighbour)) stones.add(neighbour);
			
//			System.out.print("unblocking: ");// TODO DEBUG
			for(Stone stone : stones) {
				if(!blocksVertically(stone) && !blocksLeft(stone) && !blocksRight(stone)) {
//					System.out.print(stone + " ");// TODO DEBUG
					nonBlockingStones.add(stone);
				}
			}
//			System.out.println();// TODO DEBUG
			
//			System.out.println("ZMACKNI ENTR !!!");// TODO DEBUG
//			try {
//				System.in.read();// TODO DEBUG
//			} catch (IOException e) {e.printStackTrace();}
			
		}
		
		// check .:
		for(Stone stone : board.values()) {
			if(stone.getContent() == null) {
				System.err.println("!!! pruser .: stone.getContent() == null");
				return;// TODO dat sem exception
			}
		}
		
	}
	
	
	
	public boolean isBlocked(Stone stone) {
		if(getNeighbour(stone, Coordinates.ANE) != null) return true;
		if(getNeighbour(stone, Coordinates.ANW) != null) return true;
		if(getNeighbour(stone, Coordinates.ASE) != null) return true;
		if(getNeighbour(stone, Coordinates.ASW) != null) return true;
		if((getNeighbour(stone, Coordinates.WN) != null || getNeighbour(stone, Coordinates.WS) != null)&&
				(getNeighbour(stone, Coordinates.EN) != null || getNeighbour(stone, Coordinates.ES) != null)) {
			return true;
		}
		return false;
	}
	
	
	
}
