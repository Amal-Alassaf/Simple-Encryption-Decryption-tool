

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class CipherUI extends Application {

    private ComboBox<String> cipherSelector;
    private ToggleGroup actionGroup;
    private File selectedFile;
    private TextArea resultArea;
    private Ciphers algorithm = new Ciphers();
    private TextField keyField;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20px;-fx-background-color: #F5EEDC;");
        
        Label title = new Label("Welcome to Encryption/Decryption Tool");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);

        // Cipher selection
        cipherSelector = new ComboBox<>();
        cipherSelector.getItems().addAll("DES", "Ceaser", "Morse");
        cipherSelector.setPromptText("Select Cipher");
        cipherSelector.setStyle("-fx-background-radius: 5px;-fx-font-size: 16px;");
        cipherSelector.setOnAction(e -> {
            String selected = cipherSelector.getValue();
            if ("Morse".equals(selected)) {
                keyField.setDisable(true);
                keyField.clear();
            } else {
                keyField.setDisable(false);
            }
        });

        // Encrypt/Decrypt options
        actionGroup = new ToggleGroup();
        RadioButton encryptOption = new RadioButton("Encrypt");
        RadioButton decryptOption = new RadioButton("Decrypt");
        encryptOption.setToggleGroup(actionGroup);
        encryptOption.setStyle("-fx-font-size: 16px;");
        decryptOption.setToggleGroup(actionGroup);
        decryptOption.setStyle("-fx-font-size: 16px;");
        encryptOption.setSelected(true);

        // Key input
        keyField = new TextField();
        keyField.setPromptText("Enter Key");
        keyField.setStyle("-fx-font-size: 16px;-fx-background-color: #FDFAF6;");

        // File chooser
        Button uploadButton = new Button("Upload File");
        uploadButton.setStyle("-fx-background-radius: 5px;-fx-font-size: 16px;");
        uploadButton.setOnAction(e -> chooseFile(primaryStage));

        // Run button
        Button executeButton = new Button("Run");
        executeButton.setStyle("-fx-background-radius: 5px; -fx-font-size: 16px;");
        executeButton.setOnAction(e -> processFile());

        // Result display
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(300);
        resultArea.setWrapText(true);
        resultArea.setStyle("-fx-background-color: #FDFAF6; -fx-border-radius: 5px; -fx-background-radius: 5px;-fx-font-size: 20px;");

        root.getChildren().addAll(titleBox,cipherSelector, encryptOption, decryptOption, keyField, uploadButton, executeButton, resultArea);

        Scene scene = new Scene(root, 600, 520);
        primaryStage.setTitle("File Encryption/Decryption");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void chooseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            resultArea.setText("Selected File: " + selectedFile.getName());
        }
    }

    private void processFile() {
        if (selectedFile == null || cipherSelector.getValue() == null || actionGroup.getSelectedToggle() == null) {
            resultArea.setText("Please select all options and upload a file.");
            return;
        }

        String cipher = cipherSelector.getValue();
        String action = ((RadioButton) actionGroup.getSelectedToggle()).getText();
        String key = keyField.getText();

        try {
            String fileContent = new String(Files.readAllBytes(selectedFile.toPath()));
            String result = "";

            switch (cipher) {
                case "DES":
                    if (key.isEmpty()) {
                        resultArea.setText("Please enter a key");
                        return;
                    }
                    if (action.equals("Encrypt")) {
                        result = algorithm.encryptDES(selectedFile.getAbsolutePath(), key);
                    } else {
                        // For consistency, use decryptFile and capture output as a string
                        result = algorithm.decryptFile(selectedFile.getAbsolutePath(), key);
                    }
                    break;

                case "Ceaser":
                    if (key.isEmpty() || !key.matches("\\d+")) {
                        resultArea.setText("Please enter a valid numeric key for Caesar cipher.");
                        return;
                    }
                    int caesarKey = Integer.parseInt(key);
                    if (action.equals("Encrypt")) {
                        result = algorithm.ceaser(fileContent, caesarKey);
                    } else {
                        result = algorithm.ceaserDecrypt(fileContent, caesarKey);
                    }
                    break;

                case "Morse":
                    if (action.equals("Encrypt")) {
                        result = algorithm.morseEncode(fileContent);
                    } else {
                        result = algorithm.morseDecode(fileContent);
                    }
                    break;
            }

            resultArea.setText(result);

        } catch (IOException e) {
            resultArea.setText("File reading error: " + e.getMessage());
        } catch (Exception e) {
            resultArea.setText("Processing error: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
