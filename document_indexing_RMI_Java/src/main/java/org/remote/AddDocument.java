package org.remote;

import org.locals.DocumentEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AddDocument extends JFrame {

    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel yearLabel;
    private JLabel fileLabel;
    private JLabel descriptionLabel;
    private JTextField titleTextField;
    private JTextField authorTextField;
    private JTextField yearTextField;
    private JTextField descriptionTextField;
    private JButton addButton;

    private DocumentIndexService documentIndexService;

    public AddDocument() {
        setTitle("Add Document");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        titleLabel = new JLabel("Title:");
        authorLabel = new JLabel("Author:");
        yearLabel = new JLabel("Year:");
        fileLabel = new JLabel("File:");
        descriptionLabel = new JLabel("Description:");

        titleTextField = new JTextField(20);
        authorTextField = new JTextField(20);
        yearTextField = new JTextField(20);
        descriptionTextField = new JTextField(20);

        addButton = new JButton("Add");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleTextField.getText();
                String author = authorTextField.getText();
                String year = yearTextField.getText();
                String description = descriptionTextField.getText();

                if (title.isEmpty() || author.isEmpty() || year.isEmpty() || description.isEmpty()) {
                    JOptionPane.showMessageDialog(AddDocument.this, "Please fill in all fields.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
                } else {
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showOpenDialog(AddDocument.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        try {
                            byte[] fileData = Files.readAllBytes(file.toPath());
                            // Save the document to the database using RMI
                            addDocument(title, author, year, description, fileData);
                            JOptionPane.showMessageDialog(AddDocument.this, "Document added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            // Clear the text fields
                            titleTextField.setText("");
                            authorTextField.setText("");
                            yearTextField.setText("");
                            descriptionTextField.setText("");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(AddDocument.this, "Failed to read file.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        add(titleLabel);
        add(titleTextField);
        add(authorLabel);
        add(authorTextField);
        add(yearLabel);
        add(yearTextField);
        add(descriptionLabel);
        add(descriptionTextField);
        add(fileLabel);
        add(addButton);

        setVisible(true);

        // Initialize the RMI connection
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            documentIndexService = (DocumentIndexService) registry.lookup("DocumentIndexService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void addDocument(String title, String author, String year, String description, byte[] fileData) {
        try {
            // Invoke the remote method to save the document with file data as byte array
            DocumentEntity document = new DocumentEntity();
            document.setTitle(title);
            document.setAuthor(author);
            document.setDescription(description);
            document.setDocumentFile(fileData);
            documentIndexService.addDocument(document);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to add document.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AddDocument();
            }
        });
    }
}
