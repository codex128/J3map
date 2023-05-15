/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3map;

import java.util.ArrayList;

/**
 *
 * @author gary
 */
public class J3mapTemplate {
	
	String name;
	ArrayList<Property> properties = new ArrayList<>();
	
	
	public J3mapTemplate(String name) {
		
	}
	
	public boolean match(J3map map) {
		for (Property p : properties) {
			if (!map.propertiesExist(J3map.createChecker(p.name, p.type))) {
				return false;
			}
		}
		return true;
	}
	
	private static class Property {
		String name;
		Class type;
		Property(String name, Class type) {
			this.name = name;
			this.type = type;
		}
	}
	
}
