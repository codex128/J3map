/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3map;

/**
 *
 * @author gary
 */
public class ArrayWrapper <T> {
	
	String type;
	T[] array;
	boolean multiline = false;
	
	public ArrayWrapper(String type, T[] array) {
		this.type = type;
		this.array = array;
	}
	
	public String getType() {
		return type;
	}
	public T[] getArray() {
		return array;
	}
	public boolean isMultiline() {
		return multiline;
	}
	
}
