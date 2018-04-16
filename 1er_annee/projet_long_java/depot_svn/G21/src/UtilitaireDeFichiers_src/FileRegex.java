package UtilitaireDeFichiers_src;

import Exceptions_src.MissingMapSpriteException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by sdeneuvi on 15/05/17.
 */
public class FileRegex {
    /**
     * Parcourt le dossier pointé par le chemin, et renvoi la liste de tous les fichier correspondant à la regex
     * @param directory String, chemin d'accès au dossier à inspecter.
     * @param regex String, regex a appliquer.
     * @return List<String>, liste des fichiers trouvés.
     */
    public static List<String> getFilenameByRegex(String directory, String regex) {
        String [] s = new File(directory).list();
        if (s == null) throw new MissingMapSpriteException("CAUSE D'ERREUR : Dossier " + directory+ " innexistant ou vide");

        // filtrer selon la regex (0-9)(0-9)(0-9)(*).png
        Pattern p = Pattern.compile(regex);
        List<String> listeString = new ArrayList<String>();
        for (int i=0; i<s.length;i++) {
            if ( p.matcher(s[i]).matches()) {
                listeString.add(s[i]);
                //System.out.println("file found : "+s[i]);
            }
        }
        return listeString;
    }
}
