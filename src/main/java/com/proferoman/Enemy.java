package com.proferoman;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Point2D;

public class Enemy extends Component {

    private final Point2D direction = new Point2D(FXGLMath.random(-1D, 1D), FXGLMath.random(-1D, 1D));

    @Override
    public void onUpdate(double tpf) {
        entity.translate(direction.multiply(3));
        checkForBounds();
    }

    private void checkForBounds() {
        if (entity.getX() < 0) {
            remove();
        }
        if (entity.getX() >= 800) {
            remove();
        }
        if (entity.getY() < 0) {
            remove();
        }
        if (entity.getY() >= 800) {
            remove();
        }
    }

    public void remove() {
        entity.removeFromWorld();
    }
}
