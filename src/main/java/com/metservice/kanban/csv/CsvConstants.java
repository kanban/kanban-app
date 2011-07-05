package com.metservice.kanban.csv;

public abstract class CsvConstants {

    public static final String ID_COLUMN_NAME = "id";
    public static final String PARENT_ID_COLUMN_NAME = "parentId";
    public static final String NAME_COLUMN_NAME = "name";
    public static final String SIZE_COLUMN_NAME = "size";
    public static final String IMPORTANCE_COLUMN_NAME = "importance";
    public static final String NOTES_COLUMN_NAME = "notes";
    public static final String EXCLUDED_COLUMN_NAME = "excluded";
    
    private CsvConstants() {
        // static class: not intended to be instantiated
    }
}
