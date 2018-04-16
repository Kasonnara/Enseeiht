package Exceptions_src;

/**Exception levée quand le fichier texte map n'existe pas
 */

public class MissingMapData extends RuntimeException{
    public MissingMapData(String message){
        super(message);
    }
}
