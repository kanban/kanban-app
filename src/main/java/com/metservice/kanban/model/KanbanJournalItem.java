package com.metservice.kanban.model;

import org.joda.time.LocalDateTime;
import com.metservice.kanban.utils.DateUtils;


public class KanbanJournalItem implements Comparable<KanbanJournalItem> {

    private Integer id;
    private String text;
    private LocalDateTime date;
    private String userName;

    public KanbanJournalItem(String date, String text, String userName) {
        this(null, date, text, userName);
    }

    public KanbanJournalItem(Integer id, String date, String text, String userName) {
        this.id = id;
        this.text = text;
        this.date = LocalDateTime.parse(date);
        this.userName = userName;
    }


    public LocalDateTime getDate() {
        return date;
    }

    public String getDateStr() {
        return date.toString(DateUtils.DATE_FORMAT_STR);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int compareTo(KanbanJournalItem o) {

        return -date.compareTo(o.date);
    }

}