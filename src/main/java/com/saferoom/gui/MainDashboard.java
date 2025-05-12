package com.saferoom.gui;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
<<<<<<< HEAD
<<<<<<< HEAD
import javafx.scene.control.ComboBox;
=======
>>>>>>> c2efb97 (Initial commit)
=======
import javafx.scene.control.ComboBox;
>>>>>>> 1a5b1f3 (Updates & Changes)
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
<<<<<<< HEAD
<<<<<<< HEAD
import javafx.scene.shape.Circle;
=======
>>>>>>> c2efb97 (Initial commit)
=======
import javafx.scene.shape.Circle;
import javafx.scene.Group;
>>>>>>> 1a5b1f3 (Updates & Changes)

public class MainDashboard {
    public static Node createDashboard(String username) {
        // Main container
<<<<<<< HEAD
<<<<<<< HEAD
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: transparent;");

        // Responsive scaling for center content
        VBox centerContent = new VBox(32);
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.setPadding(new Insets(64, 0, 0, 0));
        centerContent.setId("centerContent");

        // --- TOP BAR ---
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(0, 32, 0, 0));
        topBar.setPrefHeight(64);
        topBar.setStyle(
            "-fx-background-color: #1a2a36;" +
            "-fx-background-radius: 0 0 18 18;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 8, 0.1, 0, 2);"
        );
        Label safeRoomLogo = new Label("SafeRoom");
        safeRoomLogo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        safeRoomLogo.setTextFill(Color.WHITE);
        Region topSpacerLeft = new Region();
        Region topSpacerRight = new Region();
        HBox.setHgrow(topSpacerLeft, Priority.ALWAYS);
        HBox.setHgrow(topSpacerRight, Priority.ALWAYS);
        Label avatar = new Label(username.substring(0,1).toUpperCase());
        avatar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        avatar.setTextFill(Color.WHITE);
        avatar.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 20; -fx-padding: 12 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 2, 0.1, 0, 1);");
        topBar.getChildren().addAll(topSpacerLeft, safeRoomLogo, topSpacerRight, avatar);
        mainLayout.setTop(topBar);

        // --- LEFT SIDEBAR ---
        VBox sidebar = new VBox(24);
        sidebar.setPadding(new Insets(48, 0, 48, 0));
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: rgba(20,40,60,0.96); -fx-background-radius: 0 32 32 0;");
        sidebar.setPrefWidth(120);
        sidebar.getChildren().addAll(
            createSidebarButton("\uD83D\uDCFA", "New Meeting", true, 36),
            createSidebarButton("\u2795", "Join", false, 36),
            createSidebarButton("\uD83D\uDCC5", "Schedule", false, 36),
            createSidebarButton("\u2B06", "Share", false, 36),
            createSidebarButton("\u2699", "Settings", false, 36),
            createSidebarButton("\u2753", "Help", false, 36)
        );
        mainLayout.setLeft(sidebar);

        // --- CENTER CONTENT ---
        HBox bigButtons = new HBox(32);
        bigButtons.setAlignment(Pos.CENTER);
        JFXButton btnCreate = createMainActionButton("\uD83D\uDCFA", "Create Meeting", Color.web("#FFD600"), Color.web("#222"), 28, 28);
        JFXButton btnJoin = createMainActionButton("\u2795", "Join Meeting", Color.web("#26C6DA"), Color.web("#222"), 28, 28);
        bigButtons.getChildren().addAll(btnCreate, btnJoin);
        btnCreate.setPrefHeight(64);
        btnJoin.setPrefHeight(64);
        btnCreate.setMinWidth(220);
        btnJoin.setMinWidth(220);

        HBox midRow = new HBox(48);
        midRow.setAlignment(Pos.CENTER);
        VBox scheduledMeetings = createScheduledMeetingsPanel(28, 22, 18);
        VBox clockCard = createClockCard(72, 32, 24);
        midRow.setStyle("-fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2.5; -fx-border-radius: 22; -fx-background-radius: 22; -fx-effect: dropshadow(gaussian, #26C6DA44, 12, 0.18, 0, 3);");
        midRow.getChildren().addAll(scheduledMeetings, clockCard);

        centerContent.getChildren().setAll(bigButtons, midRow);
        centerContent.setStyle("-fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 3; -fx-border-radius: 32; -fx-background-radius: 32; -fx-effect: dropshadow(gaussian, #26C6DA44, 18, 0.18, 0, 6);");
        mainLayout.setCenter(centerContent);

        // --- RIGHT PROFILE PANEL (modern, like PNG) ---
        VBox rightPanel = new VBox(24);
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setPadding(new Insets(36, 24, 0, 0));
        rightPanel.setStyle("-fx-background-color: transparent; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 3; -fx-border-radius: 32; -fx-background-radius: 32; -fx-effect: dropshadow(gaussian, #26C6DA44, 18, 0.18, 0, 6);");
        rightPanel.setPrefWidth(340);
        // Avatar + username card
        VBox avatarCard = new VBox(8);
        avatarCard.setAlignment(Pos.CENTER);
        avatarCard.setPadding(new Insets(24, 12, 24, 12));
        avatarCard.setStyle("-fx-background-color: rgba(20,40,60,0.97); -fx-background-radius: 18; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2; -fx-border-radius: 18; -fx-effect: dropshadow(gaussian, #26C6DA44, 8, 0.12, 0, 2);");
        StackPane avatarPane = new StackPane();
        avatarPane.setPrefSize(70, 70);
        avatarPane.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 35; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 2, 0.1, 0, 1);");
        Label avatarBig = new Label(username.substring(0,1).toUpperCase());
        avatarBig.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        avatarBig.setTextFill(Color.WHITE);
        avatarPane.getChildren().add(avatarBig);
        Label userLabel = new Label(username);
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        userLabel.setTextFill(Color.WHITE);
        avatarCard.getChildren().addAll(avatarPane, userLabel);
        // Account switcher card
        VBox accountCard = new VBox(10);
        accountCard.setAlignment(Pos.CENTER_LEFT);
        accountCard.setPadding(new Insets(18, 18, 18, 18));
        accountCard.setStyle("-fx-background-color: rgba(20,40,60,0.93); -fx-background-radius: 14; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, #26C6DA44, 6, 0.10, 0, 1);");
        ComboBox<String> switchAccount = new ComboBox<>();
        switchAccount.getItems().addAll(username + "@saferoom.com", "demo@saferoom.com");
        switchAccount.setValue(username + "@saferoom.com");
        switchAccount.setStyle("-fx-background-radius: 10; -fx-background-color: #223344; -fx-text-fill: #fff; -fx-font-size: 14; -fx-padding: 4 10;");
        accountCard.getChildren().addAll(new Label("Switch Account:"), switchAccount);
        // Status card
        VBox statusCard = new VBox(8);
        statusCard.setAlignment(Pos.CENTER_LEFT);
        statusCard.setPadding(new Insets(16, 18, 16, 18));
        statusCard.setStyle("-fx-background-color: rgba(20,40,60,0.93); -fx-background-radius: 14; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, #26C6DA44, 6, 0.10, 0, 1);");
        statusCard.getChildren().addAll(
            createStatusRow("Available", Color.LIMEGREEN),
            createStatusRow("Away", Color.ORANGE),
            createStatusRow("Do not disturb", Color.RED)
        );
        // Profile/settings/help buttons card
        VBox profileBtnsCard = new VBox(18);
        profileBtnsCard.setAlignment(Pos.CENTER_LEFT);
        profileBtnsCard.setPadding(new Insets(16, 18, 16, 18));
        profileBtnsCard.setStyle("-fx-background-color: rgba(20,40,60,0.93); -fx-background-radius: 14; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, #26C6DA44, 6, 0.10, 0, 1);");
        // Yan yana My Profile ve Help
        HBox profileHelpRow = new HBox(18);
        profileHelpRow.setAlignment(Pos.CENTER_LEFT);
        JFXButton btnProfile = createProfileBtn("My Profile");
        JFXButton btnHelp = createProfileBtn("Help");
        profileHelpRow.getChildren().addAll(btnProfile, btnHelp);
        // Altına geniş Sign Out
        JFXButton btnSignOut = createSignOutBtn("Sign Out");
        profileBtnsCard.getChildren().setAll(profileHelpRow, btnSignOut);
        // Open source label
        Label openSource = new Label("Open Source & Free");
        openSource.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        openSource.setTextFill(Color.web("#26C6DA"));
        openSource.setPadding(new Insets(12,0,0,0));
        rightPanel.getChildren().setAll(avatarCard, accountCard, statusCard, profileBtnsCard, openSource);
        mainLayout.setRight(rightPanel);

        // --- DIŞ ÇERÇEVE ---
        StackPane outerFrame = new StackPane(mainLayout);
        outerFrame.setStyle("-fx-border-color: linear-gradient(to bottom right, #26C6DA, #B2EBF2); -fx-border-width: 4; -fx-border-radius: 38; -fx-background-radius: 38; -fx-effect: dropshadow(gaussian, #26C6DA66, 32, 0.22, 0, 10);");
        // Responsive scaling
        outerFrame.widthProperty().addListener((obs, oldVal, newVal) -> {
            double scale = Math.max(0.7, Math.min(outerFrame.getWidth() / 1800.0, outerFrame.getHeight() / 1000.0));
            mainLayout.setScaleX(scale > 1.3 ? 1.3 : scale);
            mainLayout.setScaleY(scale > 1.3 ? 1.3 : scale);
        });
        outerFrame.heightProperty().addListener((obs, oldVal, newVal) -> {
            double scale = Math.max(0.7, Math.min(outerFrame.getWidth() / 1800.0, outerFrame.getHeight() / 1000.0));
            mainLayout.setScaleX(scale > 1.3 ? 1.3 : scale);
            mainLayout.setScaleY(scale > 1.3 ? 1.3 : scale);
        });
        return outerFrame;
    }

    private static VBox createSidebarButton(String icon, String label, boolean selected, int iconSize) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI Emoji", iconSize));
        iconLabel.setTextFill(selected ? Color.web("#26C6DA") : Color.web("#B0BEC5"));
        Label textLabel = new Label(label);
        textLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        textLabel.setTextFill(selected ? Color.web("#26C6DA") : Color.web("#B0BEC5"));
        box.getChildren().addAll(iconLabel, textLabel);
        box.setStyle(selected ? "-fx-background-color: #163040; -fx-background-radius: 16;" : "-fx-background-color: transparent;");
        box.setPadding(new Insets(12, 0, 12, 0));
        box.setOnMouseEntered(e -> {
            iconLabel.setTextFill(Color.web("#26C6DA"));
            textLabel.setTextFill(Color.web("#26C6DA"));
            box.setStyle("-fx-background-color: #163040; -fx-background-radius: 16;");
        });
        box.setOnMouseExited(e -> {
            iconLabel.setTextFill(selected ? Color.web("#26C6DA") : Color.web("#B0BEC5"));
            textLabel.setTextFill(selected ? Color.web("#26C6DA") : Color.web("#B0BEC5"));
            box.setStyle(selected ? "-fx-background-color: #163040; -fx-background-radius: 16;" : "-fx-background-color: transparent;");
        });
        return box;
    }

    private static JFXButton createMainActionButton(String icon, String label, Color bg, Color fg, int iconSize, int fontSize) {
        JFXButton btn = new JFXButton(icon + "  " + label);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, fontSize));
        btn.setStyle(
            "-fx-background-color: " + toRgba(bg, 1.0) + ";" +
            "-fx-text-fill: " + toRgba(fg, 1.0) + ";" +
            "-fx-background-radius: 22;" +
            "-fx-padding: 32 64;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 12, 0.1, 0, 3);"
        );
        btn.setRipplerFill(bg.deriveColor(1,1,1,0.18));
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + toRgba(bg.brighter(), 1.0) + ";" +
            "-fx-text-fill: " + toRgba(fg, 1.0) + ";" +
            "-fx-background-radius: 22;" +
            "-fx-padding: 32 64;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 20, 0.15, 0, 5);"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + toRgba(bg, 1.0) + ";" +
            "-fx-text-fill: " + toRgba(fg, 1.0) + ";" +
            "-fx-background-radius: 22;" +
            "-fx-padding: 32 64;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 12, 0.1, 0, 3);"
        ));
        return btn;
    }

    private static VBox createScheduledMeetingsPanel(int titleSize, int nameSize, int timeSize) {
        VBox panel = new VBox(24);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPadding(new Insets(36));
        panel.setMinWidth(440);
        panel.setStyle("-fx-background-color: rgba(20,40,60,0.90); -fx-background-radius: 22; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2.5; -fx-border-radius: 22; -fx-effect: dropshadow(gaussian, #26C6DA44, 8, 0.12, 0, 2);");
        Label title = new Label("Scheduled Meeting");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, titleSize));
        title.setTextFill(Color.WHITE);
        panel.getChildren().add(title);
        for (int i = 0; i < 3; i++) {
            VBox meeting = new VBox(8);
            meeting.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-background-radius: 14;");
            meeting.setPadding(new Insets(16, 18, 16, 18));
            Label name = new Label("Daily Design Session");
            name.setFont(Font.font("Segoe UI", FontWeight.BOLD, nameSize));
            name.setTextFill(Color.WHITE);
            Label time = new Label("12:00 - 2:30 pm  |  starts in 16 hours");
            time.setFont(Font.font("Segoe UI", FontWeight.NORMAL, timeSize));
            time.setTextFill(Color.web("#B0BEC5"));
            HBox actions = new HBox(12);
            actions.setAlignment(Pos.CENTER_LEFT);
            JFXButton startBtn = new JFXButton("Start");
            startBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            startBtn.setStyle("-fx-background-color: #26C6DA; -fx-text-fill: #fff; -fx-background-radius: 10; -fx-padding: 8 28; -fx-cursor: hand;");
            JFXButton moreBtn = new JFXButton("⋮");
            moreBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #B0BEC5; -fx-font-size: 24; -fx-cursor: hand;");
            actions.getChildren().addAll(startBtn, moreBtn);
            meeting.getChildren().addAll(name, time, actions);
            panel.getChildren().add(meeting);
        }
        return panel;
    }

    private static VBox createClockCard(int clockSize, int dateSize, int btnSize) {
        VBox card = new VBox(28);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(48));
        card.setMinWidth(420);
        card.setStyle("-fx-background-color: linear-gradient(135deg, #1a2a36 60%, #26C6DA 100%); -fx-background-radius: 22; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2.5; -fx-border-radius: 22; -fx-effect: dropshadow(gaussian, #26C6DA44, 8, 0.12, 0, 2);");
        Label clock = new Label();
        clock.setFont(Font.font("Segoe UI", FontWeight.BOLD, clockSize));
        clock.setTextFill(Color.WHITE);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            clock.setText(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        Label date = new Label(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE")));
        date.setFont(Font.font("Segoe UI", FontWeight.NORMAL, dateSize));
        date.setTextFill(Color.WHITE);
        JFXButton scheduleBtn = new JFXButton("\uD83D\uDCC5  Schedule a meeting");
        scheduleBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, btnSize));
        scheduleBtn.setStyle("-fx-background-color: #26C6DA; -fx-text-fill: #fff; -fx-background-radius: 10; -fx-padding: 16 32; -fx-cursor: hand;");
        card.getChildren().addAll(clock, date, scheduleBtn);
        return card;
=======
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");
=======
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: transparent;");
>>>>>>> 1a5b1f3 (Updates & Changes)

        // Responsive scaling for center content
        VBox centerContent = new VBox(32);
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.setPadding(new Insets(64, 0, 0, 0));
        centerContent.setId("centerContent");

        // --- TOP BAR ---
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(0, 32, 0, 0));
        topBar.setPrefHeight(64);
        topBar.setStyle(
            "-fx-background-color: #1a2a36;" +
            "-fx-background-radius: 0 0 18 18;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 8, 0.1, 0, 2);"
        );
        Label safeRoomLogo = new Label("SafeRoom");
        safeRoomLogo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        safeRoomLogo.setTextFill(Color.WHITE);
        Region topSpacerLeft = new Region();
        Region topSpacerRight = new Region();
        HBox.setHgrow(topSpacerLeft, Priority.ALWAYS);
        HBox.setHgrow(topSpacerRight, Priority.ALWAYS);
        Label avatar = new Label(username.substring(0,1).toUpperCase());
        avatar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        avatar.setTextFill(Color.WHITE);
        avatar.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 20; -fx-padding: 12 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 2, 0.1, 0, 1);");
        topBar.getChildren().addAll(topSpacerLeft, safeRoomLogo, topSpacerRight, avatar);
        mainLayout.setTop(topBar);

        // --- LEFT SIDEBAR ---
        VBox sidebar = new VBox(24);
        sidebar.setPadding(new Insets(48, 0, 48, 0));
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: rgba(20,40,60,0.96); -fx-background-radius: 0 32 32 0;");
        sidebar.setPrefWidth(120);
        sidebar.getChildren().addAll(
            createSidebarButton("\uD83D\uDCFA", "New Meeting", true, 36),
            createSidebarButton("\u2795", "Join", false, 36),
            createSidebarButton("\uD83D\uDCC5", "Schedule", false, 36),
            createSidebarButton("\u2B06", "Share", false, 36),
            createSidebarButton("\u2699", "Settings", false, 36),
            createSidebarButton("\u2753", "Help", false, 36)
        );
        mainLayout.setLeft(sidebar);

        // --- CENTER CONTENT ---
        HBox bigButtons = new HBox(32);
        bigButtons.setAlignment(Pos.CENTER);
        JFXButton btnCreate = createMainActionButton("\uD83D\uDCFA", "Create Meeting", Color.web("#FFD600"), Color.web("#222"), 28, 28);
        JFXButton btnJoin = createMainActionButton("\u2795", "Join Meeting", Color.web("#26C6DA"), Color.web("#222"), 28, 28);
        bigButtons.getChildren().addAll(btnCreate, btnJoin);
        btnCreate.setPrefHeight(64);
        btnJoin.setPrefHeight(64);
        btnCreate.setMinWidth(220);
        btnJoin.setMinWidth(220);

        HBox midRow = new HBox(48);
        midRow.setAlignment(Pos.CENTER);
        VBox scheduledMeetings = createScheduledMeetingsPanel(28, 22, 18);
        VBox clockCard = createClockCard(72, 32, 24);
        midRow.setStyle("-fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2.5; -fx-border-radius: 22; -fx-background-radius: 22; -fx-effect: dropshadow(gaussian, #26C6DA44, 12, 0.18, 0, 3);");
        midRow.getChildren().addAll(scheduledMeetings, clockCard);

        centerContent.getChildren().setAll(bigButtons, midRow);
        centerContent.setStyle("-fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 3; -fx-border-radius: 32; -fx-background-radius: 32; -fx-effect: dropshadow(gaussian, #26C6DA44, 18, 0.18, 0, 6);");
        mainLayout.setCenter(centerContent);

        // --- RIGHT PROFILE PANEL (modern, like PNG) ---
        VBox rightPanel = new VBox(24);
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setPadding(new Insets(36, 24, 0, 0));
        rightPanel.setStyle("-fx-background-color: transparent; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 3; -fx-border-radius: 32; -fx-background-radius: 32; -fx-effect: dropshadow(gaussian, #26C6DA44, 18, 0.18, 0, 6);");
        rightPanel.setPrefWidth(340);
        // Avatar + username card
        VBox avatarCard = new VBox(8);
        avatarCard.setAlignment(Pos.CENTER);
        avatarCard.setPadding(new Insets(24, 12, 24, 12));
        avatarCard.setStyle("-fx-background-color: rgba(20,40,60,0.97); -fx-background-radius: 18; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2; -fx-border-radius: 18; -fx-effect: dropshadow(gaussian, #26C6DA44, 8, 0.12, 0, 2);");
        StackPane avatarPane = new StackPane();
        avatarPane.setPrefSize(70, 70);
        avatarPane.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 35; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 2, 0.1, 0, 1);");
        Label avatarBig = new Label(username.substring(0,1).toUpperCase());
        avatarBig.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        avatarBig.setTextFill(Color.WHITE);
        avatarPane.getChildren().add(avatarBig);
        Label userLabel = new Label(username);
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        userLabel.setTextFill(Color.WHITE);
        avatarCard.getChildren().addAll(avatarPane, userLabel);
        // Account switcher card
        VBox accountCard = new VBox(10);
        accountCard.setAlignment(Pos.CENTER_LEFT);
        accountCard.setPadding(new Insets(18, 18, 18, 18));
        accountCard.setStyle("-fx-background-color: rgba(20,40,60,0.93); -fx-background-radius: 14; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, #26C6DA44, 6, 0.10, 0, 1);");
        ComboBox<String> switchAccount = new ComboBox<>();
        switchAccount.getItems().addAll(username + "@saferoom.com", "demo@saferoom.com");
        switchAccount.setValue(username + "@saferoom.com");
        switchAccount.setStyle("-fx-background-radius: 10; -fx-background-color: #223344; -fx-text-fill: #fff; -fx-font-size: 14; -fx-padding: 4 10;");
        accountCard.getChildren().addAll(new Label("Switch Account:"), switchAccount);
        // Status card
        VBox statusCard = new VBox(8);
        statusCard.setAlignment(Pos.CENTER_LEFT);
        statusCard.setPadding(new Insets(16, 18, 16, 18));
        statusCard.setStyle("-fx-background-color: rgba(20,40,60,0.93); -fx-background-radius: 14; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, #26C6DA44, 6, 0.10, 0, 1);");
        statusCard.getChildren().addAll(
            createStatusRow("Available", Color.LIMEGREEN),
            createStatusRow("Away", Color.ORANGE),
            createStatusRow("Do not disturb", Color.RED)
        );
        // Profile/settings/help buttons card
        VBox profileBtnsCard = new VBox(18);
        profileBtnsCard.setAlignment(Pos.CENTER_LEFT);
        profileBtnsCard.setPadding(new Insets(16, 18, 16, 18));
        profileBtnsCard.setStyle("-fx-background-color: rgba(20,40,60,0.93); -fx-background-radius: 14; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2; -fx-border-radius: 14; -fx-effect: dropshadow(gaussian, #26C6DA44, 6, 0.10, 0, 1);");
        // Yan yana My Profile ve Help
        HBox profileHelpRow = new HBox(18);
        profileHelpRow.setAlignment(Pos.CENTER_LEFT);
        JFXButton btnProfile = createProfileBtn("My Profile");
        JFXButton btnHelp = createProfileBtn("Help");
        profileHelpRow.getChildren().addAll(btnProfile, btnHelp);
        // Altına geniş Sign Out
        JFXButton btnSignOut = createSignOutBtn("Sign Out");
        profileBtnsCard.getChildren().setAll(profileHelpRow, btnSignOut);
        // Open source label
        Label openSource = new Label("Open Source & Free");
        openSource.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        openSource.setTextFill(Color.web("#26C6DA"));
        openSource.setPadding(new Insets(12,0,0,0));
        rightPanel.getChildren().setAll(avatarCard, accountCard, statusCard, profileBtnsCard, openSource);
        mainLayout.setRight(rightPanel);

        // --- DIŞ ÇERÇEVE ---
        StackPane outerFrame = new StackPane();
        outerFrame.setStyle("-fx-background-color: transparent; -fx-border-color: linear-gradient(to bottom right, #26C6DA, #B2EBF2); -fx-border-width: 4; -fx-border-radius: 28; -fx-background-radius: 28; -fx-effect: dropshadow(gaussian, #26C6DA66, 24, 0.18, 0, 6);");

        // Ana layout'u bir Group'a koy
        Group mainGroup = new Group(mainLayout);
        StackPane.setAlignment(mainGroup, Pos.CENTER);
        outerFrame.getChildren().add(mainGroup);
        outerFrame.setPadding(new Insets(0));
        outerFrame.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Responsive scaling: Group'u pencereye sığacak şekilde scale et
        final double BASE_WIDTH = 1800.0;
        final double BASE_HEIGHT = 1000.0;
        Runnable doScaleAndCenter = () -> {
            double windowWidth = outerFrame.getWidth();
            double windowHeight = outerFrame.getHeight();
            if (windowWidth == 0 || windowHeight == 0) return;
            double scale = Math.min(windowWidth / BASE_WIDTH, windowHeight / BASE_HEIGHT);
            scale = Math.min(scale, 1.0);
            mainGroup.setScaleX(scale);
            mainGroup.setScaleY(scale);
        };
        outerFrame.widthProperty().addListener((obs, oldVal, newVal) -> doScaleAndCenter.run());
        outerFrame.heightProperty().addListener((obs, oldVal, newVal) -> doScaleAndCenter.run());
        outerFrame.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((o, ov, nv) -> doScaleAndCenter.run());
                newScene.heightProperty().addListener((o, ov, nv) -> doScaleAndCenter.run());
                newScene.windowProperty().addListener((o2, ow, nw) -> {
                    if (nw != null) {
                        nw.widthProperty().addListener((o3, ov3, nv3) -> doScaleAndCenter.run());
                        nw.heightProperty().addListener((o3, ov3, nv3) -> doScaleAndCenter.run());
                        javafx.application.Platform.runLater(doScaleAndCenter);
                    }
                });
            }
        });
        return outerFrame;
    }

    private static VBox createSidebarButton(String icon, String label, boolean selected, int iconSize) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI Emoji", iconSize));
        iconLabel.setTextFill(selected ? Color.web("#26C6DA") : Color.web("#B0BEC5"));
        Label textLabel = new Label(label);
        textLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        textLabel.setTextFill(selected ? Color.web("#26C6DA") : Color.web("#B0BEC5"));
        box.getChildren().addAll(iconLabel, textLabel);
        box.setStyle(selected ? "-fx-background-color: #163040; -fx-background-radius: 16;" : "-fx-background-color: transparent;");
        box.setPadding(new Insets(12, 0, 12, 0));
        box.setOnMouseEntered(e -> {
            iconLabel.setTextFill(Color.web("#26C6DA"));
            textLabel.setTextFill(Color.web("#26C6DA"));
            box.setStyle("-fx-background-color: #163040; -fx-background-radius: 16;");
        });
        box.setOnMouseExited(e -> {
            iconLabel.setTextFill(selected ? Color.web("#26C6DA") : Color.web("#B0BEC5"));
            textLabel.setTextFill(selected ? Color.web("#26C6DA") : Color.web("#B0BEC5"));
            box.setStyle(selected ? "-fx-background-color: #163040; -fx-background-radius: 16;" : "-fx-background-color: transparent;");
        });
        return box;
    }

    private static JFXButton createMainActionButton(String icon, String label, Color bg, Color fg, int iconSize, int fontSize) {
        JFXButton btn = new JFXButton(icon + "  " + label);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, fontSize));
        btn.setStyle(
            "-fx-background-color: " + toRgba(bg, 1.0) + ";" +
            "-fx-text-fill: " + toRgba(fg, 1.0) + ";" +
            "-fx-background-radius: 22;" +
            "-fx-padding: 32 64;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 12, 0.1, 0, 3);"
        );
        btn.setRipplerFill(bg.deriveColor(1,1,1,0.18));
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + toRgba(bg.brighter(), 1.0) + ";" +
            "-fx-text-fill: " + toRgba(fg, 1.0) + ";" +
            "-fx-background-radius: 22;" +
            "-fx-padding: 32 64;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 20, 0.15, 0, 5);"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + toRgba(bg, 1.0) + ";" +
            "-fx-text-fill: " + toRgba(fg, 1.0) + ";" +
            "-fx-background-radius: 22;" +
            "-fx-padding: 32 64;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 12, 0.1, 0, 3);"
        ));
        return btn;
    }

<<<<<<< HEAD
    private static JFXButton createBigButton(String icon, String label, Color color, int size) {
        VBox box = new VBox(24);
        box.setAlignment(Pos.CENTER);
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI Emoji", size / 2));
        iconLabel.setTextFill(Color.WHITE);
        JFXButton button = new JFXButton();
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setRipplerFill(color.deriveColor(1,1,1,0.18));
        button.setStyle(
            "-fx-background-color: " + toRgba(color, 1.0) + ";" +
            "-fx-background-radius: 48;" +
            "-fx-min-width: " + size + "px;" +
            "-fx-min-height: " + size + "px;" +
            "-fx-max-width: " + (size+40) + "px;" +
            "-fx-max-height: " + (size+40) + "px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.32), 32, 0.22, 0, 8);"
        );
        button.setPrefSize(size, size);
        button.setGraphic(iconLabel);
        Label text = new Label(label);
        text.setFont(Font.font("Segoe UI", FontWeight.BOLD, size / 5));
        text.setTextFill(Color.web("#E0E0E0"));
        box.getChildren().addAll(button, text);
        VBox.setMargin(button, new Insets(0,0,0,0));
        VBox.setMargin(text, new Insets(24,0,0,0));
        JFXButton wrapper = new JFXButton();
        wrapper.setGraphic(box);
        wrapper.setStyle("-fx-background-color: transparent;");
        wrapper.setPrefSize(size+60, size+90);
        // Enhanced hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: " + toRgba(color.brighter(), 1.0) + ";" +
                "-fx-background-radius: 48;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 48, 0.32, 0, 12);"
            );
            button.setScaleX(1.08);
            button.setScaleY(1.08);
        });
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + toRgba(color, 1.0) + ";" +
                "-fx-background-radius: 48;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.32), 32, 0.22, 0, 8);"
            );
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
        return wrapper;
>>>>>>> c2efb97 (Initial commit)
=======
    private static VBox createScheduledMeetingsPanel(int titleSize, int nameSize, int timeSize) {
        VBox panel = new VBox(24);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPadding(new Insets(36));
        panel.setMinWidth(440);
        panel.setStyle("-fx-background-color: rgba(20,40,60,0.90); -fx-background-radius: 22; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2.5; -fx-border-radius: 22; -fx-effect: dropshadow(gaussian, #26C6DA44, 8, 0.12, 0, 2);");
        Label title = new Label("Scheduled Meeting");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, titleSize));
        title.setTextFill(Color.WHITE);
        panel.getChildren().add(title);
        for (int i = 0; i < 3; i++) {
            VBox meeting = new VBox(8);
            meeting.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-background-radius: 14;");
            meeting.setPadding(new Insets(16, 18, 16, 18));
            Label name = new Label("Daily Design Session");
            name.setFont(Font.font("Segoe UI", FontWeight.BOLD, nameSize));
            name.setTextFill(Color.WHITE);
            Label time = new Label("12:00 - 2:30 pm  |  starts in 16 hours");
            time.setFont(Font.font("Segoe UI", FontWeight.NORMAL, timeSize));
            time.setTextFill(Color.web("#B0BEC5"));
            HBox actions = new HBox(12);
            actions.setAlignment(Pos.CENTER_LEFT);
            JFXButton startBtn = new JFXButton("Start");
            startBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            startBtn.setStyle("-fx-background-color: #26C6DA; -fx-text-fill: #fff; -fx-background-radius: 10; -fx-padding: 8 28; -fx-cursor: hand;");
            JFXButton moreBtn = new JFXButton("⋮");
            moreBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #B0BEC5; -fx-font-size: 24; -fx-cursor: hand;");
            actions.getChildren().addAll(startBtn, moreBtn);
            meeting.getChildren().addAll(name, time, actions);
            panel.getChildren().add(meeting);
        }
        return panel;
    }

    private static VBox createClockCard(int clockSize, int dateSize, int btnSize) {
        VBox card = new VBox(28);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(48));
        card.setMinWidth(420);
        card.setStyle("-fx-background-color: linear-gradient(135deg, #1a2a36 60%, #26C6DA 100%); -fx-background-radius: 22; -fx-border-color: linear-gradient(to bottom right, #26C6DA88, #B2EBF288); -fx-border-width: 2.5; -fx-border-radius: 22; -fx-effect: dropshadow(gaussian, #26C6DA44, 8, 0.12, 0, 2);");
        Label clock = new Label();
        clock.setFont(Font.font("Segoe UI", FontWeight.BOLD, clockSize));
        clock.setTextFill(Color.WHITE);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            clock.setText(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        Label date = new Label(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE")));
        date.setFont(Font.font("Segoe UI", FontWeight.NORMAL, dateSize));
        date.setTextFill(Color.WHITE);
        JFXButton scheduleBtn = new JFXButton("\uD83D\uDCC5  Schedule a meeting");
        scheduleBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, btnSize));
        scheduleBtn.setStyle("-fx-background-color: #26C6DA; -fx-text-fill: #fff; -fx-background-radius: 10; -fx-padding: 16 32; -fx-cursor: hand;");
        card.getChildren().addAll(clock, date, scheduleBtn);
        return card;
>>>>>>> 1a5b1f3 (Updates & Changes)
    }

    private static String toRgba(Color c, double alpha) {
        return String.format("rgba(%d,%d,%d,%.2f)", (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255), alpha);
    }
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 1a5b1f3 (Updates & Changes)

    private static HBox createStatusRow(String label, Color color) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(7, color);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));
        lbl.setTextFill(Color.WHITE);
        row.getChildren().addAll(dot, lbl);
        return row;
    }

    private static JFXButton createProfileBtn(String label) {
        JFXButton btn = new JFXButton(label);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        btn.setStyle("-fx-background-color: #223344; -fx-text-fill: #fff; -fx-background-radius: 10; -fx-padding: 8 24; -fx-cursor: hand;");
        btn.setRipplerFill(Color.web("#26C6DA", 0.18));
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #26C6DA; -fx-text-fill: #fff; -fx-background-radius: 10; -fx-padding: 8 24; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #223344; -fx-text-fill: #fff; -fx-background-radius: 10; -fx-padding: 8 24; -fx-cursor: hand;"));
        return btn;
    }

    private static JFXButton createSignOutBtn(String label) {
        JFXButton btn = new JFXButton(label);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        btn.setStyle("-fx-background-color: #e53935; -fx-text-fill: #fff; -fx-background-radius: 10; -fx-padding: 12 0; -fx-cursor: hand; -fx-pref-width: 180px;");
        btn.setRipplerFill(Color.web("#fff", 0.18));
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setMinWidth(180);
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #b71c1c; -fx-text-fill: #fff; -fx-background-radius: 10; -fx-padding: 12 0; -fx-cursor: hand; -fx-pref-width: 180px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #e53935; -fx-text-fill: #fff; -fx-background-radius: 10; -fx-padding: 12 0; -fx-cursor: hand; -fx-pref-width: 180px;"));
        return btn;
    }
<<<<<<< HEAD
=======
>>>>>>> c2efb97 (Initial commit)
=======
>>>>>>> 1a5b1f3 (Updates & Changes)
}
