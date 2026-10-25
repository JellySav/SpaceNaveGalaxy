package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import java.util.HashSet;
import java.util.Set;

public class Nave4 {
    private static final float VELOCIDAD = 260f;
    private static final float TIEMPO_INVULNERABLE = 1.5f;
    private static final float INTERVALO_PARPADEO = 0.12f;
    public static final int DANO_DASH = 20;
    private final Set<Enemigo> golpeadosDash = new HashSet<>();
    private final Vector2 inicioDash = new Vector2();
    private final Vector2 finDash = new Vector2();
    private boolean recorrioDash;

    private final Sprite sprite;
    private Sprite apariencia;
    private TextureRegion regionApariencia;
    private final Sound sonidoHerido;
    private final Sound sonidoDisparo;
    private final Texture texturaBala;
    private final EstadoVidas estadoVidas = new EstadoVidas(3);
    private float tiempoInvulnerable;
    private EnergiaDash energiaDash = new EnergiaDash();
    private float ultimaDireccionX;
    private float ultimaDireccionY = 1f;
    private float direccionDashX;
    private float direccionDashY = 1f;
    private boolean reparando;
    public static final float INTERVALO_DISPARO = 0.4f;
    private float esperaDisparo;
    private int teclaDisparoActiva;
    private static final int[] TECLAS_DISPARO = {
        Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT
    };
    private int nivelArma = 1;
    private EstrategiaDisparo estrategiaDisparo = new DisparoSimple();

    public Nave4(int x, int y, Texture textura, Sound sonidoHerido,
                 Texture texturaBala, Sound sonidoDisparo) {
        this.sprite = new Sprite(textura);
        this.sprite.setBounds(x, y, 45f, 45f);
        this.sprite.setOriginCenter();
        this.sonidoHerido = sonidoHerido;
        this.texturaBala = texturaBala;
        this.sonidoDisparo = sonidoDisparo;
        aplicarMejoras();
    }

    public void draw(SpriteBatch batch, PantallaJuego juego) {
        draw(batch, juego, 1f / 60f);
    }

    public void draw(SpriteBatch batch, PantallaJuego juego, float delta) {
        actualizar(juego, delta);
        dibujar(batch);
    }

    public void actualizar(PantallaJuego juego, float delta) {
        if (!estadoVidas.estaDestruida()
                && Gdx.input.isKeyPressed(Input.Keys.F8) && Gdx.input.isKeyPressed(Input.Keys.F9)
                && (Gdx.input.isKeyJustPressed(Input.Keys.F8)
                    || Gdx.input.isKeyJustPressed(Input.Keys.F9))) {
            ProgresionMejoras.obtenerInstancia().alternarInvulnerabilidadPruebas();
        }
        tiempoInvulnerable = Math.max(0f, tiempoInvulnerable - delta);
        actualizarMovimiento(delta);
        actualizarDisparo(juego, delta);
    }

    private void actualizarDisparo(PantallaJuego juego, float delta) {
        if (delta <= 0f) return;
        esperaDisparo -= delta;
        for (int tecla : TECLAS_DISPARO) {
            if (Gdx.input.isKeyJustPressed(tecla) && Gdx.input.isKeyPressed(tecla)) {
                teclaDisparoActiva = tecla;
            }
        }
        if (teclaDisparoActiva == 0 || !Gdx.input.isKeyPressed(teclaDisparoActiva)) {
            teclaDisparoActiva = 0;
            for (int tecla : TECLAS_DISPARO) {
                if (Gdx.input.isKeyPressed(tecla)) { teclaDisparoActiva = tecla; break; }
            }
        }
        if (reparando || estadoVidas.estaDestruida() || teclaDisparoActiva == 0) {
            esperaDisparo=Math.max(0f,esperaDisparo);
            return;
        }
        float dx = teclaDisparoActiva == Input.Keys.RIGHT ? 1f
                : teclaDisparoActiva == Input.Keys.LEFT ? -1f : 0f;
        float dy = teclaDisparoActiva == Input.Keys.UP ? 1f
                : teclaDisparoActiva == Input.Keys.DOWN ? -1f : 0f;
        sprite.setRotation((float) Math.toDegrees(Math.atan2(dy, dx)) - 90f);
        if (esperaDisparo > 0f) return;
        while(esperaDisparo<=0f) {
        estrategiaDisparo.disparar(getCentroX() + dx * (sprite.getWidth() / 2f + 4f),
                getCentroY() + dy * (sprite.getHeight() / 2f + 4f),
                dx, dy, texturaBala, bala -> {
                    ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
                    bala.configurarJugador(p,
                            java.util.concurrent.ThreadLocalRandom.current());
                    juego.agregarBala(bala);
                });
        sonidoDisparo.play(PreferenciasJuego.obtenerInstancia().getVolumenEfectos());
        esperaDisparo += 1f / ProgresionMejoras.obtenerInstancia().getCadencia();
        }
    }

    public void dibujar(SpriteBatch batch) {
        if(tiempoInvulnerable>0f && ((int)((TIEMPO_INVULNERABLE-tiempoInvulnerable)/INTERVALO_PARPADEO)&1)==1)return;
        if (energiaDash.protegeEsteFrame()) {
            // Reutiliza el sprite como estela; no necesita nuevas imagenes.
            for (int i = 3; i >= 1; i--) {
                float x = sprite.getX();
                float y = sprite.getY();
                sprite.setPosition(x - direccionDashX * i * 14f, y - direccionDashY * i * 14f);
                sprite.setColor(0.2f, 0.85f, 1f, 0.12f * (4 - i));
                dibujarApariencia(batch);
                sprite.setPosition(x, y);
            }
            sprite.setColor(0.35f, 0.95f, 1f, 1f);
            dibujarApariencia(batch);
            sprite.setColor(1f, 1f, 1f, 1f);
        } else if (energiaDash.tieneEscudo()) {
            sprite.setColor(0.35f,1f,0.5f,1f);dibujarApariencia(batch);sprite.setColor(1f,1f,1f,1f);
        } else {
            dibujarApariencia(batch);
        }
    }

    /** Cambia solo el dibujo; movimiento, origen de disparo y colision usan el sprite logico. */
    public void usarApariencia(TextureRegion region) {
        if(regionApariencia==region)return;
        regionApariencia=region;
        apariencia=region==null?null:new Sprite(region);
        if(apariencia!=null) {
            float ancho=sprite.getWidth();
            apariencia.setSize(ancho,ancho*region.getRegionHeight()/region.getRegionWidth());
            apariencia.setOriginCenter();
        }
    }
    private void dibujarApariencia(SpriteBatch batch) {
        if(apariencia==null){sprite.draw(batch);return;}
        apariencia.setCenter(getCentroX(),getCentroY());
        apariencia.setRotation(sprite.getRotation());
        apariencia.setColor(sprite.getColor());
        apariencia.draw(batch);
    }

    private void actualizarMovimiento(float delta) {
        recorrioDash = false;
        if (estadoVidas.estaDestruida() || reparando) {
            energiaDash.detener();
            energiaDash.actualizar(delta);
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

        if (magnitud > 0f) {
            ultimaDireccionX = direccionX;
            ultimaDireccionY = direccionY;
        }
        if ((Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)
                || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)) && energiaDash.iniciar()) {
            direccionDashX = ultimaDireccionX;
            direccionDashY = ultimaDireccionY;
            golpeadosDash.clear();
        }
        float velocidadDash = ProgresionMejoras.obtenerInstancia().getVelocidadDash();
        float tramoDash = energiaDash.actualizar(delta);
        float tramoNormal = delta - tramoDash;
        float desplazamientoX = direccionDashX * velocidadDash * tramoDash
                + direccionX * ProgresionMejoras.obtenerInstancia().getVelocidad() * tramoNormal;
        float desplazamientoY = direccionDashY * velocidadDash * tramoDash
                + direccionY * ProgresionMejoras.obtenerInstancia().getVelocidad() * tramoNormal;

        float maxX = Math.max(0f, MundoJuego.ANCHO - sprite.getWidth());
        float maxY = Math.max(0f, MundoJuego.ALTO - sprite.getHeight());
        if (tramoDash > 0f) {
            recorrioDash = true;
            inicioDash.set(getCentroX(), getCentroY());
            finDash.set(Math.max(0f, Math.min(maxX,
                    sprite.getX() + direccionDashX * velocidadDash * tramoDash))
                    + sprite.getWidth() / 2f,
                    Math.max(0f, Math.min(maxY,
                    sprite.getY() + direccionDashY * velocidadDash * tramoDash))
                    + sprite.getHeight() / 2f);
        }
        sprite.setPosition(
                Math.max(0f, Math.min(maxX, sprite.getX() + desplazamientoX)),
                Math.max(0f, Math.min(maxY, sprite.getY() + desplazamientoY)));
    }

    /** Detecta todo el tramo recorrido, incluso si cruza al enemigo entre cuadros. */
    public boolean golpearConDash(Enemigo enemigo) {
        if (!recorrioDash || !enemigo.estaActivo() || golpeadosDash.contains(enemigo)) return false;
        Rectangle objetivo = new Rectangle(enemigo.getArea());
        objetivo.x -= sprite.getWidth() / 2f;
        objetivo.y -= sprite.getHeight() / 2f;
        objetivo.width += sprite.getWidth();
        objetivo.height += sprite.getHeight();
        if (!Intersector.intersectSegmentRectangle(inicioDash, finDash, objetivo)) return false;
        golpeadosDash.add(enemigo);
        enemigo.recibirDano(ProgresionMejoras.obtenerInstancia().getDanoDash());
        return true;
    }

    public boolean checkCollision(Enemigo enemigo) {
        if (!ProgresionMejoras.obtenerInstancia().esInvulnerablePruebas()
                && tiempoInvulnerable <= 0f && !energiaDash.protegeEsteFrame() && !energiaDash.tieneEscudo() && enemigo.estaActivo()
                && enemigo.getArea().overlaps(sprite.getBoundingRectangle())) {
            recibirDano();
            return true;
        }
        return false;
    }

    public boolean recibirDano() {
        if (ProgresionMejoras.obtenerInstancia().esInvulnerablePruebas()
                || tiempoInvulnerable > 0f || energiaDash.protegeEsteFrame() || energiaDash.tieneEscudo() || !estadoVidas.recibirDano()) {
            return false;
        }
        tiempoInvulnerable = TIEMPO_INVULNERABLE;
        sonidoHerido.play(PreferenciasJuego.obtenerInstancia().getVolumenEfectos());
        return true;
    }

    public void setNivelArma(int nivel) {
        nivelArma = Math.max(1, Math.min(4, nivel));
        if (nivelArma == 1) {
            estrategiaDisparo = new DisparoSimple();
        } else if (nivelArma == 2) {
            estrategiaDisparo = new DisparoDoble();
        } else if(nivelArma==3) {
            estrategiaDisparo = new DisparoTriple();
        } else {
            estrategiaDisparo = new DisparoQuintuple();
        }
    }

    public void aplicarMejoras() {
        ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
        estadoVidas.actualizarMaximo(p.getVidasMaximas());
        setNivelArma(p.getNivelArma());
    }
    public int getVidasMaximas(){return estadoVidas.getMaximas();}

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

    public EnergiaDash getEnergiaDash() { return energiaDash; }

    public void setEnergiaDash(EnergiaDash energiaDash) { this.energiaDash = energiaDash; }

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

    public float getDireccionFrenteX(){return (float)Math.cos(Math.toRadians(sprite.getRotation()+90f));}
    public float getDireccionFrenteY(){return (float)Math.sin(Math.toRadians(sprite.getRotation()+90f));}

    public float getCentroX() {
        return sprite.getX() + sprite.getWidth() / 2f;
    }

    public float getCentroY() {
        return sprite.getY() + sprite.getHeight() / 2f;
    }
}
