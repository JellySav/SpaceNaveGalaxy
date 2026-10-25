package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/** Barrido rojo a todo el ancho o alto, desde cualquiera de los cuatro bordes. */
public final class BarreraEnergia {
    public enum Direccion { ARRIBA, ABAJO, IZQUIERDA, DERECHA }
    public static final float AVISO = 1f;
    public static final float GROSOR = 30f;
    public static final float VELOCIDAD = 180f;
    private final Rectangle area = new Rectangle();
    private final Direccion direccion;
    private final float velocidad;
    private float tiempo;
    private boolean danoAplicado;

    public BarreraEnergia() { this(Direccion.ARRIBA, VELOCIDAD); }

    public BarreraEnergia(Direccion direccion, float velocidad) {
        if (direccion == null || velocidad <= 0f) throw new IllegalArgumentException("Barrera invalida");
        this.direccion = direccion;
        this.velocidad = velocidad;
        actualizarAreas();
    }

    public void actualizar(float delta) {
        tiempo += Math.max(0f, delta);
        actualizarAreas();
    }

    private void actualizarAreas() {
        float recorrido = velocidad * Math.max(0f, tiempo - AVISO);
        switch (direccion) {
            case ARRIBA: area.set(0f, MundoJuego.ALTO - GROSOR - recorrido, MundoJuego.ANCHO, GROSOR); break;
            case ABAJO: area.set(0f, recorrido, MundoJuego.ANCHO, GROSOR); break;
            case IZQUIERDA: area.set(recorrido, 0f, GROSOR, MundoJuego.ALTO); break;
            case DERECHA: area.set(MundoJuego.ANCHO - GROSOR - recorrido, 0f, GROSOR, MundoJuego.ALTO); break;
        }
    }

    public boolean toca(Rectangle nave) {
        return !estaAvisando() && !termino() && area.overlaps(nave);
    }

    public void comprobarImpacto(Nave4 nave) {
        if (!danoAplicado && toca(nave.getArea()) && nave.recibirDano()) danoAplicado = true;
    }

    public void dibujar(ShapeRenderer formas) {
        if (termino()) return;
        boolean horizontal = direccion == Direccion.ARRIBA || direccion == Direccion.ABAJO;
        if (estaAvisando()) {
            float pulso = 0.4f + 0.25f * (float) Math.sin(tiempo * 18f);
            formas.setColor(1f, 0.08f, 0.12f, pulso);
            formas.rect(area.x, area.y, horizontal ? area.width : 6f, horizontal ? 6f : area.height);
            formas.setColor(1f, 0.12f, 0.16f, 0.12f);
            formas.rect(area.x, area.y, area.width, area.height);
        } else {
            formas.setColor(0.9f, 0.02f, 0.06f, 0.65f);
            formas.rect(area.x, area.y, area.width, area.height);
            formas.setColor(1f, 0.32f, 0.36f, 1f);
            formas.rect(horizontal ? area.x : area.x + GROSOR / 2f - 3f,
                    horizontal ? area.y + GROSOR / 2f - 3f : area.y,
                    horizontal ? area.width : 6f, horizontal ? 6f : area.height);
        }
    }

    public boolean estaAvisando() { return tiempo < AVISO; }
    public boolean termino() {
        switch (direccion) {
            case ARRIBA: return area.y + GROSOR <= 0f;
            case ABAJO: return area.y >= MundoJuego.ALTO;
            case IZQUIERDA: return area.x >= MundoJuego.ANCHO;
            case DERECHA: return area.x + GROSOR <= 0f;
            default: throw new IllegalStateException();
        }
    }
    public float getY() { return area.y; }
    public Direccion getDireccion() { return direccion; }
}
