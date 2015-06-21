package org.aifargonos.games.genericmahjong;



public final class Utils {
	
	
	
	private Utils() {}
	
	
	
	/**
	 * This is a bijection from pairs of integers to integers.
	 * TODO: Check that it works for integers with overflow!
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static int hashCombine(final int x, final int y) {
		final int s = x + y;
		return (s * (s + 1))/2 + x;
	}
	
	public static int hashCombine(int... x) {
		if(x == null || x.length == 0) {
			return 0;
		}
		int ret = x[0];
		for(int i = 1; i < x.length; i++) {
			ret = hashCombine(ret, x[i]);
		}
		return ret;
	}
	
	
	
}
