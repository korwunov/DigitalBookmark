package com.Client.utils;

import com.vaadin.flow.component.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalStorage {
    private static final Logger log = LoggerFactory.getLogger(LocalStorage.class);
    public static void saveToLocalStorage(UI ui, String key, String value) {
        String expressionToSave = String.format("localStorage.setItem(%s, %s);", key, value);
        log.info("Expression to execute: {}", expressionToSave);
        ui.getCurrent().getPage().executeJs(expressionToSave);
    }
}
