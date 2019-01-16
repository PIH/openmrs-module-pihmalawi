package org.openmrs.module.pihmalawi.alert;

import java.util.List;

public class AlertNotification {

    private String name;
    private List<String> categories;
    private String alert;
    private String action;

    public AlertNotification() {
    }

    public AlertNotification(AlertNotification notification) {
        this.name = notification.getName();
        this.categories = notification.getCategories();
        this.alert = notification.getAlert();
        this.action = notification.getAction();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return getName();
    }
}
