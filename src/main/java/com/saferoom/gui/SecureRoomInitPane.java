package com.saferoom.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SecureRoomInitPane {
    public static Node build() {
        VBox content = new VBox(36);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(64, 0, 0, 0));
        content.setStyle("-fx-background-color: transparent;");

        // Başlık ve bilgiler
        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER);
        Label title = new Label("SECURE ROOM INITIALIZATION");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#00eaff"));
        Label roomId = new Label("Room ID: sr-7F1A3C");
        roomId.setFont(Font.font("Segoe UI", 20));
        roomId.setTextFill(Color.WHITE);
        Label encryption = new Label("Encryption: AES-256 + RSA - 4096");
        encryption.setFont(Font.font("Segoe UI", 18));
        encryption.setTextFill(Color.web("#7eeaff"));
        Label relay = new Label("Relay: Direct (No relay)");
        relay.setFont(Font.font("Segoe UI", 18));
        relay.setTextFill(Color.web("#7eeaff"));
        Label stun = new Label("STUN Server: auto-selected (RTT: 42ms)");
        stun.setFont(Font.font("Segoe UI", 18));
        stun.setTextFill(Color.web("#7eeaff"));
        infoBox.getChildren().addAll(title, roomId, encryption, relay, stun);

        // Ana grid
        HBox mainGrid = new HBox(48);
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setPadding(new Insets(48, 0, 0, 0));

        // Video Preview
        VBox videoBox = new VBox(18);
        videoBox.setAlignment(Pos.TOP_CENTER);
        videoBox.setPadding(new Insets(32));
        videoBox.setStyle("-fx-background-color: rgba(10,30,40,0.92); -fx-border-color: #00eaff; -fx-border-radius: 24; -fx-background-radius: 24;");
        Label videoTitle = new Label("Video Preview");
        videoTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        videoTitle.setTextFill(Color.web("#00eaff"));
        ImageView cam = new ImageView(new Image("https://randomuser.me/api/portraits/men/1.jpg", 140, 140, true, true));
        cam.setFitWidth(140); cam.setFitHeight(140);
        ComboBox<String> quality = new ComboBox<>();
        quality.getItems().addAll("480p", "720p", "1080p");
        quality.setValue("720p");
        quality.setStyle("-fx-font-size: 18px;");
        videoBox.getChildren().addAll(videoTitle, cam, quality);

        // Audio Setup
        VBox audioBox = new VBox(18);
        audioBox.setAlignment(Pos.TOP_CENTER);
        audioBox.setPadding(new Insets(32));
        audioBox.setStyle("-fx-background-color: rgba(10,30,40,0.92); -fx-border-color: #00eaff; -fx-border-radius: 24; -fx-background-radius: 24;");
        Label audioTitle = new Label("Audio Setup");
        audioTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        audioTitle.setTextFill(Color.web("#00eaff"));
        ComboBox<String> mic = new ComboBox<>();
        mic.getItems().addAll("Microphone", "External Mic");
        mic.setValue("Microphone");
        mic.setStyle("-fx-font-size: 18px;");
        Button testBtn = new Button("Test");
        testBtn.setStyle("-fx-font-size: 16px; -fx-padding: 8 24;");
        Button copyBtn = new Button("⧉");
        copyBtn.setStyle("-fx-font-size: 16px; -fx-padding: 8 16;");
        HBox audioBtns = new HBox(12, testBtn, copyBtn);
        audioBtns.setAlignment(Pos.CENTER);
        audioBox.getChildren().addAll(audioTitle, mic, audioBtns);

        // Secure Key
        VBox keyBox = new VBox(18);
        keyBox.setAlignment(Pos.TOP_CENTER);
        keyBox.setPadding(new Insets(32));
        keyBox.setStyle("-fx-background-color: rgba(10,30,40,0.92); -fx-border-color: #00eaff; -fx-border-radius: 24; -fx-background-radius: 24;");
        Label keyTitle = new Label("Secure Key");
        keyTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        keyTitle.setTextFill(Color.web("#00eaff"));
        ImageView qr = new ImageView(new Image("https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=securekey"));
        qr.setFitWidth(160); qr.setFitHeight(160);
        Button shareBtn = new Button("Share key");
        shareBtn.setStyle("-fx-font-size: 16px; -fx-padding: 8 24;");
        Button copyKeyBtn = new Button("⧉");
        copyKeyBtn.setStyle("-fx-font-size: 16px; -fx-padding: 8 16;");
        HBox keyBtns = new HBox(12, shareBtn, copyKeyBtn);
        keyBtns.setAlignment(Pos.CENTER);
        keyBox.getChildren().addAll(keyTitle, qr, keyBtns);

        mainGrid.getChildren().addAll(videoBox, audioBox, keyBox);

        // Room Policy
        VBox policyBox = new VBox(12);
        policyBox.setAlignment(Pos.CENTER_LEFT);
        policyBox.setPadding(new Insets(32, 0, 0, 0));
        policyBox.setMaxWidth(600);
        Label policyTitle = new Label("Room Policy");
        policyTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        policyTitle.setTextFill(Color.web("#00eaff"));
        CheckBox autoDestroy = new CheckBox("Auto-destroy after exit");
        autoDestroy.setSelected(true);
        autoDestroy.setTextFill(Color.WHITE);
        autoDestroy.setStyle("-fx-font-size: 16px;");
        CheckBox noLogs = new CheckBox("No logs");
        noLogs.setSelected(true);
        noLogs.setTextFill(Color.WHITE);
        noLogs.setStyle("-fx-font-size: 16px;");
        CheckBox ramOnly = new CheckBox("RAM-only stream");
        ramOnly.setSelected(true);
        ramOnly.setTextFill(Color.WHITE);
        ramOnly.setStyle("-fx-font-size: 16px;");
        policyBox.getChildren().addAll(policyTitle, autoDestroy, noLogs, ramOnly);

        // Start Room butonu
        Button startBtn = new Button("INITIATE ZERO-TRACE ROOM");
        startBtn.setStyle("-fx-background-color: #00eaff; -fx-text-fill: #222; -fx-font-size: 24px; -fx-font-weight: bold; -fx-background-radius: 24; -fx-padding: 20 48;");
        startBtn.setMaxWidth(600);

        // Alt panel
        VBox bottomPanel = new VBox(6);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(32, 0, 0, 0));
        Label netInfo = new Label("Public IP: 5.123.21.7 | NAT Type: Port Restricted Cone | Hole Status: \uD83D\uDD13 OPEN");
        netInfo.setFont(Font.font("Segoe UI", 16));
        netInfo.setTextFill(Color.web("#7eeaff"));
        Label phantom = new Label("Phantom Datagram Init: \uD83D\uDFE2 Ready");
        phantom.setFont(Font.font("Segoe UI", 16));
        phantom.setTextFill(Color.web("#00eaff"));
        bottomPanel.getChildren().addAll(netInfo, phantom);

        content.getChildren().addAll(infoBox, mainGrid, policyBox, startBtn, bottomPanel);

        // Responsive ana container
        StackPane responsiveRoot = new StackPane();
        responsiveRoot.setStyle("-fx-background-color: transparent;");
        responsiveRoot.getChildren().add(content);
        // Responsive binding
        content.maxWidthProperty().bind(responsiveRoot.widthProperty().multiply(0.98));
        content.maxHeightProperty().bind(responsiveRoot.heightProperty().multiply(0.98));
        // Scroll desteği
        ScrollPane scroll = new ScrollPane(responsiveRoot);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scroll;
    }
} 