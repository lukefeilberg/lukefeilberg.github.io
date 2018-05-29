import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Tennis extends Applet implements Runnable, KeyListener
{
	
	final int WIDTH = 700, HEIGHT = 500;
	
	Thread thread;
	
	HumanPaddle p1;
	HumanPaddle p2;
	AIPaddle p2a;
	
	Ball b1;
	
	boolean gameStarted;
	boolean gameOver;
	boolean newGame;
	boolean twoPlayer;
	int bounces; //number of bounces
	int scoreLeft;
	int scoreRight;
	
	Graphics gfx;
	Image img;
	
	public void init() 
	{		
		this.resize(WIDTH,HEIGHT);
		gameStarted = false;
		gameOver = false;
		newGame = false;
		bounces = 0;
		scoreLeft = 0;
		scoreRight = 0;
		
		this.addKeyListener(this);

		b1 = new Ball();
		p1 = new HumanPaddle(1);
		p2 = new HumanPaddle(2);
		p2a = new AIPaddle(2, b1);	
		
		img = createImage(WIDTH,HEIGHT);
		gfx = img.getGraphics();
		
		thread = new Thread(this);
		thread.start();
	}
	
	public void paint(Graphics g)
	{
		gfx.setColor(Color.black);
		gfx.fillRect(0, 0, WIDTH, HEIGHT);
		
		if(twoPlayer)
		{
			gfx.setColor(Color.white);
			gfx.drawString(Integer.toString(scoreLeft), 10, 30);
			gfx.setColor(Color.white);
			gfx.drawString(Integer.toString(scoreRight), WIDTH - 12, 30);
		}
		
		if(b1.getX() < -10 || b1.getX() > WIDTH + 10)
		{
			
			if(b1.getX() < -10 && gameOver==false)
				scoreRight++;
			else if(b1.getX() > WIDTH + 10 && gameOver==false)
				scoreLeft++;
			
			gameOver = true;
			gfx.setColor(Color.red);
			gfx.drawString("Game Over", WIDTH/2 - 30, HEIGHT/2);
			gfx.setColor(Color.white);
			gfx.drawString("Bounces: " + bounces, WIDTH/2 - 34, 30);
		}
		else
		{
			gfx.setColor(Color.white);
			if (gameStarted)
				gfx.drawString("Bounces: " + bounces, WIDTH/2 - 34, 30);
			p1.draw(gfx);
			b1.draw(gfx); 
			if(twoPlayer)
				p2.draw(gfx);
			else
				p2a.draw(gfx);
		}
		
		if(!gameStarted) 
		{
			gfx.setColor(Color.green);
			gfx.drawString("YO RIP MY NEW PONG", 286, 100);
			gfx.setColor(Color.white);
			gfx.drawString("press 1 for one play, 2 for two player", 252, 130);
		}
		g.drawImage(img, 0, 0, this);
	}
	
	public void update(Graphics g)
	{
		paint(g);
	}
	
	@Override
	public void run() //
	{
		boolean output;
		
		for(;;)
		{
			output = false;
			if( (gameStarted && !gameOver) || (newGame && !gameOver) )
			{
				p1.move();
				if(twoPlayer)
					p2.move();
				else
					p2a.move();
				b1.move();
				if(twoPlayer) 
				{
					output = b1.checkPaddleCollision(p2, p1);
					if (output)
						bounces++;
				}
				else
				{
					output = b1.checkPaddleCollision(p1, p2a);
					if (output)
						bounces++;
				}
			}		
			
			repaint();
			
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		
		if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			p1.setUpAccel(true);
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			p1.setDownAccel(true);
		}
		else if(e.getKeyCode() == KeyEvent.VK_W)
		{
			p2.setUpAccel(true);
		}
		else if(e.getKeyCode() == KeyEvent.VK_S)
		{
			p2.setDownAccel(true);
		}
		else if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{	
			if(gameStarted == false)
			{
				gameStarted = true;
				twoPlayer = false;
			}
			else if(gameOver == true)
			{
				newGame = true;
				gameOver = false;
				
				bounces = 0;
				b1 = new Ball();
				if(twoPlayer)
				{
					p1 = new HumanPaddle(2);
					p2 = new HumanPaddle(1);
				}
				else
				{
					p1 = new HumanPaddle(1);
					p2a = new AIPaddle(2, b1);	
				}
				
			}
		}	
		else if(e.getKeyCode() == KeyEvent.VK_1)
		{
			if(gameStarted == false)
			{
				gameStarted = true;
				twoPlayer = false;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_2)
		{
			gameStarted = true;
			twoPlayer = true;
			p1 = new HumanPaddle(2);
			p2 = new HumanPaddle(1);
		}
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{

		if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			p1.setUpAccel(false);
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			p1.setDownAccel(false);
		}
		else if(e.getKeyCode() == KeyEvent.VK_W)
		{
			p2.setUpAccel(false);
		}
		else if(e.getKeyCode() == KeyEvent.VK_S)
		{
			p2.setDownAccel(false);
		}
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) 
	{		
	}

}
