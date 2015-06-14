package org.aifargonos.games.genericmahjong.engine;



public interface EngineListener {
	
	void selectionChanged(Stone stone);
	
	void stoneAdded(Stone stone);
	
	void stoneRemoved(Stone stone);
	
}
