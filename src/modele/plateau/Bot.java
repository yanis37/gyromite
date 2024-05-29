/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.deplacements.Direction;
import modele.deplacements.Etat;

import java.util.Random;

/**
 * Ennemis (Smicks)
 */
public class Bot extends EntiteDynamique {
    public Bot(Jeu _jeu) {
        super(_jeu);
    }

    private Direction directionCourante = Direction.droite;
    private Etat etat;

    private boolean onCorde = false;
    public void setOnCorde(boolean b){this.onCorde = b;}
    public boolean isOnCorde() { return onCorde; }



    public boolean peutMonterDescendre() { return false; }
    public boolean peutEtreEcrase(Direction d) { return (jeu.vaSur(this, d).peutServirDeSupport()); }
    public boolean peutEtreRamasse() { return false; }
    public boolean peutServirDeSupport() { return false; }
    public boolean peutPermettreDeMonterDescendre() { return false; };
    public boolean peutCombatre(){return true;};
    public Direction getDirectionCourante() {
        return directionCourante;
    }
    public void setDirectionCourante(Direction directionCourante) {
        this.directionCourante = directionCourante;
    }
    public Etat getEtat() {
        return etat;
    }
    public void setEtat(Etat etat) {
        this.etat = etat;
    }
    public Entite seraSur(Direction d) {return jeu.seraSur(this, d);}
    public boolean isBad(){return true;};

}
