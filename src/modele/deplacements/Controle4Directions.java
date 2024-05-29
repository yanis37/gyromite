package modele.deplacements;

import modele.plateau.Entite;
import modele.plateau.Heros;

/**
 * Controle4Directions permet d'appliquer une direction (connexion avec le clavier) à un ensemble d'entités dynamiques
 */
public class Controle4Directions extends RealisateurDeDeplacement {
    private Direction directionCourante;
    // Design pattern singleton
    private static Controle4Directions c3d;

    public static Controle4Directions getInstance() {
        if (c3d == null) {
            c3d = new Controle4Directions();
        }
        return c3d;
    }

    public void setDirectionCourante(Direction _directionCourante) {
        directionCourante = _directionCourante;
    }

    public boolean realiserDeplacement() {
        boolean ret = false;
        for (Heros e : lstEntitesJoueur) {

            e.setOnCorde(!e.isOnCorde());

            if (directionCourante != null) {

                switch (directionCourante) {
                    case bas:
                    case gauche:
                        if (e.avancerDirectionChoisie(directionCourante)) {
                            e.setEtat(Etat.regardGauche);
                            ret = true;
                        }
                        break;
                    case droite:
                        if (e.avancerDirectionChoisie(directionCourante)) {
                            e.setEtat(Etat.regardDroit);
                            ret = true;
                        }
                        break;

                    case haut:
                        Entite eBas = e.regarderDansLaDirection(Direction.bas);
                        Entite eHaut = e.regarderDansLaDirection(Direction.haut);
                        if (eBas != null && (eBas.peutServirDeSupport() || (eHaut != null && eHaut.peutPermettreDeMonterDescendre()))) {
                            if (e.avancerDirectionChoisie(Direction.haut)) {
                                ret = true;

                            }
                        }

                        break;
                }
            }


        }

        return ret;

    }

    public void resetDirection() {
        directionCourante = null;
    }

    public static Controle4Directions reset(){
        c3d = new Controle4Directions();
        return c3d;
    }
}
