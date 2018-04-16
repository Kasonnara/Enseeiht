package Evenements_src;

import main_src.Game;

/**Les effets
 * Created by sdeneuvi on 03/05/17.
 */
interface EvenementEffect {
    /*
     * Chaque implementation doit posseder un constructeur par liste
     * @param params String[], liste des paramètre du fichier de config (identificateur de l'effet compris)
     */

    /**
     * Methode principale permettant d'executer l'effet
     * @param jeu main_src.Game, l'instance principal du jeu contenant toutes les
     *            données sur lesquelles l'effet peut s'appliquer.
     */
    void executer(Game jeu);
}
