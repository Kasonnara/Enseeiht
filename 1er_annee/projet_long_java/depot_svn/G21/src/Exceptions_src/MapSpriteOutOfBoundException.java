package Exceptions_src;

/**Exception levé quand le terrain utilise une image innexistante dans la
 * liste des sprites de terrain.
 *
 * Si cette erreur survient il est probable que le design de la carte
 * soit erronée (utilise des indices auquels aucune image n'est associé)
 * ou que les images en question ai mal été détéctée a cause d'une erreur
 * dans la regex analysant le nom du fichier image (cf InvalideSpriteIndexException).
 */
public class MapSpriteOutOfBoundException extends RuntimeException{
}
