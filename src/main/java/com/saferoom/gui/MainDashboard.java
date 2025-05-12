package com.saferoom.gui;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class MainDashboard {
    public static Node createDashboard(String username) {
        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: transparent;");

        // Top Bar
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(0, 32, 0, 0));
        topBar.setPrefHeight(64);
        topBar.setStyle("-fx-background-color: #1a2a36; -fx-background-radius: 0 0 18 18;");
        Label safeRoomLogo = new Label("SafeRoom");
        safeRoomLogo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        safeRoomLogo.setTextFill(Color.WHITE);
        Region topSpacerLeft = new Region();
        Region topSpacerRight = new Region();
        HBox.setHgrow(topSpacerLeft, Priority.ALWAYS);
        HBox.setHgrow(topSpacerRight, Priority.ALWAYS);
        Label avatar = new Label(username.substring(0, 1).toUpperCase());
        avatar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        avatar.setTextFill(Color.WHITE);
        avatar.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 20; -fx-padding: 12 20;");
        topBar.getChildren().addAll(topSpacerLeft, safeRoomLogo, topSpacerRight, avatar);
        mainLayout.setTop(topBar);

        // Sidebar
        VBox sidebar = new VBox(24);
        sidebar.setPadding(new Insets(48, 0, 48, 0));
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: rgba(20,40,60,0.96); -fx-background-radius: 0 32 32 0;");
        sidebar.setPrefWidth(120);
        sidebar.getChildren().addAll(
                createSidebarButton("\uD83D\uDCFA", "New Meeting"),
                createSidebarButton("\u2795", "Join")
        );
        mainLayout.setLeft(sidebar);

        // Center Content
        VBox centerContent = new VBox(32);
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.setPadding(new Insets(64, 0, 0, 0));
        JFXButton btnCreate = new JFXButton("\uD83D\uDCFA Create Meeting");
        btnCreate.setStyle("-fx-background-color: #FFD600; -fx-text-fill: #222; -fx-background-radius: 22; -fx-padding: 16 32;");
        JFXButton btnJoin = new JFXButton("\u2795 Join Meeting");
        btnJoin.setStyle("-fx-background-color: #26C6DA; -fx-text-fill: #222; -fx-background-radius: 22; -fx-padding: 16 32;");
        HBox bigButtons = new HBox(32, btnCreate, btnJoin);
        bigButtons.setAlignment(Pos.CENTER);
        centerContent.getChildren().add(bigButtons);
        mainLayout.setCenter(centerContent);

        // Right Panel
        VBox rightPanel = new VBox(24);
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setPadding(new Insets(36, 24, 0, 0));
        rightPanel.setStyle("-fx-background-color: transparent;");
        Label avatarBig = new Label(username.substring(0,1).toUpperCase());
        avatarBig.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        avatarBig.setTextFill(Color.WHITE);
        VBox avatarCard = new VBox(avatarBig);
        avatarCard.setAlignment(Pos.CENTER);
        avatarCard.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 35; -fx-padding: 24;");
        rightPanel.getChildren().addAll(avatarCard);
        mainLayout.setRight(rightPanel);

        // Outer Frame
        StackPane outerFrame = new StackPane(new Group(mainLayout));
        outerFrame.setStyle("-fx-border-color: linear-gradient(to bottom right, #26C6DA, #B2EBF2); -fx-border-width: 4;");
        return outerFrame;
    }

    private static VBox createSidebarButton(String icon, String label) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI Emoji", 36));
        iconLabel.setTextFill(Color.web("#26C6DA"));
        Label textLabel = new Label(label);
        textLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        textLabel.setTextFill(Color.web("#26C6DA"));
        box.getChildren().addAll(iconLabel, textLabel);
        box.setPadding(new Insets(12));
        return box;
    }
} 
