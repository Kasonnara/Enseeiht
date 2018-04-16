package exceptions;

public class DaemonUnreachable extends HidoopFail{
    public DaemonUnreachable(String daemonID){
        super("Impossible de contacter le démon " + daemonID);
    }
}
