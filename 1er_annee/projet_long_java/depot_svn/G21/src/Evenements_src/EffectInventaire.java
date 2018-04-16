package Evenements_src;

import Exceptions_src.InvalideEvenementParametre;
import Exceptions_src.ObjetNonPresentException;
import main_src.Game;
import main_src.joueur.Objet;


public class EffectInventaire implements EvenementEffect {
    public enum ObjetEffectType {
        ADD, // le joueur gagne un exemplaire de l'ojet
        REMOVE, // le joueur perd un exemplaire de l'ojet
    }

    private String objectName;
    private ObjetEffectType effectType;

    /**
     * Constructeur parseur,
     * objetEffet=objetName;[ADD|REMOVE]
     * identifie les paramètres dont il a besoin parmi la liste de chaine de caratère fournie
     * @param params String[], liste des paramètres issus du fichier de config (inclu l'identificateur de l'effet en position 0)
     */
    public EffectInventaire(String[] params) {
        int c = Evenement.countRealParametreNumber(params);
        if (c < 3 || c > 3 ) throw new InvalideEvenementParametre("CAUSE D'ERREUR : Nombre de paramètre de la condition statistique invalide ");

        this.objectName = params[1];

        this.effectType = parseConditionType(params[2]);

    }

    /**
     * cherche a identifier un opérateur et si ce n'est pas le cas renvoi une RuntimeException précise de l'erreur
     * @param s String, le paramètre a analyser
     * @return StatCondtitionType, le type de l'opération.
     */
    private ObjetEffectType parseConditionType(String s) {
        try {
            return ObjetEffectType.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new InvalideEvenementParametre("CAUSE D'ERREUR : Type d'opération sur l'inventaire, inconnu.\n" +
                    "Liste des opérations possibles : " +
                    "        'ADD' : le joueur gagne un exemplaire de l'ojet\n" +
                    "        'REMOVE': le joueur perd un exemplaire de l'ojet\n");
        }
    }

    @Override
    public void executer(Game jeu) {
        // Vérifier que l'objet existe bien
        Objet.getObjectRef(this.objectName, jeu.getAllObjectList());

        //ajouter ou retirer l'objet de l'inventaire du joueur
        if (this.effectType == ObjetEffectType.ADD) {
            jeu.getMainPlayer().getInventaire().addObjet(this.objectName);
        } else {
            try {
                jeu.getMainPlayer().getInventaire().removeObjet(this.objectName);
            } catch (ObjetNonPresentException e) {
                throw new InvalideEvenementParametre("CAUSE D'ERREUR : L'objet " + e.getMessage() + " n'est pas présent dans l'inventaire du joueur, la condition \n     'objetCondition=" + e.getMessage() + ";EXIST' aurait du etre spécifiée dans l'évenement");
            }
        }

    }
}
