package exceptions;

/** This abstract class is use by Job main to filter custom exceptions due to a
 * missconfiguration of hidoop by the user*/
public abstract class HidoopMissUsed extends Error {
    public HidoopMissUsed(String message) {
        super(message);
    }
}
