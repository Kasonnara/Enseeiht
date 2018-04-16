package Affichage_src.menu_src;

import java.awt.*;
import java.awt.event.*;

/**
  * @author Maryam Seifddine
  */


public class Menu {
	static String ressourcePath;
    public static void main_menu(String args[]){
    	Menu.ressourcePath = args[0];
    	MenuInitInteraction mii = new MenuInitInteraction();
		WindowListener l = new WindowAdapter(){
	    public void windowClosing(WindowEvent e){
		System.exit(0);
	    }
	};
    }
} 
