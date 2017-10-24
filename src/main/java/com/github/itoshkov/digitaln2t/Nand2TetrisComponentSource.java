package com.github.itoshkov.digitaln2t;

import de.neemann.digital.draw.library.ComponentManager;
import de.neemann.digital.draw.library.ComponentSource;
import de.neemann.digital.draw.library.ElementLibrary;
import de.neemann.digital.draw.library.InvalidNodeException;
import de.neemann.digital.gui.Main;

public class Nand2TetrisComponentSource implements ComponentSource {
    @Override
    public void registerComponents(ComponentManager componentManager) throws InvalidNodeException {
        componentManager.addComponent("nand2tetris", HackDisplay.DESCRIPTION);
    }

    /**
     * Start Digital with this ComponentSource attached to make debugging easier.
     * IMPORTANT: Remove the jar from Digital's settings!!!
     *
     * @param args args
     */
    public static void main(String[] args) {
        new Main.MainBuilder()
                .setLibrary(new ElementLibrary().registerComponentSource(new Nand2TetrisComponentSource()))
                .openLater();
    }
}
