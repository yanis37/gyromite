/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.deplacements.Direction;
import modele.deplacements.Etat;

public abstract class Entite {
    protected Jeu jeu;
    public Entite(Jeu _jeu) {
        jeu = _jeu;
    }

    public abstract boolean peutCombatre();
    public abstract boolean peutEtreRamasse();
    public abstract boolean peutEtreEcrase(Direction d);
    public abstract boolean peutMonterDescendre();
    public abstract boolean peutServirDeSupport();
    public abstract boolean peutPermettreDeMonterDescendre();
    public abstract void setOnCorde(boolean b);
    public abstract boolean isOnCorde();
    public abstract boolean isBad();


    public abstract Etat getEtat();
    public abstract void setEtat(Etat e);


}
