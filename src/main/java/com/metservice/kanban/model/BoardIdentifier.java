package com.metservice.kanban.model;

public enum BoardIdentifier {
    WALL;
    
    public String getName() {
        return super.toString().toLowerCase();
    };
}
