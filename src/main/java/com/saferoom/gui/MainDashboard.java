package com.saferoom.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;


public class MainDashboard {
    public static Node createDashboard(String username) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: transparent;");

        // Responsive grid için StackPane
        StackPane centerPane = new StackPane();
        centerPane.setStyle("-fx-background-color: transparent;");

        // Üst Menü
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 32, 0, 32));
        topBar.setPrefHeight(56);
        topBar.setSpacing(32);
        topBar.setStyle("-fx-background-color: rgba(10,20,30,0.95);");

        // Search Rooms alanı
        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        Label searchRooms = new Label("Search Rooms");
        searchRooms.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
        searchRooms.setTextFill(Color.web("#7eeaff"));
        searchBox.getChildren().add(searchRooms);

        // Search bar ve ikon (gizli başta)
        HBox searchBar = new HBox(6);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setVisible(false);
        searchBar.setManaged(false);
        TextField searchField = new TextField();
        searchField.setPromptText("Search rooms...");
        searchField.setStyle("-fx-background-radius: 16; -fx-background-color: #162a36; -fx-text-fill: #7eeaff; -fx-prompt-text-fill: #7eeaff99; -fx-border-color: #00eaff; -fx-border-radius: 16; -fx-padding: 4 32 4 12;");
        searchField.setPrefWidth(180);
        Label searchIcon = new Label("🔍");
        searchIcon.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 18));
        searchIcon.setTextFill(Color.web("#7eeaff"));
        searchBar.getChildren().addAll(searchField, searchIcon);

        // Search Rooms tıklanınca search bar'a dönüşsün
        searchRooms.setOnMouseClicked(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), searchBox);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.setOnFinished(ev -> {
                searchBox.setVisible(false);
                searchBox.setManaged(false);
                searchBar.setVisible(true);
                searchBar.setManaged(true);
                FadeTransition ft2 = new FadeTransition(Duration.millis(200), searchBar);
                ft2.setFromValue(0);
                ft2.setToValue(1);
                ft2.play();
                searchField.requestFocus();
            });
            ft.play();
        });
        // Search bar dışına tıklanınca geri dönsün
        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                FadeTransition ft = new FadeTransition(Duration.millis(200), searchBar);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished(ev -> {
                    searchBar.setVisible(false);
                    searchBar.setManaged(false);
                    searchBox.setVisible(true);
                    searchBox.setManaged(true);
                    FadeTransition ft2 = new FadeTransition(Duration.millis(200), searchBox);
                    ft2.setFromValue(0);
                    ft2.setToValue(1);
                    ft2.play();
                });
                ft.play();
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label safeRoomLogo = new Label("SafeRoom");
        safeRoomLogo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        safeRoomLogo.setTextFill(Color.WHITE);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // LOGS ve açılır menü
        VBox logsBox = new VBox();
        logsBox.setAlignment(Pos.CENTER);
        Label logs = new Label("Logs");
        logs.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
        logs.setTextFill(Color.web("#7eeaff"));
        logsBox.getChildren().add(logs);
        VBox logsMenu = new VBox();
        logsMenu.setStyle("-fx-background-color: #162a36; -fx-background-radius: 12; -fx-border-color: #00eaff; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, #00eaff55, 8, 0.2, 0, 2);");
        logsMenu.setVisible(false);
        logsMenu.setManaged(false);
        logsMenu.setSpacing(0);
        logsMenu.setTranslateY(8);
        Label manageAccount = new Label("Manage Account");
        manageAccount.setFont(Font.font("Segoe UI", 15));
        manageAccount.setTextFill(Color.WHITE);
        manageAccount.setPadding(new Insets(8, 24, 8, 24));
        Label manageLast = new Label("Manage Last In/Last Outs");
        manageLast.setFont(Font.font("Segoe UI", 15));
        manageLast.setTextFill(Color.WHITE);
        manageLast.setPadding(new Insets(8, 24, 8, 24));
        logsMenu.getChildren().addAll(manageAccount, manageLast);
        logsBox.getChildren().add(logsMenu);
        logs.setOnMouseClicked(e -> {
            boolean show = !logsMenu.isVisible();
            logsMenu.setVisible(show);
            logsMenu.setManaged(show);
            FadeTransition ft = new FadeTransition(Duration.millis(200), logsMenu);
            ft.setFromValue(show ? 0 : 1);
            ft.setToValue(show ? 1 : 0);
            ft.play();
        });

        // SETTINGS ve açılır menü
        VBox settingsBox = new VBox();
        settingsBox.setAlignment(Pos.CENTER);
        Label settings = new Label("Settings");
        settings.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
        settings.setTextFill(Color.web("#7eeaff"));
        settingsBox.getChildren().add(settings);
        VBox settingsMenu = new VBox();
        settingsMenu.setStyle("-fx-background-color: #162a36; -fx-background-radius: 12; -fx-border-color: #00eaff; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, #00eaff55, 8, 0.2, 0, 2);");
        settingsMenu.setVisible(false);
        settingsMenu.setManaged(false);
        settingsMenu.setSpacing(0);
        settingsMenu.setTranslateY(8);
        Label manageProfile = new Label("Manage Profile");
        manageProfile.setFont(Font.font("Segoe UI", 15));
        manageProfile.setTextFill(Color.WHITE);
        manageProfile.setPadding(new Insets(8, 24, 8, 24));
        settingsMenu.getChildren().add(manageProfile);
        settingsBox.getChildren().add(settingsMenu);
        settings.setOnMouseClicked(e -> {
            boolean show = !settingsMenu.isVisible();
            settingsMenu.setVisible(show);
            settingsMenu.setManaged(show);
            FadeTransition ft = new FadeTransition(Duration.millis(200), settingsMenu);
            ft.setFromValue(show ? 0 : 1);
            ft.setToValue(show ? 1 : 0);
            ft.play();
        });

        topBar.getChildren().addAll(searchBox, searchBar, spacer, safeRoomLogo, spacer2, logsBox, settingsBox);
        mainLayout.setTop(topBar);

        // Orta kutular
        GridPane grid = new GridPane();
        grid.setHgap(36);
        grid.setVgap(36);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(48, 0, 0, 0));

        String boxStyle = "-fx-background-color: rgba(10,30,40,0.92); -fx-border-color: #00eaff; -fx-border-radius: 18; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, #00eaff33, 0, 0, 0, 0);";
        String boxStyleHover = "-fx-background-color: rgba(20,50,70,0.98); -fx-border-color: #00eaff; -fx-border-radius: 18; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, #00eaff, 24, 0.5, 0, 0);";

        VBox box1 = createDashboardBox("INSTANT SECURE ROOM", "New Meeting", "Create zero-trace encrypted room", boxStyle, boxStyleHover);
        VBox box2 = createDashboardBox("CONNECT TO TUNNEL", "Join Room", "Enter access code / session ID", boxStyle, boxStyleHover);
        VBox box3 = createDashboardBox("PROGRAMMATIC SYNC", "Schedule Room", "Auto schedule + key exchange time", boxStyle, boxStyleHover);
        VBox box4 = createDashboardBox("FILEVAULT", "Encrypted Files", "236-bit AES encrypted P2P file sharing", boxStyle, boxStyleHover);

        grid.add(box1, 0, 0);
        grid.add(box2, 1, 0);
        grid.add(box3, 0, 1);
        grid.add(box4, 1, 1);

        centerPane.getChildren().clear();
        centerPane.getChildren().add(grid);
        mainLayout.setCenter(centerPane);

        // Alt bilgi çubuğu
        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(0, 32, 0, 32));
        bottomBar.setPrefHeight(36);
        bottomBar.setSpacing(24);
        bottomBar.setStyle("-fx-background-color: rgba(10,20,30,0.92);");
        Label rtt = new Label("RUN RTT: 65ms");
        rtt.setTextFill(Color.web("#7eeaff"));
        Label udp = new Label("UDP Hole Status: Open");
        udp.setTextFill(Color.web("#7eeaff"));
        Label relay = new Label("Last Relay: N/A");
        relay.setTextFill(Color.web("#7eeaff"));
        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);
        Label proto = new Label("Phantom Datagram Protocol Alpha");
        proto.setTextFill(Color.web("#7eeaff"));
        bottomBar.getChildren().addAll(rtt, udp, relay, bottomSpacer, proto);
        mainLayout.setBottom(bottomBar);

        StackPane outerFrame = new StackPane(mainLayout);
        outerFrame.setStyle("-fx-background-color: transparent;");
        return outerFrame;
    }

    private static VBox createDashboardBox(String title, String subtitle, String desc, String style, String styleHover) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(28, 32, 28, 32));
        box.setMinSize(320, 160);
        box.setMaxSize(340, 180);
        box.setStyle(style);
        box.setSpacing(4);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#00eaff"));
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        subtitleLabel.setTextFill(Color.WHITE);
        Label descLabel = new Label(desc);
        descLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        descLabel.setTextFill(Color.web("#b2ebf2"));
        box.getChildren().addAll(titleLabel, subtitleLabel, descLabel);
        // Hover efekti
        box.setOnMouseEntered(e -> box.setStyle(styleHover));
        box.setOnMouseExited(e -> box.setStyle(style));
        // Tıklama efekti
        box.setOnMouseClicked((MouseEvent e) -> {
            System.out.println("Clicked: " + title);
            if (title.equals("INSTANT SECURE ROOM")) {
                // Sadece sade scale ve fade animasyonu, arka planı etkileme
                com.saferoom.gui.VantaEffectFXMouse.mode = 0; // Hep mavi efekt
                ScaleTransition scale = new ScaleTransition(Duration.millis(250), box);
                scale.setToX(1.08); scale.setToY(1.08);
                FadeTransition fade = new FadeTransition(Duration.millis(250), box);
                fade.setToValue(0);
                scale.play(); fade.play();
                fade.setOnFinished(ev -> {
                    StackPane parent = (StackPane) box.getParent().getParent();
                    parent.getChildren().clear();
                    Node securePane = SecureRoomInitPane.build();
                    securePane.setOpacity(0);
                    parent.getChildren().add(securePane);
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(400), securePane);
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.play();
                });
            }
        });
        return box;
    }
} 
