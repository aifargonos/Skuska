package org.aifargonos.games.genericmahjong.engine;

import org.aifargonos.games.genericmahjong.data.Coordinates;

/**
 * TODO
 * 
 * ak chcem spravit inteligentne Stone, musi ti vediet povedat .:
 * 	obsah
 * 	poziciu
 * 		si bude checkovat s field a pri zmene sa bude rovno premiestnovat
 * 			musi mat referenciu na field, do ktoreho patri
 * 	susedov
 * 	ci nieco blokuje
 * 	kolkymi je blokovana
 * 		musi mat mnozinu blokujucich, ktora sa musi menit s
 * 			poziciou
 * 			obsahom
 * 
 * @author aifargonos
 */
public class Stone implements Comparable<Stone> {
	
	
	
	private final Coordinates position = new Coordinates();
	private StoneContent content = null;
	
	
	
	public Stone() {}
	
	public Stone(Coordinates position) {
		this.position.set(position);
	}
	
	
	
	public StoneContent getContent() {
		return content;
	}
	public void setContent(StoneContent content) {
		this.content = content;
	}
	
	public Coordinates getPosition() {
		return new Coordinates(position);
	}
	public Coordinates getPosition(Coordinates c) {
		c.set(position);
		return c;
	}
	public void setPosition(Coordinates position) {
		this.position.set(position);
	}
	
	
	
	@Deprecated
	public int compareTo(Stone tile) {
		/* TODO .: this is not a good idea, because two stones with different content may be the same!
		 * If I want to compare coordinates, then I should use comparator that extracts coordinates and compare them! 
		 */
		return position.compareTo(tile.getPosition());
	}
	
	
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + position;
	}
	
	
	
}
