import java.awt.Color;
import java.awt.Graphics;

public class Ball 
{
	double xVel, yVel, x, y;
	
	public Ball()
	{
		x = 350;
		y = 250;
		xVel = getRando() * getRandomDirection() + 0.15;
		yVel = getRando() * getRandomDirection() - 0.15;
		
		
	}
	
	public double getRando()
	{
		return  (Math.random()*1.5 + 3.2 );
	}
	
	public int getRandomDirection()
	{
		int rand = (int)(Math.random() * 2);
		if(rand == 1)
			return 1;
		else
			return -1;
	}
	
	public void draw (Graphics g)
	{
		g.setColor(Color.white);
		//subtract 10 cuz we want the position based on center of the
		//the circle, not the default top left corner
		g.fillOval((int)x-10, (int)y-10, 20, 20);
	}
	
	public /*void*/boolean checkPaddleCollision(Paddle p1, Paddle p2)
	{
		//when ball is at x=50 its touching paddle
		//rectangle = 20 wide, 20 away from yaxis and radius ball=10
		boolean output = false;
						
		if (x <= 50 && x > 43) 
		{
			if(y >= p1.getY() && y <= p1.getY() + 80)
			{
				xVel = -xVel;
				//System.out.println("COLLISION");
				output = true;
			}
		}
		else if (x >= 650 && x < 657)
		{
			if(y >= p2.getY() && y <= p2.getY() + 80)
			{
				xVel = -xVel;
				//System.out.println("COLLISION");
				output = true;
			}
		}
		
		return output;

	}
	
	
	public void move()
	{
		x += xVel;
		y += yVel;
		//System.out.println(x);		
		
		if(y < 10)
			yVel = -yVel; //10 is top of screen so this is the bounce
		if(y > 490)
			yVel = -yVel;
		
		
	}
	
	public int getX()
	{
		return (int)x;
	}
	
	public int getY()
	{
		return (int)y;
	}
	
}
