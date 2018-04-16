package exceptions;

public class ConfigNotFound extends HidoopMissUsed {
    public ConfigNotFound(String expected_path){
        super("Le fichier de configuration n'a pas été trouvé a l'emplacement : " + expected_path);
    }
}
