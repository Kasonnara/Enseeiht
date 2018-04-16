package Affichage_src.menu_src;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.*;

/**
  * @author Maryam Seifddine
  */


public class MenuSimpleDialog extends Dialog {

    String str;

    
    String bouton1 = "  RETOUR  ";
    String bouton2 = "  GO ...  ";
    ImageIcon b_retourIcon = new ImageIcon("images/go.gif");
    JButton b_retour = new JButton(bouton1,b_retourIcon);
    JButton b_go = new JButton(bouton2);
  
    JTextField t1 = new JTextField("" + str);
    //JFrame frame;
    //InterfaceDialog frameinterface;

    //construire la grille d'affichage pour le GridBagLayout
    public void buildConstraints(GridBagConstraints gbc, int gx, int gy, 
				 int gw, int gh, int wx, int wy, int ix,
				 int iy){
	gbc.gridx = gx;
	gbc.gridy = gy;
	gbc.gridwidth = gw;
	gbc.gridheight = gh;
	gbc.weightx = wx;
	gbc.weighty = wy;
	gbc.ipadx = ix;
	gbc.ipady = iy;
    }

    public Insets getInsets(){
	return new Insets(30,100,30,100);
    }
    
    public MenuSimpleDialog(JFrame parent , ActionListener al){
	super(parent,"entrez le nom du joueur", true);
	
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints constraints = new GridBagConstraints();

	JLabel l1 = new JLabel("Nom du joueur (puis tapez entree): ");
	buildConstraints(constraints,0,0,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(l1, constraints);

	//JTextField t1 
	buildConstraints(constraints,1,0,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(t1, constraints);


	// b_retour 
	buildConstraints(constraints,0,3,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_retour, constraints);
	b_retour.addActionListener(al);


	// b_go
	buildConstraints(constraints,1,3,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_go, constraints);
	b_go.addActionListener(al);

	

	add(l1);
	add(t1);
	add(b_retour);
	add(b_go);
	setLayout(gridbag);

	pack();
	
    }
  
}
