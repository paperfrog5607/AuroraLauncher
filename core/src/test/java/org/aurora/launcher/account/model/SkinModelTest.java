package org.aurora.launcher.account.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SkinModelTest {

    @Test
    void values_containsAllModels() {
        SkinModel[] models = SkinModel.values();
        assertEquals(2, models.length);
    }

    @Test
    void valueOf_steve() {
        assertEquals(SkinModel.STEVE, SkinModel.valueOf("STEVE"));
    }

    @Test
    void valueOf_alex() {
        assertEquals(SkinModel.ALEX, SkinModel.valueOf("ALEX"));
    }
}