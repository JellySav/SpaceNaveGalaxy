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
    public void puntajeDesbloqueaSinConsumirBateriasNiExigirObjetivos() {
        progresion.registrarBateria();
        progresion.sumarPuntos(249);
        assertFalse(progresion.hayMejorasPendientes());
        progresion.sumarPuntos(1);
        assertTrue(progresion.hayMejorasPendientes());
        assertTrue(progresion.elegirMejora(TipoMejora.DOBLE));
        assertEquals(2,progresion.getNivelArma());
        assertEquals(1,progresion.getBaterias());
    }

    @Test
    public void dobleYTripleSonIndependientesYCombinanQuintuple() {
        progresion.sumarPuntos(750);
        assertTrue(progresion.elegirMejora(TipoMejora.TRIPLE));
        assertEquals(3,progresion.getNivelArma());
        assertFalse(progresion.tieneDron());
        assertTrue(progresion.elegirMejora(TipoMejora.DOBLE));
        assertEquals(4,progresion.getNivelArma());
        assertFalse(TipoMejora.DOBLE.disponible(progresion));
    }

    @Test
    public void sectoresMarcanRondasDeJefeYEscalan() {
        assertFalse(SectorGalactico.CINTURON_DE_VELA.esRondaDeJefe(3));
        assertTrue(SectorGalactico.CINTURON_DE_VELA.esRondaDeJefe(4));
        assertTrue(SectorGalactico.NEBULOSA_DE_ION.esRondaDeJefe(8));
        assertFalse(SectorGalactico.FRONTERA_DE_KHEPRI.esRondaDeJefe(11));
        assertTrue(SectorGalactico.FRONTERA_DE_KHEPRI.esRondaDeJefe(12));
        assertEquals(15, OleadaNaves.cantidadInicial(2));
        assertEquals(21, OleadaNaves.cantidadInicial(6));
        assertEquals(0, OleadaNaves.cantidadInicial(12));
        for (int ronda = 9; ronda <= 40; ronda++) {
            assertEquals(ronda >= 12 && ronda % 4 == 0,
                    SectorGalactico.FRONTERA_DE_KHEPRI.esRondaDeJefe(ronda));
        }
        assertEquals(SectorGalactico.CINTURON_DE_VELA, SectorGalactico.desdeRonda(2));
        assertEquals(SectorGalactico.NEBULOSA_DE_ION, SectorGalactico.desdeRonda(5));
        assertEquals(SectorGalactico.FRONTERA_DE_KHEPRI, SectorGalactico.desdeRonda(9));
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

    @Test
    public void tresEtapasAntesDeCadaJefeYVidaEscaladaDelTercero() {
        for (int ronda=1;ronda<=60;ronda++) {
            SectorGalactico sector=SectorGalactico.desdeRonda(ronda);
            assertEquals(ronda % 4 == 0, sector.esRondaDeJefe(ronda));
            assertEquals(ronda % 4 == 0, OleadaNaves.cantidadInicial(ronda)==0);
        }
        assertTrue(SectorGalactico.NEBULOSA_DE_ION.iniciaEnRonda(5));
        assertTrue(SectorGalactico.FRONTERA_DE_KHEPRI.iniciaEnRonda(9));
        assertEquals(40,SectorGalactico.desdeRonda(4).getResistenciaJefe(4));
        assertEquals(200,SectorGalactico.desdeRonda(8).getResistenciaJefe(8));
        assertEquals(300,SectorGalactico.desdeRonda(12).getResistenciaJefe(12));
        assertEquals(330,SectorGalactico.desdeRonda(16).getResistenciaJefe(16));
        assertEquals(360,SectorGalactico.desdeRonda(20).getResistenciaJefe(20));
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
