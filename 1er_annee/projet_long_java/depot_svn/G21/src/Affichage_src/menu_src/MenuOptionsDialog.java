package Affichage_src.menu_src;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.*;

/**
  * @author Maryam Seifddine
  */


public class MenuOptionsDialog extends Dialog{


    // boutons en global pour les ecouteurs
    String bouton1 = "  RETOUR  ";
    ImageIcon b_retourIcon = new ImageIcon("images/left.gif");
    JButton b_retour = new JButton(bouton1,b_retourIcon);
    int value_r1;
    int value_r2;
    JScrollBar r1 = new JScrollBar(JScrollBar.HORIZONTAL,8,0,4,14);
    JScrollBar r2 = new JScrollBar(JScrollBar.HORIZONTAL,5,0,1,12);

   
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
    
    public MenuOptionsDialog(JFrame parent , ActionListener al){
	super(parent,"modifiez les options", true);

	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints constraints = new GridBagConstraints();

	JLabel l1 = new JLabel("Vitesse du Joueur : ");
	buildConstraints(constraints,0,0,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(l1, constraints);


	// JScrollBar r1
	r1.addAdjustmentListener((AdjustmentListener)al);
	buildConstraints(constraints,0,1,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(r1, constraints);
	
	JLabel l2 = new JLabel("Volume du son : ");
	buildConstraints(constraints,0,2,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(l2, constraints);

	// JScrollBar r2 
	r2.addAdjustmentListener((AdjustmentListener)al);
	buildConstraints(constraints,0,3,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(r2, constraints);
	
	JLabel l3 = new JLabel("");
	buildConstraints(constraints,0,4,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(l3, constraints);
	
	
	// b_retour
	buildConstraints(constraints,0,5,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_retour, constraints);
	b_retour.addActionListener(al);


	add(l1);
	add(l2);

	add(l3);
	add(b_retour);
	add(r1);
	add(r2);

	setLayout(gridbag);
	pack();
    }


}
