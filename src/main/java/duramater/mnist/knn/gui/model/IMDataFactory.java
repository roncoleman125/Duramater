package duramater.mnist.knn.gui.model;

import java.io.IOException;

public interface IMDataFactory {
    public MDigit[] getDigits() throws IOException;
    public int getPixelsMagic();
    public int getLabelsMagic();
    public long getChecksum();
}
