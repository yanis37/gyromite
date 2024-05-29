/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.deplacements.Direction;
import modele.deplacements.Etat;

/**
 * Héros du jeu
 */
public class Heros extends EntiteDynamique {
    private int vie = 3;
    private Etat etat;
    private Entite ramasse;
    public boolean corde = false;

    public Heros(Jeu _jeu) {
        super(_jeu);
    }
    public boolean peutMonterDescendre() { return true; }
    public boolean peutEtreRamasse() { return false; }
    public boolean peutEtreEcrase(Direction d) { return (jeu.vaSur(this, d) != null && jeu.vaSur(this, d).peutServirDeSupport()); }
    public boolean peutServirDeSupport() { return false;}
    public boolean peutPermettreDeMonterDescendre() { return false; };
    public boolean isBad(){return false;};

    public boolean peutCombatre(){return true;}

    public boolean isOnCorde(){ return corde; }
    public void setOnCorde(boolean c){ this.corde = c; }


    public Etat getEtat() {
        return etat;
    }
    public void setEtat(Etat etat) {
        this.etat = etat;
    }


    public Entite getRamasse() {
        return ramasse;
    }
    public void setRamasse(Entite ramasse) {
        this.ramasse = ramasse;
    }

}
