import java.awt.*;
import java.util.*;

public class Asteroid{
    //Your main enemy in this game. Asteroids range in various shapes and sizes and may split when they are hit. 
    //As the game progresses, asteroids may become smaller and travel faster, but larger asteroids also have the potential to split into multiple smaller ones.
    protected Polygon aster;		//Asteroid hitbox. Can be used to draw the asteroid
    protected int centerX;
    protected int centerY;
    protected double velocity;
    protected double direction;     // The direction the asteroid is travelling represented by an angle in degrees. (With 0 degrees being horizontal to the right)
    protected int IFrames;			//Prevents asteroids from being destroyed the moment they are created
    protected int weight;			//A number that represents how potent the asteroid is. Larger number are more dangerous. This number is used to calculate if more asteroids need to be added to the game isn't too easy
    Random rnd = new Random();

    public Asteroid(int rad)		//Gives a radius that will determine approximate size of asteroid
    {
        centerX = 0;						//Set up at 0,0
        centerY = 0;
        int nsides = rnd.nextInt(6)+10;		//An asteroid can have between 10 and 15 sides
        int xpoints[] = new int [nsides];	//Store coordinates of asteroid
        int ypoints [] = new int [nsides];
        direction = Math.random()*360;
        velocity = Math.random()+1.2;
        IFrames = 50;						//50 frames (~1.3 seconds) need to pass before an asteroid can be destroyed

        //Generates vertices of asteroid
        double currentAngle = 0;
        for (int i=0;i<nsides;i++)
        {
            currentAngle += 360.0/(double) nsides;											//Split up a circle so each vertex is in its own region. ex) if an asteroid had 4 verticies, vertex 1 is between 0° and 90°, vertex 2 is between 90° and 180° etc
            int lengthX = rnd.nextInt(rad/2)+rad/2, lengthY = rnd.nextInt(rad/2)+rad/2;
            this.weight += Math.hypot(lengthX,lengthY);
            int x = (int) (centerX+lengthX*Math.cos(Math.toRadians(currentAngle)));
            int y = (int) (centerY+lengthY*Math.sin(Math.toRadians(currentAngle)));
            xpoints[i]=x;
            ypoints[i]=y;

        }
        weight/=nsides;
        aster = new Polygon(xpoints,ypoints,nsides);										//Create the polygon
    }

    public void changePosition(int changeX, int changeY)
    {
        //Changes the asteroid position and hitbox by some amount
        aster.translate((changeX),(changeY));
        centerX += changeX;
        centerY += changeY;
    }
    public void updateCooldowns(){
        if(this.IFrames > 0){
            this.IFrames--;
        }
    }

    public void move()
    {
        //Moves asteroid based on velocity and direction. Same logic as player and bullet
        int oldx = this.centerX, oldy = this.centerY;
        centerX += (this.velocity*Math.cos(Math.toRadians(this.direction))+1080);
        centerY += (this.velocity*Math.sin(Math.toRadians(this.direction))+720);

        centerX%=1080;
        centerY%=720;

        this.aster.translate(centerX-oldx,centerY-oldy);
    }

    public Asteroid split()
    	//Called when an bullet hits an asteroid. Will split the asteroid in to multiple smaller asteroids
    {
        int newWeight = rnd.nextInt(this.weight/2)+weight/4;	// The new asteroids will be at least one quarter of the size of the original asteroid
        
        Asteroid a = new Asteroid(newWeight);
        a.changePosition(this.centerX,this.centerY);
        a.direction = Math.random()*360;
        a.velocity = this.velocity+0.4; 						// The new asteroid have a slightly faster velocity than the original asetroid
        return a;
    }
}
