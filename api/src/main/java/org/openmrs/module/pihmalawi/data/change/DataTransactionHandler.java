package org.openmrs.module.pihmalawi.data.change;

/**
 * All classes that implement this interface will be called to process each DataTransaction
 * This is not threadsafe, so care should be taken with any instance variables
 */
public interface DataTransactionHandler {

    void handle(DataTransaction transaction);

}
