package Evenements_src;

import Exceptions_src.InvalideEvenementParametre;
import main_src.Game;


public class ConditionStatistique implements EvenementCondition{

    public enum StatConditionType {
        SUP, // a suppérieur à b
        INF, // a inférieur à b
        EQU, // a égale à b
        MUL, // a multiple de b
    }

    private String statName1;
    private StatConditionType conditionType;
    private String statName2;

    private int statLevel;


    /**
     * Constructeur parseur,
     * statCondition=statName1;conditionType;[statName2|Value]
     * identifie les paramètres dont il a besoin parmi la liste de chaine de caratère fournie
     * @param params String[], liste des paramètres issus du fichier de config (inclu l'identificateur de l'effet en position 0)
     */
    public ConditionStatistique(String[] params) {
        int c = Evenement.countRealParametreNumber(params);
        if (c < 4 || c > 4 ) throw new InvalideEvenementParametre("CAUSE D'ERREUR : Nombre de paramètre de la condition statistique invalide ");

        this.statName1 = params[1];

        this.conditionType = parseConditionType(params[2]);
        try {
            this.statLevel = Integer.parseInt(params[3]);
            // le 3e paramètre est une valeur numérique
            this.statName2 = "$$ValeurNumérique$$";
            // TODO s'assurer qu'un utilisateur ne puisse pas utiliser ce nom de statistique
        } catch (NumberFormatException e) {
            // le 3e paramètre est un nom de statistique
            this.statName2 = params[3];
        }
    }

    /**
     * cherche a identifier un opérateur et si ce n'est pas le cas renvoi une RuntimeException précise de l'erreur
     * @param s String, le paramètre a analyser
     * @return StatCondtitionType, le type de l'opération.
     */
    private StatConditionType parseConditionType(String s) {
        try {
            return StatConditionType.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new InvalideEvenementParametre("CAUSE D'ERREUR : Type de test sur les statistique, inconnu.\n" +
                    "Liste des opérations possibles : " +
                    "        'SUP' : a suppérieur à b\n" +
                    "        'INF' : a inférieur à b\n" +
                    "        'EQU' : a égale à b\n" +
                    "        'MUL' : a multiple de b\n");
        }
    }

    @Override
    public boolean isExecutable(Game jeu) {
        boolean result = false;

        int value1 = jeu.getMainPlayer().getStats().getStatistique(this.statName1);
        int value2 = (this.statName2.equals("$$ValeurNumérique$$") ? this.statLevel : jeu.getMainPlayer().getStats().getStatistique(this.statName2));

        switch (this.conditionType) {
            case SUP:
                result = value1 > value2;
                break;
            case INF:
                result = value1 < value2;
                break;
            case EQU:
                result = value1 == value2;
                break;
            case MUL:
                result = (value1 % value2) == 0;
                break;
        }
        return result;
    }
}
