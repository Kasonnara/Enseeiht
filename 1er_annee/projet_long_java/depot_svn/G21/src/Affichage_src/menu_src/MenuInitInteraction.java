package Affichage_src.menu_src;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.*;

/**
  * @author Maryam Seifddine
  */


class MenuInitInteraction implements ActionListener{
    MenuInit mi;
    int val1=0;//on pourra passer les valeurs de vitesse et de son en parametre
    int val2=0;//a MenuSimpleDialogInteraction()
    public MenuInitInteraction(){
	mi = new MenuInit(this);
	mi.pack();
	mi.setVisible(true);
    }

    public void actionPerformed(ActionEvent evt){
	Object source = evt.getSource();
	if (source == mi.b_aide){
	    System.out.println("bouton aide enfonce");
	     MenuAideDialogInteraction mrdi = new MenuAideDialogInteraction(mi);
	} 
	   	else if (source == mi.b_jouer){
	    System.out.println("bouton jeu simple enfonce");
	    //mi.setVisible(false);
	    MenuSimpleDialogInteraction msdi = new MenuSimpleDialogInteraction(mi,val1,val2);
	    
	}
	else if (source == mi.b_options){ 
	    System.out.println("bouton options enfonce");
	    MenuOptionsDialogInteraction modi = new MenuOptionsDialogInteraction(mi);
	    val1 = modi.mod.r1.getValue();//.value_r1;
	    val2 = modi.mod.r2.getValue();//.value_r2;
	    System.out.println("val1 "+val1);
	    System.out.println(" val2 "+val2);
	}
	else if (source == mi.b_exit){
	    System.out.println("bouton exit enfonce");
	    System.exit(0);
	}
    }
	

}
