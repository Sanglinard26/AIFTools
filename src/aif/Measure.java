/*
 * Creation : 15 mars 2018
 */
package aif;

import java.util.ArrayList;
import java.util.List;

public class Measure {

    private String name;
    private String unit;
    private List<String> data;

    public Measure(String name) {
        this.name = name;
        this.unit = "";
        this.data = new ArrayList<String>();
    }

    public final String getName() {
        return this.name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getUnit() {
        return this.unit;
    }

    public final void setUnit(String unit) {
        this.unit = unit;
    }

    public final List<String> getData() {
        return this.data;
    }

    public final void setData(List<String> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public boolean equals(Object name) {
    	return this.name.equals(name.toString());
    }

}