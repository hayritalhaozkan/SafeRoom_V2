package com.saferoom.gui;


import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.List;

public class StarEffect {

	
    public static void triggerStarEffect(List<VantaEffectFXMouse.Point3D> points) {

    	for (VantaEffectFXMouse.Point3D p : points) {
            p.vx = -8;  
        }

    	PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
        pt.setOnFinished(e -> {
            for (VantaEffectFXMouse.Point3D p : points) {
                p.vx *= 0.2; 
            }
        });
        pt.play();
    }
}
