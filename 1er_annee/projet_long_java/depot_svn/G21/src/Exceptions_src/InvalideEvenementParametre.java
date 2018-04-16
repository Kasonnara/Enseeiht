/** Erreur renvoyé quand un paramètre d'une condition ou d'un effet d'un évenement est invalide.
 * plus de détail est fournir par le message d'erreur de l'exception
 * Created by sdeneuvi on 04/05/17.
 */
package Exceptions_src;

public class InvalideEvenementParametre extends RuntimeException{
    public InvalideEvenementParametre(String messageErreur){
        super(messageErreur);
    }
}
