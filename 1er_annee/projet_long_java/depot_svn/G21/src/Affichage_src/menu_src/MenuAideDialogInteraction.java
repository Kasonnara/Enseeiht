package Affichage_src.menu_src;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.*;

/**
  * @author Maryam Seifddine
  */


public class MenuAideDialogInteraction implements ActionListener{
    
    MenuAideDialog mad;
    
    public MenuAideDialogInteraction(JFrame parent){
	mad = new MenuAideDialog(parent,this);
	mad.pack();
	mad.setVisible(true);
    }

 
    public void actionPerformed(ActionEvent evt){
	Object source = evt.getSource();
	if (source == mad.b_retour){
	    System.out.println("bouton retour enfonce");
	    mad.setVisible(false);
	
	}
    }

}
