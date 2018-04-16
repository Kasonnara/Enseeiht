package Exceptions_src;

/**Exception levée si l'indexation des fichiers images du terrain est incorrect
 * Les noms des fichiers images doivent:
 * - commencer par trois chiffres.
 * - ces nombres forment un indice unique a chaque fichier et les indices doivent etre consécutifs de 0 à N
 * - se termine par '.png'
 *
 * Si cette erreur apparait vérifiez que les noms des fichiers images vérifient bien les
 * trois règles ci dessus
 */
public class InvalidSpriteIndexException extends RuntimeException{}
