package org.aifargonos.games.genericmahjong.engine;

import java.util.ArrayList;
import java.util.Collection;



public class Engine {
	
	
	
	private Stone selectedStone = null;
	
	private final Board board;
	
	private final Collection<EngineListener> engineListeners = new ArrayList<EngineListener>();
	
	
	
	public Engine(final Board board) {
		this.board = board;
		board.setEngine(this);
	}
	
	
	
	void onStoneClicked(final Stone stone) {
		if(stone == null) {
			return;
		}
		
		if(stone.isSelected) {
			// The stone is selected, so it is getting deselected.
			
			stone.isSelected = false;
			dispatchSelectionChanged(stone);
			if(selectedStone == stone) {
				selectedStone = null;
			}
			
		} else {
			// The stone is not selected, so it is getting selected.
			
			if(board == null) {
				// Just select it and deselect anything that may be selected.
				
				if(selectedStone != null) {
					selectedStone.isSelected = false;
					dispatchSelectionChanged(selectedStone);
				}
				stone.isSelected = true;
				dispatchSelectionChanged(stone);
				selectedStone = stone;
				
			} else {
				// Select it only if it is not blocked.
				if(!board.isBlocked(stone)) {
					
					if(selectedStone != null && selectedStone.getContent() != null && stone.getContent() != null
							&& selectedStone.getContent().isAssociatedWith(stone.getContent())) {
						// The stones are associated, so remove them ;-)
						
						board.remove(selectedStone);
						board.remove(stone);
						dispatchStoneRemoved(selectedStone);
						dispatchStoneRemoved(stone);
						
						selectedStone.isSelected = false;
						selectedStone = null;
						
						// TODO [undo]
						
					} else {
						// The stones are not associated, so just change the selection.
						
						if(selectedStone != null) {
							selectedStone.isSelected = false;
							dispatchSelectionChanged(selectedStone);
						}
						stone.isSelected = true;
						dispatchSelectionChanged(stone);
						selectedStone = stone;
						
					}
					
				}
			}
			
		}
		
		
	}
	
	
	
	public void addEngineListener(final EngineListener engineListener) {
		engineListeners.add(engineListener);
	}
	
	public void removeEngineListener(final EngineListener engineListener) {
		engineListeners.remove(engineListener);
	}
	
	private void dispatchSelectionChanged(final Stone stone) {
		for(EngineListener boardListener : engineListeners) {
			boardListener.selectionChanged(stone);
		}
	}
	
	private void dispatchStoneAdded(final Stone stone) {
		for(EngineListener boardListener : engineListeners) {
			boardListener.stoneAdded(stone);
		}
	}
	
	private void dispatchStoneRemoved(final Stone stone) {
		for(EngineListener boardListener : engineListeners) {
			boardListener.stoneRemoved(stone);
		}
	}
	
	
	
	public Board getBoard() {
		return board;
	}
	
	
	
}
