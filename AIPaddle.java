import java.awt.Color;
import java.awt.Graphics;

public class AIPaddle implements Paddle 
{

	double y, yVel;
	boolean upAccel, downAccel;
	final double GRAVITY = 0.94;
	int player, x;
	Ball b1;
	
	public AIPaddle(int player, Ball b)
	{
		upAccel = false; downAccel = false;
		b1 = b;
		y = 210; yVel = 0;
		
		if(player == 1)
			x = 20;	
		else
			x = 660;
		
	}
	
	
	public void draw(Graphics g) 
	{
		g.setColor(Color.white);
		g.fillRect(x, (int)y, 20, 80);		
	}

	public void move() 
	{
		

		y = b1.getY() - 40; //keeps paddle centered (cuz its 80 tall)
		
		if(y < 0)
			y = 0;
		if( y > (500 - 80))
			y = (500 - 80);
		
	}
	
	
	
	public int getY() 
	{
		return (int)y;
	}

}
