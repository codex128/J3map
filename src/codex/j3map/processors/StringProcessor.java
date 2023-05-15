/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3map.processors;


/**
 *
 * @author gary
 */
public class StringProcessor implements J3mapPropertyProcessor<String> {
	
	@Override
	public String getPropertyIdentifier() {
		return "String";
	}
	@Override
	public String[] createArray(int length) {
		return new String[length];
	}
	@Override
	public Class<String> type() {
		return String.class;
	}
	@Override
	public String process(String str) {
		if (!str.startsWith("\"") || !str.endsWith("\"")) {
			return null;
		}
		String out = "";
		boolean inside = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\"') {
				inside = !inside;
			}
			else if (inside) {
				out += c;
			}
		}
		return out;
	}
	@Override
	public String[] export(String property) {
		return new String[]{"\""+property+"\""};
	}
	
}
