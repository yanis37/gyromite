package modele.deplacements;

import modele.plateau.EntiteDynamique;
import modele.plateau.Jeu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

import static java.lang.Thread.*;

public class Ordonnanceur extends Observable implements Runnable {
    private Jeu jeu;
    private ArrayList<RealisateurDeDeplacement> lstDeplacements = new ArrayList<RealisateurDeDeplacement>();
    private long pause;
    public void add(RealisateurDeDeplacement deplacement) {
        lstDeplacements.add(deplacement);
    }

    private boolean resetCorde = false;

    public Ordonnanceur(Jeu _jeu) {
        jeu = _jeu;
    }

    public void start(long _pause) {
        pause = _pause;
        new Thread(this).start();
    }

    @Override
    public void run() {
        boolean update = false;

        while(true) {
            jeu.resetCmptDepl();
            if (jeu.getEtatJeu() == Etat.mort) {
                jeu.Mort();
            } else if (jeu.getEtatJeu() != Etat.victoire){
                for (RealisateurDeDeplacement d : lstDeplacements) {
                    if (d.realiserDeplacement()){
                        update = true;
                    }
                }
            }



            Controle4Directions.getInstance().resetDirection();


            if (update) {
                setChanged();
                notifyObservers();
            }

            try {
                sleep(pause);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public boolean getResetCorde() {
        return resetCorde;
    }

    public void setResetCorde(boolean resetCorde) {
        this.resetCorde = resetCorde;
    }

    public void clear(){
        lstDeplacements.clear();
    }
}
