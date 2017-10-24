package com.github.itoshkov.digitaln2t;

import javax.swing.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("WeakerAccess")
public class HackGraphicsDialog extends JDialog {
    private final HackGraphicsComponent graphicComponent;

    /**
     * Creates a new instance of the given size
     */
    public HackGraphicsDialog(BufferedImage image, int scaleFactor) {
        super((JFrame) null, "HACK Display", false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        graphicComponent = new HackGraphicsComponent(image, scaleFactor);
        getContentPane().add(graphicComponent);
        pack();

        setAlwaysOnTop(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Updates the graphics data
     */
    public void updateGraphic() {
        graphicComponent.repaint();
    }
}
