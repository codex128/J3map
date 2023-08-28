/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.j3map;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author gary
 */
public class J3mapTemplate {

    private final String name;
    private final ArrayList<Property> properties = new ArrayList<>();

    public J3mapTemplate(String name) {
        this.name = name;
    }
    public J3mapTemplate(String name, Property... properties) {
        this.name = name;
        this.properties.addAll(Arrays.asList(properties));
    }

    public boolean match(J3map map) {
        for (Property p : properties) {
            if (!map.propertiesExist(p)) {
                return false;
            }
        }
        return true;
    }
    public void verify(J3map map) {
        for (Property p : properties) {
            if (!map.propertiesExist(p)) {
                throw new NullPointerException("Missing property: "+p);
            }
        }
    }

}
