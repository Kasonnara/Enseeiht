package exceptions;

public class CanNotReadConfig extends HidoopFail{
    public CanNotReadConfig(){
        super("Problème lors de la lecture du fichier de configuration");
    }
}
