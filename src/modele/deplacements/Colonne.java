package modele.deplacements;

import modele.plateau.*;

import java.util.ArrayList;

/**
 * A la reception d'une commande, toutes les cases (EntitesDynamiques) des colonnes se déplacent dans la direction définie
 * (vérifier "collisions" avec le héros)
 */
public class Colonne extends RealisateurDeDeplacement {
    private static Direction lastActionColonne = Direction.bas;
    private static Direction actualActionColonne = null;

    private static Colonne col;

    public static Colonne getInstance() {
        if (col == null) {
            col = new Colonne();
        }
        return col;
    }

    protected boolean realiserDeplacement() {
        boolean ret = false;
        if (actualActionColonne != null){
            ArrayList <modele.plateau.Colonne> colDirection = (ArrayList)lstColonnes.clone();
            int cmp = 0;
            while (!colDirection.isEmpty()) {
                modele.plateau.Colonne e = colDirection.get(cmp);
                Entite edir = e.regarderDansLaDirection(actualActionColonne);
                if (edir == null || !edir.peutServirDeSupport()) {
            // si il n'y a pas d'obstacle
                    colDirection.remove(e);
                    cmp--;
                    if(!e.avancerDirectionChoisie(actualActionColonne)){
            // Si
                        lastActionColonne = Direction.bas;
                        actualActionColonne = null;
                        break;
                    }
                    ret = true;
                } else if (!lstColonnes.contains(edir)){
            // si c'est une colonne
                    lastActionColonne = actualActionColonne;
                    actualActionColonne = null;
                    break;
                }
                cmp ++;
                if (cmp == colDirection.size()) {
                    cmp = 0;
                }
            }
        }

        return ret;
    }

    public void setAction() {
        if (lastActionColonne == Direction.bas){
            actualActionColonne = Direction.haut;
            lastActionColonne = null;
        } else if (lastActionColonne == Direction.haut){
            actualActionColonne = Direction.bas;
            lastActionColonne = null;
        }
    }

    public static Colonne reset(){
        col = new Colonne();
        return col;
    }

}
