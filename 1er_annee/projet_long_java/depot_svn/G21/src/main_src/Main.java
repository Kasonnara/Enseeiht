package main_src;

import Affichage_src.menu_src.Menu;
import java.io.File;

/**
 * Created by kasonnara on 13/05/17.
 */
public class Main {
    /**
     * Lance le jeu
     * Le premier paramètre de la commande doit indiquer
     * le chemin d'accès au dossier ressource du jeu.
     * Si aucun paramètre n'est fourni, recherche un dossier "*ressources*" dans le dossier courant
     *
     * @param argv paramètre de la ligne de commande
     */
    public static void main(String[] argv){

        String ressourcesPath = "ressources/";
        //Lire les paramètre de le ligne de commande
        if (argv.length == 1){
            ressourcesPath = argv[0];
        } else if (argv.length > 1) {
            System.err.println("ERREUR : Trop d'argument fourni\n Usage: java Init [ressourceDirectory]\n ressourceDirectory : chemin relatif ou absolu jusqu'au dessier ressources du jeu\n    valeur par défaut : 'ressources'");
            System.exit(1);
        }

        //Verifier que le dossier ressources existe.
        File ressourceDirectory = new File(ressourcesPath);
        if (!(ressourceDirectory.exists() && ressourceDirectory.isDirectory())){
            System.err.println("ERREUR : Dossier ressource inexistant : " + ressourceDirectory.getAbsolutePath());
            System.exit(1);
        }



        //initialiser le jeu
        //Game jeu = new Game(ressourcesPath);
        String[] argv2 = {ressourcesPath};
        Menu.main_menu(argv2);
        //Démarrer le jeu TODO dès que possible remplacer ce qui suit par le lancement du menu
        //try {
            //jeu.mainLoop();
        //} catch (RuntimeException e) {
        //    System.err.println("ERREUR : " + e.getMessage());
        //}



    }
}
