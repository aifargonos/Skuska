package org.aifargonos.games.genericmahjong.engine;

import java.util.Set;

import org.aifargonos.games.genericmahjong.data.Coordinates;
import org.aifargonos.games.genericmahjong.data.DataFactory;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Stone implements Parcelable {
	
	
	
	private final Coordinates position;
	private StoneContent content = null;
	
	private Engine engine;
	boolean isSelected = false;
	
	// Data for generator.
	boolean hasContentToRight = false;
	boolean hasContentToLeft = false;
	Set<Stone> directlyBlocks;
	
	
	
	public Stone(final Coordinates position) {
		this.position = position;
	}
	
	
	
	public StoneContent getContent() {
		return content;
	}
	public void setContent(final StoneContent content) {
		this.content = content;
	}
	
	public Coordinates getPosition() {
		return position;
	}
	
	public Engine getEngine() {
		return engine;
	}
	void setEngine(Engine engine) {
		this.engine = engine;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	public void click() {
		if(engine == null) {
			isSelected = !isSelected;
		} else {
			engine.onStoneClicked(this);
		}
	}
	
	
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + position.x() + "," + position.y() + "," + position.z() + ")";
	}
	
	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(position.x());
		dest.writeInt(position.y());
		dest.writeInt(position.z());
		dest.writeParcelable(content, 0);
	}
	
	public static final Creator<Stone> CREATOR = new Creator<Stone>() {
		
		@Override
		public Stone createFromParcel(final Parcel source) {
			final int x = source.readInt();
			final int y = source.readInt();
			final int z = source.readInt();
			final StoneContent content = source.readParcelable(null);
			final Stone result = new Stone(DataFactory.newCoordinates(x, y, z));
			result.setContent(content);
			return result;
		}
		
		@Override
		public Stone[] newArray(int size) {
			return new Stone[size];
		}
		
	};
	
	
	
}
