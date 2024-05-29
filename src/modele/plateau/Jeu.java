/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.deplacements.*;
import modele.deplacements.Colonne;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

/**
 * Actuellement, cette classe gère les postions
 * (ajouter conditions de victoire, chargement du plateau, etc.)
 */

public class Jeu {
    public static int SIZE_X = 30;
    public static int SIZE_Y = 16;

    // compteur de déplacements horizontal et vertical (1 max par défaut, à chaque
    // pas de temps)
    private HashMap<Entite, Integer> cmptDeplH = new HashMap<Entite, Integer>();
    private HashMap<Entite, Integer> cmptDeplV = new HashMap<Entite, Integer>();
    private boolean GameOver = false;
    private Heros hector;
    Point spawn;
    private Etat etatJeu = null;
    private int nbrVie = 3;

    // definition bombe
    protected ArrayList<Entite> lstBombes = new ArrayList<Entite>();

    private HashMap<Entite, Point> map = new HashMap<Entite, Point>(); // permet de récupérer la position d'une entité à
                                                                       // partir de sa référence
    private Entite[][] grilleEntites = new Entite[SIZE_X][SIZE_Y]; // permet de récupérer une entité à partir de ses
                                                                   // coordonnées
    // listes colonnes
    protected ArrayList<EntiteDynamique> lstColonnes = new ArrayList<EntiteDynamique>();
    private HashMap<EntiteDynamique, Point> col = new HashMap<EntiteDynamique, Point>();

    // listes colonnes
    protected ArrayList<Corde> lstCordes = new ArrayList<Corde>();
    private HashMap<Corde, Point> cordes = new HashMap<Corde, Point>();

    // listes bots
    protected ArrayList<Bot> lstBot = new ArrayList<Bot>();
    private HashMap<EntiteDynamique, Point> bot = new HashMap<EntiteDynamique, Point>();

    private Ordonnanceur ordonnanceur = new Ordonnanceur(this);

    private boolean wasOnCorde = false;
    private Point cordeWas;

    String actNiveau = "1";
    Integer niveauMax = 2;

    public Jeu() {
        chargementNiveau("1");
    }

    public void resetCmptDepl() {
        cmptDeplH.clear();
        cmptDeplV.clear();
    }

    public void start(long _pause) {
        ordonnanceur.start(_pause);
    }

    public Entite[][] getGrille() {
        return grilleEntites;
    }

    private void addEntite(Entite e, int x, int y) {
        grilleEntites[x][y] = e;
        map.put(e, new Point(x, y));
    }



    /**
     * Permet par exemple a une entité de percevoir sont environnement proche et de
     * définir sa stratégie de déplacement
     */
    public Entite regarderDansLaDirection(Entite e, Direction d) {
        Point positionEntite = map.get(e);
        return objetALaPosition(calculerPointCible(positionEntite, d));
    }

    public Entite seraSur(Entite e, Direction d) {
        return objetALaPosition(calculerPointCible(new Point(map.get(e).x, map.get(e).y + 1), d));
    }

    public Entite vaSur(Entite e, Direction d) {
        int dir = 1;
        if (d == Direction.haut) {
            dir = -1;
        }
        return objetALaPosition(new Point(map.get(e).x, map.get(e).y + dir));
    }

    /**
     * Si le déplacement de l'entité est autorisé (pas de mur ou autre entité), il
     * est réalisé
     * Sinon, rien n'est fait.
     */

    public boolean deplacerEntite(Entite e, Direction d) {
        Point pCourant = map.get(e);
        Point pCible = calculerPointCible(pCourant, d);

        boolean retour = false;

        if (hector.getRamasse() != null) {
            lstBombes.remove(hector.getRamasse());

            if (lstBombes.size() == 0) {
                if (niveauMax == Integer.parseInt(actNiveau)){
                    etatJeu = Etat.victoire;
                    System.out.println("La partie est finie");
                } else {
                    etatJeu = Etat.fini;
                }
            } else {
                System.out.println("Il reste " + lstBombes.size() + " bombe(s) !");
            }
            hector.setRamasse(null);
        }

        //// Gestion action Bot et Hero
        if (ordonnanceur.getResetCorde() && e.peutMonterDescendre()) {
            cordeWas = map.get(e);
            wasOnCorde = true;
            ordonnanceur.setResetCorde(false);
        }

        if (contenuDansGrille(pCible)) {
            if (objetALaPosition(pCible) == null) {
                // If going on nothing
                switch (d) {
                    case bas:
                    case haut:
                        if (cmptDeplV.get(e) == null) {
                            cmptDeplV.put(e, 1);
                            retour = true;
                        }
                        break;
                    case gauche:
                    case droite:
                        if (cmptDeplH.get(e) == null) {
                            cmptDeplH.put(e, 1);
                            retour = true;

                        }
                        break;
                }

            } else if (e.peutServirDeSupport() && objetALaPosition(pCible).peutEtreEcrase(d)) {
                objetALaPosition(pCible).setEtat(Etat.ecrase);

                if (!objetALaPosition(pCible).isBad()){
                    etatJeu = Etat.mort;
                    return false;
                } else {
                    lstBot.remove(objetALaPosition(pCible));
                    bot.remove(objetALaPosition(pCible));
                    retour = true;
                }


            } else if (e.peutServirDeSupport() && !objetALaPosition(pCible).peutServirDeSupport()) {
                // If on a colonne
                int dir = 1;
                if (d == Direction.haut) {
                    dir = -1;
                }

                deplacerEntite(pCible, new Point(pCible.x, pCible.y + dir), objetALaPosition(pCible));
                retour = true;

            } else if (objetALaPosition(pCible).peutEtreRamasse()) {
                hector.setRamasse(objetALaPosition(pCible));
                etatJeu = Etat.ramasse;
                // If bombe or collectible
                switch (d) {
                    case bas:
                    case haut:
                        if (cmptDeplV.get(e) == null) {
                            cmptDeplV.put(e, 1);
                        }
                        break;
                    case gauche:
                    case droite:
                        if (cmptDeplH.get(e) == null) {
                            cmptDeplH.put(e, 1);
                        }
                        break;
                }
                retour = true;

            } else if (objetALaPosition(pCible).peutPermettreDeMonterDescendre() && e.peutMonterDescendre()) {
                // If Corde
                if (e.getEtat() == Etat.cordeMonte1 || e.getEtat() == Etat.corde) {
                    e.setEtat(Etat.cordeMonte2);
                } else {
                    e.setEtat(Etat.cordeMonte1);
                }
                ordonnanceur.setResetCorde(true);
                e.setOnCorde(true);
                switch (d) {
                    case bas:
                    case haut:
                        if (cmptDeplV.get(e) == null) {
                            cmptDeplV.put(e, 1);
                            retour = true;
                        }
                        break;
                    case gauche:
                    case droite:
                        if (cmptDeplH.get(e) == null) {
                            cmptDeplH.put(e, 1);
                            retour = true;

                        }
                        break;
                }

            } else if (objetALaPosition(pCible).peutCombatre() && e.peutCombatre()) {
                if(!objetALaPosition(pCible).isBad()){
                    objetALaPosition(pCible).setEtat(Etat.mort);
                    etatJeu = Etat.mort;

                } else if (!e.isBad()){
                    e.setEtat(Etat.mort);
                    etatJeu = Etat.mort;

                } else {
                    retour = true;
                }
            }
        }

        if (retour) {
            deplacerEntite(pCourant, pCible, e);
        }

        if (e.peutMonterDescendre() && wasOnCorde && retour) {
            Corde corde = new Corde(this);
            addEntite(corde, cordeWas.x, cordeWas.y);
            wasOnCorde = false;
        }

        return retour;
    }

    private Point calculerPointCible(Point pCourant, Direction d) {
        Point pCible = null;
        if (pCourant != null)
        switch (d) {
            case haut:
                pCible = new Point(pCourant.x, pCourant.y - 1);
                break;
            case bas:
                pCible = new Point(pCourant.x, pCourant.y + 1);
                break;
            case gauche:
                pCible = new Point(pCourant.x - 1, pCourant.y);
                break;
            case droite:
                pCible = new Point(pCourant.x + 1, pCourant.y);
                break;

        }

        return pCible;
    }

    private void deplacerEntite(Point pCourant, Point pCible, Entite e) {
        grilleEntites[pCourant.x][pCourant.y] = null;
        grilleEntites[pCible.x][pCible.y] = e;
        map.put(e, pCible);
    }

    /**
     * Indique si p est contenu dans la grille
     */
    private boolean contenuDansGrille(Point p) {
        return p != null &&p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }

    private Entite objetALaPosition(Point p) {
        Entite retour = null;

        if (contenuDansGrille(p)) {
            retour = grilleEntites[p.x][p.y];
        }

        return retour;
    }

    public Ordonnanceur getOrdonnanceur() {
        return ordonnanceur;
    }


    public Etat getEtatJeu() {
        return etatJeu;
    }

    public void setEtatJeu(Etat etatJeu) {
        this.etatJeu = etatJeu;
    }

    public void Mort() {
        wasOnCorde = false;
        cordeWas = null;
        nbrVie --;
        hector.setEtat(Etat.mort);
        System.out.println("Tu es mort");
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        etatJeu = Etat.ok;
        wasOnCorde = false;
        hector.setEtat(Etat.regardDroit);
        hector.setOnCorde(false);
        grilleEntites[spawn.x][spawn.y] = null;
        deplacerEntite(map.get(hector), spawn, hector);
        for (EntiteDynamique c : lstColonnes) {
            deplacerEntite(map.get(c), col.get(c), c);
        }
        for (Bot c : lstBot) {
            deplacerEntite(map.get(c), bot.get(c), c);
            c.setDirectionCourante(Direction.droite);
        }
        for (Corde c : lstCordes) {
            deplacerEntite(map.get(c), cordes.get(c), c);
        }
    }

    public int getNbrVie() {
        return nbrVie;
    }

    public void chargementNiveau(String niveau) {

        etatJeu = null;
        ArrayList<String[]> objects = new ArrayList<String[]>();
        String l;

        try {
            BufferedReader br = new BufferedReader(new FileReader("Niveaux/niveau" + niveau + ".csv"));
            while((l = br.readLine()) != null) {
                objects.add(l.split(","));
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Erreur fichier");
            e.printStackTrace();
        }

        for (String[] obj : objects) {
            int x = Integer.parseInt(obj[1]);
            int y = Integer.parseInt(obj[2]);


            switch (obj[0]) {
                case "Hector":
                    hector = new Heros(this);
                    addEntite(hector, x, y);
                    spawn = new Point (x,y);
                    Controle4Directions.getInstance().addEntiteJoueur(hector);
                    break;
                case "Bot":
                    Bot bot1 = new Bot(this);
                    addEntite(bot1, x, y);
                    IA.getInstance().addBot(bot1);
                    lstBot.add(bot1);
                    bot.put(bot1, new Point(x, y));
                    break;
                case "Colonne":
                    modele.plateau.Colonne col1 = new modele.plateau.Colonne(this);
                    addEntite(col1, x, y);
                    lstColonnes.add(col1);
                    col.put(col1, new Point(x, y));
                    Colonne.getInstance().addColonnes(col1);
                    break;
                case "Mur":
                    addEntite(new Mur(this), x, y);
                    break;
                case "Bombe":
                    Bombe b = new Bombe(this);
                    addEntite(b, x, y);
                    lstBombes.add(b);
                    break;
                case "Corde":
                    Corde c = new Corde(this);
                    addEntite(c, x, y);
                    lstCordes.add(c);
                    cordes.put(c, new Point(x, y));
                    break;
            }

            // murs extérieurs horizontaux
            for (int m = 0; m < SIZE_X; m++) {
                addEntite(new Mur(this), m, 0);
                addEntite(new Mur(this), m, SIZE_Y - 1);
            }

            // murs extérieurs verticaux
            for (int n = 1; n < SIZE_Y - 1; n++) {
                addEntite(new Mur(this), 0, n);
                addEntite(new Mur(this), SIZE_X - 1, n);
            }

        }

            ordonnanceur.add(Controle4Directions.getInstance());
            ordonnanceur.add(Colonne.getInstance());
            ordonnanceur.add(IA.getInstance());

            Gravite g = new Gravite();
            g.addEntiteDynamique(hector);
            for (EntiteDynamique e : lstBot) {
                g.addEntiteDynamique(e);
            }
            ordonnanceur.add(g);


    }

    public void restart(int vie){
        nbrVie = vie;
        etatJeu = Etat.ok;
        lstColonnes.clear();
        lstBombes.clear();
        lstBot.clear();
        lstCordes.clear();
        for (int x = 0; x<SIZE_X; x++){
            for (int j = 0; j<SIZE_Y; j++){
                grilleEntites[x][j] = null;
            }
        }
        ordonnanceur.clear();
        map.clear();
        Controle4Directions.reset();
        Colonne.reset();
        IA.reset();
        chargementNiveau(actNiveau);
    }

    public String getActNiveau() {
        return actNiveau;
    }

    public void setActNiveau(String actNiveau) {
        this.actNiveau = actNiveau;
    }

    public Integer getNiveauMax() {
        return niveauMax;
    }


}