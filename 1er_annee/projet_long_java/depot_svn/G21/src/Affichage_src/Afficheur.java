package Affichage_src; /**
 * Created by deneuvs on 20/04/17
 */

import Exceptions_src.InvalidSpriteIndexException;
import Exceptions_src.MapSpriteOutOfBoundException;
import UtilitaireDeFichiers_src.FileRegex;
import main_src.Game;
import main_src.joueur.Personnage;
import main_src.Terrain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Afficheur implements Observer{

    /**
     * Mémorise la taille courante des cases du terrain
     */
    private int currentCaseScale;
    /**Fenetre principale de l'application*/
    private JFrame fenetre;

    /**Tableau des images des cas du terrain du jeu*/
    private ImageIcon sprites[];
    /**
     * Zone d'affichage du terrain
     */
    private JPanel backgroundArea;
    private JLabel forgroundArea;
        /**Contient tous les composant de l'affichage du terrain*/
    private JLabel[][][] terrainLabels;

    /**
     * zone d'affichage de l'interface en avant plan
     */
    private GUInterface guiArea;
    /**
     * composant graphique du joueur
     */
    private JLabel player;


    /**Constructeur recommandé
     * Recherche automatiquement les fichiers dans le dossier ressource fournit.
     * @assert jeu.mainplayer est initialisé
     * //@param ressourceDirectory String, chemin d'accès au dossier ressource du jeu.
     */
    public Afficheur(Game jeu) {
        this(Afficheur.loadSprites(jeu.getRessourcesPath()), jeu);
    }

    /** Constructeur principal (Non recommandé)
     * Pour definir manuellement le tableau des images.
     * @assert jeu.mainplayer est initialisé
     * @param sprites ImageIcon[], le tableau des images du terrain du jeu
     */
    public Afficheur(ImageIcon sprites[], Game jeu){
        System.out.println("Initialisation Affichage_src.Afficheur");

        this.sprites = sprites;
        this.currentCaseScale = 0;

        // Initialisationd le la fenêtre principale
        this.fenetre = new JFrame("n7 Simulator");
        this.fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.fenetre.setVisible(true);

        // Ajout d'un JLayerPAne à plusieurs couches (pour superposer joueur, terrain et interface)
        JLayeredPane CouchesArea = new JLayeredPane();
        CouchesArea.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.fenetre.getContentPane().add(CouchesArea);

        // Ajouter la couche de l'interface (GUI)
        this.guiArea = new GUInterface(jeu.getRessourcesPath());
        CouchesArea.add(guiArea, 0);

        this.forgroundArea =  new JLabel();
        CouchesArea.add(this.forgroundArea, 1);

        // Ajouter la couche du personnage
        this.player = new JLabel();
        CouchesArea.add(player, 2);

        // Ajouter la couche de fond, le terrain
        this.backgroundArea = new JPanel();
        this.backgroundArea.setBackground(new Color(0,0,0));
        this.backgroundArea.setOpaque(true);
        this.backgroundArea.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        CouchesArea.add(backgroundArea, 3);

        this.fenetre.pack();

        // S'enregister en tant qu'observeur
        jeu.addObserver(this);
        jeu.getMainPlayer().addObserver(this);
    }

    /**Recherche et charge automatiquement les images du dossier ressource fourni
     * vérifiant la regex (0-9)(0-9)(0-9)(?*).png
     * @param ressourceDirectory String, le chemin d'accès au dossier ressource du jeu
     * @return ImageIcon[], le tableau des images extraites.
     */
    private static ImageIcon[] loadSprites(String ressourceDirectory){

        // lister tous les fichier du dossier ressource
        List<String> listeImages = FileRegex.getFilenameByRegex(ressourceDirectory+"/sprite_terrain" ,"\\d\\d\\d.*.png");

        // charger les images
        int listLenght = listeImages.size();
        ImageIcon spritesList[] = new ImageIcon[listLenght];
        for (int i=0; i<listLenght;i++) {
            // extraction de l'index
            //System.out.println(listeImages.get(i).substring(0,3));
            int imageIndex = Integer.parseInt(listeImages.get(i).substring(0,3));
            // verifier que l'index n'est pas dépassé
            //System.out.println("index "+imageIndex);
            if (imageIndex < listLenght) {
                // enregistrer l'image
                spritesList[imageIndex] = new ImageIcon(ressourceDirectory+"/sprite_terrain/"+listeImages.get(i)
                 );
            }
            else throw new InvalidSpriteIndexException();

        }
        return spritesList;
    }

    /**Affiche un terrain à l'écran
     * @param terr main_src.Terrain, le terrain a afficher
     */
    private void afficherTerrain(Terrain terr){
        System.out.println("Affichage du terrain " + terr.getName());
        this.backgroundArea.setVisible(false);
        // Calculer la taille du terrain
        Dimension afficheurSize =  this.fenetre.getSize();
        // cas particulier de la taille null a l'initialisation
        if (afficheurSize.getHeight() < 100 || afficheurSize.getWidth() < 100) {
            afficheurSize = Toolkit.getDefaultToolkit().getScreenSize();
        }

        // Calculer la taille des cases du terrain
        this.currentCaseScale =  (int)Math.min(
                Math.round(afficheurSize.getHeight() / terr.getLengthY()),
                Math.round(afficheurSize.getWidth() / terr.getLengthX())
        );
        // reinitialiser et redimensionner le gridlayout de la fenêtre
        Container backgroundContainer = this.backgroundArea;
        backgroundContainer.removeAll();
        backgroundContainer.setLayout(new GridLayout(terr.getLengthY(), terr.getLengthX(), 0, 0));
        this.backgroundArea.setSize(new Dimension(this.currentCaseScale * terr.getLengthX(), this.currentCaseScale * terr.getLengthY()));

        Container forgroundContainer = this.forgroundArea;
        forgroundContainer.removeAll();
        forgroundContainer.setLayout(new GridLayout(terr.getLengthY(), terr.getLengthX(), 0, 0));
        this.forgroundArea.setSize(new Dimension(this.currentCaseScale * terr.getLengthX(), this.currentCaseScale * terr.getLengthY()));

        // placer une image dans chaque case
        this.terrainLabels = new JLabel[terr.getLengthX()][terr.getLengthY()][2];
        for (int y = 0; y < terr.getLengthY(); y++) {
             for (int x = 0; x < terr.getLengthX(); x++){

                //System.out.println("add image " + terr.getID(x,y));
                JLabel newCase = new JLabel();
                // verifier que l'image à charger existe
                int imageID = terr.getID(x,y);
                if (imageID >= this.sprites.length) throw new MapSpriteOutOfBoundException();
                // redimensionner l'image
                newCase.setIcon(new ImageIcon(this.sprites[imageID].getImage().getScaledInstance(this.currentCaseScale, this.currentCaseScale, Image.SCALE_DEFAULT)));
                // ajouter l'image

                if (terr.getCollisionID(x,y) == -1) {
                    this.terrainLabels[x][y][0]= new JLabel();
                    this.terrainLabels[x][y][1]=newCase;
                } else {
                    this.terrainLabels[x][y][0]=newCase;
                    this.terrainLabels[x][y][1]=new JLabel();

                }
                 backgroundContainer.add(this.terrainLabels[x][y][0]);
                 forgroundContainer.add(this.terrainLabels[x][y][1]);
             }
        }
        this.backgroundArea.setVisible(true);
    }

    /**
     * Met a jour la position et la taille du joueur a l'écran
     * @param perso main_src.joueur.Personnage, le personnage a afficher
     * @assert le personnage se situe a l'intérieur des limites de la carte
     */
    public void afficherPersonnage(Personnage perso) {
        assert perso != null : "poignée personnage nulle";
        //System.out.println("Update personnage affichage");
        this.player.setBounds(
                perso.getPositionX() * this.currentCaseScale,
                perso.getPositionY() * this.currentCaseScale,
                this.currentCaseScale,
                this.currentCaseScale);
    }

    public void updatePersoImage(Personnage perso) {
        //System.out.println("Update personnage Image");
        assert perso != null : "Erreur : poignée perso nulle";
        this.player.setIcon(new ImageIcon(perso.getSprite().getImage().getScaledInstance(this.currentCaseScale, this.currentCaseScale, Image.SCALE_DEFAULT)));
    }


    public void updateTerrainImage(int x, int y, boolean editBackground, ImageIcon newIcone) {
        assert x >= 0 && x < this.terrainLabels.length : "Edition d'une case hors du terrain (x)";
        assert y >= 0 && y < this.terrainLabels[0].length : "Edition d'une case hors du terrain (y)";
        assert newIcone != null : "poignée newIcone nulle";
        int editBG = (editBackground? 0 : 1);
        // modifier l'image et redimensionne
        //assert newIcone.getImage() != null : "image nulle";
        //ImageIcon resizedIcone = new ImageIcon(newIcone.getImage().getScaledInstance(this.currentCaseScale, this.currentCaseScale, Image.SCALE_DEFAULT));

        this.terrainLabels[x][y][editBG].setIcon(newIcone);
    }

    /**
     * Notification d'une mise a jour importante du jeu :
     *      jeu mis en pause ou relancé ou changement de carte
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        //System.out.println("notification d'update afficheur");
        if (arg instanceof String && o instanceof Game) {
            String sarg = (String)arg;
            Game jeu = (Game)o;
            if (sarg != "$game_paused$" && sarg != "$game_resumed$") {
                // Afficher le nouveau terrain
                this.afficherTerrain(jeu.getCurrentTerrain());
                this.updatePersoImage(jeu.getMainPlayer());
            } else {
                //TODO masquer ou démasquer le menu
            }
        }else if (o instanceof Personnage) {
            Personnage perso = (Personnage)o;
            this.afficherPersonnage(perso);
        }
    }

    public void addKeyListener(KeyListener ecouteurClavier) {
        this.fenetre.addKeyListener(ecouteurClavier);
    }

    public void showDialogue(String text) {
        this.guiArea.redimensionnerDialogue(this.fenetre.getSize());
        this.guiArea.displayDialogue(text);
    }
}