package org.openmrs.module.pihmalawi.data.change;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Test Data Transaction Handler
 */
@Component
public class TestDataTransactionHandler implements DataTransactionHandler {

    private List<DataTransaction> transactions = new ArrayList<DataTransaction>();

    @Override
    public void handle(DataTransaction transaction) {
        transactions.add(transaction);
    }

    public List<DataTransaction> getTransactions() {
        return transactions;
    }
}
