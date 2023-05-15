/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3map.processors;

import codex.j3map.Numbers;

/**
 *
 * @author gary
 */
public class IntegerProcessor implements J3mapPropertyProcessor<Integer> {

	@Override
	public String getPropertyIdentifier() {
		return "Integer";
	}
	@Override
	public Integer[] createArray(int length) {
		return new Integer[length];
	}
	@Override
	public Class<Integer> type() {
		return Integer.class;
	}
	@Override
	public Integer process(String str) {
		int out = 0, sign = 1;
		int length = str.length();
		for (int i = 0; i < length; i++) {
			char c = str.charAt(i);
			if (c == '-') {
				sign = -1;
				continue;
			}
			Integer number = Numbers.NUMERALS.get(c);
			if (number == null) return null;
			out += number*(int)Math.pow(10, length-i-1);
		}
		return out*sign;
	}
	
}
