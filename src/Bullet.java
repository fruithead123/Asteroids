import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;

public class Bullet{
	//Bullets allow the player to score points by shooting down asteroids, but can also kill the player if hit by an alien
	protected int life;			//represents how many long the bullet can be alive before it is deleted. Distance is irrelevent in this case
	protected int bulletX;
	protected int bulletY;
	protected double direction;
	protected double velocity;
	public Bullet(int x, int y, double dir, double velocity, int life){
		this.life = life;
		this.bulletX = x;
		this.bulletY = y;
		this.direction = dir;
		this.velocity = velocity;
	}

	public void move(){
		this.bulletX -= (this.velocity*Math.cos(Math.toRadians(this.direction)) -1080);	//Updates bullet position based on direction and scaled up by its velocity
		this.bulletY -= (this.velocity*Math.sin(Math.toRadians(this.direction)) -720);
		this.life -= 1;																	//Life decreases as the bullet moves

		this.bulletX %= 1080;															//Allows bullets to reappear on the other side of the screen when they leave
		this.bulletY %= 720;
	}

	public int collideAsteroid (ArrayList<Asteroid> arr)
	{
		//Returns the index of the asteroid that is hit.
		//The checking of the collision of the asteroids is done by checking the area of the intersection
		// of the bullet and the asteroid. If the area is greater not 0, than they collide.
		for (int i = 0;i<arr.size();i++) {
			Asteroid a = arr.get(i);
			Area x = new Area(new Rectangle(bulletX, bulletY, 2, 2));
			Area y = new Area(a.aster);
			x.intersect(y);					//If the area if their intersection is 0, they have not collided
			if (!x.isEmpty()) return i;
		}
		return -1; // Returns -1 if the bullet does not collide with any asteroids in the ArrayList
	}

}
