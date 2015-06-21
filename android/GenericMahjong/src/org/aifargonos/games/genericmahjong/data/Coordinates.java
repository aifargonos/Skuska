package org.aifargonos.games.genericmahjong.data;


public interface Coordinates extends DataObject {
	
	
	
	public static final Coordinates NW =  DataFactory.newCoordinates( 0, -1,  0);
	public static final Coordinates NE =  DataFactory.newCoordinates( 1, -1,  0);
	public static final Coordinates SW =  DataFactory.newCoordinates( 0,  2,  0);
	public static final Coordinates SE =  DataFactory.newCoordinates( 1,  2,  0);
	public static final Coordinates WN =  DataFactory.newCoordinates(-1,  0,  0);
	public static final Coordinates WS =  DataFactory.newCoordinates(-1,  1,  0);
	public static final Coordinates EN =  DataFactory.newCoordinates( 2,  0,  0);
	public static final Coordinates ES =  DataFactory.newCoordinates( 2,  1,  0);
	public static final Coordinates ANW = DataFactory.newCoordinates( 0,  0,  1);
	public static final Coordinates ANE = DataFactory.newCoordinates( 1,  0,  1);
	public static final Coordinates ASW = DataFactory.newCoordinates( 0,  1,  1);
	public static final Coordinates ASE = DataFactory.newCoordinates( 1,  1,  1);
	public static final Coordinates UNW = DataFactory.newCoordinates( 0,  0, -1);
	public static final Coordinates UNE = DataFactory.newCoordinates( 1,  0, -1);
	public static final Coordinates USW = DataFactory.newCoordinates( 0,  1, -1);
	public static final Coordinates USE = DataFactory.newCoordinates( 1,  1, -1);
	
	
	
	int x();
	
	int y();
	
	int z();
	
	Coordinates getTranslatedCopy(int dx, int dy, int dz);
//		return new Coordinates(x + dx, y + dy, z + dz);
//	}
	
	Coordinates getTranslatedCopy(Coordinates d);
//		return getTranslatedCopy(d.x, d.y, d.z);
//	}
//	
//	public Coordinates translate(int dx, int dy, int dz) {
//		x += dx;
//		y += dy;
//		z += dz;
//		return this;
//	}
//	
//	public Coordinates translate(Coordinates d) {
//		return translate(d.x, d.y, d.z);
//	}
	
}
