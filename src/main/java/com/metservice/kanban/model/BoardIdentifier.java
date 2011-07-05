package com.metservice.kanban.model;

public enum BoardIdentifier {
    WALL, COMPLETED;
    
    public String getName() {
        return super.toString().toLowerCase();
    };
}
