/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3map;

/**
 *
 * @author codex
 */
public class Property <T> {
    
    public final String key;
    public final T property;
    
    public Property(String key, T property) {
        this.key = key;
        this.property = property;
    }
    
    public String getKey() {
        return key;
    }
    public T getProperty() {
        return property;
    }
    
    @Override
    public String toString() {
        return "Property(key=\""+key+"\", property="+property+")";
    }
    
}
