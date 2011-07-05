package com.metservice.kanban.jwebunit;

import net.sourceforge.jwebunit.junit.WebTester;

public class ChartPage {

    private final WebTester tester;

    public ChartPage(WebTester tester) {
        this.tester = tester;
    }

    public void assertImageIsValidPng(String src) {
        tester.assertImageValid(src, null);
    }

    public void dumpPageSourceToConsole() {
        System.out.println(tester.getPageSource());
    }
}
