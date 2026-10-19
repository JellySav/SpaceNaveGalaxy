package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ProyectilHostil {
    private final Sprite sprite;
    private final float velocidadX;
    private final float velocidadY;
    private boolean activo = true;

    public ProyectilHostil(float x, float y, float direccionX, float direccionY,
                           float velocidad, Texture textura) {
        float longitud = (float) Math.sqrt(direccionX * direccionX + direccionY * direccionY);
        if (longitud == 0f || velocidad <= 0f || textura == null) {
            throw new IllegalArgumentException("Proyectil hostil invalido");
        }
        velocidadX = direccionX / longitud * velocidad;
        velocidadY = direccionY / longitud * velocidad;
        sprite = new Sprite(textura);
        sprite.setSize(16f, 24f);
        sprite.setColor(1f, 0.3f, 0.24f, 1f);
        sprite.setPosition(x, y);
    }

    public void actualizar(float delta, float anchoMundo, float altoMundo) {
        sprite.setPosition(sprite.getX() + velocidadX * delta,
                sprite.getY() + velocidadY * delta);
        if (sprite.getX() + sprite.getWidth() < 0f || sprite.getX() > anchoMundo
                || sprite.getY() + sprite.getHeight() < 0f || sprite.getY() > altoMundo) {
            activo = false;
        }
    }

    public void dibujar(SpriteBatch batch) {
        if (activo) {
            sprite.draw(batch);
        }
    }

    public boolean toca(Rectangle area) {
        return activo && sprite.getBoundingRectangle().overlaps(area);
    }

    public boolean estaActivo() {
        return activo;
    }

    public void destruir() {
        activo = false;
    }
}