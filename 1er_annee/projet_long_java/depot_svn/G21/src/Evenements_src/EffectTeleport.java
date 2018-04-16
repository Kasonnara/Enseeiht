package Evenements_src;

import Exceptions_src.InvalideEvenementParametre;
import main_src.Game;

/**Effet de téléportation du joueur
 * Cet effet se défini par deux coordonnées x et y et le nom de la carte de destination.
 * Cependant, il n'est pas obligatoire de parametrer les 3 valeur.
 * les paramètre laissées à -1 pour les coordonnées ou à "" pour la carte
 * ne seront simplement pas altéré par l'effet.
 *
 * Created by sdeneuvi on 03/05/17.
 */
public class EffectTeleport implements EvenementEffect{

    private int x;
    private int y;
    private String carte;

    /**
     * Constructeur parseur,
     * identifie les paramètres dont il a besoin parmi la liste de chaine de caratère fournie
     * @param params String[], liste des paramètres issus du fichier de config (inclu l'identificateur de l'effet en position 0)
     */
    public EffectTeleport(String[] params) {
        int c = Evenement.countRealParametreNumber(params);
        if (c < 3 || c > 4)
            throw new InvalideEvenementParametre("CAUSE D'ERREUR : Nombre de paramètre de l'effet de téléportation invalide ");
        try {
            this.x = Integer.parseInt(params[1]);
            this.y = Integer.parseInt(params[2]);
        } catch (NumberFormatException e) {
            throw new InvalideEvenementParametre("CAUSE D'ERREUR: le paramètre n'est pas un entier.");
        }
        this.carte = "";
        if (params.length == 4) {
            this.carte = params[3];
        }
    }

    /**Execute la téléportation du joueur principal du jeu.
     * @param jeu main_src.Game, l'instance principal du jeu contenant toutes les
     *            données sur lesquelles l'effet peut s'appliquer.
     * @runtimeExceptions peut renvoyer l'exception InvalideEvenementParametre si
     *            les coordonnées indiquées designent une case en dehors du terrain
     *            Cela ne devrait pas se produire sauf en cas d'erreur de conception
     *            de l'évenement.
     */
    @Override
    public void executer(Game jeu) {
        if (this.carte.length() > 0){
            jeu.changeCurrentTerrain(this.carte);
        }
        if (this.x >= jeu.getCurrentTerrain().getLengthX() || this.y >= jeu.getCurrentTerrain().getLengthY()){
            throw new InvalideEvenementParametre("CAUSE D'ERREUR : coordonnées de téléportation trop grande :(" + x + ", " + y + ") pour un terrain de taille (" + jeu.getCurrentTerrain().getLengthX() + ", " + jeu.getCurrentTerrain().getLengthY() + ").");
        }
        if (this.x > -1) {
            jeu.getMainPlayer().setPositionX(this.x);
        }
        if (this.y > -1) {
            jeu.getMainPlayer().setPositionY(this.y);
        }
    }



}
