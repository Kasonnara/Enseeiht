package Affichage_src.menu_src;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;

/**
  * @author Maryam Seifddine
  */


public class MenuAideDialog extends Dialog{
    
    // variable de classe pour les textfields
    String indice1 = "Indice1";
    String indice2 = "Indice2";
    String indice3 = "Indice3";
    String ind1 = "...";
    String ind2 = "...";
    String ind3 ="...";


//public class TestIndice {
 
 // public static void main(String[] args) {
         
   // ArrayList al = new ArrayList();
    // al.add(ind1);
    // al.add(ind2);
    // al.add(ind3);
                
    // for(int i = 0; i < al.size(); i++)
   //  {
   //   System.out.println("l'indice " + i + " = " + al.get(i));
   // }               
  //}
//}
   
  
    
    // bouton en global pour les ecouteurs
    String bouton1 = "  RETOUR  ";
    ImageIcon b_retourIcon1 = new ImageIcon("images/left.gif");
    JButton b_retour = new JButton(bouton1,b_retourIcon1);
    
    JLabel t1 = new JLabel(" " + indice1);
    JLabel t2 = new JLabel(" " + indice2);
    JLabel t3 = new JLabel(" " + indice3);
    JLabel t4 = new JLabel(" " + ind1);
    JLabel t5 = new JLabel(" " + ind2);
    JLabel t6 = new JLabel(" " + ind3);
   
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
    
    public MenuAideDialog(JFrame parent , ActionListener al){
	super(parent,"modifiez les options", true);
	
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints constraints = new GridBagConstraints();

	JLabel l1 = new JLabel("T A B L E    D E S    3  D E R N I E R S    I N D I C E S");
	buildConstraints(constraints,0,0,2,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(l1, constraints);

	JLabel l2 = new JLabel("");
	buildConstraints(constraints,0,5,2,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(l2, constraints);
	
	JLabel l3 = new JLabel("Liste des indices");
	buildConstraints(constraints,0,1,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(l3, constraints);
	
	JLabel l4 = new JLabel("Voilà");
	buildConstraints(constraints,1,1,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(l4, constraints);
	
	// JLabel t1
	buildConstraints(constraints,0,2,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(t1, constraints);

	// JLabel t2
	buildConstraints(constraints,0,3,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(t2, constraints);

	// JLabel t3 
	buildConstraints(constraints,0,4,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(t3, constraints);

	// JLabel t4
	buildConstraints(constraints,1,2,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(t4, constraints);

	// JLabel t5 
	buildConstraints(constraints,1,3,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(t5, constraints);

	// JLabel t6
	buildConstraints(constraints,1,4,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(t6, constraints);

	// b_retour 
	buildConstraints(constraints,0,6,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_retour, constraints);
	b_retour.addActionListener(al);


	add(l1);
	add(l2);
	add(l3);
	add(l4);
	add(t1);
	add(t2);
	add(t3);
	add(t4);
	add(t5);
	add(t6);
	add(b_retour);

	
	setLayout(gridbag);

    }

}
