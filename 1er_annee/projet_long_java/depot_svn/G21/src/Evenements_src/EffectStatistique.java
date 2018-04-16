package Evenements_src;

import Exceptions_src.InvalideEvenementParametre;
import main_src.Game;


public class EffectStatistique implements EvenementEffect {

    public enum StatEffectType {
        PLUS, // ajoute a et b dans a
        MOINS,// soustrait b à a dans a
        MULT, // multiplie a par b dans a
        DIVI, // divise a par b dans a
        COPY, // copie b dans a
    }

    private String statName1;
    private StatEffectType effectType;
    private String statName2;

    private int statLevel;


    /**
     * Constructeur parseur,
     * statEffect=statName1;EffectType;[statName2|Value]
     * identifie les paramètres dont il a besoin parmi la liste de chaine de caratère fournie
     * @param params String[], liste des paramètres issus du fichier de config (inclu l'identificateur de l'effet en position 0)
     */
    public EffectStatistique(String[] params) {
        int c = Evenement.countRealParametreNumber(params);
        if (c < 4 || c > 4 ) throw new InvalideEvenementParametre("CAUSE D'ERREUR : Nombre de paramètre de la condition statistique invalide ");

        this.statName1 = params[1];

        this.effectType = parseEffectType(params[2]);
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
    private StatEffectType parseEffectType(String s) {
        try {
            return StatEffectType.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new InvalideEvenementParametre("CAUSE D'ERREUR : Type d'opération sur les statistique inconnue.\n" +
                    "Liste des opérations possibles : " +
                    "        'PLUS'  : ajoute a et b dans a\n" +
                    "        'MOINS' : soustrait b à a dans a\n" +
                    "        'MULT'  : multiplie a par b dans a\n" +
                    "        'DIVI'  : divise a par b dans a\n" +
                    "        'COPY'  : copie b dans\n");
        }
    }

    @Override
    public void executer(Game jeu) {
        int value1 = jeu.getMainPlayer().getStats().getStatistique(this.statName1);
        int value2 = (this.statName2.equals("$$ValeurNumérique$$") ? this.statLevel : jeu.getMainPlayer().getStats().getStatistique(this.statName2));

        switch (this.effectType) {
            case PLUS:
                value1 += value2;
                break;
            case MOINS:
                value1 -= value2;
                break;
            case MULT:
                value1 *= value2;
                break;
            case DIVI:
                value1 /= value2;
                break;
            case COPY:
                value1 = value2;
                break;
        }
        jeu.getMainPlayer().getStats().setStatistique(this.statName1, value1);
    }
}
