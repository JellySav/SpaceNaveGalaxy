package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class BateriaEnergia {
    private final Sprite sprite;
    private final float x;
    private final float y;
    private float tiempo;
    private boolean activa = true;

    public BateriaEnergia(float x, float y, Texture textura) {
        this.x = x;
        this.y = y;
        sprite = new Sprite(textura);
        sprite.setSize(18f, 28f);
        sprite.setColor(0.35f, 1f, 0.72f, 1f);
        sprite.setPosition(x, y);
    }

    public void actualizar(float delta) {
        tiempo += delta;
        sprite.setPosition(x, y + (float) Math.sin(tiempo * 4f) * 5f);
    }

    public void dibujar(SpriteBatch batch) {
        if (activa) {
            sprite.draw(batch);
        }
    }

    public boolean toca(Rectangle area) {
        return activa && sprite.getBoundingRectangle().overlaps(area);
    }

    public void recoger() {
        activa = false;
    }
}