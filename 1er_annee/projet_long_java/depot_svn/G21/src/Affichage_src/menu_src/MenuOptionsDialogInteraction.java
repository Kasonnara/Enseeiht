package Affichage_src.menu_src;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.*;

/**
  * @author Maryam Seifddine
  */


public class MenuOptionsDialogInteraction 
    implements ActionListener, AdjustmentListener{

    MenuOptionsDialog mod;

    int value_r1;
    int value_r2;

    public MenuOptionsDialogInteraction(JFrame parent){
	mod = new MenuOptionsDialog(parent,this);
	value_r1 = mod.r1.getValue();
	value_r2 = mod.r2.getValue();
	mod.pack();
	mod.setVisible(true);
	
    }

    public int valeurValue_r1(){
        return mod.r1.getValue();
    }
 
    public int valeurValue_r2(){
        return mod.r2.getValue();
    }
 

    public void actionPerformed(ActionEvent evt){
	Object source = evt.getSource();
	if (source == mod.b_retour){
	    System.out.println("bouton retour enfonce");
	    mod.setVisible(false);
	}
    }

    public void adjustmentValueChanged(AdjustmentEvent evt){
	Object source = evt.getSource();
	if (source == mod.r1){
	    System.out.println("vitesse joueur changee:" + mod.r1.getValue());
	}
	else if (source == mod.r2){
	    System.out.println("volume son changee:" + mod.r2.getValue());
	}
    }

}

