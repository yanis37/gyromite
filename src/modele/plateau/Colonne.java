package modele.plateau;


import modele.deplacements.Direction;
import modele.deplacements.Etat;

public class Colonne extends EntiteDynamique {

    public Colonne(Jeu _jeu) { super(_jeu); };

    private boolean onCorde = false;
    public boolean isOnCorde() { return onCorde; }
    public void setOnCorde(boolean b){this.onCorde = b;}

    private Etat etat;
    public Etat getEtat(){ return etat;}
    public void setEtat(Etat e){this.etat = e;}

    public boolean peutEtreEcrase(Direction d) { return false; };
    public boolean peutServirDeSupport() { return true; };
    public boolean peutPermettreDeMonterDescendre() { return false; };
    public boolean peutEtreRamasse() { return false; }
    public boolean peutMonterDescendre() { return false; }
    public boolean peutCombatre(){return false;};
    public boolean isBad(){return false;};





}
