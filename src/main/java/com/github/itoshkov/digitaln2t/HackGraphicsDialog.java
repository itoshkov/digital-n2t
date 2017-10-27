package com.github.itoshkov.digitaln2t;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

@SuppressWarnings("WeakerAccess")
public class HackGraphicsDialog extends JDialog {
    private final HackGraphicsComponent graphicsComponent;

    /**
     * Creates a new instance of the given size
     */
    public HackGraphicsDialog(BufferedImage image, int scaleFactor) {
        super((JFrame) null, "HACK Display", false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        graphicsComponent = new HackGraphicsComponent(image, scaleFactor);
        getContentPane().add(graphicsComponent);
        pack();

        setAlwaysOnTop(true);
        setLocationRelativeTo(null);
        setVisible(true);
        graphicsComponent.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                HackGraphicsDialog.this.pack();
            }
        });
    }

    /**
     * Updates the graphics data
     */
    public void updateGraphic() {
        graphicsComponent.repaint();
    }
}
