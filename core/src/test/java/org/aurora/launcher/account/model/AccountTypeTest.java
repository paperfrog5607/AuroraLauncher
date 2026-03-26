package org.aurora.launcher.account.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountTypeTest {

    @Test
    void values_containsAllTypes() {
        AccountType[] types = AccountType.values();
        assertEquals(3, types.length);
    }

    @Test
    void valueOf_microsoft() {
        assertEquals(AccountType.MICROSOFT, AccountType.valueOf("MICROSOFT"));
    }

    @Test
    void valueOf_offline() {
        assertEquals(AccountType.OFFLINE, AccountType.valueOf("OFFLINE"));
    }

    @Test
    void valueOf_custom() {
        assertEquals(AccountType.CUSTOM, AccountType.valueOf("CUSTOM"));
    }
}