package puppy.code;

import java.util.Random;
import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;

/** Artillero naranja: patrulla una zona corta en altura y apunta sin perseguir. */
public final class AlienArtillero extends AlienEnemigo {
    public static final int RESISTENCIA = 5;
    private final Random random;
    private final boolean lateral;
    private final float altura;
    private final float entradaX;
    private final float limiteMinimo;
    private final float limiteMaximo;
    private boolean entrando = true;
    private float pausa;
    private float destinoPatrulla;
    private boolean moviendo;
    private float restanteAnterior;
    private float tiempoBloqueado;
    private float ladoTrasBloqueo;
    private float anclaPerpendicular;
    private float esperaDisparo = 0.15f;
    private boolean disparoPendiente = true;

    public AlienArtillero(float x, float y, Texture textura, Random random) {
        super(x, y, 140f, RESISTENCIA, textura);
        this.random = random;
        lateral = x < 0f || x >= MundoJuego.ANCHO;
        altura = lateral ? Math.max(180f, Math.min(MundoJuego.ALTO - 180f, y))
                : 540f + random.nextFloat() * 160f;
        entradaX = x < 0f ? 96f + random.nextFloat() * 80f
                : x >= MundoJuego.ANCHO ? MundoJuego.ANCHO - 224f + random.nextFloat() * 80f : x;
        float centroPatrulla = lateral ? altura : entradaX;
        limiteMinimo = Math.max(lateral ? 60f : 24f, centroPatrulla - 110f);
        limiteMaximo = Math.min((lateral ? MundoJuego.ALTO - 108f : MundoJuego.ANCHO - 72f), centroPatrulla + 110f);
        destinoPatrulla = centroPatrulla;
        anclaPerpendicular = lateral ? entradaX : altura;
        pausa = 0.6f + random.nextFloat() * 0.6f;
        setColor(1f, 0.5f, 0.08f, 1f);
    }

    public static int cantidadEnRonda(int ronda) {
        return OleadaNaves.cantidadNaranjas(ronda);
    }

    @Override
    public boolean estaEntrando() { return entrando; }

    @Override
    protected boolean rebotaEnBordes() { return !entrando; }

    @Override
    protected void actualizarComportamiento(float delta, float objetivoX, float objetivoY) {
        if (entrando) {
            float dx = entradaX - getArea().x;
            float dy = altura - getArea().y;
            float distancia = (float) Math.sqrt(dx * dx + dy * dy);
            if (distancia > 0.01f) {
                float velocidad = Math.min(140f, distancia / delta);
                setVelocidad(dx / distancia * velocidad, dy / distancia * velocidad);
                return;
            }
            entrando = false;
        }
        float correccion = Math.max(-140f, Math.min(140f,
                (anclaPerpendicular - (lateral ? getArea().x : getArea().y)) / delta));
        if (!moviendo) {
            pausa -= delta;
            setVelocidad(lateral ? correccion : 0f, lateral ? 0f : correccion);
            if (pausa <= 0f) {
                // Elige el lado con mas espacio, con distancia variable dentro de su zona.
                float posicion = lateral ? getArea().y : getArea().x;
                float lado = ladoTrasBloqueo != 0f ? ladoTrasBloqueo
                        : posicion - limiteMinimo < limiteMaximo - posicion ? 1f : -1f;
                ladoTrasBloqueo = 0f;
                destinoPatrulla = Math.max(limiteMinimo, Math.min(limiteMaximo,
                        posicion + lado * (70f + random.nextFloat() * 60f)));
                moviendo = true;
                restanteAnterior = destinoPatrulla - posicion;
                tiempoBloqueado = 0f;
            }
            return;
        }
        float restante = destinoPatrulla - (lateral ? getArea().y : getArea().x);
        float avance = Math.abs(restanteAnterior) - Math.abs(restante);
        float esperado = Math.min(160f * delta, Math.abs(restanteAnterior));
        tiempoBloqueado = esperado > 0.1f && avance < esperado * 0.2f
                ? tiempoBloqueado + delta : 0f;
        restanteAnterior = restante;
        if (Math.abs(restante) < 0.1f || tiempoBloqueado >= 0.3f) {
            if (tiempoBloqueado >= 0.3f) {
                ladoTrasBloqueo = restante > 0f ? -1f : 1f;
                // No intentar volver a una linea que otra nave esta ocupando.
                anclaPerpendicular = lateral ? getArea().x : getArea().y;
            }
            moviendo = false;
            tiempoBloqueado = 0f;
            pausa = 0.6f + random.nextFloat() * 0.6f;
            esperaDisparo = 0.15f;
            disparoPendiente = true;
            setVelocidad(0f, 0f);
        } else {
            float velocidad = Math.max(-160f, Math.min(160f, restante / delta));
            setVelocidad(lateral ? correccion : velocidad, lateral ? velocidad : correccion);
        }
    }

    public boolean estaEnPausa() {
        return !entrando && !moviendo && Math.abs(getVelocidadY()) < 0.01f && Math.abs(getVelocidadX()) < 0.01f;
    }

    public void actualizarDisparo(float delta, float jugadorX, float jugadorY,
                                  Texture textura, Consumer<ProyectilHostil> crearProyectil) {
        if (!estaActivo() || !estaEnPausa() || !disparoPendiente || delta <= 0f) return;
        esperaDisparo -= delta;
        if (esperaDisparo > 0f) return;
        float dx = jugadorX - getCentroX();
        float dy = jugadorY - getCentroY();
        if (dx == 0f && dy == 0f) dy = -1f;
        crearProyectil.accept(new ProyectilHostil(getCentroX() - 8f, getCentroY() - 12f,
                dx, dy, 220f, textura));
        disparoPendiente = false;
    }
}
