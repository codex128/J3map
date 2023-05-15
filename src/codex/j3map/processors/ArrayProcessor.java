/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3map.processors;

import codex.j3map.ArrayWrapper;
import codex.j3map.J3mapFactory;
import java.util.LinkedList;

/**
 *
 * @author gary
 */
public class ArrayProcessor implements J3mapPropertyProcessor<ArrayWrapper> {

	@Override
	public Class<ArrayWrapper> type() {
		return ArrayWrapper.class;
	}
	@Override
	public ArrayWrapper process(String str) {
		if (!str.startsWith("Array") || !str.endsWith("]")) {
			return null;
		}
		// step 1: seperate raw array elements
		LinkedList<String> elements = new LinkedList<>();
		String generic = "";
		String el = "";
		int brackets = 0;
		boolean inGeneric = false;
		boolean inArray = false;
		boolean inString = false;
		final char open = '(', close = ')';
		final char quote = '"';
		final char seperator = ',';
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == quote) {
				inString = !inString;
			}
			if (!inString) {
				if (brackets == 0) {
					if (c == '[') {
						inArray = true;
						continue;
					}
					else if (c == ']') {
						inArray = false;
						continue;
					}
				}
				if (inArray) {
					if (c == open && brackets++ == 0) {
						continue;
					}
					else if (c == close) {
						brackets--;
					}
				}
				else {
					if (c == '<') {
						inGeneric = true;
						continue;
					}
					else if (c == '>') {
						inGeneric = false;
						continue;
					}
				}
			}
			if (inGeneric) {				
				generic += c;
				continue;
			}
			if (!inArray) {
				continue;
			}
			if (brackets < 0) {
				return null;
			}
			else if (brackets == 0 && !inString && c == seperator) {
				elements.addLast(el.trim());
				el = "";
			}
			else if (brackets > 0) {
				el += c;
			}
		}
		if (generic.isEmpty()) {
			return null;
		}
		if (!el.isEmpty()) {
			elements.addLast(el.trim());
		}
		generic = generic.trim();
		// step 2: find the correct processor
		J3mapPropertyProcessor processor = null;	
		for (J3mapPropertyProcessor p : J3mapFactory.processors) {
			if (p.getPropertyIdentifier() != null &&
					p.getPropertyIdentifier().equals(generic)) {
				processor = p;
				break;
			}
		}
		if (processor == null) {
			throw new NullPointerException("No processor found for \""+generic+"\"");
		}
		// step 3: create array
		Object[] array = processor.createArray(elements.size());
		if (array == null) {
			return null;
		}
		// step 4: parse each element
		int i = 0;
		for (String element : elements) {
			array[i++] = processor.process(element);
		}
		// step 5: return the sparkly new array :)
		return new ArrayWrapper<>(generic, array);
	}
	@Override
	public String[] export(ArrayWrapper property) {
		String out = "Array<"+property.getType()+">[";
		if (property.getArray().length > 0) {
			J3mapPropertyProcessor processor = J3mapFactory.getProcessor(
					property.getArray()[0].getClass());
			for (Object p : property.getArray()) {
				String[] ex = processor.export(processor.type().cast(p));
				String el = "(";
				for (String s : ex) {
					el += s;
				}
				out += el+"),";
			}
		}
		return new String[] {out+"]"};
	}
	@Override
	public String getPropertyIdentifier() {
		return "Array";
	}
	@Override
	public ArrayWrapper[] createArray(int length) {
		return new ArrayWrapper[length];
	}
	
}
