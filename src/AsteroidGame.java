/*
ASTEROIDS -- By Gary Sun and Leo Chen
A recreation of a classical arcade game.
Enjoy flying through space while shooting at asteroids and aliens. Difficulty scales with how long you've been alive so get good or get destroyed
Features simple background music, crazy sound effects, and an authentic experience you'll never forget!
How long can you survuve?
*/
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import java.util.*;

public class AsteroidGame extends JFrame implements ActionListener, KeyListener{
    public static javax.swing.Timer myTimer;

    protected static GamePanel game;						//Game needs to be accessed from other panels
    protected MenuPanel menu;								//A panel for each screen
    protected PausePanel pause;
    protected DeathPanel death;
    protected ControlPanel controls;

    protected static Font titleFont;						//Different fonts used in the game
    protected static Font pauseFont;
    protected static Font scoreFont;

    public static CardLayout cLayout = new CardLayout();	//A card layout manager to store different game screens
    public static JPanel cards = new JPanel(cLayout);
    public static String page = "menu";						//Some functions are only used when the game is on a certain page

    public AsteroidGame(){
        //Initialization
        super("Asteroids!");
        setSize(1080,720);
        initializeFont();							//Makes us able to display fonts
        myTimer = new javax.swing.Timer(15, this);
        myTimer.addActionListener(this);
        myTimer.start();
        											//Create different screens and add them to the layour manager
        game = new GamePanel();
        menu = new MenuPanel();
        pause = new PausePanel();
        death = new DeathPanel();
        controls = new ControlPanel();
        cards.add(menu, "menu");
        cards.add(game, "game");
        cards.add(pause, "pause");
        cards.add(death, "death");
        cards.add(controls, "control");

        add(cards);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        setResizable(false);
        setVisible(true);
    }
    public void initializeFont()
    {
        try {

            titleFont = Font.createFont(Font.TRUETYPE_FONT, new File("Hyperspace Italic.otf")).deriveFont(150f);
            pauseFont = Font.createFont(Font.TRUETYPE_FONT, new File("Hyperspace Italic.otf")).deriveFont(75f);
            scoreFont = Font.createFont(Font.TRUETYPE_FONT, new File("Hyperspace Italic.otf")).deriveFont(30f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //register the font
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Hyperspace Italic.otf")));
        } catch (IOException e) {

        } catch(FontFormatException e) {

        }
    }

    public void actionPerformed(ActionEvent evt){
        Object source = evt.getSource();
        if(game!= null && source == myTimer && page != "pause"){
            //The game updates whenever it's not paused. This allows for an animated menu screen
            requestFocus();
            game.refresh();
            game.repaint();
        }
        //Paints which ever menu is currently active
        if(source == myTimer && page == "menu" && menu != null){
            menu.repaint();
        }
        if(source == myTimer && page == "pause"){
            pause.repaint();
        }
        if(source == myTimer && page == "death"){
            death.repaint();
        }
        if(source == myTimer && page == "controls"){
            controls.repaint();
        }

    }
    public void keyTyped(KeyEvent e) {} //Not used

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE && page == "game"){
			//Pressing escape while playing the game will pause it
			cLayout.show(cards, "pause");
			page = "pause";
		}
        if(e.getKeyCode() == KeyEvent.VK_SPACE && page == "menu"){
        	//Pressing space in the menu will cause the game to start
        	cLayout.show(cards, "game");
        	page = "game";
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE && page == "death"){
        	//Pressing space in the death screen will bring up the menu
        	cLayout.show(cards, "menu");
        	page = "menu";
        	game.reset();		//Reset the game so that they start fresh the next time they play
        }
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE && page == "controls"){
        	//Pressing escape in the controls screen will bring up the menu
        	cLayout.show(cards, "menu");
        	page = "menu";
        }
        game.setKey(e.getKeyCode(),true);
    }

    public void keyReleased(KeyEvent e) {
        game.setKey(e.getKeyCode(),false);
    }
    public static void main(String[] arguments) {
        //Allows you to play this marvelous game
        AsteroidGame game = new AsteroidGame();
    }
}
///////////////////////////////////////////////////////////////////
class MenuPanel extends JPanel implements ActionListener{
    //Initalize the buttons on this screen
    JButton playBtn;
    JButton controlBtn;
    public MenuPanel(){
        //sets layout and creates all buttons shown on this screen
        setLayout(null);
        playBtn = new JButton(new ImageIcon("Images/play.png"));
        controlBtn = new JButton(new ImageIcon("Images/controls.png"));
        
        playBtn.addActionListener(this);
        playBtn.setSize(300,48);
        playBtn.setLocation(420,350);
        playBtn.setBorderPainted(false);
        add(playBtn);
        
        controlBtn.addActionListener(this);
        controlBtn.setSize(300,48);
        controlBtn.setLocation(420,450);
        controlBtn.setBorderPainted(false);
        add(controlBtn);
		//Adds the panel to the card layout so it can be accessed and displayed
        AsteroidGame.cards.add(this, "menu");
    }
    public void actionPerformed(ActionEvent evt){
        Object source = evt.getSource();
        //Changes screens if the user clicks the buttons
        if(source == playBtn){
            AsteroidGame.cLayout.show(AsteroidGame.cards, "game");
            AsteroidGame.page = "game";
        }
        if(source == controlBtn){
        	AsteroidGame.cLayout.show(AsteroidGame.cards, "control");
        	AsteroidGame.page = "controls";
        }
    }

    @Override
    public void paintComponent(Graphics g){
        //Paints the screen so the user can see
        Graphics2D g2D = (Graphics2D)g;

        g2D.setColor(Color.black);
        g2D.fillRect(0,0,getWidth(),getHeight());
        g2D.setColor(Color.white);
        g2D.setFont(AsteroidGame.titleFont);

        for(Asteroid a : AsteroidGame.game.asteroids){
            //Animated asteroids
            g2D.setColor(Color.black);
            g2D.fillPolygon(a.aster);
            g2D.setColor(Color.white);
            g2D.drawPolygon(a.aster);
        }
        g2D.drawString("ASTEROIDS", 100,200);
    }
}
////////////////////////////////////////////////////////////////////
class PausePanel extends JPanel implements ActionListener{
    //Same format as the menu screen
    JButton resumeBtn;
    JButton goToMenuBtn;
    public PausePanel(){
        setLayout(null);
        resumeBtn = new JButton(new ImageIcon("Images/resume.png"));
        goToMenuBtn = new JButton(new ImageIcon("Images/menu.png"));

        resumeBtn.addActionListener(this);
        resumeBtn.setSize(300,48);
        resumeBtn.setLocation(420,350);
        resumeBtn.setBorderPainted(false);
        add(resumeBtn);

        goToMenuBtn.addActionListener(this);
        goToMenuBtn.setSize(300,48);
        goToMenuBtn.setLocation(420,450);
        goToMenuBtn.setBorderPainted(false);
        add(goToMenuBtn);

        AsteroidGame.cards.add(this, "pause");
    }
    public void actionPerformed(ActionEvent evt){
        Object source = evt.getSource();
        if(source == resumeBtn){
            AsteroidGame.cLayout.show(AsteroidGame.cards, "game");
            AsteroidGame.page = "game";
        }
        if(source == goToMenuBtn){
            AsteroidGame.cLayout.show(AsteroidGame.cards, "menu");
            AsteroidGame.page = "menu";
            AsteroidGame.game.reset();
        }
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2D = (Graphics2D)g;
        g2D.setColor(Color.black);
        g2D.fillRect(0,0,getWidth(),getHeight());
        for(Asteroid a : AsteroidGame.game.asteroids){
            g2D.setColor(Color.black);
            g2D.fillPolygon(a.aster);
            g2D.setColor(Color.white);
            g2D.drawPolygon(a.aster);
        }
        AsteroidGame.game.drawShip(g2D, AsteroidGame.game.plr);
        g2D.setFont(AsteroidGame.pauseFont);
        g2D.setColor(Color.white);
        g2D.drawString("Game Paused", 325,150);
    }
}
////////////////////////////////////////////////////////////////////
class DeathPanel extends JPanel implements ActionListener{
    //Same format as the menu screen
    JButton againBtn;
    JButton goToMenuBtn;
    public DeathPanel(){
        setLayout(null);
        againBtn = new JButton(new ImageIcon("Images/again.png"));
        goToMenuBtn = new JButton(new ImageIcon("Images/menu.png"));

        againBtn.addActionListener(this);
        againBtn.setSize(300,46);
        againBtn.setLocation(420,350);
        againBtn.setBorderPainted(false);
        add(againBtn);

        goToMenuBtn.addActionListener(this);
        goToMenuBtn.setSize(300,48);
        goToMenuBtn.setLocation(420,450);
        goToMenuBtn.setBorderPainted(false);
        add(goToMenuBtn);

        AsteroidGame.cards.add(this, "death");
    }
    public void actionPerformed(ActionEvent evt){
        Object source = evt.getSource();
        if(source == againBtn){
            AsteroidGame.cLayout.show(AsteroidGame.cards, "game");
            AsteroidGame.page = "game";
            AsteroidGame.game.reset();
        }
        if(source == goToMenuBtn){
            AsteroidGame.cLayout.show(AsteroidGame.cards, "menu");
            AsteroidGame.page = "menu";
            AsteroidGame.game.reset();
        }
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2D = (Graphics2D)g;
        g2D.setColor(Color.black);
        g2D.fillRect(0,0,getWidth(),getHeight());
        for(Asteroid a : AsteroidGame.game.asteroids){
            g2D.setColor(Color.black);
            g2D.fillPolygon(a.aster);
            g2D.setColor(Color.white);
            g2D.drawPolygon(a.aster);
        }
        g2D.setColor(Color.white);
        g2D.setFont(AsteroidGame.titleFont);
        g2D.drawString("GAME OVER", 120,200);
    }
}
////////////////////////////////////////////////////////////////////
class ControlPanel extends JPanel implements ActionListener{
    //Same format as the menu screen
    JButton goToMenuBtn;
    Image tut;				//This picture cointains the instructions 
    public ControlPanel(){
        setLayout(null);
        goToMenuBtn = new JButton(new ImageIcon("Images/menu.png"));
        tut = new ImageIcon("Images/knowledge.png").getImage();
        
        goToMenuBtn.addActionListener(this);
        goToMenuBtn.setSize(300,48);
        goToMenuBtn.setLocation(420,600);
        goToMenuBtn.setBorderPainted(false);
        add(goToMenuBtn);
		
        AsteroidGame.cards.add(this, "controls");
    }
    public void actionPerformed(ActionEvent evt){
        Object source = evt.getSource();
        
        if(source == goToMenuBtn){
            AsteroidGame.cLayout.show(AsteroidGame.cards, "menu");
            AsteroidGame.page = "menu";
        }
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2D = (Graphics2D)g;
        g2D.setColor(Color.black);
        g2D.fillRect(0,0,getWidth(),getHeight());
        for(Asteroid a : AsteroidGame.game.asteroids){
            g2D.setColor(Color.black);
            g2D.fillPolygon(a.aster);
            g2D.setColor(Color.white);
            g2D.drawPolygon(a.aster);
        }
        g2D.setColor(Color.white);
        g2D.setFont(AsteroidGame.titleFont);
        g2D.drawString("CONTROLS", 175,150);
        
        g2D.drawImage(tut,150,175,this);
    }
}
////////////////////////////////////////////////////////////////////
class GamePanel extends JPanel{
    Random rnd;
    private boolean[] keys;							//Keeps track of which keys are pushed down
    private Polygon[] lifeSyms = new Polygon[3];	//Stores the polygons that represent the player's health
    private int totalTime = 0;						//How long the player has been alive. Used to scale difficulty
    private int score = 0;							//How many points the player has accumulated
    public static int beatTimer = 0;				//Keeps track of the basic beat pattern in the background

    Player plr;							//The player
    ArrayList<Bullet> bullets;			//Keeps track of all player bullets
    ArrayList<Bullet> alienBullets;		//			""		 alien bullets
    ArrayList<Asteroid> asteroids;		//  		""       asteroids
    ArrayList<Alien> aliens;			//			""		 aliens


    public GamePanel(){
        rnd = new Random ();
        keys = new boolean[KeyEvent.KEY_LAST+1];
        setSize(1080,720);
		
		//Creates an instance of every object we need to run the game
        plr = new Player();
        bullets = new ArrayList<Bullet>();
        alienBullets = new ArrayList<Bullet>();
        asteroids = new ArrayList<Asteroid>();
        aliens = new ArrayList<Alien>();

        //Creates the ships in the top right to show how many lives the player has left
        int[] lifeX = {0,-15,0,15};
        int[] lifeY = {36,65,50,65};
        Polygon lifeDisplay = new Polygon(lifeX, lifeY, 4);
		//Translate the polygons their respective positions
        lifeDisplay.translate(950,50);
        lifeSyms[0] = new Polygon(lifeDisplay.xpoints, lifeDisplay.ypoints, lifeDisplay.npoints);
        lifeDisplay.translate(35,0);
        lifeSyms[1] = new Polygon(lifeDisplay.xpoints, lifeDisplay.ypoints, lifeDisplay.npoints);
        lifeDisplay.translate(35,0);
        lifeSyms[2] = new Polygon(lifeDisplay.xpoints, lifeDisplay.ypoints, lifeDisplay.npoints);

    }
    public void setKey(int k, boolean v) {
        //Changes the state of keys when they are pushed down or released
        keys[k] = v;
    }
    public void refresh(){
        //Updates the game each frame
        if(AsteroidGame.page == "game"){
            totalTime ++;										//Increase total time
            if(keys[KeyEvent.VK_RIGHT] ){						//Rotate/move the player depending on which key they press
                plr.rotate(2);
            }
            if(keys[KeyEvent.VK_LEFT] ){
                plr.rotate(-2);
            }
            if(keys[KeyEvent.VK_UP] ){
                plr.accel(-0.2);
                playSound(5);
            }
            if(keys[KeyEvent.VK_DOWN] ){
                plr.accel(0.2);
                playSound(5);
            }

            if(keys[KeyEvent.VK_Z] && plr.shotCooldown == 0){		//Allows the player to shoot when their cooldown is over
                plr.shoot(bullets);
                playSound(0);										//Plays the shooting sound
            }
            if(!keys[KeyEvent.VK_Z]){								//Resets the shot cooldown when the shoot button is released. This is faster than holding the button down
                plr.shotCooldown = 0;
            }
            if(keys[KeyEvent.VK_X] && plr.hyperSpaceCooldown == 0){	//Allows the player to blink when their cooldown is over
                playSound(6);										//Plays the blink sound
                plr.blink();
            }
        }
        playBeat();												//Plays the background beat
        for(int i=bullets.size()-1; i>=0; i--){
            //Updates each bullet's position
            Bullet b = bullets.get(i);
            b.move();

            int removal = b.collideAsteroid(asteroids);			//Gets the index of the asteroid that the bullet hit (-1 if it didn't hit an asteroid)
            if(b.life < 1 ){									//Removes the bullet is it is passed it's lifespan
                bullets.remove(b);
            }
            else if (removal!=-1){
                //hit asteroid	
                bullets.remove(b);			
                Asteroid big = asteroids.get(removal);			//Remove the bullet that hit the asteroid and get the asteroid hit
                if(big.IFrames == 0){							//If the asteroid has existed long enough to get hit
                    score += (-96/17 * big.weight + 584);		//Increase player's score and play the sound effect
                    playSound(2);
                    for (int k=0;k<rnd.nextInt(2)+2;k++)		//Each asteroid will split into 2-3 smaller asteroids
                    {
                        asteroids.add(big.split());
                    }
                    asteroids.remove(removal);					//Remove the original asteroid
                }
            }
        }
        for(int i=alienBullets.size()-1; i>=0; i--){
            Bullet b = alienBullets.get(i);
            b.move();						//Updates alien bullets
            if(b.life < 1){
                alienBullets.remove(b);		//Removes the bullet if it lasted longer than its lifespan
            }
            else if(plr.IFrames == 0 && polyCollide(plr.poly, new Rectangle(b.bulletX, b.bulletY,4,4))){
                plr.hp--;					//Deals damage to the player and resets player invincibillity frames when hit by a bullet
                plr.IFrames = 200;
                playSound(4);
            }
        }
        for (int i=asteroids.size()-1; i>=0; i--){
            Asteroid a = asteroids.get(i);
            if (a.weight<(-11*totalTime/3000  + 60))
            {
                asteroids.remove(a);				//Removes asteroids if they are too small. As time increased, asteroids can become smaller
            }
            else if (polyCollide(plr.poly, a.aster) && plr.IFrames == 0 && AsteroidGame.page == "game")
            {
                plr.hp--;							//Deals damage to the player and resets player invincibillity frames when hit by an asteroid
                plr.IFrames = 200;
                playSound(4);
            }
            a.move();								//Update asteroid position
        }
        for(int i=aliens.size()-1; i>=0; i--){
            Alien a = aliens.get(i);
            //Updates and runs alien ai
            a.track(plr, totalTime);
            a.move();
            a.shoot(plr, alienBullets, totalTime);
            a.updateCooldowns();

            for(int j=bullets.size()-1; j>=0; j--){
                //If the alien is hit by a bullet it will die
                Bullet b = bullets.get(j);
                if (polyCollide(a.poly, new Rectangle(b.bulletX, b.bulletY, 2, 2))){
                    bullets.remove(j);
                    aliens.remove(a);
                    playSound(3);
                    break;
                }
            }
        }

        for(Asteroid a : asteroids){
            //Makes sure asteroids arn't invincible forever
            a.updateCooldowns();
        }

        if(plr.hp < 1 && AsteroidGame.page == "game"){
            //Shows death screen when the player has no more lives
            AsteroidGame.cLayout.show(AsteroidGame.cards, "death");
            AsteroidGame.page = "death";
        }
		//Updates player position and cooldowns
        plr.move();
        plr.updateCooldowns();		

        //Manages the number of asteroids and aliens
        spawnAlien(aliens);
        generateAsteroid(asteroids, totalTime);
    }
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D)g;

        g2D.setColor(Color.black);																//background
        g2D.fillRect(0,0,getWidth(),getHeight());
        g2D.setColor(Color.white);
        drawShip(g2D, plr);

        for(int i=bullets.size()-1; i>=0;i--){
            Bullet b = bullets.get(i);
            g2D.fillRect(b.bulletX, b.bulletY, 2,2);											//Each bullet is represented by a small rectangle
        }

        for (int i=asteroids.size()-1; i>=0; i--){												//Each asteroid has a white outline and solid black interior to show "layers"
            Asteroid a = asteroids.get(i);
            g2D.setColor(Color.black);
            g2D.fillPolygon(a.aster);
            g2D.setColor(Color.white);
            g2D.drawPolygon(a.aster);
        }
        for(int i=0; i<aliens.size(); i++){
            Alien a = aliens.get(i);
            g2D.setColor(Color.white);
            g2D.drawPolygon(a.poly);
            g2D.drawLine(a.poly.xpoints[0], a.poly.ypoints[0], a.poly.xpoints[5], a.poly.ypoints[5]);	//Draws the 2 lines across each alien ship since they arn't included in the polygon
            g2D.drawLine(a.poly.xpoints[1], a.poly.ypoints[1], a.poly.xpoints[4], a.poly.ypoints[4]);
        }
        for(int i=alienBullets.size()-1; i>=0; i--){
            Bullet b = alienBullets.get(i);
            g2D.setColor(Color.red);
            g2D.fillOval(b.bulletX, b.bulletY, 4,4);											//Alien bullets are larger and red so they are easier to see

        }

        g2D.setColor(Color.white);
        g2D.setFont(AsteroidGame.scoreFont);
        g2D.drawString(score+"", 950 - 10 * (int)Math.log10((double)score), 50);				//The last digit of the player's score doesn't move. As score increased, the number moves to the left to compensate

        for(int i=0; i<Math.min(plr.hp,3); i++){
            g2D.drawPolygon(lifeSyms[i]);														//Displays player lives
        }
    }
    public void drawShip(Graphics2D g2D, Player plr){
        //Allows player rotation
        AffineTransform curForm = g2D.getTransform();									//Same current transform so it can be re-applied
        AffineTransform rot = new AffineTransform();
        rot.setToRotation(Math.toRadians(plr.direction-90), plr.playerX, plr.playerY);	//Create a new transform based on the player's direction
        g2D.transform(rot);																//Apply the transform and draw the player in the transformed space
        g2D.drawPolygon(plr.poly);
        g2D.setTransform(curForm);														//Revert the changes so the player seems rotated
    }

    public int getTotalWeight(ArrayList<Asteroid> asteroids){
        //Returns the sum of the weights of all active asteroids. If the number is too small, the spawnAsteroids function will add more
        int tot = 0;
        for(Asteroid a : asteroids){
            tot += a.weight;
        }
        return tot;
    }

    public void spawnAlien(ArrayList<Alien> aliens){
        if(aliens.size() < 5 && Math.random() > 0.999){
            											//Ideally, the game runs at 67 fps, so an alien has a 0.06% chance of spawning every second ==> 1 alien every ~15 seconds
            int spawnX = 1080 * rnd.nextInt(2);
            int spawnY = rnd.nextInt(700) + 10;			//The aliens can spawn just off of either side of the screen
            aliens.add(new Alien(spawnX, spawnY));
        }
    }
    public void generateAsteroid(ArrayList<Asteroid> asteroids, int time){
        //Generates more asteroids so the game is not empty and boring
        if(getTotalWeight(asteroids) < 240){
            //As time increased, asteroids can beceome smaller, but larger asteroids can also split into multiple small asteroids
            if(time > 3000){
                asteroids.add(new Asteroid(rnd.nextInt(10)+65));
            }
            if(time > 8000){
                asteroids.add(new Asteroid(rnd.nextInt(28)+48));
            }
            if(time > 14000){
                asteroids.add(new Asteroid(rnd.nextInt(49)+27));
            }
            if(time > 20000){
                asteroids.add(new Asteroid(rnd.nextInt(70)+65));
            }
            asteroids.add(new Asteroid(75));
        }
    }
    public void reset(){
        //Resets game variables so the game can be re-played
        plr = new Player();
        totalTime = 0;
        asteroids = new ArrayList<Asteroid>();
        bullets = new ArrayList<Bullet>();
        aliens = new ArrayList<Alien>();
        score = 0;
    }
    public boolean polyCollide(Polygon a, Polygon b){
        //Checks if 2 polygons are colliding by determining the area of their overlap
        Area a1 = new Area(a);
        Area a2 = new Area(b);
        a1.intersect(a2);
        return !a1.isEmpty();
    }
    public boolean polyCollide(Polygon a, Rectangle b){
        //Changes a rectangle to a polygon so bullets can also use the polyCollided method
        int[] pX = {b.x, b.x + b.width, b.x + b.width,  b.x};
        int[] pY = {b.y, b.y,           b.x + b.height, b.y + b.height};
        return polyCollide(a, new Polygon(pX, pY, 4));
    }

    public void playSound(int s){
        //Plays all the game's sounds
        String soundName;
        AudioInputStream ais;
        Clip clip;
        // We have found incompatibility with linux machines running the game.
        // The exception handling is specifically placed for linux users. But it still works in windows
        try{
            if(s == 0){
                //The player shoots a bullet
                soundName = "Sounds/fire.wav";
                ais = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());	//Load the sound into the audio system
                clip = AudioSystem.getClip();													//Get a new clip and open the audio system
                clip.open(ais);
                clip.start();																	//Play the clip
            }
            if(s == 1){
                //The alien shoots
                soundName = "Sounds/alienFire.wav";
                ais = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            }
            if(s == 2){
                //The player hits an asteroid
                soundName = "Sounds/hitAsteroid.wav";
                ais = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            }
            if(s == 3){
                //The player hits an alien ship
                soundName = "Sounds/hitShip.wav";
                ais = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            }
            if(s == 4){
                //The player takes damage
                soundName = "Sounds/hitPlr.wav";
                ais = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            }
            if(s == 5){
                //The player moves forwards or backwards
                soundName = "Sounds/thrust.wav";
                ais = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            }
            if(s == 6){
                //The player blinks
                soundName = "Sounds/blink.wav";
                ais = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            }
            //These two are pitches used for the background beat while you are playing the game
            if(s == 7){
                soundName = "Sounds/beat1.wav";
                ais = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            }
            if(s == 8){
                soundName = "Sounds/beat2.wav";
                ais = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            }
        }
        catch(UnsupportedAudioFileException | LineUnavailableException | IOException | IllegalArgumentException e) {};
    }

    public void playBeat(){
        //Plays the beat in the background while the game runs
        //The beat is at ~115 bpm, while the game runs at ~67fps, so each beat happens every 70 frames
        beatTimer ++;
        if(beatTimer == 70){
            playSound(7);
        }
        if(beatTimer == 140){
            playSound(8);
            beatTimer = 0;
        }
    }
}