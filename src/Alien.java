import java.awt.*;
import java.util.*;

public class Alien{
	//An enemy the player must face. They regularly shoot the player and become more difficult as the game progresses. 
	//Aliens have the ability to track onto the player when the player is too close and shoot at regular intervals which decrease as the game progresses
    protected int centerX;
    protected int centerY;
    protected double direction;
    protected double velocity;
    protected int shotCooldown;	//Prevents them from shooting every frame
    protected Polygon poly;		//Hitbox

    public Alien(int x, int y){
    	Random rnd = new Random();
    	this.centerX= x;
    	this.centerY = y;
    	this.direction = 180 * rnd.nextInt(2);			//The alien only travels left ot right by default
    	this.velocity = 2;
    	this.shotCooldown = 10;

    	int[] xPoints = {-2,10,12,18,20,32,20,10};		//Sets up the polygon and translates it to a given position
    	int[] yPoints = {0,-5,-10,-10,-5,0,7,7};
    	this.poly = new Polygon(xPoints, yPoints, 8);
    	this.poly.translate(x,y);
    }
    public void updateCooldowns(){
    	//The shot cooldown decreases to a minimum of 0, where the alien is free to shoot
    	this.shotCooldown = Math.max(this.shotCooldown-1, 0);
    }
    public void move(){
    	//Moves the alien
    	int oldX = this.centerX;
    	int oldY = this.centerY;

    	this.centerX += (this.velocity*Math.cos(Math.toRadians(this.direction))+1080);	//Updates alien's position based on direction and scales it up based on velocity
        this.centerY += (this.velocity*Math.sin(Math.toRadians(this.direction))+720);

        centerX %= 1080;																//The alien may clip to the other side of the screen when is leaves on one side
        centerY %= 720;

        this.poly.translate(centerX - oldX, centerY - oldY);							//Translates the alien once movements have been calculated
    }
    
    public void track(Player plr, int time){
    	//Changes the alien's direction is they are too close to the player
    	double dist = Math.hypot((double)(this.centerX - plr.playerX), (double)(this.centerY - plr.playerY));								//Gets the distance between the player and alien
    	if(dist < 10+time/20 && AsteroidGame.page == "game"){																				//The homing range increases as time progresses and only happens when in game
    		this.direction = 180 + Math.toDegrees(Math.atan2((double)(this.centerY - plr.playerY), (double)(this.centerX - plr.playerX)));	//The alien homes towards the player if it is close enough
    	}
    }
    
    public void shoot(Player plr, ArrayList<Bullet> alienBullets, int time){
    	if(this.shotCooldown == 0 && AsteroidGame.page == "game"){
    		AsteroidGame.game.playSound(1);
    		double angle = Math.toDegrees(Math.atan2((double)(this.centerY - plr.playerY), (double)(this.centerX - plr.playerX)));	//Fires towards the player
    		double spread = -9*time/2500 + 90; 																						//Alien is more accurate the longer you play the game
    		double velocity = time/3125 + 2; 																						//Bullet is faster the longer you play the game
    		
    		alienBullets.add(new Bullet(this.centerX, this.centerY, angle + Math.random()*2*spread - spread/2, velocity, 150));
    		this.shotCooldown = Math.max(15,(int)(-9*time/500 + 500));																//Aliens shoot faster the longer you play the game
    	}
    }
}