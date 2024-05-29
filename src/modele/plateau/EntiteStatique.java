package modele.plateau;

import modele.deplacements.Direction;
import modele.deplacements.Etat;

/**
 * Ne bouge pas (murs...)
 */
public abstract class EntiteStatique extends Entite {
    public EntiteStatique(Jeu _jeu) {
    super(_jeu);
}

    private boolean onCorde = false;
    private Etat etat;
    public Etat getEtat(){ return etat;}
    public void setEtat(Etat e){this.etat = e;}

    public boolean peutEtreEcrase(Direction d) { return false; }
    public boolean peutMonterDescendre() { return false; }
    public boolean peutEtreRamasse() { return false; }
    public boolean peutServirDeSupport() { return true; }
    public boolean peutPermettreDeMonterDescendre() { return false; }
    public boolean peutCombatre(){return false;}
    public void setOnCorde(boolean b){this.onCorde = b;}
    public boolean isOnCorde() { return onCorde; }
    public boolean isBad(){return false;};

}