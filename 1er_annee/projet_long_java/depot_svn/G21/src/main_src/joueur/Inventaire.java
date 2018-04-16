package main_src.joueur;

import Exceptions_src.ObjetNonPresentException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kasonnara on 20/05/17.
 */
public class Inventaire {
    /**
     * Dictionnaire de toutes les statistiques
     */
    private Map<String,Integer> objetsListe;

    public Inventaire() {
        this.objetsListe = new HashMap();
    }

    /**
     * Indique si l'objet existe déjà dans l'inventaire
     * @param objetName String, le nom de l'objet
     * @return boolean, true si l'objet existe déjà, false sinon.
     */
    public boolean exist(String objetName) {
        return this.objetsListe.containsKey(objetName);
    }

    /**
     * Indique si l'objet existe déjà dans l'inventaire
     * @param o Objet, l'objet
     * @return boolean, true si l'objet existe déjà, false sinon.
     */
    public boolean exist(Objet o) {
        return this.objetsListe.containsKey(o.getName());
    }

    /**
     * ajoute l'objet à l'inventaire.
     * @param newObjetName Objet, le nouvel objet.
     */
    public void addObjet(String newObjetName) {
        if (exist(newObjetName)) {
            objetsListe.put(newObjetName,objetsListe.get(newObjetName) + 1);
        } else {
            objetsListe.put(newObjetName,1);
        }
    }
    /**
     * retire l'objet à l'inventaire,
     *      si l'objet existe déjà, une exception est renvoyée,
     *      si il n'existe pas encore l'objet est enregistré.
     * @param rObjetName Objet, le nouvel objet.
     */
    public void removeObjet(String rObjetName) throws ObjetNonPresentException {
        if (exist(rObjetName)) {
            int num = objetsListe.get(rObjetName) - 1;
            if (num == 0) {
                objetsListe.remove(rObjetName);
            } else {
                objetsListe.put(rObjetName, num);
            }
        } else {
            throw new ObjetNonPresentException(rObjetName);
        }
    }
}
