/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3map.processors;

import java.util.LinkedList;

/**
 * Processes arrays of strings in j3map files.
 * 
 * Use ArrayProcessor instead. It is more versatile and supports
 * multidemensional arrays.
 * 
 * @author gary
 */
@Deprecated
public class StringArrayProcessor implements J3mapPropertyProcessor<String[]> {
		
	@Override
	public Class<String[]> type() {
		return String[].class;
	}
	@Override
	public String[] process(String str) {
		if (!str.startsWith("[") || !str.endsWith("]")) {
			return null;
		}
		LinkedList<String> elements = new LinkedList<>();
		String el = null;
		boolean inString = false;
		Character prev = null;
		// consider: ["...","...","...","..."];
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '"' && (prev == null || !prev.equals('\\'))) {
				inString = !inString;
				continue;
			}
			if (inString) {
				if (el == null) el = ""+c;
				else el += c;
				continue;
			}
			if (c == ',') {
				elements.addLast(""+el);
				el = null;
			}
			prev = c;
		}
		if (el != null) {
			elements.addLast(el);
		}
		String[] array = new String[elements.size()];
		int i = 0;
		for (String element : elements) {
			array[i++] = element;
		}
		return array;
	}
	@Override
	public String[] export(String[] property) {
		String out = "[";
		for (String str : property) {
			out += "\""+str+"\",";
		}
		out += "]";
		return new String[]{out};
	}
	@Override
	public String getPropertyIdentifier() {
		return "String[]";
	}
	
}
