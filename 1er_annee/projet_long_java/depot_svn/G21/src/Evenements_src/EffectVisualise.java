package Evenements_src;

import Exceptions_src.InvalideEvenementParametre;
import main_src.Game;
import main_src.Terrain;

import javax.swing.*;
import java.io.File;

/** Cet effet est a part car il n'a pas un role ponctuel comme les autres, mais permanant.
 * Created by kasonnara on 21/05/17.
 */
public class EffectVisualise{

    private ImageIcon image;

   /**
     *
     * @param imagePath String, chemin d'accès à l'image a visulaiser, relativement a la racine du dossier ressource
     */
    public EffectVisualise(String imagePath) {
        File f = new File(imagePath);
        if (f.exists()) {
            this.image = new ImageIcon(imagePath);
        } else {
            throw new InvalideEvenementParametre("CASUE D'ERREUR : l'image demandé pour visualiser l'évenement n'existe pas :" + imagePath);
        }
    }
    public ImageIcon getImage() {
        return image;
    }
}
