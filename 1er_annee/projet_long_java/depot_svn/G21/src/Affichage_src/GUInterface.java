package Affichage_src;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.LinkedList;

/**
 * Created by sdeneuvi on 16/05/17.
 */
public class GUInterface extends JPanel{

    /* Caractéristiques de la boite de dialogue qui sera affiché */
    /**Proportion de l'écran occupée par la boite de dialogue */
    private static final double dialogueProportion = 0.3;
    /**Nombre maximum de caractère par ligne */
    private static final int lineLenght = 15;
    /**Nombre maximum de ligne*/
    private static final int lineNumber = 2;


    private JPanel dialogueFond;
    /** Composant de la boite de dialogue*/
    private JLabel[] dialogueBox;



    public GUInterface(String ressourceDirectory) {
        super();
        this.setOpaque(false);
        // Création des élement du GUI
        this.dialogueBox = new JLabel[GUInterface.lineNumber];
        for (int i = 0; i < GUInterface.lineNumber; i++) {
            this.dialogueBox[i] = new JLabel();
        }
        this.dialogueFond = new JPanel();
        this.dialogueFond.setVisible(false);
        // Création du layout

        this.dialogueFond.setLayout(new FlowLayout());
        this.dialogueFond.setVisible(false);
        // Ajout des élement au layout
        this.add(this.dialogueFond);
        this.dialogueFond.setLayout(new GridLayout(GUInterface.lineNumber,1));
        for (int i = 0; i < GUInterface.lineNumber; i++) {
            this.dialogueFond.add(this.dialogueBox[i]);
        }
    }


    public void rescaleScreen(){
        Dimension fenetreSize = this.getParent().getSize();
        this.setBounds(0,0,fenetreSize.width,fenetreSize.height);
    }

    /**
     * Affiche le dialogue fournit en paramètre
     * (le reste du jeu sera a priori bloqué mais le événement conséquence d'entrée clavier non, le jeu devra etre mis en pause au préalable)
     * @param text String, texte a afficher dans la barre de dialogue
     */
    public void displayDialogue(String text) {
        rescaleScreen();
        System.out.println("Dialogue :" + text);
        List<String> lignes = cutText(text);
        dynamicLigneDisplay(lignes);
    }

    /**
     * Coupe le texte en différentes lignes en respectant si possible les espace, retour a la ligne etc
     * @param rawtext String, le text brute a découper
     * @return List<String> une liste de lignes.
     */
    private static List<String> cutText(String rawtext) {

        int lastSpace = -1;
        List<String> result = new LinkedList<String>();
        int currentLigneStart = 0;
        for (int i = 0; i < rawtext.length(); i++) {
            char c = rawtext.charAt(i);
            //System.out.println("i:"+i+", start:"+currentLigneStart+", space:"+lastSpace+", char:"+c);
            if (c == ' ') {
                lastSpace = i;
                if(i==currentLigneStart){
                    currentLigneStart = i+1;
                }
            }
            if (c == '\n') {
                // retour a la ligne
                result.add(rawtext.substring(currentLigneStart, i));
                currentLigneStart = i+1;
            } else if(i - currentLigneStart > GUInterface.lineLenght) {
                if (lastSpace <= currentLigneStart) {
                    // il n'y a pas un seul espace dans la ligne
                    result.add(rawtext.substring(currentLigneStart, i));
                    currentLigneStart = i+1;
                    //System.out.println("Forced cut");
                } else {
                    // il y a un epace dans la ligne
                    result.add(rawtext.substring(currentLigneStart, lastSpace));
                    currentLigneStart = lastSpace+1;
                    //System.out.println("smooth cut");
                }
            }

        }
        // ajouter les dernier caractères:
        result.add(rawtext.substring(currentLigneStart, rawtext.length()-1));
        return result;
    }
    public void redimensionnerDialogue(Dimension maxDim) {
        // redimensionner le JPanel

        this.dialogueFond.setBounds(0, (int)(maxDim.height * (1-GUInterface.dialogueProportion)), maxDim.width, (int)(maxDim.height * GUInterface.dialogueProportion));
        Dimension dialogueBoxSize = new Dimension(maxDim.width, (int)(maxDim.height * GUInterface.dialogueProportion));
        this.dialogueFond.setPreferredSize(dialogueBoxSize);
        //this.dialogueFond.setSize(dialogueBoxSize);

        //redimensionner la police d'écriture
        Font lastFont = this.dialogueBox[0].getFont();
        int largeurParChar = (dialogueBoxSize.width) / (this.lineLenght);
        int hauteurParChar = (dialogueBoxSize.height) / (this.lineNumber);
        for (int i = 0; i < GUInterface.lineNumber; i++) {
            this.dialogueBox[i].setFont(new Font(lastFont.getName(), Font.PLAIN, Math.min(largeurParChar, hauteurParChar)));
        }
    }

    private void dynamicLigneDisplay(List<String> lignes){
        assert lignes != null : "poignée lignes nulle";
        //afficher la boite de dialogue
        this.dialogueFond.setVisible(true);

        //affichage pour chaque ligne, chaque ligne
        for (int i = 0; i < lignes.size(); i++) {
            String s = "";
            // conserver l'affichage des <lineNumber> dernière lignes
            for (int k = 0; k < lineNumber - 1; k++) {
                if (i - GUInterface.lineNumber + 1 + k >= 0){
                    this.dialogueBox[k].setText(lignes.get(i - GUInterface.lineNumber + 1 + k));
                } else {
                    this.dialogueBox[k].setText("");
                }
            }
            // affichage caractère par caractère
            String currentLine = lignes.get(i);
            //System.out.println("affichage "+ currentLine);
            for (int j = 0; j < currentLine.length(); j++) {
                s = s + currentLine.charAt(j);
                dialogueBox[GUInterface.lineNumber - 1].setText(s);
                try {
                    if (currentLine.charAt(j) == '.') {
                        Thread.sleep(1000);
                    } else {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    // rien a faire de spécial
                }
            }
        }
        this.dialogueFond.setVisible(false);

    }

}
