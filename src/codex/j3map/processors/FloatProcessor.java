/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3map.processors;

import codex.j3map.Numbers;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author gary
 */
public class FloatProcessor implements J3mapPropertyProcessor<Float> {
	
	@Override
	public String getPropertyIdentifier() {
		return "Float";
	}
	@Override
	public Float[] createArray(int length) {
		return new Float[length];
	}
	@Override
	public Class<Float> type() {
		return Float.class;
	}
	@Override
	public Float process(String str) {
		if (!str.endsWith("f")) return null;
		return Float.parseFloat(str);
	}
	@Override
	public String[] export(Float property) {
		return new String[]{property.toString()+"f"};
	}
	
}
