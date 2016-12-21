package com.ncdu.circles;

import com.ncdu.circles.CirclesPictureGenerator.Circle;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CircleDet {

    public List<Circle> detect(String path) {

        BufferedImage img;
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            final Logger logger = Logger.getLogger("logger");
                if (logger.isDebugEnabled() ) {
                    logger.error("File not found");
                }
                return new ArrayList<>();
            }
        List<Circle> circlesList = new ArrayList<>();
        Circle cr;

        while(true) {
            int li = 0;
            int lj = 0;
            int ri = 0;
            int rj = 0;
            int circleFind = 0;
            int circleUp = 0;
            int circleDown = 0;
            Point pointUp = new Point();
            Point pointDown = new Point();
            int pixelpre;
            int pixelprei;

            for (int j = 0; j < img.getHeight(); j++) {
                for (int i = 0; i < img.getWidth(); i++) {
                    int pixel = img.getRGB(i, j);
                    if (j > 0) {
                        pixelpre = img.getRGB(i, j - 1);
                    } else {
                        pixelpre = img.getRGB(i, j);
                    }
                    if (i > 0) {
                        pixelprei = img.getRGB(i - 1, j);
                    } else {
                        pixelprei = img.getRGB(i, j);
                    }
                    if ((pixel & 0x00FFFFFF) == 0 && circleFind == 0) {
                        li = i;
                        lj = j;
                        circleFind = 1;
                    }
                    if ((pixelprei & 0x00FFFFFF) == 0 && (pixel & 0x00FFFFFF) != 0 && circleUp == 0) {
                        ri = i - 1;
                        rj = j;
                        i = li + (ri - li) / 2;
                        j = rj;

                        pointUp = new Point(i, j);
                        circleUp = 1;
                    }
                    if (i == li + (ri - li) / 2 && j > rj) {
                        if ((pixelpre & 0x00FFFFFF) == 0 && (pixel & 0x00FFFFFF) != 0) {
                            circleDown = circleDown + 1;
                            if (circleDown == 2) {
                                pointDown = new Point(i, j - 1);
                                break;
                            }
                        }
                    }
                }
            }
            if(li == 0 && lj == 0){
                break;
            }

            cr = new Circle((int)Math.round(pointDown.getX()),(int)Math.round(pointDown.getY() - (pointDown.getY() - pointUp.getY()) / 2)
            ,(int)Math.round((pointDown.getY() - pointUp.getY()) / 2));


            int y0 = (int)Math.round(pointUp.getY());
            int y1 = (y0 + 2*cr.getRadius());
            int x0 = (int)(Math.round(pointUp.getX()) - cr.getRadius());
            int x1 = (int)(Math.round(pointUp.getX()) + cr.getRadius());

            //delete circle that was detected
            for (int j = y0; j <=y1; j++) {
                for (int i = x0; i <=x1; i++) {
                    if(Math.sqrt((i - cr.getCenterX())*(i - cr.getCenterX()) +
                            (j - cr.getCenterY())*(j - cr.getCenterY())) <= cr.getRadius()+2) {
                        img.setRGB(i, j, -1);
                    }
                }
            }
//            final Logger logger = Logger.getLogger("logger");
//            if (logger.isDebugEnabled() ) {
//                logger.debug(cr.toString());
//            }
            if(cr != null){
                circlesList.add(cr);
            }
        }
        circlesList.sort(Circle.CircleRadiusComparator);
        return circlesList;
    }


}
