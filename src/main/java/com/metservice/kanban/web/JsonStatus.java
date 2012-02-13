package com.metservice.kanban.web;


public class JsonStatus {

    public String status;
    public String message;

    private JsonStatus(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static JsonStatus createOkStatus(String message) {
        return new JsonStatus("ok", message);
    }

    public static JsonStatus createOkStatus() {
        return createOkStatus("");
    }

    public static JsonStatus createErrorStatus(String message) {
        return new JsonStatus("error", message);
    }
}
