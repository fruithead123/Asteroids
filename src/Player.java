import java.awt.*;
import java.awt.geom.Area;
import java.util.*;
class Player{
	//The player is represented with a triangle ish polygon. It can shoot with "Z", change direction with arrow keys and blink to a random spot onscreen with "C". 
	//**If you blink into an asteroid, you loose a life if your invincibillity frames are out
	protected int playerX;			//pos
	protected int playerY;
	protected double velocity;	
	protected double direction;
	protected int hp;
	protected Polygon poly;			//Hitbox
	
	public int IFrames;				//Prevents the player from getting hit right after they lost a life
	public int shotCooldown;		//Prevents the player from shooting every frame
	public int hyperSpaceCooldown;	//blink cooldown

	Random rnd = new Random();

	public Player(){
		this.playerX = 540;						//Middle of the screen
		this.playerY = 360;
		this.velocity = 0;
		this.direction = 90;					//Facing up
		this.hp = 3;	
		int[] coordsX = {540, 530, 540, 550};	//Hotbox coords
		int[] coordsY = {354, 375, 368, 375};
		this.poly = new Polygon(coordsX, coordsY, 4);

		this.IFrames = 30;
		this.shotCooldown = 0;
		this.hyperSpaceCooldown = 0;
	}

	public void move(){
		//Moves the player based on direction and velocity
		int oldX = this.playerX;
		int oldY = this.playerY;

		this.playerX += (int)(this.velocity*Math.cos(Math.toRadians(this.direction))) + 1080;	//Calculates new position
		this.playerY += (int)(this.velocity*Math.sin(Math.toRadians(this.direction))) + 720;
		this.playerX %= 1080;																	//Allows player to clip through edges of screen to the other side
		this.playerY %= 720;
		this.poly.translate(this.playerX-oldX, this.playerY-oldY);								//Updates hitbox
		this.velocity = this.velocity>0 ? this.velocity-0.03 : this.velocity+0.03;				//Player's velocity approaches 0. If the move buttons are released, the player will slide before stopping
	}
	public void accel(double v){
		//Accelerates the player by some amount to a maximum magnitude of 4
		this.velocity += v;
		if(this.velocity>4){
			this.velocity = 4;
		}
		if(this.velocity < -4){
			this.velocity = -4;
		}
	}
	public void shoot(ArrayList<Bullet> bullets){
		//Creates a new bullet in the direction the player is facing and resets the shot cooldown. The player can only shoot every 2.5 frames;
		bullets.add(new Bullet(this.playerX, this.playerY, this.direction, 7, 100));
		this.shotCooldown = 40; 
	}
	public void rotate(double angle){
		//Updates the player's direction by some angle. Ensures the angle is between 0 and 360
		this.direction = (this.direction+angle+360)%360;
	}
	public void blink(){
		//Allows the player to teleport to a random location on the screen. If the user lands in an asteroid, they will take damage if they have no IFrames. Used as a last resort option
		int oldX = this.playerX;
		int oldY = this.playerY;

		this.playerX = rnd.nextInt(1080);							//Gets a random spot on screen
		this.playerY = rnd.nextInt(720);
		this.poly.translate(this.playerX-oldX, this.playerY-oldY);	//Moves the player's hitbox
		this.hyperSpaceCooldown = 500;								//Resets the cooldown. Ability can be used once every ~5 seconds
	}
	public void updateCooldowns(){
		//Updates the player's IFrames, shot cooldown and blink cooldown
		if(this.IFrames > 0){
			this.IFrames -= 1;
		}
		if(this.shotCooldown > 0){
			this.shotCooldown -= 1;
		}
		if(this.hyperSpaceCooldown > 0){
			this.hyperSpaceCooldown -= 1;
		}
	}
}