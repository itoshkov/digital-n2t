package com.github.itoshkov.digitaln2t;

import de.neemann.digital.core.BitsException;
import de.neemann.digital.core.Node;
import de.neemann.digital.core.NodeException;
import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.ObservableValues;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Key;
import de.neemann.digital.core.element.Keys;
import de.neemann.digital.core.memory.DataField;
import de.neemann.digital.core.memory.RAMInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import static de.neemann.digital.core.element.PinInfo.input;

@SuppressWarnings("WeakerAccess")
public class HackDisplay extends Node implements Element, RAMInterface {
    public static final Key<Integer> SCALE_FACTOR = new Key.KeyInteger("Scale factor", 2) {
        @Override
        public String getName() {
            return "Scale Factor";
        }
    }
            .setComboBoxValues(new Integer[]{1, 2, 3, 4})
            .setMin(1)
            .setMax(4);
    private static final int WIDTH = 512;
    private static final int HEIGHT = 256;

    /**
     * The RAMs {@link ElementTypeDescription}
     */
    public static final ElementTypeDescription DESCRIPTION =
            new ElementTypeDescription(HackDisplay.class,
                    input("A", "address input"),
                    input("D_in", "data input"),
                    input("str", "If set, the data is stored."),
                    input("C", "clock input"),
                    input("ld", "If set, the stored data is output."))
                    .addAttribute(Keys.ROTATE)
                    .addAttribute(Keys.LABEL)
                    .addAttribute(SCALE_FACTOR);

    private final DataField memory;
    private final ObservableValue output;
    private final int addrBits;
    private final int bits;
    private final String label;
    private final int size;
    private final int scaleFactor;
    private ObservableValue addrIn;
    private ObservableValue dataIn;
    private ObservableValue strIn;
    private ObservableValue clkIn;
    private ObservableValue ldIn;
    private int addr;
    private boolean lastClk = false;
    private boolean ld;
    private HackGraphicsDialog graphicsDialog;
    private final BufferedImage image;

    /**
     * Creates a new instance
     *
     * @param attr the elements attributes
     */
    public HackDisplay(ElementAttributes attr) {
        super(true);
        scaleFactor = attr.get(SCALE_FACTOR);
        bits = 16;
        output = createOutput();
        addrBits = 13;
        size = 1 << addrBits;
        memory = new DataField(size);
        label = attr.getCleanLabel();
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
        final Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    /**
     * called to create the output value
     *
     * @return the output value
     */
    protected ObservableValue createOutput() {
        return new ObservableValue("D", bits, true).setDescription("data output pin");
    }

    @Override
    public void setInputs(ObservableValues inputs) throws NodeException {
        setAddrIn(inputs.get(0));
        setDataIn(inputs.get(1));
        setStrIn(inputs.get(2));
        setClkIn(inputs.get(3));
        setLdIn(inputs.get(4));
    }

    /**
     * Sets the addrIn input value
     *
     * @param addrIn addrIn
     * @throws BitsException BitsException
     */
    protected void setAddrIn(ObservableValue addrIn) throws BitsException {
        this.addrIn = addrIn.checkBits(addrBits, this).addObserverToValue(this);
    }

    /**
     * Sets the dataIn input value
     *
     * @param dataIn dataIn
     * @throws BitsException BitsException
     */
    protected void setDataIn(ObservableValue dataIn) throws BitsException {
        this.dataIn = dataIn.checkBits(bits, this);
    }

    /**
     * Sets the strIn input value
     *
     * @param strIn strIn
     * @throws BitsException BitsException
     */
    protected void setStrIn(ObservableValue strIn) throws BitsException {
        this.strIn = strIn.checkBits(1, this);
    }

    /**
     * Sets the clkIn input value
     *
     * @param clkIn clkIn
     * @throws BitsException BitsException
     */
    protected void setClkIn(ObservableValue clkIn) throws BitsException {
        this.clkIn = clkIn.checkBits(1, this).addObserverToValue(this);
    }

    /**
     * Sets the ldIn input value
     *
     * @param ldIn ldIn
     * @throws BitsException BitsException
     */
    protected void setLdIn(ObservableValue ldIn) throws BitsException {
        this.ldIn = ldIn.checkBits(1, this).addObserverToValue(this);
    }

    @Override
    public ObservableValues getOutputs() {
        return output.asList();
    }

    @Override
    public void readInputs() throws NodeException {
        long data = 0;
        boolean clk = clkIn.getBool();
        boolean str;
        if (!lastClk && clk) {
            str = strIn.getBool();
            if (str)
                data = dataIn.getValue();
        } else
            str = false;
        ld = ldIn.getBool();
        if (ld || str)
            addr = (int) addrIn.getValue();

        if (str) {
            memory.setData(addr, data);
            updateImage(addr, data);
            updateGraphic();
        }

        lastClk = clk;
    }

    private void updateImage(int addr, long data) {
        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int idx = addr * 2;
        final long inv = ~Long.reverse(data);
        pixels[idx] = (byte) (inv >>> 56);
        pixels[idx + 1] = (byte) (inv >>> 48);
    }

    @Override
    public void writeOutputs() throws NodeException {
        if (ld) {
            output.set(memory.getDataWord(addr), false);
        } else {
            output.set(0, true);
        }
    }

    @Override
    public DataField getMemory() {
        return memory;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getDataBits() {
        return bits;
    }

    @Override
    public int getAddrBits() {
        return addrBits;
    }

    private long lastUpdate = 0;

    private void updateGraphic() {
        long time = System.currentTimeMillis();
        if (time - lastUpdate > 10) {  // not more than 100 repaints per second
            SwingUtilities.invokeLater(() -> {
                if (graphicsDialog == null || !graphicsDialog.isVisible()) {
                    graphicsDialog = new HackGraphicsDialog(image, scaleFactor);
                    getModel().getWindowPosManager().register("HackDisplay_" + label, graphicsDialog);
                }
                graphicsDialog.updateGraphic();
            });
            lastUpdate = time;
        }
    }
}
