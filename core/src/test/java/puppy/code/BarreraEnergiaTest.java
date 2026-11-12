package puppy.code;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;

public class BarreraEnergiaTest extends EntornoGdxPrueba {
    @Before
    public void iniciarPartida() { ProgresionMejoras.obtenerInstancia().reiniciar(); }

    @After
    public void limpiarPartida() { ProgresionMejoras.obtenerInstancia().reiniciar(); }

    private JefeSector jefe(SectorGalactico sector) {
        return new JefeSector(400f, 600f, 100f, 30f, 20, sector, textura, textura);
    }

    private BarreraEnergia aLaAltura(float y) {
        BarreraEnergia barrera = new BarreraEnergia();
        barrera.actualizar(BarreraEnergia.AVISO
                + (MundoJuego.ALTO - BarreraEnergia.GROSOR - y) / BarreraEnergia.VELOCIDAD);
        return barrera;
    }

    @Test
    public void avisoDeUnSegundoNoDañaNiMueveLaBarrera() {
        BarreraEnergia barrera = new BarreraEnergia();
        Rectangle nave = new Rectangle(100f, 755f, 45f, 45f);
        barrera.actualizar(0.99f);
        assertTrue(barrera.estaAvisando());
        assertFalse(barrera.toca(nave));
        assertEquals(770f, barrera.getY(), 0.001f);
        barrera.actualizar(0.02f);
        assertFalse(barrera.estaAvisando());
        assertTrue(barrera.toca(nave));
        assertEquals(768.2f, barrera.getY(), 0.01f);
    }

    @Test
    public void barreraCubreTodoElAnchoSinZonaSegura() {
        BarreraEnergia barrera = aLaAltura(400f);
        for (float x = 0f; x <= MundoJuego.ANCHO - 45f; x += 5f) {
            assertTrue("Barrera sin huecos en x=" + x,
                    barrera.toca(new Rectangle(x, 410f, 45f, 45f)));
        }
        assertFalse(barrera.toca(new Rectangle(100f, 100f, 45f, 45f)));
    }

    @Test
    public void unBarridoSoloPuedeQuitarUnaVida() {
        BarreraEnergia barrera = aLaAltura(400f);
        Nave4 nave = nave(100, 400);
        barrera.comprobarImpacto(nave);
        assertEquals(2, nave.getVidas());
        nave.actualizar(null, 1f);
        barrera.comprobarImpacto(nave);
        assertEquals(2, nave.getVidas());
    }

    @Test
    public void dashRealPermiteAtravesarTodoElGrosorSinDano() {
        BarreraEnergia barrera = aLaAltura(300f);
        Nave4 nave = nave(100, 230);
        pulsadas.add(Input.Keys.W);
        nuevas.add(Input.Keys.SHIFT_LEFT);
        for (int frame = 0; frame < 5; frame++) {
            nave.actualizar(null, 0.05f);
            barrera.actualizar(0.05f);
            barrera.comprobarImpacto(nave);
            nuevas.clear();
        }
        assertTrue(nave.getArea().y > barrera.getY() + BarreraEnergia.GROSOR);
        assertEquals(3, nave.getVidas());
        assertTrue(nave.getEnergiaDash().getEnergia() < 100f);
    }

    @Test
    public void inmortalidadDePruebasTambienProtegeDeLaBarrera() {
        BarreraEnergia barrera = aLaAltura(400f);
        Nave4 nave = nave(100, 400);
        ProgresionMejoras.obtenerInstancia().alternarInvulnerabilidadPruebas();
        barrera.comprobarImpacto(nave);
        assertEquals(3, nave.getVidas());
    }

    @Test
    public void barreraTerminaAlSalirDeLaPantallaYSinTiempoNoAvanza() {
        BarreraEnergia barrera = new BarreraEnergia();
        barrera.actualizar(0f);
        barrera.actualizar(-3f);
        assertEquals(770f, barrera.getY(), 0f);
        assertTrue(barrera.estaAvisando());
        barrera.actualizar(6f);
        assertTrue(barrera.termino());
        assertFalse(barrera.toca(new Rectangle(100f, 0f, 45f, 45f)));
    }

    @Test
    public void cadaCatorceSegundosDaUnaCargaDeDashAntesDelBarrido() {
        AtaqueBarrera ataque = new AtaqueBarrera();
        List<CargaEnergia> cargas = new ArrayList<>();
        ataque.actualizar(13.9f, 300f, 200f, cargas::add);
        assertNull(ataque.getBarrera());
        ataque.actualizar(0.11f, 300f, 200f, cargas::add);
        assertNotNull(ataque.getBarrera());
        assertTrue(ataque.getBarrera().estaAvisando());
        assertEquals(1, cargas.size());
        assertEquals(EnergiaDash.COSTE, cargas.get(0).getCantidad(), 0f);
        assertTrue(cargas.get(0).toca(new Rectangle(280f, 180f, 45f, 45f)));
        ataque.actualizar(6f, 300f, 200f, cargas::add);
        assertNull(ataque.getBarrera());
        ataque.actualizar(7.9f, 300f, 200f, cargas::add);
        assertNull(ataque.getBarrera());
        ataque.actualizar(0.11f, 300f, 200f, cargas::add);
        assertEquals(2, cargas.size());
        assertTrue(ataque.getBarrera().estaAvisando());
    }

    @Test
    public void segundoJefeDisparaConBarreraEnMovimientoYPrimeroNoTieneBarrera() {
        JefeSector segundo = jefe(SectorGalactico.NEBULOSA_DE_ION);
        List<ProyectilHostil> balas = new ArrayList<>();
        List<CargaEnergia> cargas = new ArrayList<>();
        segundo.actualizarAtaque(2.2f, 200f, 100f, balas::add);
        assertEquals(5, balas.size());
        segundo.actualizarAtaqueEspecial(14f, 200f, 100f, cargas::add);
        assertNotNull(segundo.getBarreraEnergia());
        segundo.actualizarAtaqueEspecial(1.1f,200f,100f,cargas::add);
        assertFalse(segundo.getBarreraEnergia().estaAvisando());
        segundo.actualizarAtaque(2.2f, 200f, 100f, balas::add);
        assertEquals(10, balas.size());
        segundo.actualizarAtaqueEspecial(6f, 200f, 100f, cargas::add);
        assertNull(segundo.getBarreraEnergia());
        segundo.actualizarAtaque(2.2f, 200f, 100f, balas::add);
        assertEquals(15, balas.size());
        for (SectorGalactico sector : new SectorGalactico[]{
                SectorGalactico.CINTURON_DE_VELA}) {
            JefeSector otro = jefe(sector);
            otro.actualizarAtaqueEspecial(30f, 200f, 100f, c -> fail());
            assertNull(otro.getBarreraEnergia());
        }
    }

    @Test
    public void alMorirElJefeLaBarreraDesapareceYNoSeCreanMas() {
        JefeSector segundo = jefe(SectorGalactico.NEBULOSA_DE_ION);
        List<CargaEnergia> cargas = new ArrayList<>();
        segundo.actualizarAtaqueEspecial(14f, 200f, 100f, cargas::add);
        assertNotNull(segundo.getBarreraEnergia());
        while (segundo.estaActivo()) segundo.recibirImpacto();
        assertNull(segundo.getBarreraEnergia());
        segundo.actualizarAtaqueEspecial(30f, 200f, 100f, cargas::add);
        assertEquals(1, cargas.size());
    }
}
