package com.saferoom.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;
import java.util.List;

public class StarEffect2 {

    private static final DoubleProperty explosionOpacity = new SimpleDoubleProperty(1.0);

    public static DoubleProperty explosionOpacityProperty() {
        return explosionOpacity;
    }
    
    public static double getExplosionOpacity() {
        return explosionOpacity.get();
    }
    

    public static void explodeStarEffect(List<VantaEffectFXMouse.Point3D> points) {

    	double collapseSpeed = 3.0;  
        for (VantaEffectFXMouse.Point3D p : points) {
            double dx = -p.x;
            double dy = -p.y;
            double dz = -p.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance == 0) continue;  
            p.vx = dx / distance * collapseSpeed;
            p.vy = dy / distance * collapseSpeed;
            p.vz = dz / distance * collapseSpeed;
        }
        
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> {

        	double explosionSpeed = 8.0;
            for (VantaEffectFXMouse.Point3D p : points) {
                double randX = Math.random() - 0.5;
                double randY = Math.random() - 0.5;
                double randZ = Math.random() - 0.5;
                double mag = Math.sqrt(randX * randX + randY * randY + randZ * randZ);
                if (mag == 0) continue;
                p.vx = (randX / mag) * explosionSpeed;
                p.vy = (randY / mag) * explosionSpeed;
                p.vz = (randZ / mag) * explosionSpeed;
            }
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0), new KeyValue(explosionOpacity, 1.0)),
                    new KeyFrame(Duration.seconds(2), new KeyValue(explosionOpacity, 0.0))
            );
            timeline.setOnFinished(event -> {
                points.clear();
            });
            timeline.play();
        });
        pause.play();
    }
}
