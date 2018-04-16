package Affichage_src.menu_src;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.*;

/**
  * @author Maryam Seifddine
  */


public class MenuInit extends JFrame{

    // bouton en global pour les ecouteurs
    String b1 = "  OPTIONS  ";
    String b2 = "  JOUER  ";
    String b4 = " CHARGER PARTIE ";
    String b6 = "  AIDE  ";
    String b5 = "  EXIT  ";
 
    ImageIcon b1Icon = new ImageIcon("images/options.gif");
    ImageIcon b2Icon = new ImageIcon("images/jouer.gif");
    ImageIcon b4Icon = new ImageIcon("images/charger.gif");
    ImageIcon b6Icon = new ImageIcon("images/aide.gif");
    ImageIcon b5Icon = new ImageIcon("images/exit.gif");
       
    JButton b_options = new JButton(b1,b1Icon);
    JButton b_exit = new JButton(b5,b5Icon);
    JButton b_aide = new JButton(b6,b6Icon);
    JButton b_jouer = new JButton(b2,b2Icon);
    JButton b_charger = new JButton(b4,b4Icon);

    JFrame frame1;

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
    
    public MenuInit(ActionListener al){
	super("Game");

	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints constraints = new GridBagConstraints();

	// b_options
	buildConstraints(constraints,0,0,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_options, constraints);
	b_options.addActionListener(al);
	//b_options.setMnemonic('r');
	//b_options.setToolTipText("Options du jeu.");

	// b_jouer
	buildConstraints(constraints,0,1,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;//CENTER;
	gridbag.setConstraints(b_jouer, constraints);
	b_jouer.addActionListener(al);
	//b_jouer.setMnemonic('j');
	//b_jouer.setToolTipText("Jeu tout seul.");

	// b_charger
	buildConstraints(constraints,0,2,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_charger, constraints);
	b_charger.addActionListener(al);
	//b_exit.setMnemonic('c');
	//b_exit.setToolTipText("charger une partie.");


	// b_aide
	buildConstraints(constraints,0,3,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_aide, constraints);
	b_aide.addActionListener(al);
	//b_aide.setMnemonic('o');
	//b_aide.setToolTipText("Indices sur le jeu.");

	// b_exit
	buildConstraints(constraints,0,4,1,1,100,100,100,50);
	constraints.fill = GridBagConstraints.BOTH;
	constraints.anchor = GridBagConstraints.CENTER;
	gridbag.setConstraints(b_exit, constraints);
	b_exit.addActionListener(al);
	//b_exit.setMnemonic('e');
	//b_exit.setToolTipText("Quittez le jeu.");

       

	JPanel pane = new JPanel();

	pane.add(b_options);
	pane.add(b_jouer);
	pane.add(b_charger);
	pane.add(b_aide);
	pane.add(b_exit);

	pane.setLayout(gridbag);

	//faire du panneau le panneau de contenu du cadre. pane=container
	setContentPane(pane);
    }


}
