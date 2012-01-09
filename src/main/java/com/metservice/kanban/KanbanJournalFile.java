package com.metservice.kanban;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.metservice.kanban.model.KanbanJournalItem;

public class KanbanJournalFile {

    private File file;
    private List<KanbanJournalItem> journalItems = new ArrayList<KanbanJournalItem>();

    public KanbanJournalFile(File file) throws IOException {
        this.file = file;

        file.createNewFile();
        readJournal();
    }

    public void writeJournal() throws IOException {
        CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
        
        for (KanbanJournalItem item : journalItems) {
            writeItem(csvWriter, item);
        }
        
        csvWriter.close();
    }

    public void readJournal() throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(file));
        try {

            for (String[] row = csvReader.readNext(); row != null; row = csvReader.readNext()) {
                KanbanJournalItem item = new KanbanJournalItem(Integer.parseInt(row[0]), row[1], row[2], row[3]);

                journalItems.add(item);
            }

            //        journalItems.add(new KanbanJournalItem("2011-10-10", "Hello", "Robert"));
            //        journalItems.add(new KanbanJournalItem("2011-12-10", "World", "Rob"));
            Collections.sort(journalItems);
        } finally {
            csvReader.close();

        }
    }

    public List<KanbanJournalItem> getJournalItems() {
        return journalItems;
    }

    public void setJournalItems(List<KanbanJournalItem> items) {
        journalItems = items;
    }

    private void writeItem(CSVWriter writer, KanbanJournalItem item) {
        String[] row = new String[] {
            item.getId().toString(),
            item.getDate().toString(),
            item.getText(),
            item.getUserName()
        };
        writer.writeNext(row);

    }
}
