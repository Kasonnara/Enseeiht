package Exceptions_src;

/**
 * Created by kasonnara on 25/05/17.
 */
public class ObjetInnexistant extends RuntimeException {
    public ObjetInnexistant(String objectName) {
        super("Aucun objet '" + objectName + "' n'existe dans le jeu.");
    }
}
