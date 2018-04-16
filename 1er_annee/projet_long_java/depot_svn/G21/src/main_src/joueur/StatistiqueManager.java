package main_src.joueur;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kasonnara on 25/05/17.
 */
public class StatistiqueManager {

    private Map<String, Integer> statistiqueListe;

    public StatistiqueManager() {
        this.statistiqueListe = new HashMap();
    }

    /**
     * Indique si la statistique existe déjà.
     * @param statName String, le nom de la statistique.
     * @return boolean, true si la statistique existe déjà, false sinon.
     */
    public boolean exist(String statName) {
        return this.statistiqueListe.containsKey(statName);
    }

    /**
     * Renvoi la valeur de la statistique demandé, si elle n'existe pas la valeur initiale est renvoyée (0).
     * @param statName String, le nom dela statistique.
     * @return int, lavaleur de la statistique.
     */
    public int getStatistique(String statName) {
        if (this.exist(statName)) {
            return statistiqueListe.get(statName);
        } else {
            return 0;
        }
    }

    /**
     * Affecte la valeur de la statistique,
     *      si la statistique n'existe pas l'ancienne valeur est ecrasée,
     *      si elle n'existe pas la statistiqueest crée.
     * @param statName String, le nom dela statistique.
     * @param newValue int, la nouvelle valeur de la statistique.
     */
    public void setStatistique(String statName, int newValue) {
        statistiqueListe.put(statName, newValue);
    }

    /**
     * Ajoute la valeur add Value à la statistique, si elle n'existe pas la statistique est créée (initialisée à 0 + addValue).
     * @param statName String, le nom dela statistique.
     * @param addValue int, la nouvelle valeur de la statistique.
     */
    public void addStatistique(String statName, int addValue) {
        if (this.exist(statName)) {
            int lastValue = statistiqueListe.get(statName);
            statistiqueListe.put(statName, lastValue + addValue);
        } else {
            statistiqueListe.put(statName, addValue);
        }
    }
}
