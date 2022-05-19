package gui.main_frame.graphics;

import data.Movie;

import javax.swing.*;
import java.awt.*;

public class MovieGraphics extends JButton {
    private final Movie movie;
    private final int k;
    private int alpha = 45;

    public MovieGraphics(Movie movie, Dimension graphicsPanelSize) {
        this.movie = movie;
        k = movie.getOscarsCount() <= 2 ? movie.getOscarsCount() + 1 : 3;
        setGraphicsBounds(graphicsPanelSize);
    }

    private void setGraphicsBounds(Dimension graphicsPanelSize) {
        int x = (int) Math.floor(Math.min(movie.getCoordinates().getX(), graphicsPanelSize.getWidth()));
        int y = (int) Math.floor(Math.min(movie.getCoordinates().getY(), graphicsPanelSize.getHeight()));
        setBounds(x, y, k*62, k*50);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        Color backgroundColor = UIManager.getColor ("Panel.background");
        g.setColor(backgroundColor);
        g.fillRect(0, 0, k*62, k*50);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.BLACK);

        g.fillRoundRect(0, k*21, k*44, k*26, k*2, k*2);
        g.fillPolygon(new int[] {k*44, k*44, k*62, k*62}, new int[] {k*42, k*26, k*16, k*50}, 4);

        g.fillOval(k, k, k*18, k*18);
        g.fillOval(k*23, k, k*18, k*18);

        g.setColor(backgroundColor);
        fillRotatingCircle(g, k, k, k*18, alpha);
        fillRotatingCircle(g, k, k, k*18, alpha+90);
        fillRotatingCircle(g, k, k, k*18, alpha+180);
        fillRotatingCircle(g, k, k, k*18, alpha+270);

        fillRotatingCircle(g, k*23, k, k*18, alpha);
        fillRotatingCircle(g, k*23, k, k*18, alpha+90);
        fillRotatingCircle(g, k*23, k, k*18, alpha+180);
        fillRotatingCircle(g, k*23, k, k*18, alpha+270);
    }

    private void fillRotatingCircle(Graphics2D g, int bigCircleX, int bigCircleY, int bigCircleDiameter, int alphaDegree) {
        int r = 5; // r - radius of circle, that contains centers of rotating circles
        double x = bigCircleX + (double) bigCircleDiameter/2 + k * r * Math.cos(Math.toRadians(alphaDegree));
        double y = bigCircleY + (double) bigCircleDiameter/2 + k * r * Math.sin(Math.toRadians(alphaDegree));
        g.fill(new RotatingCircle(x, y, k*5));
    }

    public void transitionStep() {
        alpha += 2;
        if (alpha >= 360) alpha -= 360;
    }
}
