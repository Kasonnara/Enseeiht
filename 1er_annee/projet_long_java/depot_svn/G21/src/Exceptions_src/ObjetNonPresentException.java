package Exceptions_src;

/** Exception controlé par le compilateur renvoyé quand un objet a ajouté a un inventaire alors qu'il y est déjà
 * Created by kasonnara on 20/05/17.
 */
public class ObjetNonPresentException extends Exception {
    public ObjetNonPresentException(String name) {
        super(name);
    }
}
