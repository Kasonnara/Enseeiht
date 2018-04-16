package Affichage_src.menu_src;
import main_src.Game;

import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.*;

/**
  * @author Maryam Seifddine
  */


class MenuSimpleDialogInteraction 
    implements ActionListener{
    int val1 ;
    int val2 ;
    MenuSimpleDialog msd;

    public MenuSimpleDialogInteraction(JFrame parentframe,int jvit,int volson){
	val1=jvit;
	val2=volson;
	msd = new MenuSimpleDialog(parentframe,this);
	msd.pack();
	msd.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent evt){
	Object source = evt.getSource();
	
	if (source instanceof JTextField){
	    System.out.println("texte ecrit");
	    msd.str = msd.t1.getText();
	    msd.t1.setText(msd.str);
	    System.out.println("  " + msd.str);
	}
	else if (source == msd.b_retour){
	    System.out.println("bouton retour enfonce");
	    msd.setVisible(false);
	}
	else if (source == msd.b_go){
	    System.out.println("bouton go enfonce");
	    msd.setVisible(false);
	  //  InterfaceInteraction ii = new InterfaceInteraction(msd,val1,val2);
        Game jeu = new Game(Menu.ressourcePath);
        jeu.mainLoop();

	}
    }

 

}
