package Evenements_src;

import Exceptions_src.InvalideEvenementParametre;
import main_src.Game;
import main_src.Terrain;

/**
 * Created by kasonnara on 21/05/17.
 */
public class EffectTerrain implements EvenementEffect{
    private int x;
    private int y;

    private int newTerrainIndice;
    /**
     * Constructeur parseur,
     * identifie les paramètres dont il a besoin parmi la liste de chaine de caratère fournie
     * @param params String[], liste des paramètres issus du fichier de config (inclu l'identificateur de l'effet en position 0)
     */
    public EffectTerrain(String[] params) {
        int c = Evenement.countRealParametreNumber(params);
        if (c < 4 || c > 4 ) throw new InvalideEvenementParametre("CAUSE D'ERREUR : Nombre de paramètre de l'effet de terrain invalide ");
        try {
            this.x = Integer.parseInt(params[1]);
            this.y = Integer.parseInt(params[2]);
            this.newTerrainIndice = Integer.parseInt(params[3]);
        } catch (NumberFormatException e) {
            throw new InvalideEvenementParametre("CAUSE D'ERREUR: le paramètre n'est pas un entier.");
        }
    }

    /**Execute la modification du terrain actuel du jeu.
     * @param jeu main_src.Game, l'instance principal du jeu contenant toutes les
     *            données sur lesquelles l'effet peut s'appliquer.
     * @runtimeExceptions peut renvoyer l'exception InvalideEvenementParametre si
     *            les coordonnées indiquées designent une case en dehors du terrain
     *            Cela ne devrait pas se produire sauf en cas d'erreur de conception
     *            de l'évenement.
     */
    @Override
    public void executer(Game jeu) {
        jeu.getCurrentTerrain().editTerrainTexture(this.x,this.y,this.newTerrainIndice);
    }

    /**utilisé  au rechargement de la carte, il préexecute l'évement car il s'est déjà produit*/
    public void preExecuter(Terrain loadingTerrain) {
        loadingTerrain.editTerrainTexture(this.x,this.y,this.newTerrainIndice);
    }
}
