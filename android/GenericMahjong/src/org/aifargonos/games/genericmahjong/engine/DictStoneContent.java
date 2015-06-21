package org.aifargonos.games.genericmahjong.engine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.os.Parcel;



public class DictStoneContent implements StoneContent {
	
	
	
	public final String text;
	public final Set<String> associatedWith;
	
	
	
	public DictStoneContent(final String text, final Collection<String> associatedWith) {
		if(text == null) {
			throw new IllegalArgumentException("text cannot be null!");
		}
		this.text = text;
		this.associatedWith = associatedWith==null ? Collections.<String>emptySet() : Collections.unmodifiableSet(new HashSet<String>(associatedWith));
	}
	
	
	
	@Override
	public boolean isAssociatedWith(final StoneContent sc) {
		if(sc == null || !(sc instanceof DictStoneContent)) {
			return false;
		}
		return associatedWith.contains(((DictStoneContent)sc).text);
	}
	
	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(text);
		dest.writeStringArray(associatedWith.toArray(new String[associatedWith.size()]));
	}
	
	public static final Creator<DictStoneContent> CREATOR = new Creator<DictStoneContent>() {
		
		@Override
		public DictStoneContent createFromParcel(final Parcel source) {
			final String text = source.readString();
			final String[] associatedWith = source.createStringArray();
			return new DictStoneContent(text, Arrays.asList(associatedWith));
		}
		
		@Override
		public DictStoneContent[] newArray(int size) {
			return new DictStoneContent[size];
		}
		
	};
	
	
	
}
