package org.aifargonos.games.genericmahjong.data;



abstract class AbstractDataObject implements DataObject {
	
	
	
	@Override
	public StringBuilder prettyPrint(StringBuilder result) {
		if(result == null) {
			result = new StringBuilder();
		}
		return internalPrettyPrint(result);
	}
	
	@Override
	public String prettyPrint() {
		return prettyPrint(null).toString();
	}
	
	abstract StringBuilder internalPrettyPrint(StringBuilder result);
	
	
	
}
