package main_src;

import Affichage_src.Afficheur;
import Evenements_src.Evenement;
import Exceptions_src.MissingMapData;
import main_src.joueur.Objet;
import main_src.joueur.Personnage;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;


/** Gère toutes les données et l'execution globale du jeu.
 * Le jeu est observable sur deux paramètre
 *
 * Created by sdeneuvi on 03/05/17.
 */
public class Game extends Observable {
    //Données de l'état du jeu
    /** Instance du personnage principale du joueur*/
    private Personnage mainPlayer;
    /** Instance du terrain actuellement chargé, null si aucun terrain n'est chargé*/
    private Terrain currentTerrain;
    /** liste des types d'objets chargé dans l'ensemble jeu*/
    private final Map<String, Objet> allObjectList;

    /**true  si le jeu est en pause (menu, inventaire ou dialogue)
     * false si le jeu est en cours */
    private boolean isGamePaused;
    
    /** Le tableau des evenements qui ont déjà été "initialisés" */
    List<Evenement> eventlist;

    /** Le nombre d'évènements dans le tableau */
    int nbevents;

    // Données de fonctionnement
    /** Chemin d'accès au dossier ressource du jeu */
    private String ressourcesPath;
    /** Instance de l'afficheur du jeu */
    private Afficheur mainAfficheur;



    /**
     * Constructeur automatique du jeu.
     * Le jeu construit est initialisé dans l'état 'en pause' et aucun terrain n'est chargé
     * @param ressourcesPath String, chemin d'accès au dossier ressources du jeu
     */
    public Game(String ressourcesPath){
        System.out.println("Initialisation main_src.Game(" + ressourcesPath + ")");
        this.ressourcesPath = ressourcesPath;
        this.allObjectList = Objet.loadAllObjects(ressourcesPath);
        this.mainPlayer =  new Personnage(0, 0, this);
        this.isGamePaused = true;
        this.currentTerrain = null;
        this.mainAfficheur = new Afficheur(this);
        this.mainAfficheur.addKeyListener(this.mainPlayer);
        this.nbevents = 0;
        this.eventlist =  new LinkedList<>();

    }

    public Personnage getMainPlayer() {
        return mainPlayer;
    }
    public Terrain getCurrentTerrain() {
        return currentTerrain;
    }
    public Afficheur getMainAfficheur() {
        return mainAfficheur;
    }
    public String getRessourcesPath() {
        return ressourcesPath;
    }
    public boolean isGamePaused() {
        return isGamePaused;
    }
    public Map<String, Objet> getAllObjectList() {
        return allObjectList;
    }

	/** Sauvegarde la partie dans ressources/sav/ */

	/* public void sauvegarder() {
		Gson gson = new Gson();
		gson.toJson(this, new FileWriter(this.ressourcesPath + "sav/sauvegarde.json"));
	} */
	
	
	/** Charge la partie */
	
	/* public void charger() {
		Gson gson = new Gson();
		this = gson.fromJson(new FileReader(this.ressourcesPath + "sav/sauvegarde.json""), main_src.Game.class);
	} */
		
    /**
     * Change le terrain courant par un nouveau designé par son nom et notifie les observateur du changement
     * @param carteName String, le nom du nouveau terrain a charger
     */
    public void changeCurrentTerrain(String carteName) {
        System.out.println("Changement de terrain " + carteName);
        //Changer le terrain
        Terrain newTerrain = new Terrain(this.ressourcesPath + "terrains/" + carteName + "/", this);
        this.currentTerrain = newTerrain;
        // notifier les observeurs du changement (notament l'afficheur)
        //this.mainAfficheur.afficherTerrain(this.currentTerrain);
        this.setChanged();
        this.notifyObservers(carteName);
    }

    /**
     * Défini l'état du jeu (pause/resumed) en notifiant les observateur du changement
     * @param newPauseState boolean, true passe le jeu en pause, false relance le jeu.
     */
    public void setPauseState(boolean newPauseState){
        if (this.isGamePaused != newPauseState) {
            this.isGamePaused = newPauseState;
            this.setChanged();
            this.notifyObservers( (newPauseState ? "$game_paused$" : "$game_resumed$") );
        }
    }

    public void mainLoop() {
        //trouveret charger la carte initiale du jeu (qui contient 'begin' dans son nom)
        try {
            this.changeCurrentTerrain( getPremiereCarte(this.ressourcesPath) );
        } catch (MissingMapData e) {
            System.err.println("ERREUR : " + e.getMessage());
            System.exit(1);
        }
        // Charger le sprite du personnage
        this.mainAfficheur.updatePersoImage(this.mainPlayer);
        Terrain firstTerrain = this.getCurrentTerrain();
        this.mainPlayer.setPosition(firstTerrain.getLengthX()/2, firstTerrain.getLengthY()/2);
        this.setPauseState(false);
    }

    private static String getPremiereCarte(String ressourcesPath){
        // lister tous les fichier du dossier ressource
        String [] fileList = new File(ressourcesPath+"/terrains").list();
        if (fileList == null) throw new MissingMapData("CAUSE D'ERREUR : dossier des matrices des terrains" + ressourcesPath + "/terrains" + " innexistant ou vide");

        // filtrer selon la regex *begin
        String regex = ".*begin";
        Pattern p = Pattern.compile(regex);
        boolean beginMapFound = false;
        int i = 0;
        while (i<fileList.length && !beginMapFound) {
            if ( p.matcher(fileList[i]).matches()) {
                beginMapFound = true;
                //System.out.println("file found : "+fileList[i]);
            } else {
                i++;
            }
        }
        // fin de boucle : (beginMapFound) OU (i >= fileList.length)
        if (beginMapFound) {
            return fileList[i];
        } else {
            throw new MissingMapData("CAUSE D'ERREUR : Impossible de trouver la carte de départ du jeu (le fichier de celle ci doit se terminer par 'beginmap.txt')");
        }
    }

    /**
     * Ajoute un évement a la liste des évenement déjà chargés.
     * @param evenement Evenements_src.Evenement, l'évenement à ajouter
     */
    public void ajouterEvenement(Evenement evenement) {
        this.nbevents += 1;
        this.eventlist.add(evenement);
    }
}
