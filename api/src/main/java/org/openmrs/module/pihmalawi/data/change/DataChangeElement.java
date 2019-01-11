package org.openmrs.module.pihmalawi.data.change;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.openmrs.util.OpenmrsUtil;

/**
 * Represents a data change to a property, with before and after values
 */
public class DataChangeElement {

    private String propertyName;
    private Object valueBefore;
    private Object valueAfter;

    public DataChangeElement(String propertyName, Object valueBefore, Object valueAfter) {
        this.propertyName = propertyName;
        this.valueBefore = valueBefore;
        this.valueAfter = valueAfter;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder b = new HashCodeBuilder(17, 37);
        b.append(propertyName).append(valueBefore).append(valueAfter);
        return b.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataChangeElement) {
            DataChangeElement that = (DataChangeElement) obj;
            if (OpenmrsUtil.nullSafeEquals(this.getPropertyName(), that.getPropertyName())) {
                if (OpenmrsUtil.nullSafeEquals(this.getValueBefore(), that.getValueBefore())) {
                    if (OpenmrsUtil.nullSafeEquals(this.getValueAfter(), that.getValueAfter())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return propertyName + ": " + valueBefore + " -> " + valueAfter;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getValueBefore() {
        return valueBefore;
    }

    public void setValueBefore(Object valueBefore) {
        this.valueBefore = valueBefore;
    }

    public Object getValueAfter() {
        return valueAfter;
    }

    public void setValueAfter(Object valueAfter) {
        this.valueAfter = valueAfter;
    }
}
