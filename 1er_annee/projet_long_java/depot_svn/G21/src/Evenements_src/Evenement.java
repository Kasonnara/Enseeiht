package Evenements_src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import Exceptions_src.InvalideEvenementParametre;
import Exceptions_src.MissingEvenementData;
import UtilitaireDeFichiers_src.FileRegex;
import main_src.Game;
import main_src.Terrain;

/**
 * Created by sdeneuvi on 01/05/17.
 */
public class Evenement{
    // les données suivantes sont les conditions par défauts hard-codés dans tout événement
    /** Compte le nombre de fois que l'évenement a été déclenché */
    private int triggerCount;
    /** Indique le nombre maximal de fois que l'évenement peut etre déclanché */
    private int maxTrigger;
    /** Position de la case de déclanchement de l'évenement*/
    private int positionX;
    private int positionY;

    private EffectVisualise visualisation;

    // Ici suivent les autres conditions de déclenchement facultatives et les effets
    /** Liste des conditions de l'évenement */
    EvenementCondition conditions[];
    /** Liste des effets de l'évenement */
    EvenementEffect effects[];

    /**Lien vers l'instance principale du jeu, necessaire pour réaliser les effet*/
    Game jeu;

    /**
     * Charge un evenement a partir du chemin d'accès à son fichier.
     * @param path String, chemin d'accès complet au fichier de l'évenement
     * @param jeu main_src.Game, instance principale du jeu.
     */
    public Evenement(String path, Game jeu, Terrain loadingTerrain){
        this(path, 0, jeu, loadingTerrain);
    }

    /**
     * Charge un evenement a partir du chemin d'accès à son fichier
     * et force la valeur de triggerCount.
     * (A utiliser pour charger une partie à partir d'une sauvegarde)
     * @param path String, chemin d'accès complet au fichier de l'évenement
     * @param triggerCount int, nombre de fois que l'évenement a été déclenché.
     */
    public Evenement(String path, int triggerCount, Game jeu, Terrain loadingTerrain){
        super();
        this.triggerCount = triggerCount;
        this.maxTrigger = -1;
        this.positionX = -1;
        this.positionY = -1;
        jeu.ajouterEvenement(this);
        this.jeu = jeu;
        //lire le fichier et extraire les données
        String[][] evenementData = readEvenementFile(path);

        // Instancier les conditions et les effets
        this.visualisation = null;
        parseConditionsEtEffets(evenementData, path, loadingTerrain);

    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public boolean isVisalisable() {
        return this.visualisation != null;
    }
    public EffectVisualise getVisualisation() {
        assert isVisalisable() : "ERREUR: Impossible d'obtenir l'image d'un évement non visualisable.";
        return visualisation;
    }
    /**
     * Crée et renvoi une HashMap contenant
     * pour chaque case "x:y" l'événement activable sur celle-ci.
     * @param terrainPath String, chemin d'accès au dossier du terrain
     * @param loadingTerrain Terrain, le terrain contenant les évenement
     * @return Evenements_src.Evenement[][]
     */
    public static Map<String,Evenement> loadMapEvenments(String terrainPath, Game jeu, Terrain loadingTerrain){
        //Récupération de la liste des fichiers correspondants à la regex [0-9][0-9][0-9][a-Z]*.event
        List<String> listeEvenements = FileRegex.getFilenameByRegex(terrainPath,"\\d\\d\\d.*.event");

        //Création de la grille des evenement
        Map<String,Evenement> eventMap = new HashMap();
        int i;
        for (i = 0; i < listeEvenements.size(); i++) {
            //TODO recuperer le 'triggercount' de la sauvegarde s'il y en a une
            Evenement event = new Evenement(terrainPath + listeEvenements.get(i), jeu, loadingTerrain);
            eventMap.put(Integer.toString(event.getPositionX()) +":"+ Integer.toString(event.getPositionY()), event);
            //System.out.println("adding event in " +Integer.toString(event.getPositionX()) +":"+ Integer.toString(event.getPositionY()));
            if (event.isVisalisable()) {
                loadingTerrain.addVisualisation(event);
            }
        }
        return eventMap;
    }

    /**
     * Lit le fichier et en extrait un tableau.
     * Chaque ligne contient en première élément l'identifiant
     * de la condition ou de l'effet puis l'ensemble de ses paramètres.
     * @param path String, le chemin d'accès au fichier de l'évenement.
     * @return le tableau des conditions et effets avec leur paramètres.
     */
    protected static String[][] readEvenementFile(String path){
        /** Tableau dynamique pour enregistrer chaque condition/effet
         * du fichier et ses paramètres, au fur et à mesure.*/
        List<String[]> dataList = new LinkedList<>();

        try {
            /** Compteur de ligne pour détailler le rapport d'erreur en cas de problème*/
            int ligneCount = 0;
            /** Lecteur du fichier texte*/
            Scanner input = new Scanner (new File(path));
            //Lire toutes les lignes du fichier
            while(input.hasNextLine()) {
                String ligne = input.nextLine();
                //ignorer les lignes vides et les commentaires
                if (ligne.length() != 0 && ligne.charAt(0) != '#') {
                    List<String> extractedData = new LinkedList<>();
                    /** indice glissant délimitant les paramètres lus*/
                    int i = -1;
                    int j = ligne.indexOf("=");
                    //Lire élements de la ligne.
                    while (j != -1) {
                        extractedData.add(ligne.substring(i + 1, j));
                        i = j;
                        j = ligne.indexOf(";", j + 1);
                    }
                    //extraction du dernier élément
                    extractedData.add(ligne.substring(i + 1, ligne.length()));
                    //Conversion en tableau classique
                    dataList.add(extractedData.toArray(new String[dataList.size()]));
                }
                ligneCount += 1;
            }
            //Conversion en tableau classique
            return dataList.toArray(new String[dataList.size()][]);
        } catch (FileNotFoundException e) {
            throw new MissingEvenementData("CAUSE D'ERREUR: Fichier d'évenement innexistant :'" + path + "'.");
        }
    }


    protected void parseConditionsEtEffets(String[][] evenementData, Terrain loadingTerrain){
        parseConditionsEtEffets(evenementData, "", loadingTerrain);
    }
    /**
     * Identifie et instancie les conditions et effets de l'évenement.
     * @param evenementData String[][], tableau des conditions/effets et leur paramètres.
     * @param path String, (facultatif) si précisé, permet en cas d'erreur d'indiquer le chemin du fichier erroné.
     */
    protected void parseConditionsEtEffets(String[][] evenementData, String path, Terrain loadingTerrain){
        int index_ligne;
        List<EvenementEffect> tempEffectList = new LinkedList<>();
        List<EvenementCondition> tempConditionList = new LinkedList<>();

        for (index_ligne = 0; index_ligne < evenementData.length; index_ligne++){
            try {
                switch (evenementData[index_ligne][0]) {
                    //Analyse des conditions
                    case "nb_trigger":
                        // condition maximum d'activation
                        this.maxTrigger = Integer.parseInt(evenementData[index_ligne][1]);
                        break;
                    case "position":
                        try {
                            this.positionX = Integer.parseInt(evenementData[index_ligne][1]);
                            this.positionY = Integer.parseInt(evenementData[index_ligne][2]);
                        } catch (NumberFormatException e) {
                            throw new InvalideEvenementParametre("CAUSE D'ERREUR: le paramètre n'est pas un entier.");
                        }
                        if (this.positionX < 0 || this.positionX >= loadingTerrain.getLengthX() || this.positionY < 0 || this.positionY >= loadingTerrain.getLengthY()) {
                            throw  new InvalideEvenementParametre("CASUE D'ERREUR: les coordonnées de l'évenement (" + this.positionX + ", " + this.positionY +") sont en dehors des limites du terrain.");
                        }
                        break;
                    case "statCondition":
                        tempConditionList.add(new ConditionStatistique(evenementData[index_ligne]));
                        break;
                    case "objetCondition":
                        tempConditionList.add(new ConditionInventaire(evenementData[index_ligne]));
                        break;
                    //Analyse des Effets
                    case "teleportEffect":
                        tempEffectList.add(new EffectTeleport(evenementData[index_ligne]));
                        break;
                    case "dialogueEffect":
                        tempEffectList.add(new EffectDialogue(evenementData[index_ligne]));
                        break;
                    case "statEffect":
                        tempEffectList.add(new EffectStatistique(evenementData[index_ligne]));
                        break;
                    case "objetEffect":
                        if (countRealParametreNumber(evenementData[index_ligne]) >= 3 && evenementData[index_ligne][3].equals("REMOVE")) {
                            String tempParamList[] = {"objetCondition", evenementData[index_ligne][1], "EXIST"};
                            tempConditionList.add(new ConditionInventaire(tempParamList));
                        } else {
                            //rien
                        }
                        tempEffectList.add(new EffectInventaire(evenementData[index_ligne]));
                        break;
                    case "terrainEffect":
                        EffectTerrain effetT = new EffectTerrain(evenementData[index_ligne]);
                        tempEffectList.add(effetT);
                        if (this.maxTrigger != -1 && this.triggerCount > 0) {
                            effetT.preExecuter(loadingTerrain);
                        }
                        break;
                    case "collisionEffect":
                        EffectCollision effetC = new EffectCollision(evenementData[index_ligne]);
                        tempEffectList.add(effetC);
                        if (this.maxTrigger != -1 && this.triggerCount > 0) {
                            effetC.preExecuter(loadingTerrain);
                        }
                        break;
                    case "visualiseEffect":
                        if (countRealParametreNumber(evenementData[index_ligne]) == 2) {
                            String ressourceDirectory = path.substring(0,path.lastIndexOf("/"));
                            ressourceDirectory = ressourceDirectory.substring(0,ressourceDirectory.lastIndexOf("/"));
                            ressourceDirectory = ressourceDirectory.substring(0,ressourceDirectory.lastIndexOf("/")+1);
                            //System.out.println("Recupération du chemin pour visulaisation : "+ressourceDirectory);
                            this.visualisation = new EffectVisualise(ressourceDirectory + evenementData[index_ligne][1]);
                        } else {
                            throw new InvalideEvenementParametre("CAUSE D'ERREUR : Nombre de paramètre de l'effet de visualisation invalide ");
                        }
                        break;
                    case "collectable":
                        // Evenement compositie pour simplifier.
                        // représente un objet posé sur le sol
                        this.maxTrigger = 1;
                        if (this.positionX == -1 || this.positionY == -1) {
                            throw new InvalideEvenementParametre("CASUE D'ERREUR: la position de l'évenement doit etre spécifié AVANT l'effet composite 'collectable'");
                        }
                        if (countRealParametreNumber(evenementData[index_ligne]) >= 2) {
                            String tempParamList[] = {"objetEffect", evenementData[index_ligne][1], "ADD"};
                            tempEffectList.add(new EffectInventaire(tempParamList));

                            // visualisation
                            //System.out.println("Recupération du chemin pour visulaisation : "+path);
                            String ressourceDirectory = path.substring(0,path.lastIndexOf("/"));
                            ressourceDirectory = ressourceDirectory.substring(0,ressourceDirectory.lastIndexOf("/"));
                            ressourceDirectory = ressourceDirectory.substring(0,ressourceDirectory.lastIndexOf("/")+1);
                            //System.out.println("Recupération du chemin pour visulaisation : "+ressourceDirectory);
                            this.visualisation = new EffectVisualise(ressourceDirectory +"objets/"+ evenementData[index_ligne][1]+".png");
                        } else {
                            throw new InvalideEvenementParametre("CAUSE D'ERREUR : Nombre de paramètre de l'effet collectable invalide");
                        }
                        break;
                    default:
                        throw new InvalideEvenementParametre("CAUSE D'ERREUR: Type d'effet ou de condition inconnu ");
                }
            } catch (InvalideEvenementParametre e) {
                // On remonte l'erreur mais en ajoutant des informations pour que le game designer la retrouve plus facilement
                throw new InvalideEvenementParametre(e.getMessage() + "( fichier '" + path + "', effet/condition numéro " + index_ligne + " )");
            }
        }
        //Affectation des conditions identifié
        this.conditions = new EvenementCondition[tempConditionList.size()];
        tempConditionList.toArray(this.conditions);

        //Affectation des effets identifié
        this.effects = new EvenementEffect[tempEffectList.size()];
        tempEffectList.toArray(this.effects);

    }

    /**
     * Verifie les conditions d'execution d'un événement.
     * @return boolean, True si toutes les conditions sont vérifiés (sauf la position)
     */
    public boolean isExecutable(){
        boolean valid = this.maxTrigger == -1 || (this.triggerCount < this.maxTrigger);
        int i = 0;
        while (valid && i < this.conditions.length) {
            valid = conditions[i].isExecutable(this.jeu);
            i += 1;
        }
        return valid;
    }

    /**
     * Execute (obligatoirment) les effets de l'interaction
     */
    private void executer(){
        int k;
        System.out.println("Execution event");
        for (k = 0; k < this.effects.length; k++){
            this.effects[k].executer(this.jeu);
        }
        this.triggerCount += 1;
        this.jeu.getCurrentTerrain().updateVisualisations(this.jeu);
    }


    /**
     * Execute si les conditions le permettent les effets de l'interaction
     */
    public void trigger(){
        if ((this.maxTrigger == -1 || (this.triggerCount < this.maxTrigger)) && this.isExecutable()) {
            this.executer();
        }
    }

    public static int countRealParametreNumber(String[] params) {
        int i=0;
        //System.out.println("Comptage des paramètre");
        while ( i < params.length && params[i] !=  null) {
            i++;
            //System.out.println(params[i]);
        }
        //System.out.println("compté :" + i);
        return i;
    }
}
