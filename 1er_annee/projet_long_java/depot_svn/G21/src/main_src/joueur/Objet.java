package main_src.joueur;

import Exceptions_src.NomObjetDejaUtilise;
import Exceptions_src.ObjetInnexistant;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static UtilitaireDeFichiers_src.FileRegex.getFilenameByRegex;

/**
 * Created by kasonnara on 20/05/17.
 */
public class Objet {

    private String name;
    private ImageIcon image;

    private Objet(String name, ImageIcon image) {
        assert name != null : "ERREUR: poignée name nulle.";
        assert image != null : "ERREUR: poignée image nulle";
        this.name = name;
        this.image = image;
    }

    /**
     * Construit un objet a partir du chemin d'accès au fichier image de l'objet
     * @param name String, le nom de l'objet
     * @param imagePath String, le chemin d'accès a l'image de l'objet
     */
    private Objet(String name, String imagePath) {
        this(name, new ImageIcon(imagePath));
        assert imagePath != null : "ERREUR: poignée imagePath nulle.";
        assert new File(imagePath).exists() : "ERREUR: Fichier " + imagePath + " innexistant.";
    }

    public String getName() {
        return this.name;
    }

    public ImageIcon getImage() {
        return this.image;
    }

    /** renvoi l'instance de l'objet dans la map des objets*/
    public static Objet getObjectRef(String objectName, Map<String, Objet> allObjectMap){
        if ( allObjectMap.containsKey(objectName) ) {
            return allObjectMap.get(objectName);
        } else {
            throw new ObjetInnexistant(objectName);
        }
    }

    /**
     * Charge tous les objet contenu dans le dossier 'objets' du dossier ressource, dans la variable globale de la classe.
     * @param ressourceDirectory String, le chemin d'accès au dossier ressource du jeu.
     */
    public static Map<String, Objet> loadAllObjects(String ressourceDirectory) {
        List<String> objectsFilenames = getFilenameByRegex(ressourceDirectory + "objets/", ".*.png");
        Map<String, Objet> objetMap = new HashMap();
        for (int i = 0; i < objectsFilenames.size(); i++) {
            String filename = objectsFilenames.get(i);
            String name = filename.substring(0, filename.length() - 4);
            System.out.println("name: " + name + ", filename : " + filename);
            if (objetMap.containsKey(name)) {
                throw new NomObjetDejaUtilise(name);
            } else {
                objetMap.put(name, new Objet(name, ressourceDirectory + "objets/" + objectsFilenames.get(i)));
            }
        }
        return objetMap;
    }
}
