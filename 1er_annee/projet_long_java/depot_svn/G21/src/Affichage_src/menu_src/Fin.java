package Affichage_src.menu_src;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.*;

/**
  * @author Maryam Seifddine
  */

public class Fin extends JFrame implements ActionListener{

    JButton b_quit; 
    JButton b_retour;

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
    
    public Fin(InterfaceDialog i, Frame fr){

	super("Fin");
	i.setVisible(false);
	fr.setVisible(false);

	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints constraints = new GridBagConstraints();

	JLabel lab =new JLabel("GAME OVER");
	buildConstraints(constraints,0,0,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(lab, constraints);

	b_quit =new JButton("QUIT GAME");
	buildConstraints(constraints,1,0,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_quit, constraints);
	b_quit.addActionListener(this);


	b_retour =new JButton("RETOUR MENU");
	buildConstraints(constraints,2,0,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_retour, constraints);
 	b_retour.addActionListener(this);

	JPanel pane = new JPanel();

	pane.add(lab);
	pane.add(b_quit);
	pane.add(b_retour);

	getContentPane().setLayout(gridbag);
	
	//faire du panneau le panneau de contenu du cadre. pane=container
	setContentPane(pane);
    }

    public void actionPerformed(ActionEvent evt){
	Object source = evt.getSource();
	if (source == b_quit){
	    System.out.println("bouton quit enfonce");
	    System.exit(0);
	}
	else if (source == b_retour){
	    System.out.println("bouton retour defonce");
	    setVisible(false);
	    
	}
    }


}

