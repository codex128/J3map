/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codex.j3map;

import codex.j3map.processors.J3mapPropertyProcessor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author codex
 */
public class J3map implements Cloneable {
	
	private static final Logger LOG = Logger.getLogger(J3map.class.getName());
	
	String src;
	String name;
	HashMap<String, Property> properties = new HashMap<>();
	LinkedList<Property> proplist = new LinkedList<>();
	J3map parent;
	
	
	public J3map() {}
	public J3map(File file) {
		try {
			src = file.getAbsolutePath();
			readFile(file);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "File does not exist", ex);
		}
	}
	public J3map(InputStream stream) throws IOException {
		readInputStream(stream);
	}
	protected J3map(J3map parent, String name) {
		this.parent = parent;
		this.name = name;
	}
	private J3map(HashMap<String, Property> properties, LinkedList<Property> proplist) {
		this.properties.putAll(properties);
		this.proplist.addAll(proplist);
	}
	
	
	// static methods
	/**
	 * Casts the asserted Object to a J3map.
	 * 
	 * Useful for more easily casting an asset to J3map for jMonkeyEngine.
	 * 
	 * @throws NullPointerException if the asserted Object is not a J3map.
	 * @param map
	 * @return 
	 */
	public static J3map openJ3map(Object map) {
		if (map instanceof J3map) return (J3map)map;
		else throw new IllegalArgumentException(
				"Asserted Object not assignable from J3map");
	}
	/**
	 * Creates a property checker.
	 * @param key
	 * @param type
	 * @return 
	 */
	public static PropertyChecker createChecker(String key, Class<?> type) {
		return new PropertyChecker(key, type);
	}
	
	// parsing
	/**
	 * Reads the asserted file.
	 * @param file
	 * @throws IOException if file not found.
	 */
	public void readFile(File file) throws IOException {
		J3mapFactory.applyInputStreamToJ3map(this, new FileInputStream(file));
	}
	/**
	 * Reads the asserted <code>InputStream</code>.
	 * This is the main method used for reading a j3map file.
	 * @param stream 
	 * @throws java.io.IOException 
	 */
	public void readInputStream(InputStream stream) throws IOException {
		J3mapFactory.applyInputStreamToJ3map(this, stream);
	}
	
	// getting properties
	/**
	 * Get the Object stored at the key.
	 * @param key
	 * @return 
	 */
	public Object getObject(String key) {
		Property p = properties.get(key);
		if (p == null) return null;
		else return p.property;
	}
	/**
	 * Returns the Object stored at the key.
	 * If the object does not exist or is null, returns the default value.
	 * @param key
	 * @param defaultvalue
	 * @return 
	 */
	public Object getObject(String key, Object defaultvalue) {
		Object property = getObject(key);
		if (property != null) return property;
		else return defaultvalue;
	}
	/**
	 * Get the property stored at the key.
	 * The property, if an instance of <code>classtype</code>, is cast
	 * to <code>classtype</code> then returned. Otherwise, returns null.
	 * <p>
	 * Can be used to get properties processed by custom processors, but does
	 * not use <code>J3mapPropertyProcessor.get(...)</code> to cast the property.
	 * For more reliability and precision, use <code>getProcessor(...).get(...)</code>.
	 * @param <T>
	 * @param classtype
	 * @param key
	 * @return 
	 */
	public <T> T getProperty(Class<T> classtype, String key) {
//		J3mapPropertyProcessor processor = J3mapFactory.pmap.get(classtype);
//		if (processor == null) return null;
//		else return (T)processor.get(this, key);
		Object property = getObject(key);
		if (property != null && classtype.isAssignableFrom(property.getClass())) {
			return (T)property;
		}
		else {
			return null;
		}
	}
	/**
	 * Get the property stored at the key.The property, if an instance of <code>classtype</code>, is cast
 to <code>classtype</code> then returned.
	 * Otherwise, returns null.
 <p>
	 * Can be used to get properties processed by custom processors, but does
	 * not use <code>J3mapPropertyProcessor.get(...)</code> to cast the property.
	 * For more reliability and precision, use <code>getProcessor(...).get(...)</code>.
	 * @param <T>
	 * @param classtype
	 * @param key
	 * @param defaultvalue
	 * @return if property found, returns the found property,
	 * otherwise returns the default value.
	 */
	public <T> T getProperty(Class<T> classtype, String key, T defaultvalue) {
//		J3mapPropertyProcessor processor = J3mapFactory.pmap.get(classtype);
//		if (processor == null) return defaultvalue;
//		else return (T)processor.get(this, key);
		T property = getProperty(classtype, key);
		if (property != null) return property;
		else return defaultvalue;
	}
	/**
	 * Get the J3map stored at the key.
	 * @param key
	 * @return 
	 */
	public J3map getJ3map(String key) {
		Object obj = getObject(key);
		if (obj != null && obj instanceof J3map) return (J3map)obj;
		else return null;
	}
	/**
	 * Get the ArrayWrapper stored at the key.
	 * @param key
	 * @return 
	 */
	public ArrayWrapper getArrayWrapper(String key) {
		return getProperty(ArrayWrapper.class, key);
	}
	/**
	 * Get the array stored at the key.
	 * Use this method (or similar methods) to access arrays since
	 * arrays wrapped in <code>ArrayWrappers</code>.
	 * @param <T>
	 * @param classtype
	 * @param key
	 * @return 
	 */
	public <T> T getArray(Class<T> classtype, String key) {
		Object property = getObject(key);
		if (property != null && property instanceof ArrayWrapper) {
			Object[] array = ((ArrayWrapper)property).getArray();
			return (T)array;
		}
		return null;
	}
	/**
	 * Get the array stored at the key.
	 * Use this method (or similar methods) to access arrays since
	 * arrays wrapped in <code>ArrayWrappers</code>.
	 * @param <T>
	 * @param classtype
	 * @param key
	 * @param defaultvalue
	 * @return 
	 */
	public <T> T getArray(Class<T> classtype, String key, T defaultvalue) {
		Object property = getObject(key);
		if (property != null && property instanceof ArrayWrapper) {
			Object[] array = ((ArrayWrapper)property).getArray();
			return (T)array;
		}
		return defaultvalue;
	}
	/**
	 * Get the Float stored at the key.
	 * If the object at the key is null or not a float, returns null.
	 * @param key
	 * @return 
	 */
	public Float getFloat(String key) {
		Object obj = getObject(key);
		if (obj != null && obj instanceof Float) return ((Float)obj).floatValue();
		else return null;
	}
	/**
	 * Returns the float stored at the key.
	 * If the object does not exist or is null or is not a float, returns the default value.
	 * @param key
	 * @param defaultvalue
	 * @return 
	 */
	public float getFloat(String key, float defaultvalue) {
		Float f = getFloat(key);
		if (f != null) return f;
		else return defaultvalue;
	}
	/**
	 * Get the Integer stored at the key.
	 * If the object at the key is null or not a integer, returns null.
	 * @param key
	 * @return 
	 */
	public Integer getInteger(String key) {
		Object obj = getObject(key);
		if (obj != null && obj instanceof Integer) return ((Integer)obj).intValue();
		else return null;
	}
	/**
	 * Returns the integer stored at the key.
	 * If the object does not exist or is null or is not an integer, returns the default value.
	 * @param key
	 * @param defaultvalue
	 * @return 
	 */
	public int getInteger(String key, int defaultvalue) {
		Integer i = getInteger(key);
		if (i != null) return i;
		else return defaultvalue;
	}
	/**
	 * Get the String stored at the key.
	 * If the object at the key is null or not a string, returns null.
	 * @param key
	 * @return 
	 */
	public String getString(String key) {
		Object obj = getObject(key);
		if (obj != null && obj instanceof String) return ((String)obj)+"";
		else return null;
	}	
	/**
	 * Returns the String stored at the key.
	 * If the object does not exist or is null or is not a String, returns the default value.
	 * @param key
	 * @param defaultvalue
	 * @return 
	 */
	public String getString(String key, String defaultvalue) {
		String s = getString(key);
		if (s != null) return s;
		else return defaultvalue;
	}
	/**
	 * Get the Boolean stored at the key.
	 * If the object at the key is null or not a boolean, returns null.
	 * @param key
	 * @return 
	 */
	public Boolean getBoolean(String key) {
		Object obj = getObject(key);
		if (obj != null && obj instanceof Boolean) return ((Boolean)obj).booleanValue();
		else return null;
	}	
	/**
	 * Returns the boolean stored at the key.
	 * If the object does not exist or is null or is not a boolean, returns the default value.
	 * @param key
	 * @param defaultvalue
	 * @return 
	 */
	public boolean getBoolean(String key, boolean defaultvalue) {
		Boolean b = getBoolean(key);
		if (b != null) return b;
		else return defaultvalue;
	}
	/**
	 * Get the String[] stored at the key.
	 * If the object at the key is null or not a String[], returns null.
	 * @param key
	 * @return 
	 */
	public String[] getStringArray(String key) {
		Object obj = getObject(key);
		if (obj != null && obj instanceof String[]) return ((String[])obj).clone();
		else return null;
	}	
	/**
	 * Returns the String[] stored at the key.
	 * If the object does not exist or is null or is not a String[], returns the default value.
	 * @param key
	 * @param defaultvalue
	 * @return 
	 */
	public String[] getStringArray(String key, String... defaultvalue) {
		String[] s = getStringArray(key);
		if (s != null) return s;
		else return defaultvalue;
	}
	
	// storing
	/**
	 * Stores the asserted property at the key if no property is
	 * currently stored at the key.
	 * @param key
	 * @param property
	 * @return operation successful
	 */
	public boolean store(String key, Object property) {
		Property p = new Property(key, property);
		if (properties.putIfAbsent(key, p) == null) {
			proplist.addLast(p);
			if (property instanceof J3map) {
				((J3map)property).setParent(this);
			}
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * Forcibly overwrites the property stored at the asserted key.
	 * @param key
	 * @param property
	 * @return the object previously associated with the asserted key (if any)
	 */
	public Object overwrite(String key, Object property) {
		Property prev = properties.get(key);
		Property np = new Property(key, property);
		properties.put(key, np);
		proplist.addLast(np);
		if (prev != null) {
			proplist.remove(prev);
			if (prev.property instanceof J3map) {
				((J3map)prev.property).setParent(null);
			}
		}
		if (property instanceof J3map) {
			((J3map)property).setParent(this);
		}
		return prev;
	}
	/**
	 * Removes the property stored at the key from this map.
	 * @param key
	 * @return the property stored at the key.
	 */
	public Object delete(String key) {
		Property remove = properties.remove(key);
		if (remove != null) {
			proplist.remove(remove);
			if (remove.property instanceof J3map) {
				((J3map)remove.property).setParent(null);
			}
			return remove.property;
		}
		return null;
	}
	/**
	 * Replaces the old key with a new key.
	 * Involves deleting the old property and immediately restore it
	 * at the new key.
	 * @param oldkey
	 * @param newkey 
	 */
	public void replaceKey(String oldkey, String newkey) {
		store(newkey, delete(oldkey));
	}
	/**
	 * Clears this map of all properties.
	 */
	public void clear() {
		forEachType(J3map.class, (property) -> {
			property.setParent(null);
		});
		properties.clear();
		proplist.clear();
	}
	
	// property testing
	/**
	 * Checks if the map contains the asserted key.
	 * @param key
	 * @return 
	 */
	public boolean propertyExists(String key) {
		return properties.containsKey(key);
	}
	/**
	 * Checks if all asserted property mappings exist.
	 * @param mappings
	 * @return true if all mappings exist
	 */
	public boolean propertiesExist(String... mappings) {
		for (String str : mappings) {
			if (!properties.containsKey(str)) return false;
		}
		return true;
	}
	/**
	 * Checks if all the properties represented by each asserted PropertyChecker
	 * exists in this j3map.
	 * @param properties
	 * @return true, if all properties exist.
	 */
	public boolean propertiesExist(PropertyChecker... properties) {
		for (PropertyChecker checker : properties) {
			Object prop = getObject(checker.key);
			if (prop == null || !prop.getClass().isAssignableFrom(checker.type)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Requires that all asserted property mappings exist.
	 * A more severe version of <code>propertiesExist(...)</code> in which
	 * an exception is thrown if a mapping does not exist.
	 * @throws MissingPropertyException if property mapping does not exist.
	 * @param mappings 
	 */
	public void requireProperties(String... mappings) {
		for (String str : mappings) if (!properties.containsKey(str)) {
			LOG.log(Level.SEVERE, null, new MissingPropertyException(str));
			break;
		}
	}
	/**
	 * Requires that this j3map contain the data provided.
	 * @param properties 
	 */
	public void requireProperties(PropertyChecker... properties) {
		for (PropertyChecker p : properties) {
			Object prop = getObject(p.key);
			if (prop == null) {
				LOG.log(Level.SEVERE, null, new MissingPropertyException(p.key));
				break;
			}
			if (!prop.getClass().isAssignableFrom(p.type)) {
				LOG.log(Level.SEVERE, "Class type does not match",
						new MissingPropertyException(p.key));
				break;
			}
		}
	}
	/**
	 * Checks if the property at the asserted key is assignable to
	 * the asserted class type.
	 * @param type
	 * @param key
	 * @return 
	 */
	public boolean typesMatch(Class<?> type, String key) {
		return type.isAssignableFrom(getObject(key).getClass());
	}	
	/**
	 * Executes the Consumer if a property of the asserted type and key exists.
	 * @param <T>
	 * @param key
	 * @param type
	 * @param command 
	 */
	public <T> void onPropertyExists(String key, Class<T> type, Consumer<T> command) {
		if (propertiesExist(J3map.createChecker(key, type))) {
			command.accept(getProperty(type, key));
		}
	}
	/**
	 * Executes the command.
	 * If the property at the key exists, passes the property into the command.
	 * Otherwise, passes the default value into the command.
	 * @param <T>
	 * @param key
	 * @param type
	 * @param defaultvalue
	 * @param command 
	 */
	public <T> void commandProperty(String key, Class<T> type, T defaultvalue, Consumer<T> command) {
		T value;
		if (propertiesExist(J3map.createChecker(key, type))) {
			value = getProperty(type, key);
		}
		else {
			value = defaultvalue;
		}
		command.accept(value);
	}
	/**
	 * Executes the Function if a property of the asserted type and key exists.
	 * @param <T> type of property
	 * @param <R> type of return value
	 * @param key
	 * @param type
	 * @param returntype
	 * @param command
	 * @return 
	 */
	public <T, R> R onPropertyExists(String key, Class<T> type,
			Class<R> returntype, Function<T, R> command) {
		if (propertiesExist(J3map.createChecker(key, type))) {
			return command.apply(getProperty(type, key));
		}
		return null;
	}
	/**
	 * Executes the Function if a property of the asserted type and key exists.
	 * @param <T> type of property
	 * @param <R> type of return value
	 * @param key
	 * @param type
	 * @param returntype
	 * @param defaultvalue
	 * @param command
	 * @return 
	 */
	public <T, R> R onPropertyExists(String key, Class<T> type,
			Class<R> returntype, R defaultvalue, Function<T, R> command) {
		if (propertiesExist(J3map.createChecker(key, type))) {
			return command.apply(getProperty(type, key));
		}
		return defaultvalue;
	}
	
	// setters
	/**
	 * Artificially set the source path if the source path is null.
	 * @param src 
	 */
	public void setSourcePath(String src) {
		if (this.src == null) this.src = src;
	}
	/**
	 * Sets the name of this j3map.
	 * If this j3map is stored by another j3map, then this will also change where
	 * this j3map is stored in the parent j3map to the new name.
	 * @param name 
	 */
	public void setName(String name) {
		assert name != null;
		if (parent != null) {
			assert this.name != null;
			parent.replaceKey(this.name, name);
		}
		this.name = name;
	}
	/**
	 * Set the parent of this j3map.
	 * @param map 
	 */
	private void setParent(J3map map) {
		parent = map;
	}
	
	// getters
	/**
	 * Get the path of the file this j3map was loaded from.
	 * Does not work if this j3map was loaded from an InputStream or
	 * created by another j3map as an inner j3map.
	 * Use <code>setSourcePath(...)</code> to artificially set the source path
	 * if the path is null.
	 * @return 
	 */
	public String getSourcePath() {
		return src;
	}
	/**
	 * Get a cloned map containing all properties.
	 * <strong><em>Do not</em></strong> edit properties using this.
	 * @return 
	 */
	public HashMap<String, Object> getProperties() {
		HashMap<String, Object> clone = new HashMap<>();
		clone.putAll(properties);
		return clone;
	}
	/**
	 * Gets a list of all properties in the order that they were parsed.
	 * <strong><em>Do not</em></strong> edit properties using this.
	 * @return 
	 */
	public LinkedList<Property> getOrderedPropertyList() {
		return proplist;
	}
	/**
	 * Get the parent <code>J3map</code> of this map.
	 * Returns null if this map has no parent.
	 * @return 
	 */
	public J3map getParent() {
		return parent;
	}
	
	// iterating
	/**
	 * Execute the BiConsumer for each property stored in this j3map.
	 * Is not parse order sensitive.
	 * @param foreach 
	 */
	public void forEach(BiConsumer<String, Object> foreach) {
		properties.forEach(foreach);
	}
	/**
	 * Execute the BiConsumer for each property of the asserted
	 * class type in this j3map.
	 * Is not parse order sensitive.
	 * @param <T>
	 * @param classtype
	 * @param foreach 
	 */
	public <T> void forEachType(Class<T> classtype, BiConsumer<String, T> foreach) {
		properties.forEach((key, property) -> {
			if (classtype.isAssignableFrom(property.getClass())) {
				foreach.accept(key, (T)property);
			}
		});
	}
	/**
	 * Execute the Consumer for each property stored in this j3map.
	 * Is parse order sensitive.
	 * @param foreach 
	 */
	public void forEach(Consumer<Object> foreach) {
		proplist.forEach(foreach);
	}
	/**
	 * Execute the Consumer for each property of the asserted
	 * class type in this j3map.
	 * Is parse order sensitive.
	 * @param <T>
	 * @param classtype
	 * @param foreach 
	 */
	public <T> void forEachType(Class<T> classtype, Consumer<T> foreach) {
		for (Property property : proplist) {
			if (classtype.isAssignableFrom(property.property.getClass())) {
				foreach.accept((T)property.property);
			}
		}
	}
	
	// printing
	/**
	 * Prints out the list of properties and keys.
	 * Includes properties and keys from all inner maps.
	 */
	public void printMap() {
		printMap("", false, false);
	}
	/**
	 * Prints out the list of properties and keys.
	 * Only prints properties that this map stores directly.
	 */
	public void printLocalMap() {
		printMap("", true, false);
	}
	/**
	 * Prints out a clean list of properties and keys.
	 * Includes properties and keys from all inner maps.
	 */
	public void printCleanMap() {
		printMap("", false, true);
	}
	/**
	 * Prints out a clean list of properties and keys.
	 * Only prints properties that this map stores directly.
	 */
	public void printCleanLocalMap() {
		printMap("", true, true);
	}
	/**
	 * Prints this map to the console.
	 * @param tabs represents the number of tabs; useful for child maps
	 * @param local print only this map, no children
	 * @param clean do not display property types
	 */
	private void printMap(String tabs, boolean local, boolean clean) {
		System.out.println(tabs+toString()+": {");
		properties.forEach((String key, Object property) -> {
			if (property != null) {
				if (!local && property instanceof J3map) {
					((J3map)property).printMap(tabs+tab(), local, clean);
					return;
				}
				String s = (clean ? "" : property.getClass().getSimpleName()+"@");
				System.out.println(tabs+tab()+s+key+": "+property.toString());
			}
		});
		System.out.println(tabs+"}");
	}
	/**
	 * Represents one tabs.
	 * @return 
	 */
	private String tab() {
		return "  ";
	}
	/**
	 * Prints out the classes supported by J3map.
	 */
	public void printSupportedTypes() {
		throw new UnsupportedOperationException();
	}
	
	// Object overrides
	/**
	 * Creates a clone of this j3map.
	 * The properties themselves are not cloned.
	 * @return 
	 */
	@Override
	public J3map clone() {
		return new J3map(properties, proplist);
	}
	@Override
	public String toString() {
		return (name == null ? super.toString() : "J3map:"+name);
	}	
	
	// exporting
	/**
	 * Write to the source file.
	 * @param file
	 * @throws java.io.IOException
	 */
	public void export(File file) throws IOException {
		if (file.exists()) file.delete();
		file.createNewFile();
		try (FileWriter writer = new FileWriter(file)) {
			writeProperties(writer, "");
		}
	}
	/**
	 * Write to the source file.
	 * @param path
	 * @throws java.io.IOException
	 */
	public void export(String path) throws IOException {
		if (!path.endsWith(".j3map"))
			throw new IllegalArgumentException("File name must end with \".j3map\"");
		export(new File(path));
	}
	private void writeProperties(FileWriter writer, String tabs) {
		for (Property property : proplist) {
			try {
				writeProperty(property, writer, tabs);
			}
			catch (IOException ex) {
				LOG.log(Level.SEVERE, null, ex);
			}
		}
	}
	private void writeProperty(Property property, FileWriter writer, 
			String tabs) throws IOException {
		if (property == null) return;
		//writer.write(tabs+property.key+":");
		if (property.property instanceof J3map) {
			writer.write(tabs+property.key+": {\n");
			((J3map)property.property).writeProperties(writer, tabs+"\t");
			writer.write(tabs+"}\n");
			return;
		}
		J3mapPropertyProcessor processor = J3mapFactory.pmap.get(property.property.getClass());
		if (processor != null) {
			String[] out = processor.export(processor.type().cast(property.property));
			if (out != null) {
				int index = 0;
				writer.write(tabs+property.key+": ");
				for (String str : out) {
					writer.write(str+(++index == out.length ? ";" : "")+"\n");
				}
				if (out.length == 0) {
					throw new NullPointerException("Processor must export data");
				}
			}
			else {
				writer.write("// [ERROR]: null (key:"+property.key+")\n");
			}
			return;
		}
		if (property.property instanceof ArrayWrapper) {
			ArrayWrapper array = (ArrayWrapper)property.property;
			if (array.getArray().length > 0) {
				J3mapPropertyProcessor jpp = J3mapFactory.pmap.get(LOG);
			}
		}
		System.out.println("property type: "+property.property);
		writer.write("// [ERROR]: unsupported (key:"+property.key+") "
				+"(type:"+property.property.getClass().getName()+")\n");
	}

	
	public static class Property <T> {
		private String key;
		private T property;
		Property(String key, T property) {
			this.key = key;
			this.property = property;
		}
		public String getKey() {
			return key;
		}
		public T getProperty() {
			return property;
		}
	}
	public static class PropertyChecker <T> {
		String key;
		Class<T> type;
		PropertyChecker(String key, Class<T> type) {
			this.key = key;
			this.type = type;
		}
	}
	private static class MissingPropertyException extends NullPointerException {
		public MissingPropertyException(String error) {
			super(error);
		}
	}
	
}
