/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3map.processors;


/**
 *
 * @author gary
 */
public class BooleanProcessor implements J3mapPropertyProcessor<Boolean> {
	
	public static final String TRUE = "true", FALSE = "false";
	
	@Override
	public String getPropertyIdentifier() {
		return "Boolean";
	}
	@Override
	public Boolean[] createArray(int length) {
		return new Boolean[length];
	}
	@Override
	public Class<Boolean> type() {
		return Boolean.class;
	}
	@Override
	public Boolean process(String str) {
		if (str.equals(TRUE)) return true;
		else if (str.equals(FALSE)) return false;
		else return null;
	}
	
}
