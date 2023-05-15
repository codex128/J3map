/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3map;

import codex.j3map.processors.J3mapPropertyProcessor;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author codex
 */
public class J3mapFactory implements AssetLoader {
	
	private static final Logger LOG = Logger.getLogger(J3mapFactory.class.getName());	
	public static final LinkedList<J3mapPropertyProcessor> processors = new LinkedList<>();
	public static final HashMap<Class, J3mapPropertyProcessor> pmap = new HashMap<>();
	
	/**
	 * Registers the property processor with J3map.
	 * @param processor 
	 */
	public static void registerProcessor(J3mapPropertyProcessor processor) {
		processors.add(processor);
		Object p = pmap.put(processor.type(), processor);
		if (p != null) {
			LOG.log(Level.WARNING, null, new DuplicateProcessorException(
					"Duplicate processor for class "+processor.type().getName()));
		}
	}
	/**
	 * Registers a property processor with J3map.
	 * @param classtype 
	 */
	public static void registerProcessor(Class<? extends J3mapPropertyProcessor> classtype) {
		try {
			registerProcessor(classtype.getConstructor().newInstance());
		} catch (InvocationTargetException | NoSuchMethodException |
				InstantiationException | IllegalAccessException ex) {
			LOG.log(Level.SEVERE, "Failed to create instance", ex);
		}
	}
	/**
	 * Register all the asserted property processors with J3map.
	 * @param classes 
	 */
	public static void registerAllProcessors(Class<? extends J3mapPropertyProcessor>... classes) {
		for (Class<? extends J3mapPropertyProcessor> classtype : classes) {
			registerProcessor(classtype);
		}
	}
	/**
	 * Returns true if the provided class type is supported by J3map.
	 * Includes custom processors.
	 * @param classtype
	 * @return 
	 */
	public static boolean classTypeSupported(Class<?> classtype) {
		for (J3mapPropertyProcessor processor : processors) {
			if (processor.type().isAssignableFrom(classtype)) return true;
		}
		return false;
	}
	/**
	 * Execute the consumer for each processor J3map has.
	 * @param foreach 
	 */
	public static void forEachProcessor(Consumer<J3mapPropertyProcessor> foreach) {
		for (J3mapPropertyProcessor processor : processors) {
			foreach.accept(processor);
		}
	}	
	/**
	 * Get the first custom processor which matches the class type.
	 * Returns null if no processor which matches is found.
	 * @param classtype
	 * @return 
	 */
	public static J3mapPropertyProcessor getProcessor(Class<?> classtype) {
		for (J3mapPropertyProcessor processor : processors) {
			if (processor.type().isAssignableFrom(classtype)) {
				return processor;
			}
		}
		return null;
	}
	
	/**
	 * Reads the asserted <code>InputStream</code> and applies it to the asserted j3map.
	 * This is the main method used for reading a j3map file.
	 * @param map
	 * @param stream 
	 * @throws java.io.IOException 
	 */
	public static void applyInputStreamToJ3map(J3map map, InputStream stream) throws IOException {
		//System.out.println("--- Start Parsing J3map File ---");
		J3map building = map;
		boolean comment = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line, key = null, str = "";
		while ((line = getNextLine(br)) != null) {
			if (line.isEmpty()) continue;
			boolean[] com = processComment(line, comment);
			if (com[0] | (comment = com[1])) continue;
			if (key == null) {
				// end an inner j3map and move to building its parent
				if (line.equals("}")) {
					if (building.parent != null) building = building.parent;
					continue;
				}
				// get key and object seperated  by ':'
				String[] strs = line.trim().split(":", 2);
				if (strs.length != 2) continue;
				key = strs[0].trim();
				if (key.isEmpty()) continue;
				String value = strs[1].trim();
				// process inner j3map
				J3map inner = processInnerJ3map(key, value, building);
				if (inner != null) {
					building = inner;
					key = null;
					continue;
				}
				str += value;
			}
			else {
				str += line;
			}
			if (!line.endsWith(";")) continue;
			// convert the property in string form to something else
			for (J3mapPropertyProcessor processor : processors) {
				Object property = processor.process(
						str.substring(0, str.length()-1).trim());
				if (property != null) {
					//System.out.println("J3mapFactory: storing property in "+building);
					if (!building.store(key, property)) {
						LOG.log(Level.WARNING, null, new DuplicateKeyException(key));
					}
					else if (property instanceof J3map) {
						((J3map)property).parent = building;
					}
					break;
				}
			}
			// reset the key
			key = null;
			str = "";
		}
		// log syntax warnings
		if (building != map) {
			LOG.log(Level.WARNING, null,
					new SyntaxException("Inner J3map not closed!"));
		}
		if (comment) {
			LOG.log(Level.WARNING, null,
					new SyntaxException("Comment not closed!"));
		}
		if (key != null) {
			LOG.log(Level.WARNING, null, 
					new SyntaxException("Property not closed: "+key));
		}
		comment = false;
	}
	
	private static String getNextLine(BufferedReader br) throws IOException {
		String line = br.readLine();
		if (line != null) return line.trim();
		else return null;
	}
	private static J3map processInnerJ3map(String key, String value, J3map building) {
		if (!value.equals("{")) return null;		
		J3map map = new J3map(building, key);
		building.store(key, map);
		//System.out.println("J3mapFactory: innerJ3map="+map);
		return map;
	}
	/**
	 * Returns a boolean array of length 2 representing comment state.
	 * The first element indicates if the input string is inside a comment.
	 * The second element indicates if the comment continues to the next line.
	 * @param str current line
	 * @return array representing comment state
	 */
	private static boolean[] processComment(String str, boolean inComment) {
		if (str.startsWith("/*")) {
			return new boolean[]{true, true};
		}
		else if (str.startsWith("//")) {
			return new boolean[]{true, inComment};
		}
		else if (str.endsWith("*/")) {
			return new boolean[]{inComment, false};
		}
		else {
			return new boolean[]{false, inComment};
		}
	}

	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		return new J3map(assetInfo.openStream());
	}
	
	public static class SyntaxException extends NullPointerException {
		public SyntaxException(String ex) {
			super(ex);
		}
	}
	private static class DuplicateProcessorException extends NullPointerException {
		DuplicateProcessorException(String ex) {
			super(ex);
		}
	}
	private static class DuplicateKeyException extends NullPointerException {
		DuplicateKeyException(String ex) {
			super(ex);
		}
	}
	
}
