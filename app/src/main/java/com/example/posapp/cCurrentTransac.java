package com.example.posapp;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class cCurrentTransac {
    private static TmpContainer currentTransaction;

    public static void setCurrentTransaction(TmpContainer container) {
        currentTransaction = container;
    }

    public static TmpContainer getCurrentTransaction() {
        return currentTransaction;
    }
}
