package org.aifargonos.games.genericmahjong.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aifargonos.games.genericmahjong.Utils;



public final class DataFactory {
	
	
	
	private DataFactory() {
		// Does nothing..
	}
	
	
	
	private static final Map<Integer, List<CoordinatesImpl>> COORDINATES = new HashMap<Integer, List<CoordinatesImpl>>();
	
	public static Coordinates newCoordinates(final int x, final int y, final int z) {
		final int hash = hashCoordinates(x, y, z);
		List<CoordinatesImpl> list = COORDINATES.get(hash);
		if(list == null) {
			list = new ArrayList<CoordinatesImpl>();
			COORDINATES.put(hash, list);
		} else {
			for(CoordinatesImpl c : list) {
				if(equalsCoordinates(x, y, z, c)) {
					return c;
				}
			}
		}
		final CoordinatesImpl instance = new CoordinatesImpl(x, y, z, hash);
		list.add(instance);
		return instance;
	}
	
	public static int hashCoordinates(final int x, final int y, final int z) {
		return Utils.hashCombine(x, y, z);
	}
	
	public static boolean equalsCoordinates(final int x, final int y, final int z, final Coordinates c) {
		return c != null && x == c.x() && y == c.y() && z == c.z();
	}
	
	
	
}
