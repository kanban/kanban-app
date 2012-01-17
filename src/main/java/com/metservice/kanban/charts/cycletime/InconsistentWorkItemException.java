package com.metservice.kanban.charts.cycletime;


@SuppressWarnings("serial")
public class InconsistentWorkItemException extends RuntimeException {

    public InconsistentWorkItemException() {
    }

    public InconsistentWorkItemException(String message) {
        super(message);
    }
}
