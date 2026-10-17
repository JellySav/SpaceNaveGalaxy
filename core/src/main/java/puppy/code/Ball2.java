package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public class Ball2 extends AsteroideEnemigo {
    public Ball2(int x, int y, int size, int xSpeed, int ySpeed, Texture texture) {
        super(x, y, size * 2f, xSpeed * 60f, ySpeed * 60f, texture);
    }

    public int getXSpeed() {
        return Math.round(getVelocidadX());
    }

    public void setXSpeed(int speed) {
        setVelocidad(speed, getVelocidadY());
    }

    public int getySpeed() {
        return Math.round(getVelocidadY());
    }

    public void setySpeed(int speed) {
        setVelocidad(getVelocidadX(), speed);
    }

    public void checkCollision(Ball2 other) {
        if (estaActivo() && other.estaActivo() && getArea().overlaps(other.getArea())) {
            float speedX = getVelocidadX();
            float speedY = getVelocidadY();
            setVelocidad(-speedX, -speedY);
            other.setVelocidad(-other.getVelocidadX(), -other.getVelocidadY());
        }
    }
}