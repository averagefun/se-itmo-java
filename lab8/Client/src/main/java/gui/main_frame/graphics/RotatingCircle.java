package gui.main_frame.graphics;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class RotatingCircle extends Ellipse2D {
    private final double xCenter;
    private final double yCenter;
    private final double diameter;

    public RotatingCircle(double xCenter, double yCenter, double diameter) {
        this.xCenter = xCenter;
        this.yCenter = yCenter;
        this.diameter = diameter;
    }

    @Override
    public double getX() {
        return xCenter - diameter/2;
    }

    @Override
    public double getY() {
        return yCenter - diameter/2;
    }

    @Override
    public double getWidth() {
        return diameter;
    }

    @Override
    public double getHeight() {
        return diameter;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void setFrame(double x, double y, double w, double h) {}

    @Override
    public Rectangle2D getBounds2D() {
        return new Rectangle((int) Math.ceil(diameter), (int) Math.ceil(diameter));
    }
}
