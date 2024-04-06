package org.remote;

import org.locals.DocumentEntity;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

class DocumentTableModel extends AbstractTableModel {
    private List<DocumentEntity> documents;
    private String[] columnNames = {"Author", "Description", "File Name", "Publish Date"};

    public DocumentTableModel(List<DocumentEntity> documents) {
        if (documents == null) {
            this.documents = List.of();
        } else {
            this.documents = documents;
        }
    }

    public DocumentEntity getDocument(int rowIndex) {
        return documents.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return documents.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DocumentEntity document = documents.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return document.getAuthor();
            case 1:
                return document.getDescription();
            case 2:
                return document.getTitle();
            case 3:
                return document.getPublishDate();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}