package Exceptions_src;

/**Exception levé si le dossier ressource n'existe pas ou ne contient pas de sous-dossier terrain
 */
public class MissingMapSpriteException extends RuntimeException{
    public MissingMapSpriteException(String message){
        super(message);
    }
}
