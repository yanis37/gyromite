package VueControleur;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import javax.sound.sampled.*;
import javax.swing.*;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import modele.deplacements.Controle4Directions;
import modele.deplacements.Direction;
import modele.deplacements.Colonne;
import modele.deplacements.Etat;
import modele.plateau.*;

import static java.lang.Thread.sleep;

/**
 * Cette classe a deux fonctions :
 * (1) Vue : proposer une représentation graphique de l'application (cases
 * graphiques, etc.)
 * (2) Controleur : écouter les évènements clavier et déclencher le traitement
 * adapté sur le modèle (flèches direction Pacman, etc.))
 *
 */
public class VueControleurGyromite extends JFrame implements Observer {
    private Jeu jeu; // référence sur une classe de modèle : permet d'accéder aux données du modèle
                     // pour le rafraichissement, permet de communiquer les actions clavier (ou
                     // souris)
    private int frame;
    private int sizeX; // taille de la grille affichée
    private int sizeY;
    private boolean last = true;
    // icones affichées dans la grille
    private ImageIcon icoHero;
    private ImageIcon icoBot;
    private ImageIcon icoVide;
    private ImageIcon icoMur;
    private ImageIcon icoBombe;
    private ImageIcon icoColonne;
    private ImageIcon icoCorde;
    private JComponent grilleJLabels;
    private JFrame image;


    private int actualScore = 0;
    private int highScore = 0; // mettre la valeur du fichier HighScore.txt
    private int chrono = 999;
    private boolean chronoAvance = true; // Lorsque vrai alors le timer defile

    private JMenuBar menuBar;
    JMenuItem score;
    JMenuItem high;
    JMenuItem time;
    JMenuItem vie;

    private AudioInputStream audioInputStream;
    private Clip clip;

    private boolean isMainThemePlaying = true;
    private boolean isDeathSoundPlaying = false;
    private boolean isGameOverDisplay = false;
    private boolean isVictoryDisplay = false;

    private ImageIcon icoGameOver;
    private ImageIcon icoVictory;




    private JLabel[][] tabJLabel; // cases graphique (au moment du rafraichissement, chaque case va être associée
                                  // à une icône, suivant ce qui est présent dans le modèle)

    private int highscoreFromFile;

    public VueControleurGyromite(Jeu _jeu) {
        sizeX = jeu.SIZE_X;
        sizeY = _jeu.SIZE_Y;
        jeu = _jeu;

        chargerLesIcones();
        

        playSound("Musics/gyromite_main_theme.wav", true);
        placerLesComposantsGraphiques();
        ajouterEcouteurClavier();

    }

    private void ajouterEcouteurClavier() {
        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un
                                          // objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        Controle4Directions.getInstance().setDirectionCourante(Direction.gauche);
                        break;
                    case KeyEvent.VK_RIGHT:
                        Controle4Directions.getInstance().setDirectionCourante(Direction.droite);
                        break;
                    case KeyEvent.VK_DOWN:
                        Controle4Directions.getInstance().setDirectionCourante(Direction.bas);
                        break;
                    case KeyEvent.VK_UP:
                        Controle4Directions.getInstance().setDirectionCourante(Direction.haut);
                        break;
                    case KeyEvent.VK_SHIFT:
                        Colonne.getInstance().setAction();
                        break;
                    case KeyEvent.VK_ENTER :
                        if (jeu.getNbrVie() == 0 || chrono == 0){
                            reload();
                        } else if ( jeu.getEtatJeu() == Etat.victoire){
                            System.out.println("Enter victory");
                            reload();
                        }
                        break;
                }
            }
        });
    }

    private void chargerLesIcones() {
        icoHero = chargerIcone("Images/player_ca_reverse.png", 158, 0, 34, 40);
        icoBot = chargerIcone("Images/smick_ca.png", 0, 0, 30, 30);
        icoVide = chargerIcone("Images/Mur.png", 0, 0, 16, 16);
        icoColonne = chargerIcone("Images/Colonne.png", 0, 0, 18, 19);
        icoMur = chargerIcone("Images/tileset.png", 2 * (80 / 5), 0 * (192 / 12), 16, 16);
        icoBombe = chargerIcone("Images/bomb_ca.png", 20, 15, 25, 32);
        icoCorde = chargerIcone("Images/tileset.png", 1 * (80 / 5) + 2, 0 * (192 / 12), 14, 16);

        icoVictory = chargerIcone("Images/victory.jpg");
        icoGameOver = chargerIcone("Images/game_over.jpg");

    }

    private void placerLesComposantsGraphiques() {

        setTitle("Gyromite");

        // MenuBar definition
        menuBar = new JMenuBar();
        score = new JMenuItem("Score : " + actualScore);
        high = new JMenuItem("High score : " + highScore);
        time = new JMenuItem("Time : " + chrono);
        vie = new JMenuItem("Nb vie(s) : " + jeu.getNbrVie());

        // MenuBar font
        Font font = new Font("SansSerif", Font.BOLD, 18);
        score.setFont(font);
        high.setFont(font);
        time.setFont(font);
        vie.setFont(font);

        // MenuBar color
        score.setBackground(Color.black);
        high.setBackground(Color.black);
        time.setBackground(Color.black);
        vie.setBackground(Color.black);

        score.setForeground(Color.YELLOW);
        high.setForeground(Color.YELLOW);
        time.setForeground(Color.YELLOW);
        vie.setForeground(Color.YELLOW);

        menuBar.add(score);
        menuBar.add(high);
        menuBar.add(time);
        menuBar.add(vie);

        setJMenuBar(menuBar);

        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

         grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases
                                                                             // graphiques et les positionner sous la
                                                                             // forme d'une grille

        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();

                tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique
                                        // à celles-ci (voir mettreAJourAffichage() )
                grilleJLabels.add(jlab);
            }
        }
        add(grilleJLabels);
    }
    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté
     * de la vue (tabJLabel)
     * @throws FileNotFoundException
     */
    private void mettreAJourAffichage() throws FileNotFoundException {
        if (jeu.getEtatJeu() == Etat.victoire){
            System.out.println("victory");
        }
        updateVie();
        updateHighscore();
        if (jeu.getNbrVie() == 0 || chrono == 0) {
            gameOver();
        } else if (jeu.getEtatJeu() == Etat.fini){
            actualScore = actualScore + 100;
            updateScore(actualScore);
            newLevel();
        }else {
            if (jeu.getEtatJeu() == Etat.ramasse){
                actualScore = actualScore + 100;
                System.out.println("actualScore : "+ actualScore );
                updateScore(actualScore);
                jeu.setEtatJeu(null);
            } else if (jeu.getEtatJeu() == Etat.victoire && last){
                actualScore = (actualScore + 100 + chrono) * jeu.getNbrVie();
            
                try{
                    BufferedReader reader = new BufferedReader(new FileReader("score/highscore.txt"));
                    String firstLine = reader.readLine();
                    highscoreFromFile = Integer.parseInt(firstLine);
                } catch(Exception e) {
                    e.printStackTrace();
                }

                if(actualScore > highscoreFromFile) {
                    System.out.println("reader :" + actualScore);

                    try {
                        FileWriter writer = new FileWriter("score/highscore.txt");
                        writer.write("" + actualScore);
                        writer.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }



                System.out.println("actualScore : "+ actualScore );
                updateScore(actualScore);
                last = false;
            } else if(jeu.getEtatJeu() == Etat.victoire){
                victory();
            }
            frame++;
            if (frame == 7) {
                updateTime();
                frame = 0;
            }

            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    if (jeu.getGrille()[x][y] instanceof Heros) {
                        changerIcoJoueur((Heros) jeu.getGrille()[x][y]);
                        tabJLabel[x][y].setIcon(icoHero);

                        // si transparence : images avec canal alpha + dessins manuels (voir ci-dessous
                        // + créer composant qui redéfinie paint(Graphics g)), se documenter
                        // BufferedImage bi = getImage("Images/smick.png", 0, 0, 20, 20);
                        // tabJLabel[x][y].getGraphics().drawImage(bi, 0, 0, null);

                    } else if (jeu.getGrille()[x][y] instanceof Bot) {
                        changerIcoBot((Bot)jeu.getGrille()[x][y]);
                        tabJLabel[x][y].setIcon(icoBot);

                    } else if (jeu.getGrille()[x][y] instanceof Mur) {
                        tabJLabel[x][y].setIcon(icoMur);
                    } else if (jeu.getGrille()[x][y] instanceof modele.plateau.Colonne) {
                        tabJLabel[x][y].setIcon(icoColonne);
                    } else if (jeu.getGrille()[x][y] instanceof Corde) {
                        tabJLabel[x][y].setIcon(icoCorde);
                    } else if (jeu.getGrille()[x][y] instanceof Bombe) {
                        tabJLabel[x][y].setIcon(icoBombe);
                    } else {
                        tabJLabel[x][y].setIcon(icoVide);
                    }
                }
            }
        }

    }

    private void updateTime() {
        if (chronoAvance == true) {
            chrono--;
            time.setText("Time : " + chrono);
        }
    }

    private void updateScore (int newScore){
        score.setText("Score : "+ newScore);
    }


    private void updateVie() {
        vie.setText("Nb vie(s) : " + jeu.getNbrVie());
    }

    private void updateHighscore() {
        try{
            BufferedReader reader = new BufferedReader(new FileReader("score/highscore.txt"));
            String firstLine = reader.readLine();
            highscoreFromFile = Integer.parseInt(firstLine);
        } catch(Exception e) {
            e.printStackTrace();
        }   
        high.setText("High score : " + highscoreFromFile);
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            mettreAJourAffichage();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // chargement de l'image entière comme icone
    private ImageIcon chargerIcone(String urlIcone) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(urlIcone));
        } catch (IOException ex) {
            Logger.getLogger(VueControleurGyromite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return new ImageIcon(image);
    }

    // chargement d'une sous partie de l'image
    private ImageIcon chargerIcone(String urlIcone, int x, int y, int w, int h) {
        // charger une sous partie de l'image à partir de ses coordonnées dans urlIcone
        BufferedImage bi = getSubImage(urlIcone, x, y, w, h);
        // adapter la taille de l'image a la taille du composant (ici : 20x20)
        return new ImageIcon(bi.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH));
    }

    private BufferedImage getSubImage(String urlIcone, int x, int y, int w, int h) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(urlIcone));
        } catch (IOException ex) {
            Logger.getLogger(VueControleurGyromite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        BufferedImage bi = image.getSubimage(x, y, w, h);
        return bi;
    }

    public void changerIcoBot(Bot e) {
        if (e.getEtat() != null) {
            switch (e.getEtat()) {
                case regardGauche:
                    icoBot = chargerIcone("Images/smick_ca.png", 0, 0, 30, 30);
                    break;
                case mort:
                case regardDroit:
                    icoBot = chargerIcone("Images/smick_ca_reverse.png", 3 * (128 / 4), 0, 30, 30);
                    break;
            }
        }

    }

    public void changerIcoJoueur(Heros e) {
        if (e.getEtat() != null) {
            switch (e.getEtat()) {
                case regardGauche:
                    icoHero = chargerIcone("Images/player_ca.png", 0, 0, 35, 40);
                    break;
                case regardDroit:
                    icoHero = chargerIcone("Images/player_ca_reverse.png", 158, 0, 34, 40);
                    break;
                case ecrase:
                    icoHero = chargerIcone("Images/player_ca_reverse.png", 2 * (192 / 6), 2 * (220 / 5), 33, 40);
                    break;
                case corde:
                    icoHero = chargerIcone("Images/player_ca.png", 0 * (192 / 6), 2 * (220 / 5), 34, 40);
                    break;
                case cordeMonte1:
                    icoHero = chargerIcone("Images/player_ca.png", 0 * (192 / 6), 2 * (220 / 5), 34, 40);
                    break;
                case cordeMonte2:
                    icoHero = chargerIcone("Images/player_ca.png", 1 * (192 / 6) - 1, 2 * (220 / 5), 34, 40);
                    break;
                case mort:
                    icoHero = chargerIcone("Images/player_ca.png", 0 * (192 / 6), 3 * (220 / 5), 34, 40);
                    break;
                case victoire:
                    icoHero = chargerIcone("Images/player_ca.png", 5 * (192 / 6) - 1, 0 * (220 / 5), 32, 40);
                    break;
            }
        }
    }

    public void setChronoAvance(boolean chronoAvance) {
        this.chronoAvance = chronoAvance;
    }

    private void gameOver() {
        
        setChronoAvance(false);
        if (isMainThemePlaying == true) {
            stopMusic();
            isMainThemePlaying = false;
            System.out.println("isMainThemePlaying = " + isMainThemePlaying);
        }
        if (isDeathSoundPlaying == false) {
            playSound("Musics/gyromite_game_over.wav", false);
            isDeathSoundPlaying = true;
            System.out.println("isDeathSoundPlaying = " + isDeathSoundPlaying);
        }
        
        if (isGameOverDisplay == false) {
            getContentPane().removeAll();
            getContentPane().setBackground(Color.black);

            JLabel gameOver = new JLabel(icoGameOver);
            add(gameOver);
            setSize(1200,720);
            repaint();
            setVisible(true);

            isGameOverDisplay = true;
            System.out.println("isGameOverDisplay = " + isGameOverDisplay);
        }
        

        // System.out.println("GO");
    }

   



    private void victory() {
        setChronoAvance(false);
        if (isMainThemePlaying == true) {
            stopMusic();
            isMainThemePlaying = false;
        }
        if (isDeathSoundPlaying == false) {
            playSound("Musics/gyromite_victory_theme.wav", false);
            isDeathSoundPlaying = true;
        }

        if (isVictoryDisplay == false) {
            getContentPane().removeAll();
            getContentPane().setBackground(Color.black);

            JLabel victoryLabel = new JLabel(icoVictory);
            add(victoryLabel);
            setSize(1200,720);
            repaint();
            setVisible(true);
            
            isVictoryDisplay = true;
            System.out.println("isVictoryDisplay = " + isVictoryDisplay);
        }


    }

    private void playSound(String path, boolean loop) {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            if (loop == true) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    private void stopMusic() {
        clip.stop();
    }

    private void reload(){
        stopMusic();
        chargerLesIcones();
        placerLesComposantsGraphiques();
        playSound("Musics/gyromite_main_theme.wav", true);
        setChronoAvance(true);
        chrono = 999;
        isMainThemePlaying = true;
        isDeathSoundPlaying = false;
        isGameOverDisplay = false;
        isVictoryDisplay = false;
        jeu.setActNiveau("1");
        jeu.restart(3);
        updateScore(0);  
        actualScore = 0;
        System.out.println("restart vu");
    }

    private void newLevel(){

        int nv = Integer.parseInt(jeu.getActNiveau())+1;
        if (jeu.getNiveauMax() >= nv){
            System.out.println("test");
            jeu.setActNiveau(String.valueOf(nv));
            jeu.restart(jeu.getNbrVie());
        } else {
            System.out.println("else");
            victory();
        }
        System.out.println("restart vu");
    }



}
