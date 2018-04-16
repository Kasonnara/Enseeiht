package Evenements_src;

import Exceptions_src.InvalideEvenementParametre;
import main_src.Game;

/**
 * Created by kasonnara on 24/05/17.
 */
public class EffectDialogue extends Thread implements EvenementEffect{

    private String text;
    private Game tempJeu;
    /**
     * Constructeur parseur,
     * identifie les paramètres dont il a besoin parmi la liste de chaine de caratère fournie
     * @param params String[], liste des paramètres issus du fichier de config (inclu l'identificateur de l'effet en position 0)
     */
    public EffectDialogue(String[] params) {
        super();
        int c = Evenement.countRealParametreNumber(params);

        if (c < 2 || c > 2 ) throw new InvalideEvenementParametre("CAUSE D'ERREUR : Nombre de paramètre de l'effet de dialogue invalide ");
        this.text = params[1];
        this.tempJeu = null;
    }

    @Override
    public void executer(Game jeu) {
        this.tempJeu = jeu;
        if (!this.isAlive()) {
            this.start();
        }
    }

    public void run () {
        tempJeu.getMainAfficheur().showDialogue(this.text);
    }
}
