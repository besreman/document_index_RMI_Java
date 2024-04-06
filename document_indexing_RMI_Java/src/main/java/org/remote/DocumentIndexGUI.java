package org.remote;

import org.locals.DocumentEntity;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

public class DocumentIndexGUI extends JFrame {
    private DocumentIndexService documentIndexService;
    private DocumentTableModel tableModel;
    private JTable documentTable;
    private JTextField searchTextField;

    public DocumentIndexGUI(DocumentIndexService documentIndexService) throws RemoteException {
        this.documentIndexService = documentIndexService;
        setupUI();
        populateTable();
    }

    private void setupUI() {
        setTitle("Document Index");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel searchLabel = new JLabel("Search by Description:");
        searchTextField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    performSearch();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchTextField);
        searchPanel.add(searchButton);

        // Download Button
        JButton downloadButton = new JButton("Download");
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = documentTable.getSelectedRow();
                if (selectedRow != -1) {
                    try {
                        downloadFile(selectedRow);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(DocumentIndexGUI.this, "Please select a document to download.");
                }
            }
        });

        // Document Table
        documentTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(documentTable);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(downloadButton, BorderLayout.SOUTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void populateTable() throws RemoteException {
        List<DocumentEntity> documents = documentIndexService.searchDocuments("");
        tableModel = new DocumentTableModel(documents);
        documentTable.setModel(tableModel);
    }

    private void performSearch() throws RemoteException {
        String searchText = searchTextField.getText().trim();
        List<DocumentEntity> searchResults = documentIndexService.searchDocuments(searchText);
        tableModel = new DocumentTableModel(searchResults);
        documentTable.setModel(tableModel);
    }

    private void downloadFile(int rowIndex) throws RemoteException {
        DocumentEntity document = tableModel.getDocument(rowIndex);
        byte[] fileContent = document.getDocumentFile();
        String fileName = document.getTitle();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(fileName));
        int choice = fileChooser.showSaveDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(selectedFile)) {
                fos.write(fileContent);
                JOptionPane.showMessageDialog(this, "File downloaded successfully.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error occurred while downloading the file.");
            }
        }
    }

    public static void main(String[] args) {
        try {
            DocumentIndexService documentIndexService = (DocumentIndexService) Naming.lookup("rmi://localhost/DocumentIndexService");
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        new DocumentIndexGUI(documentIndexService).setVisible(true);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }}