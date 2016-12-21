package com.ncdu.circles;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CirclesPictureGenerator
{
    public static class Circle
    {
        public int getCenterX() {
            return centerX;
        }

        public int getCenterY() {
            return centerY;
        }

        private final int centerX;
        private final int centerY;

        public int getRadius() {
            return radius;
        }

        private final int radius;

        Circle(int centerX, int centerY, int radius)
        {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
        }

        private int toCircleDist(int x, int y)
        {
            return (int) toCenterDist(x, y) - radius;
        }

        private double toCenterDist(int x, int y)
        {
            return Math.sqrt((centerX - x) * (centerX - x) + (centerY - y) * (centerY - y));
        }

        public String toString()
        {
            return "Circle[centerX = " + centerX + "; centerY = " + centerY + "; R = " + radius + "]\n";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Circle)) return false;

            Circle circle = (Circle) o;

            if (centerX != circle.centerX) return false;
            if (centerY != circle.centerY) return false;
            return radius == circle.radius;

        }
        public int compareTo(Circle compareCircle) {

            int compareRadius = ((Circle) compareCircle).getRadius();

            //ascending order
            return this.radius - compareRadius;

            //descending order
            //return compareRadius - this.radius;

        }

        public static Comparator<Circle> CircleRadiusComparator
                = new Comparator<Circle>() {

            public int compare(Circle circle1, Circle circle2) {

                int CircleRad1 = circle1.getRadius();
                int CircleRad2 = circle2.getRadius();

                //ascending order
                return circle1.compareTo(circle2);

            }

        };

    }

    private static final int TREIS_TO_FIND_CENTER = 1000;
    private static final int MAX_CIRCLES_BY_DEFAULT = 30;
    private static final int MIN_CIRCLE_RADIUS = 20;
    private static final int MIN_DIST_BETWEEN_CIRCLES = 5;
    private static final int DEFAULT_PICTURE_WIDTH = 800;
    private static final int DEFAULT_PICTURE_HEIGHT = 600;

    private static List<Circle> generateCircles(int pictureW, int pictureH, int numberOfCircles)
    {
        final List<Circle> circles = new ArrayList<>(numberOfCircles);
        final int maxRadius = Math.min(pictureH, pictureW) / 4;

        int tries = 0;
        while (circles.size() < numberOfCircles)
        {
            int xRandom = (int) (Math.random() * pictureW);
            int yRandom = (int) (Math.random() * pictureH);

            // minimum of distances to border
            int maxCurrentRadius = Math.min(Math.min(xRandom, pictureW - xRandom), Math.min(yRandom, pictureH - yRandom)) - 1;

            // minimum distance to already generated circles
            for (Circle circle : circles)
            {
                int possibleRadius = circle.toCircleDist(xRandom, yRandom) - MIN_DIST_BETWEEN_CIRCLES;
                maxCurrentRadius = Math.min(maxCurrentRadius, possibleRadius);
            }

            maxCurrentRadius = Math.min(maxCurrentRadius, maxRadius);

            if (maxCurrentRadius < MIN_CIRCLE_RADIUS)
            {
                if (tries > TREIS_TO_FIND_CENTER)
                    throw new IllegalStateException("Could not find new circle center. Try to specify less number of circles.");

                tries ++;
                continue;
            }

            int randomRadius = (int) ((maxCurrentRadius - MIN_CIRCLE_RADIUS) * Math.random()) + MIN_CIRCLE_RADIUS;
            circles.add(new Circle(xRandom, yRandom, randomRadius));
        }

        circles.sort((o1, o2) -> o1.radius > o2.radius ? -1 : o1.radius < o2.radius ? 1 : 0);

        return circles;
    }

    private static void drawCirclesToPicture(List<Circle> circles, String picturePath, int pictureW, int pictureH) throws IOException
    {
        final int[] pixels = new int[pictureW * pictureH];

        for (int i = 0; i < pixels.length; i ++)
            pixels[i] = -1; // white color

        for (Circle circle : circles) {
            for (int x = 0; x <= circle.radius * 2; x ++) {
                int pixX = x + circle.centerX - circle.radius;
                for (int y = 0; y <= circle.radius * 2; y ++) {
                    int pixY = y + circle.centerY - circle.radius;
                    if (Math.abs(circle.toCenterDist(pixX, pixY) - circle.radius) < 1)
                        pixels[pixX + pixY * pictureW] = 0; // black color
                }
            }
        }

        final BufferedImage out = new BufferedImage(pictureW, pictureH, BufferedImage.TYPE_INT_RGB);

        out.setRGB(0, 0, pictureW, pictureH, pixels, 0, pictureW);

        File outputFile = new File(picturePath);
        ImageIO.write(out, "gif", outputFile);
    }

    private static List<Circle> generateAndDrawCircles(String picturePath, int pictureW, int pictureH, int numberOfCircles) throws IOException
    {
        final List<Circle> circles = generateCircles(pictureW, pictureH, numberOfCircles);
        drawCirclesToPicture(circles, picturePath, pictureW, pictureH);

        return circles;
    }

    public static List<Circle> generateAndDraw() throws IOException
    {
        // generate file name
        final DateFormat df = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
        final Date today = Calendar.getInstance().getTime();
        final String reportDate = df.format(today);
        final String fileName = "CIRCLES" + ".gif";

        // generate number of circles
        final int n = (int) (MAX_CIRCLES_BY_DEFAULT * Math.random());

        return generateAndDrawCircles(fileName, DEFAULT_PICTURE_WIDTH, DEFAULT_PICTURE_HEIGHT, n);
    }

}
