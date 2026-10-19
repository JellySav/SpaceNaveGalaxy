package puppy.code;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;

public class JuegoCoreTest {
    private ProgresionMejoras progresion;

    @Before
    public void reiniciarProgresion() {
        progresion = ProgresionMejoras.obtenerInstancia();
        progresion.reiniciar();
    }

    @Test
    public void estadoVidasLimitaDanoYReparacion() {
        EstadoVidas vidas = new EstadoVidas(9);
        assertEquals(3, vidas.getVidas());
        assertTrue(vidas.recibirDano());
        assertTrue(vidas.recibirDano());
        assertEquals(1, vidas.getVidas());
        assertTrue(vidas.reparar());
        assertEquals(2, vidas.getVidas());
        assertTrue(vidas.recibirDano());
        assertEquals(1, vidas.getVidas());
        assertTrue(vidas.recibirDano());
        assertTrue(vidas.estaDestruida());
        assertFalse(vidas.recibirDano());
        assertTrue(vidas.reparar());
        assertFalse(vidas.estaDestruida());
    }

    @Test
    public void mejoraDobleRequiereTiempoImpactosRondaYBateria() {
        for (int indice = 0; indice < 4; indice++) {
            progresion.registrarImpacto();
        }
        assertTrue(progresion.registrarBateria());
        progresion.avanzarTiempo(34f, 2);
        assertFalse(progresion.puedeInstalarMejora());
        progresion.avanzarTiempo(1f, 2);
        assertTrue(progresion.puedeInstalarMejora());
        assertTrue(progresion.instalarMejora());
        assertEquals(2, progresion.getNivelArma());
        assertEquals(0, progresion.getBaterias());
    }

    @Test
    public void mejoraTripleExigeNucleoDeJefeYConsumeRecursos() {
        for (int indice = 0; indice < 4; indice++) {
            progresion.registrarImpacto();
        }
        progresion.registrarBateria();
        progresion.avanzarTiempo(35f, 2);
        assertTrue(progresion.instalarMejora());

        for (int indice = 0; indice < 8; indice++) {
            progresion.registrarImpacto();
        }
        progresion.registrarBateria();
        progresion.registrarBateria();
        progresion.registrarAlienDerrotado();
        progresion.registrarAlienDerrotado();
        progresion.avanzarTiempo(55f, 3);
        assertFalse(progresion.puedeInstalarMejora());

        progresion.registrarJefeDerrotado();
        assertTrue(progresion.puedeInstalarMejora());
        assertTrue(progresion.instalarMejora());
        assertEquals(3, progresion.getNivelArma());
        assertEquals(0, progresion.getNucleosJefe());
        assertEquals(1, progresion.getBaterias());
    }

    @Test
    public void sectoresMarcanRondasDeJefeYEscalan() {
        assertTrue(SectorGalactico.CINTURON_DE_VELA.esRondaDeJefe(2));
        assertTrue(SectorGalactico.NEBULOSA_DE_ION.esRondaDeJefe(4));
        assertTrue(SectorGalactico.FRONTERA_DE_KHEPRI.esRondaDeJefe(5));
        assertEquals(SectorGalactico.NEBULOSA_DE_ION, SectorGalactico.desdeRonda(4));
        assertEquals(SectorGalactico.FRONTERA_DE_KHEPRI, SectorGalactico.desdeRonda(5));
    }

    @Test
    public void viewportConservaMundoYCentraVentanasDeOtrasProporciones() {
        Vector2 landscape = Scaling.fit.apply(MundoJuego.ANCHO, MundoJuego.ALTO, 1920f, 1080f);
        assertEquals(1620f, landscape.x, 0.01f);
        assertEquals(1080f, landscape.y, 0.01f);
        assertEquals(150f, (1920f - landscape.x) / 2f, 0.01f);

        Vector2 classic = Scaling.fit.apply(MundoJuego.ANCHO, MundoJuego.ALTO, 800f, 600f);
        assertEquals(800f, classic.x, 0.01f);
        assertEquals(533.333f, classic.y, 0.01f);
        assertEquals(33.333f, (600f - classic.y) / 2f, 0.01f);
    }

    @Test
    public void indiceEspacialDevuelveSoloObjetivosQueSolapan() {
        ObjetivoFalso cercano = new ObjetivoFalso(new Rectangle(20f, 20f, 16f, 16f));
        ObjetivoFalso lejano = new ObjetivoFalso(new Rectangle(500f, 500f, 16f, 16f));
        ObjetivoFalso inactivo = new ObjetivoFalso(new Rectangle(22f, 22f, 8f, 8f));
        inactivo.activo = false;
        IndiceEspacial indice = new IndiceEspacial(64f);
        indice.reconstruir(Arrays.asList(cercano, lejano, inactivo));

        List<ObjetivoDisparo> colisiones = indice.consultar(new Rectangle(25f, 25f, 8f, 8f));
        assertEquals(1, colisiones.size());
        assertTrue(colisiones.contains(cercano));
    }

    @Test
    public void builderRechazaEnemigosSinConfiguracionMinima() {
        assertThrows(IllegalStateException.class,
                () -> new EnemigoBuilder().construir(null));
        assertThrows(IllegalArgumentException.class,
                () -> new EnemigoBuilder()
                        .tipo(EnemigoBuilder.Tipo.ASTEROIDE)
                        .posicion(10f, 10f)
                        .tamano(32f)
                        .velocidad(0f, 0f)
                        .construir(null));
    }

    private static final class ObjetivoFalso implements ObjetivoDisparo {
        private final Rectangle area;
        private boolean activo = true;

        private ObjetivoFalso(Rectangle area) {
            this.area = area;
        }

        @Override
        public Rectangle getArea() {
            return area;
        }

        @Override
        public void recibirImpacto() {
            activo = false;
        }

        @Override
        public boolean estaActivo() {
            return activo;
        }
    }
}