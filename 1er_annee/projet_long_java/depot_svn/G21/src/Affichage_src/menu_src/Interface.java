package Affichage_src.menu_src;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.*;

/**
  * @author Maryam Seifddine
  */

public class Interface extends JFrame{
    
    // boutons en global pour les ecouteurs
    String pause = "  PAUSE / ON  ";    
    String quit = " QUIT  ";
    String go = "  START GAME  ";
    String pauseoff = "  PAUSE / OFF  ";

    String retour = " RETOUR AU MENU ";

  
    JLabel labpause = new JLabel("PAUSE");
    JButton b_pause = new JButton(pause);
    JButton b_pauseoff = new JButton(pauseoff);
    JButton b_quit = new JButton(quit);
    JButton b_go = new JButton(go);
    JButton b_retour = new JButton(retour);
    JLabel labnom = new JLabel("Nom ");

           
    JPanel panemenu = new JPanel();

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
    
    public Interface(ActionListener al,String playername){

	super("Ceci est une interface!!!!");
	
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints constraints = new GridBagConstraints();

	String nom = playername;
	JTextField txt_nom = new JTextField(" " + nom,10);

	// b_pause
	buildConstraints(constraints,0,0,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.NONE;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_pause, constraints);
	b_pause.addActionListener(al);

	// b_pauseoff
	buildConstraints(constraints,1,0,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.NONE;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_pauseoff, constraints);
	b_pauseoff.addActionListener(al);

	// b_quit
	buildConstraints(constraints,2,0,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.NONE;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_quit, constraints);
	b_quit.addActionListener(al);

	// b_go
	buildConstraints(constraints,3,0,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.NONE;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_go, constraints);
	b_go.addActionListener(al);

	// labpause
	buildConstraints(constraints,0,1,2,1,100,100,0,0);
	constraints.fill = GridBagConstraints.NONE;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(labpause, constraints);


	// labnom
	buildConstraints(constraints,0,3,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.NONE;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(labnom, constraints);

	// txt_nom
	buildConstraints(constraints,1,3,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.NONE;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(txt_nom, constraints);
	txt_nom.setEditable(false);

	// b_retour
	buildConstraints(constraints,2,3,1,1,100,100,0,0);
	constraints.fill = GridBagConstraints.NONE;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_retour, constraints);
	b_retour.addActionListener(al);

	getContentPane().add(labpause);
	getContentPane().add(b_pause);
	getContentPane().add(b_pauseoff);
	getContentPane().add(b_quit);
	getContentPane().add(b_go);
	getContentPane().add(labnom);
	getContentPane().add(txt_nom);
	getContentPane().add(b_retour);

	
	getContentPane().setLayout(gridbag);
			
    }

}
