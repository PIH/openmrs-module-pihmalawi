package org.openmrs.module.pihmalawi.data.change;

import org.openmrs.OpenmrsObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a list of data changes within a single transaction
 */
public class DataTransaction {

    private String transactionId;
    private Date transactionDate;
    private List<DataChange> changes = new ArrayList<DataChange>();

    public DataTransaction() {
    }

    public DataTransaction(String transactionId, Date transactionDate) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "Transaction " + transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public List<DataChange> getChanges() {
        if (changes == null) {
            changes = new ArrayList<DataChange>();
        }
        return changes;
    }

    public void setChanges(List<DataChange> changes) {
        this.changes = changes;
    }

    public void addChange(DataChange change) {
        // Adds in a new change.  If the change represents further updates to an existing change, they are merged
        DataChange existing = getChange(change.getObjectUuid(), change.getObjectType(), change.getChangeType());
        if (existing != null) {
            existing.mergeChangeElements(change.getChangeElements());
        }
        else {
            getChanges().add(change);
        }
    }

    public DataChange getChange(String objectUuid, Class<? extends OpenmrsObject> objectType, DataChangeType changeType) {
        for (DataChange dc : getChanges()) {
            if (dc.getObjectUuid().equals(objectUuid) && dc.getObjectType().equals(objectType) && dc.getChangeType().equals(changeType)) {
                return dc;
            }
        }
        return null;
    }
}
