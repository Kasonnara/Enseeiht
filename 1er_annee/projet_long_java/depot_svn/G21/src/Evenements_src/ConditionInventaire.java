package Evenements_src;

import Exceptions_src.InvalideEvenementParametre;
import main_src.Game;


public class ConditionInventaire implements EvenementCondition{

    public enum ObjetConditionType {
        EXIST, // le joueur possède l'objet
        NEXIST, // le joueur ne possède pas l'ojet
    }

    private String objectName;
    private ObjetConditionType conditionType;

    /**
     * Constructeur parseur,
     * objetCondition=objetName;[EXIST|NEXIST]
     * identifie les paramètres dont il a besoin parmi la liste de chaine de caratère fournie
     * @param params String[], liste des paramètres issus du fichier de config (inclu l'identificateur de l'effet en position 0)
     */
    public ConditionInventaire(String[] params) {
        int c = Evenement.countRealParametreNumber(params);
        if (c < 3 || c > 3 ) throw new InvalideEvenementParametre("CAUSE D'ERREUR : Nombre de paramètre de la condition statistique invalide ");

        this.objectName = params[1];

        this.conditionType = parseConditionType(params[2]);

    }

    /**
     * cherche a identifier un opérateur et si ce n'est pas le cas renvoi une RuntimeException précise de l'erreur
     * @param s String, le paramètre a analyser
     * @return StatCondtitionType, le type de l'opération.
     */
    private ObjetConditionType parseConditionType(String s) {
        try {
            return ObjetConditionType.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new InvalideEvenementParametre("CAUSE D'ERREUR : Type de test sur l'inventaire, inconnu.\n" +
                    "Liste des opérations possibles : " +
                    "        'EXIST' : le joueur possède l'objet\n" +
                    "        'NEXIST': le joueur ne possède pas l'objet\n");
        }
    }

    @Override
    public boolean isExecutable(Game jeu) {
        boolean result = jeu.getMainPlayer().getInventaire().exist(this.objectName);

        if (this.conditionType == ObjetConditionType.NEXIST) {
            result = !result;
        }
        return result;
    }
}
