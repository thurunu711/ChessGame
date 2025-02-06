package com.package1.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class ImageRender {
    private Image loadImage(String imgFileName) throws URISyntaxException, IOException {
        var classLoader = getClass().getClassLoader();
        URL resURL = classLoader.getResource("img/Bishop-black.png");

        if (resURL == null) {
            return null;
        }else {
            System.out.println("correct");
            var imgFile = new File(resURL.toURI());
            return ImageIO.read(imgFile);

        }

    }
}
