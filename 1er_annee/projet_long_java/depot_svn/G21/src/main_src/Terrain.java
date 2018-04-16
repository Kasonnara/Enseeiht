package main_src;

import Evenements_src.Evenement;
import Exceptions_src.MissingMapData;
import main_src.joueur.Personnage;

import javax.swing.*;
import java.util.*;
import java.lang.*;
import java.io.*;

/**
 * Created by stanislas Deneuville on 20/04/17.
 */
public class Terrain implements Observer {
    /**
     * Identifant du terrain
     */
    private String name;
    /**
     * la matrice des ID des images de la carte.
     */
    private int dataGrid[][];
    
    /**
     * la matrice des collisions.
     */
    private int collisionGrid[][];
    /**
     * grille des evenements du terrain
     */
    private Map<String,Evenement> evenements;

    private List<Evenement> visualisedEvenements;

    /**
     * Constructeur principal des terrain.
     * @param dataGrid int[][], la matrice des ID des images de la carte.
     */
    public Terrain(String name, int[][] dataGrid, int collisionGrid[][], Game jeu, Map<String, Evenement> evenements) {
        assert dataGrid.length > 0 && dataGrid[0].length > 0 : "Erreur : Matrice du terrain de taille nulle";
        assert collisionGrid.length == dataGrid.length && collisionGrid[0].length == dataGrid[0].length : "Erreur : tailles incohérentes";
        assert name != null : "Erreur : nom du terrain null";
        //assert evenements != null : "Erreur : poignée evenements nulle";

        System.out.println("Initialisation main_src.Terrain(" + name + ", <data>)");
        this.name = name;
        this.dataGrid = dataGrid;
        this.collisionGrid = collisionGrid;
        this.visualisedEvenements = new ArrayList<>();
        this.evenements = evenements;
        jeu.getMainPlayer().addObserver(this);
    }


	/**
	 * Constructeur hergonomique
	 * creer un terrain a partir du chemin d'accès au dossier du terrain
	 * @param cartePath String, chemin d'accès au dossier du terrain (avec un "/" final).
	 */
	public Terrain(String cartePath, Game jeu){
        System.out.println("Initialisation main_src.Terrain(" + name + ", <data>)");
        this.name = extractLastDirectory(cartePath);
        this.dataGrid = readmap(cartePath);
        this.collisionGrid = readmap(cartePath + "collision");
        this.visualisedEvenements = new ArrayList<>();
        this.evenements = Evenement.loadMapEvenments(cartePath, jeu, this);
        jeu.getMainPlayer().addObserver(this);
	}

    /**
     * Renvoie la taille du terrain sur l'axe horizontal
     * @return int, la taille du terrain sur l'axe horizontal
     */
    public int getLengthX() {
        return this.dataGrid.length;
    }

    /**
     * Renvoie la taille du terrain sur l'axe vertical
     * @return int, la taille du terrain sur l'axe vertical
     */
    public int getLengthY() {
        if (this.dataGrid.length == 0) {
            return 0;
        } else {
            return this.dataGrid[0].length;
        }
    }

    public String getName() {
        return name;
    }

    /**
     * Indique s'il y a collision à une position donnée
     */
    public boolean collides(int x, int y)
    {
    	int lenX = this.collisionGrid.length;
    	int lenY = this.collisionGrid[0].length;

    	return x < 0 || x >= lenX || y < 0 || y >= lenY || collisionGrid[x][y] > 0;
    }

    /**
     * Renvoie l'indice de l'image de la case se trouvant au coordonnée (x,y) de la carte.
     * @param x int, coordonnée x de la case recherchée.
     * @param y int, coordonnée x de la case recherchée.
     * @return int, l'indice de l'image de la case.
     */
    public int getID(int x, int y) {
        //System.out.println("x:"+x+" "+this.dataGrid.length+", y:"+y+" "+this.dataGrid[x].length);
        assert x < this.dataGrid.length : "Erreur : taille du terrain dépassée sur l'axe x "+this.dataGrid.length+">"+x;
        assert y < this.dataGrid[0].length : "Erreur : taille du terrain dépassée sur l'axe x "+this.dataGrid[0].length+">"+y;
        return this.dataGrid[x][y];
    }

    public int getCollisionID(int x, int y) {
        assert x < this.dataGrid.length : "Erreur : taille du terrain dépassée sur l'axe x "+this.dataGrid.length+">"+x;
        assert y < this.dataGrid[0].length : "Erreur : taille du terrain dépassée sur l'axe x "+this.dataGrid[0].length+">"+y;
        return this.collisionGrid[x][y];
    }
    
    /**
     * Lit la carte a partir d'un fichier 'map' du dossier ressources
     * @param chemin String, chemin d'accès du dossier ressources
     * @return int list list, la matrice des identifiants des images de chaque case de la carte
     */
     public static int[][] readmap(String chemin) {
        System.out.println("readmap(" + chemin + "map.txt)");
		try {
		    Scanner input = new Scanner (new File(chemin + "map.txt"));
		    int lignes = 0;
		    int maxColonnes = 0;
		    while(input.hasNextLine()) {
			    ++lignes;
			    int colonnes = 0;
			    Scanner colReader = new Scanner(input.nextLine());
			    while(colReader.hasNextInt())
			    {
			        colReader.nextInt();
			    	++colonnes;
			    }
			    if (colonnes > maxColonnes) {
			        maxColonnes = colonnes;
                }
		    }
		    int[][] map = new int[maxColonnes][lignes];
		    input.close();
		    //System.out.println("terrain dimension : x " + maxColonnes + ", y " + lignes);
		    input = new Scanner(new File(chemin + "map.txt"));
		    for(int j = 0; j < lignes; ++j)
		    {
			    for(int i = 0; i < maxColonnes; ++i)
			    {
				    if(input.hasNextInt())
				    {
				    	map[i][j] = input.nextInt();
				    }
			    }
		    }
            //System.out.println("terrain final dimension : x " + map.length + ", y " + map[0].length);
		    return map;
		} catch (FileNotFoundException e) {
			throw new MissingMapData("Carte innexistante : " + chemin + "map.txt");
		}
	}

    /** Edit une case du terrain */
    public void editTerrainTexture(int x, int y, int newTerrainIndice) {
        assert this.dataGrid != null : "Erreur edition du terrain avant l'initialisation de celui-ci";
        assert this.dataGrid.length != 0 : "Erreur edition d'un terrain de taille x 0";
        assert this.dataGrid[0].length != 0 : "Erreur edition d'un terrain de taille y 0";
        this.dataGrid[x][y] = newTerrainIndice;
    }
    public void editTerrainCollision(int x, int y, int newCollisionIndice) {

        assert this.collisionGrid != null : "Erreur edition des collision du terrain avant l'initialisation de celui-ci";
        assert this.collisionGrid.length != 0 : "Erreur edition des collisions d'un terrain de taille x 0";
        assert this.collisionGrid[0].length != 0 : "Erreur edition des collisions d'un terrain de taille y 0";
        this.collisionGrid[x][y] = newCollisionIndice;
    }

    public void addVisualisation(Evenement event) {
        this.visualisedEvenements.add(event);
    }

    public void updateVisualisations(Game jeu) {
        for (int i = 0; i < visualisedEvenements.size(); i++) {
            Evenement event = visualisedEvenements.get(i);
            if (event.isVisalisable()) {
                if (event.isExecutable()) {
                    jeu.getMainAfficheur().updateTerrainImage(event.getPositionX(), event.getPositionY(), false, event.getVisualisation().getImage());
                } else {
                    jeu.getMainAfficheur().updateTerrainImage(event.getPositionX(), event.getPositionY(), false, new ImageIcon());
                }
            }
        }
    }

	public static boolean[][] toBooleanMap(int[][] map){
         if (map.length > 0) {
             boolean[][] boolMap = new boolean[map.length][map[0].length];
             for (int x = 0; x < map.length; x++){
                 for (int y = 0; y < map[x].length; y++){
                     boolMap[x][y] = (map[x][y] > 0);
                 }
             }
             return boolMap;
         } else{
             return new boolean[1][0];
         }
    }

    /**
     * Recupère le dernier nom de dossier du chemin
     * exemple "racine/truc/bidule/"             ==> "bidule"
     *         "Racine/truc/bidule/chouette.txt" ==> "bidule"
     * @param path String, chemin a analyser
     */
    public static String extractLastDirectory(String path) {
	    String temp = path.substring(0, path.lastIndexOf('/'));
        return temp.substring(temp.lastIndexOf('/')+1);
    }

    /**
     * Le terrain observe le main_src.joueur.Personnage pour declencher les évenements.
     * @param observable
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof Personnage) {
            Personnage perso = (Personnage) observable;
            String key = Integer.toString(perso.getPositionX())+":"+Integer.toString(perso.getPositionY());
            if (this.evenements.containsKey(key)) {
                this.evenements.get(key).trigger();
            }
        }
    }


}
