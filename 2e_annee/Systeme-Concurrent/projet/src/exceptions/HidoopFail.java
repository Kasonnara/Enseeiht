package exceptions;

/** This abstract class is use by Job main to filter custom unpredictable exceptions that can occure
 * during the process and can not be solved*/
public abstract class HidoopFail extends RuntimeException{
    public HidoopFail(String message) {
        super(message);
    }
}
