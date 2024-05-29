package modele.deplacements;

import modele.plateau.Bot;
import modele.plateau.Colonne;
import modele.plateau.EntiteDynamique;
import modele.plateau.Heros;

import java.util.ArrayList;

/**
Tous les déplacement sont déclenchés par cette classe (gravité, controle clavier, IA, etc.)
 */
public abstract class RealisateurDeDeplacement {
    protected ArrayList<EntiteDynamique> lstEntitesDynamiques = new ArrayList<EntiteDynamique>();
    protected ArrayList<Colonne> lstColonnes = new ArrayList<Colonne>();
    protected ArrayList<Heros> lstEntitesJoueur = new ArrayList<Heros>();
    protected ArrayList<Bot> lstBot = new ArrayList<Bot>();

    protected abstract boolean realiserDeplacement();
    public void addEntiteDynamique(EntiteDynamique ed) {lstEntitesDynamiques.add(ed);};
    public void addEntiteJoueur(Heros ed) {lstEntitesJoueur.add(ed);};
    public void addColonnes(Colonne ed) {lstColonnes.add(ed);};
    public void addBot(Bot bot){lstBot.add(bot);}

}
