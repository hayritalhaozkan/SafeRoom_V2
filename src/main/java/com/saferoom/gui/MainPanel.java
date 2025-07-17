package com.saferoom.gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainPanel extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Use StackPane for star effect background
        BorderPane root = new BorderPane();
        VBox sidebar = createSidebar();
        HBox sidebarContainer = new HBox();
        sidebarContainer.setAlignment(Pos.TOP_LEFT);
        sidebarContainer.getChildren().add(sidebar);
        sidebarContainer.setPadding(new Insets(0, 0, 60, 40)); // top, right, bottom, left
        sidebar.setPadding(new Insets(30, 0, 30, 0));
        root.setLeft(sidebarContainer);

        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Main content panel (cards) with external padding for centering
        StackPane mainContentWrapper = new StackPane();
        GridPane mainContent = createMainContent();
        mainContentWrapper.getChildren().add(mainContent);
        mainContentWrapper.setPadding(new Insets(0, 60, 60, 60)); // top, right, bottom, left
        root.setCenter(mainContentWrapper);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #161D2B, #162032);");

        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setTitle("SafeRoom");
        primaryStage.setScene(scene);
        // Minimum window size (to keep responsive design intact)
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(750);
        primaryStage.show();

        // Responsive: Sidebar width proportional to window
        sidebar.prefWidthProperty().bind(scene.widthProperty().multiply(0.18));
        // Responsive: TopBar height proportional to window
        topBar.prefHeightProperty().bind(scene.heightProperty().multiply(0.12));
        // Responsive: MainContent card sizes proportional to window
        mainContent.paddingProperty().bind(Bindings.createObjectBinding(
            () -> new Insets(scene.getHeight() * 0.03, scene.getWidth() * 0.03, scene.getHeight() * 0.03, scene.getWidth() * 0.03),
            scene.widthProperty(), scene.heightProperty()
        ));
    }
    
    

    // Left menu (sidebar)
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setSpacing(30);
        sidebar.setStyle("-fx-background-color: #0B111D; -fx-border-color: #3A4A5D; -fx-border-width: 0 1 0 0; -fx-border-radius: 20; -fx-background-radius: 20;");
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setFillWidth(true);

       
        VBox menu = new VBox(20,
            createSidebarButtonWithIcon("     Home", "/icons/home.png"),
            createSidebarButtonWithIcon("     Messages", "/icons/messages.png"),
            createSidebarButtonWithIcon("     Friends", "/icons/friends.png"),
            createSidebarButtonWithIcon("     Files", "/icons/files.png"),
            createSidebarButtonWithIcon("     More", "/icons/more.png")
        );
        menu.setAlignment(Pos.TOP_CENTER);
        menu.setPadding(new Insets(0, 0, 0, 15));
        VBox.setVgrow(menu, Priority.ALWAYS);

        // Preferences at the bottom
        Button preferences = createSidebarButtonWithIcon("     Preferences", "/icons/options.png");
        preferences.setStyle("-fx-background-color: transparent; -fx-text-fill:rgb(254, 254, 254); -fx-font-size: 14;");
        VBox.setMargin(preferences, new Insets(0, 0, 0, 15));
        VBox.setVgrow(preferences, Priority.NEVER);

        sidebar.getChildren().addAll(menu, preferences);
        return sidebar;
    }

    private Button createSidebarButtonWithIcon(String text, String iconPath) {
        ImageView icon = createIconView(iconPath, 24, 24);
        Button btn = new Button(text, icon);
        btn.setContentDisplay(ContentDisplay.LEFT);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill:rgb(255, 255, 255); -fx-font-size: 16; -fx-font-family: 'Konkhmer Sleokchher';");
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: #22304A; -fx-text-fill:rgb(255, 255, 255); -fx-font-size: 16; -fx-font-family: 'Konkhmer Sleokchher';");
            btn.setScaleX(1.07);
            btn.setScaleY(1.07);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill:rgb(255, 255, 255); -fx-font-size: 16; -fx-font-family: 'Konkhmer Sleokchher';");
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
        btn.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(btn, Priority.ALWAYS);
        return btn;
    }

    private ImageView createIconView(String path, int width, int height) {
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(path)));
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        icon.setPreserveRatio(true);
        return icon;
    }

    // Top search bar and icons
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setSpacing(50);
        topBar.setStyle("-fx-background-color: transparent;");
        topBar.setAlignment(Pos.CENTER);
        
        // logo
        ImageView logo = createIconView("/logo/saferoom_logo.png", 150, 150);
        VBox logoBox = new VBox(logo);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setSpacing(5);
        logoBox.setPadding(new Insets(10, 0, 0, 40));

        // Search box and icon
        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_RIGHT);
        TextField search = new TextField();
        search.setPromptText("Search Room");
        search.setStyle("-fx-background-radius: 10; -fx-background-color: #2A3547; -fx-text-fill: #fff; -fx-font-size: 16; -fx-font-weight: bold;");
        HBox.setHgrow(search, Priority.ALWAYS);
        ImageView searchIcon = createIconView("/icons/search.png", 20, 20);
        searchBox.getChildren().addAll(search, searchIcon);
        searchBox.setPadding(new Insets(0,0,0,70));

        // Top right icons
        HBox icons = new HBox(30,
            createTopIcon("/icons/rings.png"),
            createTopIcon("/icons/profile.png"),
            createTopIcon("/icons/exit.png")
        );
        icons.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(icons, Priority.ALWAYS);
        icons.setPadding(new Insets(0,60,0,0));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(logoBox,searchBox, spacer, icons);
        return topBar;
    }

    private ImageView createTopIcon(String iconPath) {
        ImageView icon = createIconView(iconPath, 24, 24);
        // Hover effect: grow and shadow
        icon.setOnMouseEntered(e -> {
            icon.setScaleX(1.15);
            icon.setScaleY(1.15);
            icon.setStyle("-fx-effect: dropshadow(gaussian, #B0C4DE, 10, 0.5, 0, 2);");
            icon.setOpacity(0.8);
            icon.setCursor(javafx.scene.Cursor.HAND);
        });
        icon.setOnMouseExited(e -> {
            icon.setScaleX(1.0);
            icon.setScaleY(1.0);
            icon.setStyle("");
            icon.setOpacity(1.0);
            icon.setCursor(javafx.scene.Cursor.DEFAULT);
        });
        return icon;
    }

    // Main content (4 large buttons/panels)
    private GridPane createMainContent() {
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(40);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #0B111D; -fx-border-color: #3A4A5D; -fx-border-radius: 20; -fx-background-radius: 20;");

        VBox card1 = createMainCard("NEW MEETING", "Instant Secure Room");
        VBox card2 = createMainCard("JOIN ROOM", "Connect to Tunnel");
        VBox card3 = createMainCard("SCHEDULE ROOM", "Programmatic Sync");
        VBox card4 = createMainCard("ENCRYPTED FILES", "File Vault");

        // Responsive: Cards grow proportionally with window
        card1.maxWidthProperty().bind(grid.widthProperty().multiply(0.4));
        card2.maxWidthProperty().bind(grid.widthProperty().multiply(0.4));
        card3.maxWidthProperty().bind(grid.widthProperty().multiply(0.4));
        card4.maxWidthProperty().bind(grid.widthProperty().multiply(0.4));
        card1.maxHeightProperty().bind(grid.heightProperty().multiply(0.4));
        card2.maxHeightProperty().bind(grid.heightProperty().multiply(0.4));
        card3.maxHeightProperty().bind(grid.heightProperty().multiply(0.4));
        card4.maxHeightProperty().bind(grid.heightProperty().multiply(0.4));

        grid.add(card1, 0, 0);
        grid.add(card2, 1, 0);
        grid.add(card3, 0, 1);
        grid.add(card4, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);
        grid.getRowConstraints().addAll(row1, row2);

        return grid;
    }

    private VBox createMainCard(String title, String subtitle) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #232F45; -fx-background-radius: 25; -fx-effect: dropshadow(gaussian, #00000055, 10, 0.5, 0, 2); -fx-cursor: hand;");
        card.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Hover effect: shadow and scale
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #232F45; -fx-background-radius: 25; -fx-effect: dropshadow(gaussian, #B0C4DE, 10, 0.3, 0, 2); -fx-cursor: hand;");
            card.setScaleX(1.03);
            card.setScaleY(1.03);
        });
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: #232F45; -fx-background-radius: 25; -fx-effect: dropshadow(gaussian, #00000055, 10, 0.5, 0, 2); -fx-cursor: hand;");
            card.setScaleX(1.0);
            card.setScaleY(1.0);
        });

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Orbitron", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);

        card.getChildren().addAll(titleLabel, subtitleLabel);
        VBox.setVgrow(card, Priority.ALWAYS);
        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
} 