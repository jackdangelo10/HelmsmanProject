package org.example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.io.*;
import java.net.Socket;

public class GUI extends Application {
    private Font customFont;
    private ListView<String> chatArea;
    private TextField userInputField;
    private Button sendButton;
    private boolean waitingForResponse = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        customFont = Font.loadFont(getClass().getResourceAsStream("/windows_command_prompt.ttf"), 14);

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 225);

        // Set background and border colors
        root.setBackground(new Background(new BackgroundFill(Color.rgb(202, 67, 65), CornerRadii.EMPTY, Insets.EMPTY)));
        root.setBorder(new Border(new BorderStroke(Color.rgb(243, 229, 210), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        // Chat Area (North West)
        chatArea = new ListView<>();
        chatArea.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setFont(customFont);
                }
            }
        });
        root.setCenter(chatArea);

        // Face Display Area (North East)
        ImageView faceView = new ImageView(new Image("/Helmsman_Icon.png"));
        faceView.setFitHeight(200);
        faceView.setFitWidth(200);
        root.setRight(faceView);

        // Input Bar (Bottom)
        userInputField = new TextField();
        sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        userInputField.setOnKeyPressed(event -> {
            if (event.getCode().equals(javafx.scene.input.KeyCode.ENTER)) {
                sendMessage();
            }
        });

        HBox inputBar = new HBox(userInputField, sendButton);
        HBox.setHgrow(userInputField, Priority.ALWAYS); // Make the TextField fill the available space
        root.setBottom(inputBar);

        primaryStage.setTitle("Helmsman");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendMessage() {
        if (waitingForResponse) {
            // Prevent sending new messages while waiting for a response
            return;
        }

        String message = userInputField.getText().trim();
        if (!message.isEmpty()) {
            chatArea.getItems().add("You: " + message);
            userInputField.clear();

            // Connect to the server and send the message
            try (Socket socket = new Socket("localhost", 65432);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                waitingForResponse = true;
                sendButton.setDisable(true);

                // Send the message to the server
                out.println(message);

                // Wait for and display the response
                String response = in.readLine();
                if (response != null) {
                    chatArea.getItems().add("Helmsman: " + response);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                chatArea.getItems().add("Error: Unable to communicate with the server.");
            } finally {
                waitingForResponse = false;
                sendButton.setDisable(false);
            }
        }
    }
}