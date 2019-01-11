package org.openmrs.module.pihmalawi.data.change;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.openmrs.OpenmrsObject;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a data change to an OpenMRS object
 */
public class DataChange {

    private String objectUuid;
    private Class<? extends OpenmrsObject> objectType;
    private DataChangeType changeType;
    private List<DataChangeElement> changeElements = new ArrayList<DataChangeElement>();

    public DataChange(OpenmrsObject openmrsObject, DataChangeType changeType) {
        this.objectUuid = openmrsObject.getUuid();
        this.objectType = openmrsObject.getClass();
        this.changeType = changeType;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder b = new HashCodeBuilder(11, 43);
        b.append(objectUuid).append(objectType).append(changeType).append(changeElements);
        return b.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataChange) {
            DataChange that = (DataChange) obj;
            if (OpenmrsUtil.nullSafeEquals(this.getObjectUuid(), that.getObjectUuid())) {
                if (this.getObjectType() == that.getObjectType()) {
                    if (OpenmrsUtil.nullSafeEquals(this.getChangeType(), that.getChangeType())) {
                        if (this.getChangeElements().equals(that.getChangeElements())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "\n" + changeType.toString() + " " + objectType.getSimpleName() + ": " + objectUuid;
    }

    public String getObjectUuid() {
        return objectUuid;
    }

    public void setObjectUuid(String objectUuid) {
        this.objectUuid = objectUuid;
    }

    public Class<? extends OpenmrsObject> getObjectType() {
        return objectType;
    }

    public void setObjectType(Class<? extends OpenmrsObject> objectType) {
        this.objectType = objectType;
    }

    public DataChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(DataChangeType changeType) {
        this.changeType = changeType;
    }

    public List<DataChangeElement> getChangeElements() {
        if (changeElements == null) {
            changeElements = new ArrayList<DataChangeElement>();
        }
        return changeElements;
    }

    public void setChangeElements(List<DataChangeElement> changeElements) {
        this.changeElements = changeElements;
    }

    public void addChangeElement(DataChangeElement changeElement) {
        getChangeElements().add(changeElement);
    }

    public DataChangeElement getChangeElement(String propertyName) {
        for (DataChangeElement dce : getChangeElements()) {
            if (dce.getPropertyName().equals(propertyName)) {
                return dce;
            }
        }
        return null;
    }

    public void mergeChangeElements(List<DataChangeElement> changeElements) {
        for (DataChangeElement dce : changeElements) {
            DataChangeElement existing = getChangeElement(dce.getPropertyName());
            if (existing != null) {
                existing.setValueAfter(dce.getValueAfter());
            }
            else {
                addChangeElement(dce);
            }
        }
    }
}
