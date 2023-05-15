/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.j3map;

/**
 *
 * @author gary
 */
public interface J3mapKeywordProcessor {
	
	/**
	 * Returns all keywords this is triggered on.
	 * @return 
	 */
	public abstract String[] keywords();
	
	/**
	 * 
	 * @param map
	 * @param line
	 * @return true if this processor is complete
	 */
	public default boolean line(J3map map, String line) {
		return true;
	}
	
}
