package com.coursehelper.frontend.controllers;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.coursehelper.frontend.dto.DocumentResponseDto;
import com.coursehelper.frontend.exceptions.ApiException;
import com.coursehelper.frontend.service.DocumentApiService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DocumentsPageController {

    @FXML private VBox documentList;
    @FXML private Label statusLabel;
    @FXML private Button uploadButton;

    private DocumentApiService documentApiService;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public void initialize() {
        documentApiService = DocumentApiService.getInstance();

        loadDocuments();
    }

    @FXML
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PDF");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        Stage stage = (Stage) uploadButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            uploadFile(file);
        }
    }

    private void uploadFile(File file) {
        setStatus("Uploading " + file.getName() + "...");
        uploadButton.setDisable(true);

        new Thread(() -> {
            try {
                String result = documentApiService.upload(file);
                Platform.runLater(() -> {
                    setStatus(result);
                    uploadButton.setDisable(false);
                    loadDocuments(); // refresh list
                });
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    setStatus("Error: " + e.getMessage());
                    uploadButton.setDisable(false);
                });
            }
        }).start();
    }

    private void loadDocuments() {
        setStatus("Loading documents...");

        new Thread(() -> {
            try {
                List<DocumentResponseDto> documents = documentApiService.getDocuments();
                Platform.runLater(() -> {
                    documentList.getChildren().clear();
                    if (documents.isEmpty()) {
                        setStatus("No documents uploaded yet.");
                    } else {
                        setStatus("");
                        for (DocumentResponseDto doc : documents) {
                            documentList.getChildren().add(buildDocumentRow(doc));
                        }
                    }
                });
            } catch (ApiException e) {
                Platform.runLater(() -> setStatus("Failed to load documents."));
            }
        }).start();
    }

    private HBox buildDocumentRow(DocumentResponseDto doc) {
        // filename

        
        Label nameLabel = new Label(doc.getFilename());
        nameLabel.setStyle("-fx-text-fill: #888888; -fx-font-weight: bold;");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        // upload date
        String date = doc.getUploadedAt() != null
            ? doc.getUploadedAt().format(FORMATTER)
            : "";
        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 11px;");

        // download button
        Button downloadBtn = new Button("Download");
        downloadBtn.setStyle(
            "-fx-background-color: #4A90D9;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 4 10;");
        downloadBtn.setOnAction(e -> downloadDocument(doc));

        // delete button
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle(
            "-fx-background-color: #E05C5C;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 4 10;");
        deleteBtn.setOnAction(e -> deleteDocument(doc));

        HBox row = new HBox(12, nameLabel, dateLabel, downloadBtn, deleteBtn);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-background-color: #F8F8F8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;");

        return row;
    }

    private void downloadDocument(DocumentResponseDto doc) {
        // ask where to save
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.setInitialFileName(doc.getFilename());
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        Stage stage = (Stage) uploadButton.getScene().getWindow();
        File saveLocation = fileChooser.showSaveDialog(stage);

        if (saveLocation != null) {
            setStatus("Downloading " + doc.getFilename() + "...");
            new Thread(() -> {
                try {
                    documentApiService.download(doc.getId(), saveLocation);
                    Platform.runLater(() -> setStatus("Downloaded to " + saveLocation.getPath()));
                } catch (ApiException e) {
                    Platform.runLater(() -> setStatus("Download failed: " + e.getMessage()));
                }
            }).start();
        }
    }

    private void deleteDocument(DocumentResponseDto doc) {
        setStatus("Deleting " + doc.getFilename() + "...");
        new Thread(() -> {
            try {
                documentApiService.delete(doc.getId());
                Platform.runLater(() -> {
                    setStatus(doc.getFilename() + " deleted.");
                    loadDocuments(); // refresh list
                });
            } catch (ApiException e) {
                Platform.runLater(() -> setStatus("Delete failed: " + e.getMessage()));
            }
        }).start();
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}