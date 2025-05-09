package com.saferoom.gui;

import javafx.animation.AnimationTimer;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VantaTree3DGraph extends Group {

    private final List<GraphNode> nodes = new ArrayList<>();
    private final List<GraphEdge> edges = new ArrayList<>();
    private final Random random = new Random();
    private final AnimationTimer animationTimer;

    public VantaTree3DGraph(int nodeCount) {
        buildGraph(nodeCount);

        PointLight light = new PointLight(Color.AQUA);
        light.getTransforms().add(new Translate(0, -100, -300));
        getChildren().add(light);

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                animate();
            }
        };
    }

    private void buildGraph(int count) {
        for (int i = 0; i < count; i++) {
            GraphNode node = new GraphNode(
                random.nextDouble() * 800 - 400,
                random.nextDouble() * 800 - 400,
                random.nextDouble() * 800 - 400
            );
            nodes.add(node);
            getChildren().add(node.sphere);
        }

        for (int i = 0; i < count; i++) {
            int targetIndex = random.nextInt(count);
            if (i != targetIndex) {
                GraphEdge edge = new GraphEdge(nodes.get(i), nodes.get(targetIndex));
                edges.add(edge);
                getChildren().add(edge.cylinder);
            }
        }
    }

    private void animate() {
        for (GraphNode node : nodes) {
            node.update();
        }
        for (GraphEdge edge : edges) {
            edge.update();
        }
    }

    public void startAnimation() {
        animationTimer.start();
    }

    public void stopAnimation() {
        animationTimer.stop();
    }

    private static class GraphNode {
        Sphere sphere;
        double x, y, z;
        double dx, dy, dz;

        public GraphNode(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dx = Math.random() * 0.6 - 0.3;
            this.dy = Math.random() * 0.6 - 0.3;
            this.dz = Math.random() * 0.6 - 0.3;

            sphere = new Sphere(8);
            PhongMaterial mat = new PhongMaterial(Color.CYAN);
            sphere.setMaterial(mat);
            sphere.setTranslateX(x);
            sphere.setTranslateY(y);
            sphere.setTranslateZ(z);
        }

        public void update() {
            x += dx;
            y += dy;
            z += dz;

            if (Math.abs(x) > 400) dx *= -1;
            if (Math.abs(y) > 400) dy *= -1;
            if (Math.abs(z) > 400) dz *= -1;

            sphere.setTranslateX(x);
            sphere.setTranslateY(y);
            sphere.setTranslateZ(z);
        }
    }

    private static class GraphEdge {
        GraphNode a, b;
        Cylinder cylinder;

        public GraphEdge(GraphNode a, GraphNode b) {
            this.a = a;
            this.b = b;
            cylinder = new Cylinder(1.5, 1);
            cylinder.setMaterial(new PhongMaterial(Color.DEEPSKYBLUE));
            update();
        }

        public void update() {
            double dx = b.x - a.x;
            double dy = b.y - a.y;
            double dz = b.z - a.z;
            double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);

            cylinder.setHeight(distance);
            cylinder.setTranslateX((a.x + b.x) / 2);
            cylinder.setTranslateY((a.y + b.y) / 2);
            cylinder.setTranslateZ((a.z + b.z) / 2);

            double angleX = Math.toDegrees(Math.atan2(dz, dy));
            double angleZ = Math.toDegrees(Math.atan2(dx, dy));

            cylinder.getTransforms().setAll(
                new Rotate(angleX, Rotate.X_AXIS),
                new Rotate(-angleZ, Rotate.Z_AXIS)
            );
        }
    }
} 