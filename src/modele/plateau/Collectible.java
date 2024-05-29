package modele.plateau;

import modele.deplacements.Direction;
import modele.deplacements.Etat;

public class Collectible extends EntiteDynamique {
    public Collectible(Jeu _jeu) { super(_jeu); }

    private boolean onCorde = false;
    public void setOnCorde(boolean b){this.onCorde = b;}
    public boolean isOnCorde() { return onCorde; }

    private Etat etat;
    public Etat getEtat(){ return etat;}
    public void setEtat(Etat e){this.etat = e;}

    public boolean peutPorter() { return false; }
    public boolean peutCombatre() {
        return false;
    }
    public boolean peutEtreEcrase(Direction d) {
        return false;
    }
    public boolean peutMonterDescendre() { return false; }
    public boolean peutEtreRamasse() { return true; } // Mettre à true après
    public boolean peutServirDeSupport() {
        return false;
    }
    public boolean peutPermettreDeMonterDescendre() {
        return false;
    }
    public boolean isBad(){return false;};

}
