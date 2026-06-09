package com.coursehelper.frontend.util;

import java.io.ByteArrayInputStream;

import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class AvatarUtils {


    public static void makeCircular(ImageView imageView, double radius) {
    double size = radius * 2;
    
    imageView.setFitWidth(size);
    imageView.setFitHeight(size);
    imageView.setPreserveRatio(false); // ← fill the square

    // wait for image to load then set viewport
    imageView.imageProperty().addListener((obs, oldImg, newImg) -> {
        if (newImg != null) {
            centerCrop(imageView, newImg);
        }
    });

    // apply to current image if already set
    if (imageView.getImage() != null) {
        centerCrop(imageView, imageView.getImage());
    }

    Circle clip = new Circle(radius, radius, radius);
    imageView.setClip(clip);
    }

    private static void centerCrop(ImageView imageView, Image image) {
        double imgWidth = image.getWidth();
        double imgHeight = image.getHeight();
        
        // take the smaller dimension as the square size
        double squareSize = Math.min(imgWidth, imgHeight);
        
        // calculate top-left corner of center square
        double x = (imgWidth - squareSize) / 2;
        double y = (imgHeight - squareSize) / 2;
        
        // set viewport to center square
        imageView.setViewport(new Rectangle2D(x, y, squareSize, squareSize));
    }

    public static Image getProfileImage(byte[] bytes , String username){
        if (bytes != null && bytes.length > 0) {
            return new Image(new ByteArrayInputStream(bytes));

        }

        return generateDefaultAvatar(username);

    }

    public static Image generateDefaultAvatar(String username) {
        String initial = username != null && !username.isEmpty()
            ? String.valueOf(username.charAt(0)).toUpperCase()
            : "?";

        Canvas canvas = new Canvas(100, 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#4A90D9"));
        gc.fillOval(0, 0, 100, 100);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 36));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(initial, 50, 50);

        WritableImage image = new WritableImage(100, 100);
        canvas.snapshot(null, image);
        return image;
    }





    
}
