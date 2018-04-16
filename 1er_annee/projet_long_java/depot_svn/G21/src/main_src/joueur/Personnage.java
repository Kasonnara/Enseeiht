package main_src.joueur;

import main_src.Game;
import main_src.Terrain;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;

import javax.swing.ImageIcon;

public class Personnage extends Observable implements KeyListener {

    public enum Direction {
		AUCUNE,
		HAUT,
		BAS,
		GAUCHE,
		DROITE,
	}
	
	private int positionX, positionY;
	private ImageIcon sprite;
	private Game jeu;

	private StatistiqueManager stats;

	private Inventaire inventaire;
	
	public Personnage(int positionX, int positionY, ImageIcon sprite, Game jeu, StatistiqueManager stats, Inventaire inventaire) {
		System.out.println("Initialisation main_src.joueur.Personnage");
		this.positionX = positionX;
		this.positionY = positionY;
		this.sprite = sprite;
		this.jeu = jeu;
		this.stats = stats;
		this.inventaire = inventaire;
	}
	
	public Personnage(int positionX, int positionY, String nomSprite, Game jeu) {
		this(positionX, positionY, new ImageIcon(nomSprite), jeu, new StatistiqueManager(), new Inventaire()); // exception à spécifier
	}
	
	public Personnage(int positionX, int positionY, Game jeu) {
		this(positionX, positionY, jeu.getRessourcesPath() + "joueur.png", jeu);
	}

	// Getteur

	public ImageIcon getSprite() {
		return sprite;
	}

	public int getPositionX() {
		return this.positionX;
	}
	
	public int getPositionY() {
		return this.positionY;
	}

    public StatistiqueManager getStats() {
        return stats;
    }

    public Inventaire getInventaire() {
        return inventaire;
    }

// Setteur

	public void setPosition(int positionX, int positionY) {
		this.positionX = positionX;
		this.positionY = positionY;
		System.out.println("Nouvelle position : " + positionX + ", " + positionY);
		this.setChanged();
		this.notifyObservers();
	}

	public void setPositionX(int positionX) {
		setPosition(positionX, this.positionY);
	}
	
	public void setPositionY(int positionY) {
		setPosition(this.positionX, positionY);
	}

    public void setStats(StatistiqueManager stats) {
        this.stats = stats;
    }

	/* A utiliser avec un KeyListener */
	public static Direction convertirToucheEnDirection(KeyEvent e) {
		int k = e.getKeyCode();
		
		switch (k) {
		case KeyEvent.VK_UP:
			return Direction.HAUT;
		case KeyEvent.VK_DOWN:
			return Direction.BAS;
		case KeyEvent.VK_LEFT:
			return Direction.GAUCHE;
		case KeyEvent.VK_RIGHT:
			return Direction.DROITE;
		default:
			return Direction.AUCUNE;
		}
	}
	
	public boolean deplacer(Terrain terrain, Direction direction) {
		int x = this.positionX, y = this.positionY;
		switch (direction) {
		case HAUT:
			y -= 1;
			break;
		case BAS:
			y += 1;
			break;
		case GAUCHE:
			x -= 1;
			break;
		case DROITE:
			x += 1;
			break;
		default:
			break;
		}
		
		if (terrain.collides(x, y)) {
			return false;
		}
		else {
			setPosition(x, y);
			return true;
		}
	}

	// Detection évenement claviers

	@Override
	public void keyTyped(KeyEvent keyEvent) {}
	@Override
	public void keyPressed(KeyEvent keyEvent) {
        if (!jeu.isGamePaused() && jeu.getCurrentTerrain() != null) {
            this.deplacer(jeu.getCurrentTerrain(), convertirToucheEnDirection(keyEvent));
        }
    }
	@Override
	public void keyReleased(KeyEvent keyEvent) {}
}
