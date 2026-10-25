package puppy.code;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** La nave morada se rompe en dos fragmentos con impulso lateral, sin divisiones infinitas. */
public final class AlienDivisible extends AlienEnemigo {
    public static final int RESISTENCIA = 5;
    public static final int RESISTENCIA_FRAGMENTO = 2;
    public static final float TAMANO_FRAGMENTO = 28f;
    public static final float DURACION_IMPULSO = 0.4f;
    private final Texture textura;
    private final float velocidadVerde;
    private final boolean fragmento;
    private float lado;
    private float impulsoRestante;
    private boolean dividida;
    private TextureRegion aparienciaFragmento;

    public AlienDivisible(float x, float y, float velocidadVerde, Texture textura) {
        this(x, y, velocidadVerde, textura, false, 0f);
    }

    private AlienDivisible(float x, float y, float velocidadVerde, Texture textura,
                           boolean fragmento, float lado) {
        super(x, y, fragmento ? TAMANO_FRAGMENTO : 48f,
                velocidadVerde * (fragmento ? 1.1f : 0.76f),
                fragmento ? RESISTENCIA_FRAGMENTO : RESISTENCIA, textura);
        this.textura = textura;
        this.velocidadVerde = velocidadVerde;
        this.fragmento = fragmento;
        this.lado = lado;
        impulsoRestante = fragmento ? DURACION_IMPULSO : 0f;
        setColor(fragmento ? 0.85f : 0.7f, 0.24f, 1f, 1f);
    }

    public void usarApariencias(TextureRegion normal,TextureRegion fragmentos) {
        aparienciaFragmento=fragmentos;
        usarApariencia(fragmento?fragmentos:normal);
    }

    public static int cantidadEnRonda(int ronda) {
        return OleadaNaves.cantidadMoradas(ronda);
    }

    @Override
    protected void actualizarComportamiento(float delta, float objetivoX, float objetivoY) {
        if (impulsoRestante > 0f) {
            if (getArea().x <= 0f && lado < 0f) lado = 1f;
            if (getArea().x + getArea().width >= MundoJuego.ANCHO && lado > 0f) lado = -1f;
            float velocidad = 120f + 300f * impulsoRestante / DURACION_IMPULSO;
            setVelocidad(lado * velocidad, -60f);
            impulsoRestante = Math.max(0f, impulsoRestante - delta);
            return;
        }
        super.actualizarComportamiento(delta, objetivoX, objetivoY);
    }

    /** Solo una muerte real genera hijos, una vez, y siempre dentro del escenario. */
    public List<AlienDivisible> dividir() {
        if (estaActivo() || fragmento || dividida) return Collections.emptyList();
        dividida = true;
        float centroX = getCentroX();
        float y = Math.max(0f, Math.min(MundoJuego.ALTO - TAMANO_FRAGMENTO,
                getCentroY() - TAMANO_FRAGMENTO / 2f));
        float izquierda = Math.max(0f, Math.min(MundoJuego.ANCHO - 2f * TAMANO_FRAGMENTO - 4f,
                centroX - TAMANO_FRAGMENTO - 2f));
        List<AlienDivisible> fragmentos=Arrays.asList(
                new AlienDivisible(izquierda, y, velocidadVerde, textura, true, -1f),
                new AlienDivisible(izquierda + TAMANO_FRAGMENTO + 4f, y,
                        velocidadVerde, textura, true, 1f));
        for(AlienDivisible hijo:fragmentos) {
            hijo.configurarVidaParaRonda(getRondaDeVida());
            if(aparienciaFragmento!=null)hijo.usarApariencias(aparienciaFragmento,aparienciaFragmento);
        }
        return fragmentos;
    }

    public boolean esFragmento() { return fragmento; }
}
