package tk.diffusehyperion.gamemaster.Util;

/**
 * A StringBuffer that can be "completed", meaning that the buffer will not be updated anymore.
 */
public class CompletableStringBuffer{

    public StringBuffer stringBuffer = new StringBuffer();

    public boolean completed = false;

    public CompletableStringBuffer() {
    }

    public void complete() {
        completed = true;
    }

    public String toString() {
        return stringBuffer.toString();
    }
}
