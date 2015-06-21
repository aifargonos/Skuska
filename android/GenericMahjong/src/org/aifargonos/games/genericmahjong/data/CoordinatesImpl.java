package org.aifargonos.games.genericmahjong.data;



class CoordinatesImpl extends AbstractDataObject implements Coordinates {
	
	
	
	public final int x;
	public final int y;
	public final int z;
	
	public final int hash;
	
	
	
	CoordinatesImpl(final int x, final int y, final int z, final int hash) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.hash = hash;
	}
	
	
	
	@Override
	public int x() {
		return x;
	}
	
	@Override
	public int y() {
		return y;
	}
	
	@Override
	public int z() {
		return z;
	}
	
	
	
	@Override
	public Coordinates getTranslatedCopy(int dx, int dy, int dz) {
		return DataFactory.newCoordinates(x + dx, y + dy, z + dz);
	}
	
	@Override
	public Coordinates getTranslatedCopy(Coordinates d) {
		return getTranslatedCopy(d.x(), d.y(), d.z());
	}
	
	
	
	@Override
	public boolean equals(final Object o) {
		return this == o;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + x + "," + y + "," + z + ")";
	}
	
	@Override
	StringBuilder internalPrettyPrint(final StringBuilder result) {
		return result.append("[").append(x).append(", ").append(y).append(", ").append(z).append("]");
	}
	
	
	
}
