package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Nave4 {
    private static final float VELOCIDAD = 260f;
    private static final float TIEMPO_INVULNERABLE = 0.9f;

    private final Sprite sprite;
    private final Sound sonidoHerido;
    private final Sound sonidoDisparo;
    private final Texture texturaBala;
    private final EstadoVidas estadoVidas = new EstadoVidas(3);
    private float tiempoInvulnerable;
    private boolean reparando;
    private int nivelArma = 1;
    private EstrategiaDisparo estrategiaDisparo = new DisparoSimple();

    public Nave4(int x, int y, Texture textura, Sound sonidoHerido,
                 Texture texturaBala, Sound sonidoDisparo) {
        this.sprite = new Sprite(textura);
        this.sprite.setBounds(x, y, 45f, 45f);
        this.sonidoHerido = sonidoHerido;
        this.texturaBala = texturaBala;
        this.sonidoDisparo = sonidoDisparo;
    }

    public void draw(SpriteBatch batch, PantallaJuego juego) {
        draw(batch, juego, 1f / 60f);
    }

    public void draw(SpriteBatch batch, PantallaJuego juego, float delta) {
        actualizarMovimiento(delta);
        PreferenciasJuego controles = PreferenciasJuego.obtenerInstancia();
        if (!reparando && Gdx.input.isKeyJustPressed(controles.getTeclaDisparo())) {
            estrategiaDisparo.disparar(sprite.getX() + sprite.getWidth() / 2f,
                    sprite.getY() + sprite.getHeight(), texturaBala, juego::agregarBala);
            sonidoDisparo.play(PreferenciasJuego.obtenerInstancia().getVolumenEfectos());
        }

        if (tiempoInvulnerable > 0f) {
            tiempoInvulnerable = Math.max(0f, tiempoInvulnerable - delta);
            if (((int) (tiempoInvulnerable * 12f) & 1) == 0) {
                sprite.draw(batch);
            }
        } else {
            sprite.draw(batch);
        }
    }

    private void actualizarMovimiento(float delta) {
        if (tiempoInvulnerable > 0f || estadoVidas.estaDestruida() || reparando) {
            return;
        }

        PreferenciasJuego controles = PreferenciasJuego.obtenerInstancia();
        float direccionX = (Gdx.input.isKeyPressed(controles.getTeclaDerecha()) ? 1f : 0f)
            - (Gdx.input.isKeyPressed(controles.getTeclaIzquierda()) ? 1f : 0f);
        float direccionY = (Gdx.input.isKeyPressed(controles.getTeclaArriba()) ? 1f : 0f)
            - (Gdx.input.isKeyPressed(controles.getTeclaAbajo()) ? 1f : 0f);
        float magnitud = (float) Math.sqrt(direccionX * direccionX + direccionY * direccionY);
        if (magnitud > 1f) {
            direccionX /= magnitud;
            direccionY /= magnitud;
        }

        float maxX = Math.max(0f, MundoJuego.ANCHO - sprite.getWidth());
        float maxY = Math.max(0f, MundoJuego.ALTO - sprite.getHeight());
        sprite.setPosition(
                Math.max(0f, Math.min(maxX, sprite.getX() + direccionX * VELOCIDAD * delta)),
                Math.max(0f, Math.min(maxY, sprite.getY() + direccionY * VELOCIDAD * delta)));
    }

    public boolean checkCollision(Enemigo enemigo) {
        if (tiempoInvulnerable <= 0f && enemigo.estaActivo()
                && enemigo.getArea().overlaps(sprite.getBoundingRectangle())) {
            recibirDano();
            return true;
        }
        return false;
    }

    public boolean recibirDano() {
        if (tiempoInvulnerable > 0f || !estadoVidas.recibirDano()) {
            return false;
        }
        tiempoInvulnerable = TIEMPO_INVULNERABLE;
        sonidoHerido.play(PreferenciasJuego.obtenerInstancia().getVolumenEfectos());
        return true;
    }

    public void setNivelArma(int nivel) {
        nivelArma = Math.max(1, Math.min(3, nivel));
        if (nivelArma == 1) {
            estrategiaDisparo = new DisparoSimple();
        } else if (nivelArma == 2) {
            estrategiaDisparo = new DisparoDoble();
        } else {
            estrategiaDisparo = new DisparoTriple();
        }
    }

    public boolean estaDestruido() {
        return estadoVidas.estaDestruida();
    }

    public boolean estaHerido() {
        return tiempoInvulnerable > 0f;
    }

    public int getVidas() {
        return estadoVidas.getVidas();
    }

    public Rectangle getArea() {
        return sprite.getBoundingRectangle();
    }

    public void setReparando(boolean reparando) {
        this.reparando = reparando;
    }

    public boolean isReparando() {
        return reparando;
    }

    public void reparar() {
        estadoVidas.reparar();
    }

    public boolean puedeReparar() {
        return estadoVidas.puedeReparar();
    }

    public void setVidas(int vidas) {
        estadoVidas.establecerVidas(vidas);
    }

    public int getNivelArma() {
        return nivelArma;
    }

    public String getNombreArma() {
        return estrategiaDisparo.getNombre();
    }

    public float getCentroX() {
        return sprite.getX() + sprite.getWidth() / 2f;
    }

    public float getCentroY() {
        return sprite.getY() + sprite.getHeight() / 2f;
    }
}