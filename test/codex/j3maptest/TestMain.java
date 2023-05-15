/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3maptest;

import codex.j3map.J3map;
import codex.j3map.J3mapFactory;
import codex.j3map.processors.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gary
 */
public class TestMain {
	
	public static void main(String[] args) {
		J3mapFactory.registerAllProcessors(
				//ArrayProcessor.class,
				BooleanProcessor.class,
				StringProcessor.class,
				IntegerProcessor.class,
				StringArrayProcessor.class,
				FloatProcessor.class);
		File source = new File(System.getProperty("user.home")
				+"/Desktop/testmap.j3map");
		J3map map = new J3map(source);
		String[] array = map.getStringArray("stringarray");
		for (String s : array) {
			System.out.println(s);
		}
		String[] newarray = {"i", "love", "coding"};
		map.overwrite("stringarray", newarray);
		map.overwrite("t", map.getInteger("t")+1);
		map.overwrite("s", 10);
		try {
			map.export(source);
		} catch (IOException ex) {}
	}
	
}
