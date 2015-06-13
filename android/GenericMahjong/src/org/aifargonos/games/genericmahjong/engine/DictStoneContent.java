package org.aifargonos.games.genericmahjong.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;



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
	
	
	
}
