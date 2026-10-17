package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;

public abstract class Enemigo implements ObjetivoDisparo {
    private final Sprite sprite;
    private final float ancho;
    private final float alto;
    private float x;
    private float y;
    private float velocidadX;
    private float velocidadY;
    private int resistencia;
    private boolean activo = true;

    protected Enemigo(Texture textura, float x, float y, float ancho, float alto,
                      float velocidadX, float velocidadY, int resistencia) {
        if (textura == null || ancho <= 0f || alto <= 0f || resistencia <= 0) {
            throw new IllegalArgumentException("Configuracion de enemigo invalida");
        }
        this.sprite = new Sprite(textura);
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidadX = velocidadX;
        this.velocidadY = velocidadY;
        this.resistencia = resistencia;
        sprite.setSize(ancho, alto);
        sprite.setPosition(x, y);
    }

    public final void actualizar(float delta, float anchoMundo, float altoMundo,
                                 float objetivoX, float objetivoY) {
        if (!activo || delta <= 0f) {
            return;
        }

        actualizarComportamiento(delta, objetivoX, objetivoY);
        x += velocidadX * delta;
        y += velocidadY * delta;

        float maxX = Math.max(0f, anchoMundo - ancho);
        float maxY = Math.max(0f, altoMundo - alto);
        if (x < 0f) {
            x = 0f;
            velocidadX = Math.abs(velocidadX);
        } else if (x > maxX) {
            x = maxX;
            velocidadX = -Math.abs(velocidadX);
        }
        if (y < 0f) {
            y = 0f;
            velocidadY = Math.abs(velocidadY);
        } else if (y > maxY) {
            y = maxY;
            velocidadY = -Math.abs(velocidadY);
        }
        sprite.setPosition(x, y);
    }

    public final void update() {
        actualizar(1f / 60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                getCentroX(), getCentroY());
    }

    protected abstract void actualizarComportamiento(float delta, float objetivoX, float objetivoY);

    public final void dibujar(SpriteBatch batch) {
        if (activo) {
            sprite.draw(batch);
        }
    }

    protected final void setColor(float rojo, float verde, float azul, float alpha) {
        sprite.setColor(rojo, verde, azul, alpha);
    }

    public final void draw(SpriteBatch batch) {
        dibujar(batch);
    }

    @Override
    public final Rectangle getArea() {
        return sprite.getBoundingRectangle();
    }

    @Override
    public final void recibirImpacto() {
        if (activo && --resistencia <= 0) {
            activo = false;
        }
    }

    @Override
    public final boolean estaActivo() {
        return activo;
    }

    public final boolean estaDestruido() {
        return !activo;
    }

    protected final float getVelocidadX() {
        return velocidadX;
    }

    protected final float getVelocidadY() {
        return velocidadY;
    }

    protected final void setVelocidad(float x, float y) {
        velocidadX = x;
        velocidadY = y;
    }

    protected final float getCentroX() {
        return x + ancho / 2f;
    }

    protected final float getCentroY() {
        return y + alto / 2f;
    }
}