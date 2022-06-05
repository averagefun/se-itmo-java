package gui.main_frame.graphics;

import data.Movie;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;

public class MovieGraphics extends JButton {
    private final Movie movie;
    private final Color color;
    private final double k;
    private int alpha = 45;

    public MovieGraphics(Movie movie, Dimension graphicsPanelSize) {
        this.movie = movie;
        this.color = getColorByName(movie.getUsername());

        float oscars = movie.getOscarsCount();
        this.k = Math.sqrt(oscars + 1);

        setGraphicsBounds(graphicsPanelSize);
    }

    private int scale(int size) {
        return (int) Math.round(size * k);
    }

    private Color getColorByName(String username) {
        byte[] bytes = username.getBytes(StandardCharsets.UTF_8);
        int myRGB = 0;
        for (byte b: bytes) myRGB += b;
        return new Color(myRGB * myRGB * myRGB);
    }

    private void setGraphicsBounds(Dimension graphicsPanelSize) {
        int x = (int) Math.floor(Math.min(movie.getCoordinates().getX(), graphicsPanelSize.getWidth() - scale(62)));
        int y = (int) Math.floor(Math.min(movie.getCoordinates().getY(), graphicsPanelSize.getHeight() - scale(50)));
        setBounds(x, y, scale(62), scale(50));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        Color backgroundColor = UIManager.getColor ("Panel.background");
        g.setColor(backgroundColor);
        g.fillRect(0, 0, scale(62), scale(50));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(color);

        g.fillRoundRect(0, scale(21), scale(44), scale(26), scale(2), scale(2));
        g.fillPolygon(new int[] {scale(44), scale(44), scale(62), scale(62)}, new int[] {scale(42), scale(26), scale(16), scale(50)}, 4);

        g.fillOval(scale(1), scale(1), scale(18), scale(18));
        g.fillOval(scale(23), scale(1), scale(18), scale(18));

        g.setColor(backgroundColor);
        fillRotatingCircle(g, scale(1), scale(1), scale(18), alpha);
        fillRotatingCircle(g, scale(1), scale(1), scale(18), alpha+90);
        fillRotatingCircle(g, scale(1), scale(1), scale(18), alpha+180);
        fillRotatingCircle(g, scale(1), scale(1), scale(18), alpha+270);

        fillRotatingCircle(g, scale(23), scale(1), scale(18), alpha);
        fillRotatingCircle(g, scale(23), scale(1), scale(18), alpha+90);
        fillRotatingCircle(g, scale(23), scale(1), scale(18), alpha+180);
        fillRotatingCircle(g, scale(23), scale(1), scale(18), alpha+270);
    }

    private void fillRotatingCircle(Graphics2D g, int bigCircleX, int bigCircleY, int bigCircleDiameter, int alphaDegree) {
        int r = 5; // r - radius of circle, that contains centers of rotating circles
        double x = bigCircleX + (double) bigCircleDiameter/2 + k * r * Math.cos(Math.toRadians(alphaDegree));
        double y = bigCircleY + (double) bigCircleDiameter/2 + k * r * Math.sin(Math.toRadians(alphaDegree));
        g.fill(new RotatingCircle(x, y, scale(5)));
    }

    public void transitionStep() {
        alpha += 2;
        if (alpha >= 360) alpha -= 360;
    }
}
