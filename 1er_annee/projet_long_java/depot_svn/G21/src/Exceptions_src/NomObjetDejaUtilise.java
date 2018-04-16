package Exceptions_src;

/**
 * Created by kasonnara on 25/05/17.
 */
public class NomObjetDejaUtilise extends RuntimeException{
    public NomObjetDejaUtilise(String name) {
        super("L'objet " + name + " existe déjà dans le jeu.");
    }
}
