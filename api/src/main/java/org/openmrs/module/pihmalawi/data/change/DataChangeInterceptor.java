package org.openmrs.module.pihmalawi.data.change;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

/**
 * A hibernate {@link Interceptor} implementation, intercepts any inserts, updates and
 * deletes of OpenmrsObjects in a single hibernate session and provides a mechanism
 * for interested listeners to process an feed of these events within their existing transaction.
 * This will only detect changes made via the API/Hibernate.  Any direct SQL changes will not pass through this interceptor.
 */
@Component
public class DataChangeInterceptor extends EmptyInterceptor {

    @Autowired
    List<DataTransactionHandler> handlers;

	private static final long serialVersionUID = 1L;

	private ThreadLocal<Stack<DataTransaction>> changes = new ThreadLocal<Stack<DataTransaction>>();

	/**
     * Add a new List to the Stack to hold an ordered list of changes
	 * @see EmptyInterceptor#afterTransactionBegin(Transaction)
	 */
	@Override
	public void afterTransactionBegin(Transaction tx) {
	    if (changes.get() == null) {
	        changes.set(new Stack<DataTransaction>());
        }
        changes.get().push(new DataTransaction(UUID.randomUUID().toString(), new Date()));
	}

	/**
     * Triggered on insert of new data
     * Always returns false, which indicates that no changes were made to saved object
	 * @see EmptyInterceptor#onSave(Object, Serializable, Object[], String[], Type[])
	 */
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (entity instanceof OpenmrsObject) {
            OpenmrsObject object = (OpenmrsObject) entity;
            addChange(object, propertyNames, types, null, state, DataChangeType.INSERT);
		}
		return false;
	}

	/**
     * Triggered on update of existing data
     * Always returns false, which indicates that no changes were made to saved object
	 * @see EmptyInterceptor#onFlushDirty(Object, Serializable, Object[], Object[], String[], Type[])
	 */
	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		if (entity instanceof OpenmrsObject) {
			OpenmrsObject object = (OpenmrsObject) entity;
			addChange(object, propertyNames, types, previousState, currentState, DataChangeType.UPDATE);
		}
		return false;
	}

	/**
     * Triggered on delete of existing data
	 * @see EmptyInterceptor#onDelete(Object, Serializable, Object[], String[], Type[])
	 */
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (entity instanceof OpenmrsObject) {
            OpenmrsObject object = (OpenmrsObject) entity;
            addChange(object, propertyNames, types, state, null, DataChangeType.DELETE);
		}
	}

	/**
     * Triggered if a Persistent Collection has been updated
     * This records a change event as an update to the Parent entity if an element has been added/removed
	 * @see EmptyInterceptor#onCollectionUpdate(Object, Serializable)
	 */
	@Override
	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
		if (collection != null) {
			Object owningObject = getOwner(collection);
			if (owningObject instanceof OpenmrsObject) {
			    addChange((OpenmrsObject) owningObject, null, null, null, null, DataChangeType.COLLECTION_UPDATE);
			}
		}
	}

	/**
	 * @see EmptyInterceptor#afterTransactionCompletion(Transaction)
	 */
	@Override
	public void beforeTransactionCompletion(Transaction tx) {
		try {
		    DataTransaction dataTransaction = changes.get().peek();
            if (!dataTransaction.getChanges().isEmpty()) {
                if (handlers != null) {
                    for (DataTransactionHandler handler : handlers) {
                        handler.handle(dataTransaction);
                    }
                }
            }
		}
		finally {
            changes.get().pop();
            if (changes.get().empty()) {
                changes.remove();
            }
		}
	}

    /**
     * Gets the owning object of a persistent collection
     * @param collection the persistent collection
     * @return the owning object
     */
    private Object getOwner(Object collection) {
        Class<?> collectionClass;
        try {
            collectionClass = Context.loadClass("org.hibernate.collection.PersistentCollection");
        }
        catch (ClassNotFoundException oe) {
            //We are running against openmrs core 2.0 or later where it's a later hibernate version
            try {
                collectionClass = Context.loadClass("org.hibernate.collection.spi.PersistentCollection");
            }
            catch (ClassNotFoundException ie) {
                throw new HibernateException(ie);
            }
        }
        Method method = ReflectionUtils.findMethod(collectionClass, "getOwner");
        return ReflectionUtils.invokeMethod(method, collection);
    }

    /**
     * Add an object change to the list at the top of the stack
     */
	private void addChange(OpenmrsObject object, String[] propertyNames, Type[] types, Object[] beforeState, Object[] afterState, DataChangeType changeType) {
        DataChange change = new DataChange(object, changeType);
        if (propertyNames != null && types != null) {
            for (int i=0; i<propertyNames.length; i++) {
                Object before = beforeState == null ? null : beforeState[i];
                Object after = afterState == null ? null : afterState[i];
                if (propertyChanged(before, after)) {
                    change.getChangeElements().add(new DataChangeElement(propertyNames[i], before, after));
                }
            }
        }
        changes.get().peek().addChange(change);
    }

    private boolean propertyChanged(Object before, Object after) {
	    if (before == null) {
	        return after != null;
        }
        else if (after == null) {
            return true;
        }
        else if (before instanceof Date && after instanceof Date) {
            return ((Date)before).getTime() != ((Date)after).getTime();
        }
        else {
            return !before.equals(after);
        }
    }
}
