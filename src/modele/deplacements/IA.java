package modele.deplacements;

import modele.plateau.Bot;
import modele.plateau.Entite;

import static java.lang.Thread.sleep;

public class IA extends RealisateurDeDeplacement {
    private static IA ia;

    public static IA getInstance() {
        if (ia == null) {
            ia = new IA();
        }
        return ia;
    }

    protected boolean realiserDeplacement() {
        boolean ret = false;
        int nombreAleatoire = (int)(Math.random() * (8 + 1));
        for (Bot e : lstBot) {
            Direction directionCourante = e.getDirectionCourante();
            if (directionCourante != null ) {
                if(e.seraSur(directionCourante) == null
                        || (e.regarderDansLaDirection(directionCourante) != null
                        && !e.regarderDansLaDirection(directionCourante).peutCombatre())
                        || nombreAleatoire == 6){
                    switch (directionCourante) {
                        case gauche:
                            directionCourante = Direction.droite;
                            e.setDirectionCourante(Direction.droite);
                            break;
                        case droite:
                            directionCourante = Direction.gauche;
                            e.setDirectionCourante(Direction.gauche);
                            break;
                    }
            }   switch (directionCourante) {
                    case gauche:
                        if (e.avancerDirectionChoisie(directionCourante)) {
                            e.setEtat(Etat.regardGauche);
                        }
                        break;
                    case droite:
                        if (e.avancerDirectionChoisie(directionCourante)) {
                            e.setEtat(Etat.regardDroit);
                        }
                        break;
                }
                ret = true;

            }
        }
        return ret;
    }

    public static IA reset(){
        ia = new IA();
        return ia;
    }

}
