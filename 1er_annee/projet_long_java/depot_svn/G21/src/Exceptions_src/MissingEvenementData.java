package Exceptions_src;

/** Exception levée quand le fichier de l'évenement n'existe pas.
 * Cette erreur ne devrait pas pouvoir etre levé par le game designer.
 * Created by sdeneuvi on 05/05/17.
 */

public class MissingEvenementData extends RuntimeException{
    public MissingEvenementData(String message){
         super(message);
    }
}
