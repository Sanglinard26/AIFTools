/*
 * Creation : 15 mars 2018
 */
package aif;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import utils.Utilitaire;

public final class Measure implements Comparable<Measure> {

    private String name;
    private String unit;
    private List<Object> data;
    private boolean wasted;
    private BitSet bitCondition;

    public Measure(String name) {
        this.name = name;
        this.unit = "";
        this.data = new ArrayList<Object>();
        this.wasted = false;
        this.bitCondition = new BitSet();
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

    public final List<Object> getData() {
        return this.data;
    }

    public final void clearCondition() {
        this.bitCondition.clear();
    }

    public final void applyCondition(int... idx) {
        if (idx.length == 1) {
            this.bitCondition.set(idx[0]);
        } else {
            this.bitCondition.set(idx[0], idx[1]);
        }
    }

    public final BitSet getBitCondition() {
        return this.bitCondition;
    }

    public final void setData(List<Object> data) {
        this.data = data;
    }

    public final boolean getWasted() {
        return this.wasted;
    }

    public final double getMinValue() {

        double minValue = Double.POSITIVE_INFINITY;
        Object value;
        double doubleValue;

        for (int i = 0; i < this.data.size(); i++) {
            value = this.data.get(i);
            if (value instanceof Number) {
                minValue = Math.min(minValue, Double.parseDouble(value.toString()));
            } else {
                try {
                    doubleValue = Double.parseDouble(value.toString());
                    if (!Double.isNaN(doubleValue)) {
                        minValue = Math.min(minValue, doubleValue);
                    }
                } catch (NumberFormatException nfe) {
                    return Double.NaN;
                }
            }
        }
        return minValue;
    }

    public final double getMaxValue() {

        double maxValue = Double.NEGATIVE_INFINITY;
        Object value;
        double doubleValue;

        for (int i = 0; i < this.data.size(); i++) {
            value = this.data.get(i);
            if (value instanceof Number) {
                maxValue = Math.max(maxValue, Double.parseDouble(value.toString()));
            } else {
                try {
                    doubleValue = Double.parseDouble(value.toString());
                    if (!Double.isNaN(doubleValue)) {
                        maxValue = Math.max(maxValue, doubleValue);
                    }
                } catch (NumberFormatException nfe) {
                    return Double.NaN;
                }
            }
        }
        return maxValue;
    }

    public final void setWasted(boolean wasted) {
        this.wasted = wasted;
    }

    public final boolean isEmpty() {
        return this.data.isEmpty();
    }

    public final int getNbPoints() {
        return this.data.size();
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object name) {
        return this.name.equals(name.toString());
    }

    @Override
    public int compareTo(Measure measure) {
        return this.name.compareToIgnoreCase(measure.getName());
    }

    public final static Object getStorageObject(String value) {

        int idxComma = value.indexOf(',');

        if (idxComma > -1) {
            value = value.replace(',', '.');
        }

        if (Utilitaire.isNumeric(value)) {

            int idxPoint = value.indexOf('.');
            if (idxPoint > -1) {
                int decimalLength = value.length() - 1 - idxPoint;
                if (decimalLength > 6) {
                    return Double.parseDouble(value);
                }
                return Float.parseFloat(value);
            }
            if (value.length() > 10) {
                return value;
            }

            int i = Integer.parseInt(value);
            if (i <= Byte.MAX_VALUE && i >= Byte.MIN_VALUE) {
                return (byte) i;
            } else if (i <= Short.MAX_VALUE && i >= Short.MIN_VALUE) {
                return (short) i;
            } else {
                return i;
            }

        }
        return value;
    }

}
