package com.saferoom.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import com.saferoom.db.DBManager;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.saferoom.gui.MainDashboard; // Import the MainDashboard class


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VantaEffectFXMouse extends Application {

    private static final int POINT_COUNT = 200;
    private static final double BASE_MAX_DISTANCE = 150;
    private static final Color BG_COLOR = Color.BLACK;
    private static final Color BLUE_COLOR = Color.web("#00bfff");
    private static final Color RED_COLOR = Color.web("#ff0000");
    private static final Color GREEN_COLOR = Color.web("#00ff00");

    enum ErrorType {
        NONE, EMPTY_FIELDS, USER_NOT_FOUND, BLOCKED;
    }
    private ErrorType errorState = ErrorType.NONE;

    public static final List<Point3D> points = new ArrayList<>();
    private final Random random = new Random();

    private double mouseX, mouseY;
    private double width = 800, height = 600;

    private double cameraZoom = 1.7;

    private final double fov = 600;
    private double currentAngleX = 0, currentAngleY = 0;


    public static int mode = 0;
    private double scaleFactor = 1.0;

    private VBox menuOverlay;
    private Group root;
    private Node dashboardNode;
    private boolean isDashboardVisible = false;

    @Override
    public void start(Stage primaryStage) {
        root = new Group();
        Scene scene = new Scene(root, width, height, false);

        Canvas canvas = new Canvas(width, height);
        canvas.setFocusTraversable(true);
        root.getChildren().add(canvas);

        menuOverlay = createMenuOverlay();
        menuOverlay.setOpacity(0);
        root.getChildren().add(menuOverlay);

        // Add responsive scaling
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            width = newVal.doubleValue();
            canvas.setWidth(width);
            if (dashboardNode != null) {
                // Calculate scale based on aspect ratio
                double scaleX = width / 1280.0;
                double scaleY = height / 800.0;
                double scale = Math.min(scaleX, scaleY);
                dashboardNode.setScaleX(scale);
                dashboardNode.setScaleY(scale);
            }
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            height = newVal.doubleValue();
            canvas.setHeight(height);
            if (dashboardNode != null) {
                // Calculate scale based on aspect ratio
                double scaleX = width / 1280.0;
                double scaleY = height / 800.0;
                double scale = Math.min(scaleX, scaleY);
                dashboardNode.setScaleX(scale);
                dashboardNode.setScaleY(scale);
            }
        });

        scene.setOnMouseMoved(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        scene.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mode = 1;
            } else if (event.getButton() == MouseButton.SECONDARY) {
                mode = 0;
            }
            canvas.requestFocus();
        });

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    mode = 1;
                    break;
                case G:
                    mode = 2;
                    break;
                case B:
                    mode = 0;
                    break;
                case M:
                    toggleMenuOverlay();
                    break;
                case S:

                	StarEffect.triggerStarEffect(points);
                    break;
                default:
                    break;
            }
        });

        for (int i = 0; i < POINT_COUNT; i++) {
            double x = random.nextDouble() * 200 - 100;
            double y = random.nextDouble() * 200 - 100;
            double z = random.nextDouble() * 200 - 100;
            double vx = (random.nextDouble() - 0.5) * 0.5;
            double vy = (random.nextDouble() - 0.5) * 0.5;
            double vz = (random.nextDouble() - 0.5) * 0.5;
            points.add(new Point3D(x, y, z, vx, vy, vz));
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                width = canvas.getWidth();
                height = canvas.getHeight();
                double centerX = width / 2.0;
                double centerY = height / 2.0;

                double targetAngleX = (mouseY - centerY) * 0.005;
                double targetAngleY = (mouseX - centerX) * 0.005;
                currentAngleX += (targetAngleX - currentAngleX) * 0.05;
                currentAngleY += (targetAngleY - currentAngleY) * 0.05;

                if (mode == 1) {
                    scaleFactor += (1.8 - scaleFactor) * 0.05;
                } else if (mode == 2) {
                    scaleFactor += (0.7 - scaleFactor) * 0.05;
                } else {
                    scaleFactor += (1.0 - scaleFactor) * 0.05;
                }

                if (errorState == ErrorType.BLOCKED) {
                    drawErrorSymbol(gc, width, height, centerX, centerY, errorState);
                } else if (errorState == ErrorType.EMPTY_FIELDS) {
                    drawNetwork(gc, width, height, centerX, centerY, Color.WHITE);
                } else if (errorState == ErrorType.USER_NOT_FOUND) {
                    drawNetwork(gc, width, height, centerX, centerY, RED_COLOR);
                } else {
                    updatePoints();
                    drawNetwork(gc, width, height, centerX, centerY, null);
                }
            }
        }.start();

        primaryStage.setTitle("VantaEffectFXMouse - 3D Menu, 2D BG (Fixed)");
        primaryStage.setScene(scene);
        primaryStage.show();

        canvas.requestFocus();
    }

    private VBox createMenuOverlay() {
        VBox menu = new VBox(15);
        menu.setTranslateX(250);
        menu.setTranslateY(80);

        menu.setRotationAxis(Rotate.Y_AXIS);
        menu.setRotate(-10);  
        menu.setTranslateZ(-20); 

        BoxBlur blur = new BoxBlur(15, 15, 3);

        menu.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.15);" +
                "-fx-background-radius: 15;" +
                "-fx-padding: 30;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0.5, 0, 0);" +
                "-fx-border-color: rgba(255,255,255,0.3);" +
                "-fx-border-radius: 15;"
        );
        menu.setEffect(blur);

        Label signInLabel = new Label("Sign in");
        signInLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
        signInLabel.setFont(new Font("Arial", 26));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle(
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #dddddd;" +
                "-fx-background-color: transparent;" +
                "-fx-border-color: rgba(255,255,255,0.3);" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;" +
                "-fx-padding: 8;"
        );

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle(
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #dddddd;" +
                "-fx-background-color: transparent;" +
                "-fx-border-color: rgba(255,255,255,0.3);" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;" +
                "-fx-padding: 8;"
        );

        Hyperlink forgotLink = new Hyperlink("Forgot Password");
        forgotLink.setStyle("-fx-text-fill: #f0f0f0; -fx-underline: true;");
        forgotLink.setFont(new Font("Arial", 12));
        forgotLink.setOnAction(e -> {
        	System.out.println("Forgotten password protocol");
            StarEffect.triggerStarEffect(points);


        	
        });
        

        Hyperlink signupLink = new Hyperlink("Signup");
        signupLink.setStyle("-fx-text-fill: #f0f0f0; -fx-underline: true;");
        signupLink.setFont(new Font("Arial", 12));
        signupLink.setOnAction(e -> {
        	System.out.println("Register Protocol");
        	mode = 2;
            toggleMenuOverlay();

            VBox registerMenu = RegisterMenu.createRegisterMenu();

            registerMenu.setTranslateX(300);
            registerMenu.setTranslateY(100);

            root.getChildren().add(registerMenu);


        });

        HBox linksBox = new HBox(20, forgotLink, signupLink);

        Button loginButton = new Button("Login");
        loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #66ccff, #0080ff);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16;"
        );
        loginButton.setFont(new Font("Arial", 14));

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("Lütfen kullanıcı adı ve şifre giriniz.");
                errorState = ErrorType.EMPTY_FIELDS;
                return;
            }

            try {
                int log_return = Client.Login(username, password,
				" ",50051);
                if (log_return == 1) {
                    System.out.println("Kullanıcı bulunamadı!");
                    errorState = ErrorType.USER_NOT_FOUND;
                    return;
                }

                if (log_return == 2) {
                    System.out.println("Kullanıcı bloke edilmiş!");
                    errorState = ErrorType.BLOCKED;
                    return;
                }

                if (log_return == 3) {
                    DBManager.updateLastLogin(username);
                    System.out.println("Giriş başarılı!");
                    errorState = ErrorType.NONE;
                    
                    FadeTransition ft = new FadeTransition(Duration.seconds(0.7), menuOverlay);
                    ft.setFromValue(1);
                    ft.setToValue(0);
                    ft.setOnFinished(event -> {
                        menuOverlay.setVisible(false);
                        showDashboard(username);
                    });
                    ft.play();
                } else {
                    System.out.println("Giriş başarısız: Kullanıcı adı veya şifre yanlış!");
                    errorState = ErrorType.USER_NOT_FOUND;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Giriş sırasında bir hata oluştu: " + ex.getMessage());
                errorState = ErrorType.USER_NOT_FOUND;
            }
        });

        menu.getChildren().addAll(signInLabel, usernameField, passwordField, linksBox, loginButton);
        return menu;
    }

    private void toggleMenuOverlay() {
        FadeTransition ft = new FadeTransition(Duration.seconds(0.7), menuOverlay);
        if (menuOverlay.getOpacity() < 0.1) {
            ft.setFromValue(0);
            ft.setToValue(1);
        } else {
            ft.setFromValue(menuOverlay.getOpacity());
            ft.setToValue(0);
        }
        ft.play();
    }

    private void updatePoints() {
        double boundary = 200;
        for (Point3D p : points) {
            p.x += p.vx;
            p.y += p.vy;
            p.z += p.vz;
            if (p.x < -boundary || p.x > boundary) p.vx = -p.vx;
            if (p.y < -boundary || p.y > boundary) p.vy = -p.vy;
            if (p.z < -boundary || p.z > boundary) p.vz = -p.vz;
        }
    }

    private void drawNetwork(GraphicsContext gc, double w, double h, double centerX, double centerY, Color overrideColor) {
        gc.setFill(BG_COLOR);
        gc.fillRect(0, 0, w, h);

        Color currentColor;
        if (overrideColor != null) {
            currentColor = overrideColor;
        } else {
            if (mode == 1) {
                currentColor = RED_COLOR;
            } else if (mode == 2) {
                currentColor = GREEN_COLOR;
            } else {
                currentColor = BLUE_COLOR;
            }
        }

        // Adjust animation speed and opacity based on dashboard visibility
        double animationSpeed = isDashboardVisible ? 0.3 : 1.0;
        double baseOpacity = isDashboardVisible ? 0.3 : 1.0;

        List<ProjectedPoint> projectedPoints = new ArrayList<>(points.size());
        for (Point3D p : points) {
            double x0 = p.x * scaleFactor * cameraZoom;
            double y0 = p.y * scaleFactor * cameraZoom;
            double z0 = p.z * scaleFactor * cameraZoom;

            double cosAx = Math.cos(currentAngleX);
            double sinAx = Math.sin(currentAngleX);
            double y1 = y0 * cosAx - z0 * sinAx;
            double z1 = y0 * sinAx + z0 * cosAx;

            double cosAy = Math.cos(currentAngleY);
            double sinAy = Math.sin(currentAngleY);
            double x2 = x0 * cosAy + z1 * sinAy;
            double z2 = -x0 * sinAy + z1 * cosAy;

            double scale = fov / (fov + z2);
            double px = x2 * scale + centerX;
            double py = y1 * scale + centerY;

            projectedPoints.add(new ProjectedPoint(px, py, x2, y1, z2));
        }

        // Draw points with adjusted opacity
        gc.setGlobalAlpha(baseOpacity);
        gc.setFill(currentColor);
        for (ProjectedPoint pp : projectedPoints) {
            gc.fillOval(pp.screenX - 2, pp.screenY - 2, 4, 4);
        }

        double effectiveThreshold = BASE_MAX_DISTANCE;
        if (mode == 1) {
            effectiveThreshold = BASE_MAX_DISTANCE * 1.5;
        } else if (mode == 2) {
            effectiveThreshold = BASE_MAX_DISTANCE * 0.8;
        }
        double lineWidth = (mode == 1) ? 2.0 : 1.0;
        gc.setLineWidth(lineWidth);

        // Draw connections with adjusted opacity and animation
        for (int i = 0; i < projectedPoints.size(); i++) {
            ProjectedPoint p1 = projectedPoints.get(i);
            for (int j = i + 1; j < projectedPoints.size(); j++) {
                ProjectedPoint p2 = projectedPoints.get(j);
                double dx = p1.worldX - p2.worldX;
                double dy = p1.worldY - p2.worldY;
                double dz = p1.worldZ - p2.worldZ;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist < effectiveThreshold) {
                    double alpha = (mode == 1) ? 1.0 : 1.0 - (dist / effectiveThreshold);
                    gc.setGlobalAlpha(alpha * baseOpacity);
                    if (mode == 2) {
                        drawBrokenLine(gc, p1.screenX, p1.screenY, p2.screenX, p2.screenY);
                    } else {
                        gc.setStroke(currentColor);
                        gc.strokeLine(p1.screenX, p1.screenY, p2.screenX, p2.screenY);
                    }
                }
            }
        }
        gc.setGlobalAlpha(1.0);
    }

    private void drawBrokenLine(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        gc.setStroke(GREEN_COLOR);
        int segments = 5;
        double dx = (x2 - x1) / segments;
        double dy = (y2 - y1) / segments;
        double prevX = x1;
        double prevY = y1;
        for (int i = 1; i <= segments; i++) {
            double newX = x1 + i * dx;
            double newY = y1 + i * dy;
            if (random.nextDouble() > 0.3) {
                gc.strokeLine(prevX, prevY, newX, newY);
            }
            prevX = newX;
            prevY = newY;
        }
    }

    private void drawErrorSymbol(GraphicsContext gc, double w, double h, double centerX, double centerY, ErrorType error) {
        if (error != ErrorType.BLOCKED) {
            if (error == ErrorType.EMPTY_FIELDS) {
                drawNetwork(gc, w, h, centerX, centerY, Color.WHITE);
            } else if (error == ErrorType.USER_NOT_FOUND) {
                drawNetwork(gc, w, h, centerX, centerY, RED_COLOR);
            }
            return;
        }

        String symbol = "X";
        Color symbolColor = Color.RED;

        double time = System.currentTimeMillis() / 1000.0;
        double offsetX = Math.sin(time * 2) * 20;
        double offsetY = Math.cos(time * 2) * 20;

        Font font = new Font("Arial Bold", 150);
        gc.setFont(font);
        gc.setFill(symbolColor);

        Text text = new Text(symbol);
        text.setFont(font);
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();

        gc.fillText(symbol, centerX - textWidth / 2 + offsetX, centerY + textHeight / 4 + offsetY);
    }

    private void showDashboard(String username) {
        if (dashboardNode == null) {
            dashboardNode = MainDashboard.createDashboard(username);
            dashboardNode.setOpacity(0);
            root.getChildren().add(dashboardNode);
        }

        // Calculate initial scale based on aspect ratio
        double scaleX = width / 1280.0;
        double scaleY = height / 800.0;
        double scale = Math.min(scaleX, scaleY);
        dashboardNode.setScaleX(scale);
        dashboardNode.setScaleY(scale);

        // Add smooth transition for the dashboard
        FadeTransition ft = new FadeTransition(Duration.seconds(0.7), dashboardNode);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        // Add subtle animation to keep the dashboard "alive"
        Timeline dashboardAnimation = new Timeline(
            new KeyFrame(Duration.seconds(2), e -> {
                if (dashboardNode != null) {
                    ScaleTransition st = new ScaleTransition(Duration.seconds(2), dashboardNode);
                    st.setToX(dashboardNode.getScaleX() * 1.001);
                    st.setToY(dashboardNode.getScaleY() * 1.001);
                    st.play();
                }
            }),
            new KeyFrame(Duration.seconds(4), e -> {
                if (dashboardNode != null) {
                    ScaleTransition st = new ScaleTransition(Duration.seconds(2), dashboardNode);
                    st.setToX(dashboardNode.getScaleX() * 0.999);
                    st.setToY(dashboardNode.getScaleY() * 0.999);
                    st.play();
                }
            })
        );
        dashboardAnimation.setCycleCount(Timeline.INDEFINITE);
        dashboardAnimation.play();

        isDashboardVisible = true;
    }

    static class Point3D {
        double x, y, z;
        double vx, vy, vz;
        Point3D(double x, double y, double z, double vx, double vy, double vz) {
            this.x = x; this.y = y; this.z = z;
            this.vx = vx; this.vy = vy; this.vz = vz;
        }
    }

    private static class ProjectedPoint {
        double screenX, screenY;
        double worldX, worldY, worldZ;
        ProjectedPoint(double sx, double sy, double wx, double wy, double wz) {
            this.screenX = sx;
            this.screenY = sy;
            this.worldX = wx;
            this.worldY = wy;
            this.worldZ = wz;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
