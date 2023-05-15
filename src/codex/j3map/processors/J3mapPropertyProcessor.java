/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codex.j3map.processors;

import codex.j3map.J3map;


/**
 * Processes properties for J3map.
 * 
 * In general, the fewer number of processors registered with J3map,
 * the less intensive loading will be.
 * 
 * For development:
 * <ul>
 * <li>Provide a javadoc explaination of how to write your property in a j3map file.</li>
 * <li>Update the exporter after updating the processor, and vise versa.
 * Outdated processors/exporters can lead to many frustrating problems!</li>
 * </ul>
 * 
 * @author gary
 * @param <T> the class type this processor processes
 */
public interface J3mapPropertyProcessor <T> {
	
	/**
	 * @return the class that this <code>J3mapProcessor</code> is used to process.
	 */
	public abstract Class<T> type();
	
	/**
	 * Turns the data given into a value.
	 * 
	 * <strong>Implementation:</strong>
	 * Determine if the string can be processed by this processor
	 * before using <code>J3map.getNextLine(...)</code>, otherwise the
	 * buffer will be messed up for proceeding processors.
	 * If after calling <code>J3map.getNextLine(...)</code> it is determined
	 * that the string cannot be processed by this processor, throw a 
	 * <code>J3map.SyntaxException(...)</code>.
	 * 
	 * @param str line currently being processed
	 * @return parsed data
	 */
	public abstract T process(String str);
	
	public default void setPropertyValue(T property, String name, Object value) {
		
	}
	
	/**
	 * Converts the given object to a string used for j3map file writing.
	 * @param property
	 * @return 
	 */
	public default String[] export(T property) {
		return new String[]{property.toString()};
	}	
	
	/**
	 * Returns a String explicitely identifying properties.
	 * Is not usually required for parsing j3map files,
	 * but is required to support arrays.
	 * @return 
	 */
	public default String getPropertyIdentifier() {
		return null;
	}
		
	/**
	 * Get the object stored at the key and cast it the this processor's type.
	 * if this object does not this processor's type, returns null.
	 * @param map
	 * @param key
	 * @return 
	 */
	public default Object get(J3map map, String key) {
		return map.getObject(key);
	}
	
	/**
	 * Create an array of the given length.
	 * Returns null if this processor does not support arrays (default).
	 * @param length
	 * @return 
	 */
	public default T[] createArray(int length) {
		return null;
	}
	
}
