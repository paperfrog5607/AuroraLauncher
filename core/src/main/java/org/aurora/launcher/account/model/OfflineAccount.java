package org.aurora.launcher.account.model;

public class OfflineAccount extends Account {

    public OfflineAccount() {
        super();
        this.type = AccountType.OFFLINE;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void refresh() {
    }

    @Override
    public String getAccessToken() {
        return "";
    }

    @Override
    public void logout() {
    }
}