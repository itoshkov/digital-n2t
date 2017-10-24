package com.github.itoshkov.digitaln2t;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

@SuppressWarnings("WeakerAccess")
public class HackGraphicsComponent extends JComponent {

    private final BufferedImage image;
    private final int scaleFactor;

    /**
     * Creates a new instance.
     */
    public HackGraphicsComponent(BufferedImage image, int scaleFactor) {
        this.image = image;
        this.scaleFactor = scaleFactor;

        setPreferredSize(new Dimension(image.getWidth() * scaleFactor, image.getHeight() * scaleFactor));
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        final BufferedImage scaledImage = scale(image, scaleFactor);
        g.drawImage(scaledImage, 0, 0, null);
    }

    public static BufferedImage scale(BufferedImage image, double factor) {
        return scale(image, image.getType(), (int) (image.getWidth() * factor), (int) (image.getHeight() * factor),
                     factor, factor);
    }

    /**
     * scale image
     *
     * @param sbi       image to scale
     * @param imageType type of image
     * @param dWidth    width of destination image
     * @param dHeight   height of destination image
     * @param fWidth    x-factor for transformation / scaling
     * @param fHeight   y-factor for transformation / scaling
     * @return scaled image
     */
    public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight,
                                      double fWidth, double fHeight) {
        final BufferedImage dbi = new BufferedImage(dWidth, dHeight, imageType);
        final Graphics2D g = dbi.createGraphics();
        final AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
        g.drawRenderedImage(sbi, at);
        return dbi;
    }
}
