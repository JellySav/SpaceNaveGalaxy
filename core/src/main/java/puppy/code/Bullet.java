package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public class Bullet {

	private float xSpeed;
	private float ySpeed;
	private boolean destroyed = false;
	private Sprite spr;
	    
	    public Bullet(float x, float y, float xSpeed, float ySpeed, Texture tx) {
	    	spr = new Sprite(tx);
	    	spr.setPosition(x, y);
	        this.xSpeed = xSpeed;
	        this.ySpeed = ySpeed;
	    }
	    public void update(float delta) {
	        spr.setPosition(spr.getX()+xSpeed * delta, spr.getY()+ySpeed * delta);
	        if (spr.getX() < 0 || spr.getX()+spr.getWidth() > MundoJuego.ANCHO) {
	            destroyed = true;
	        }
	        if (spr.getY() < 0 || spr.getY()+spr.getHeight() > MundoJuego.ALTO) {
	        	destroyed = true;
	        }
	        
	    }
	    
	    public void draw(SpriteBatch batch) {
	    	spr.draw(batch);
	    }

	    public Rectangle getArea() {
	        return spr.getBoundingRectangle();
	    }
	    
	    public void update() {
	        update(1f / 60f);
	    }

	    public boolean checkCollision(ObjetivoDisparo objetivo) {
	        if (!destroyed && objetivo.estaActivo()
	                && spr.getBoundingRectangle().overlaps(objetivo.getArea())) {
	            this.destroyed = true;
	            objetivo.recibirImpacto();
	            return true;
	
	        }
	        return false;
	    }
	    
	    public boolean isDestroyed() {return destroyed;}
	
}
