package com.metservice.kanban.utils;

public class MessageUtils {
    public static String decorateWithChar(char c, String str) {
        return c + str + c;
    }

    public static String decorateSingleQuotes( String s ) {
        return decorateWithChar('\'', s);
    }
}
