package puppy.code;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;

public class BarrerasTercerJefeTest extends EntornoGdxPrueba {
    @Test public void alternaArribaDerechaYAbajoIzquierdaSinAcumularBarreras() {
        AtaqueBarrera ataque = new AtaqueBarrera(true);
        List<CargaEnergia> cargas = new ArrayList<>();
        for (int ciclo = 0; ciclo < 4; ciclo++) {
            ataque.actualizar(ciclo == 0 ? 14f : 7f, 600f, 400f, cargas::add);
            List<BarreraEnergia> barreras = ataque.getBarreras();
            assertEquals(2, barreras.size());
            assertEquals(ciclo % 2 == 0 ? BarreraEnergia.Direccion.ARRIBA
                    : BarreraEnergia.Direccion.ABAJO, barreras.get(0).getDireccion());
            assertEquals(ciclo % 2 == 0 ? BarreraEnergia.Direccion.DERECHA
                    : BarreraEnergia.Direccion.IZQUIERDA, barreras.get(1).getDireccion());
            assertTrue(barreras.get(0).estaAvisando());
            ataque.actualizar(0f, 600f, 400f, cargas::add);
            assertEquals(2, barreras.size());
            ataque.actualizar(7f, 600f, 400f, cargas::add);
            assertTrue(ataque.getBarreras().isEmpty());
            assertEquals(ciclo + 1, cargas.size());
        }
    }

    @Test public void cuatroDireccionesAvisanLuegoAvanzanYSalenDelLadoOpuesto() {
        for (BarreraEnergia.Direccion direccion : BarreraEnergia.Direccion.values()) {
            BarreraEnergia barrera = new BarreraEnergia(direccion, 216f);
            Rectangle destino;
            switch (direccion) {
                case ARRIBA: destino = new Rectangle(500f, 560f, 45f, 45f); break;
                case ABAJO: destino = new Rectangle(500f, 210f, 45f, 45f); break;
                case IZQUIERDA: destino = new Rectangle(210f, 400f, 45f, 45f); break;
                default: destino = new Rectangle(950f, 400f, 45f, 45f); break;
            }
            barrera.actualizar(0.5f);
            assertTrue(barrera.estaAvisando());
            assertFalse(barrera.toca(destino));
            barrera.actualizar(1.5f);
            assertTrue(barrera.toca(destino));
            assertFalse(barrera.termino());
            barrera.actualizar(6f);
            assertTrue(barrera.termino());
            assertFalse(barrera.toca(destino));
        }
    }

    @Test public void tercerJefeDisparaConBarrerasYLasRetiraAlMorir() {
        JefeSector jefe = new JefeSector(500f, 600f, 100f, 30f, 260,
                SectorGalactico.FRONTERA_DE_KHEPRI, textura, textura);
        List<ProyectilHostil> balas = new ArrayList<>();
        List<CargaEnergia> cargas = new ArrayList<>();
        jefe.actualizarAtaqueEspecial(14f, 600f, 400f, cargas::add);
        assertEquals(2, jefe.getBarrerasEnergia().size());
        jefe.actualizarAtaqueEspecial(1.1f,600f,400f,cargas::add);
        for(BarreraEnergia barrera:jefe.getBarrerasEnergia())assertFalse(barrera.estaAvisando());
        jefe.actualizarAtaque(4f, 600f, 400f, balas::add);
        assertEquals(7,balas.size());
        jefe.actualizarAtaque(0.36f,600f,400f,balas::add);
        assertEquals(14,balas.size());
        jefe.actualizarAtaqueEspecial(7f, 600f, 400f, cargas::add);
        jefe.actualizarAtaque(3.6f, 600f, 400f, balas::add);
        assertEquals(21, balas.size());
        jefe.actualizarAtaqueEspecial(7f, 600f, 400f, cargas::add);
        assertEquals(2, jefe.getBarrerasEnergia().size());
        while (jefe.estaActivo()) jefe.recibirImpacto();
        assertTrue(jefe.getBarrerasEnergia().isEmpty());
        jefe.actualizarAtaqueEspecial(30f, 600f, 400f, cargas::add);
        assertTrue(jefe.getBarrerasEnergia().isEmpty());
        assertEquals(2, cargas.size());
    }

    @Test public void dashPermiteCruzarBarreraVerticalMasRapida() {
        ProgresionMejoras.obtenerInstancia().reiniciar();
        BarreraEnergia barrera = new BarreraEnergia(BarreraEnergia.Direccion.IZQUIERDA, 216f);
        barrera.actualizar(2f);
        Nave4 nave = nave(275, 400);
        pulsadas.add(Input.Keys.A);
        nuevas.add(Input.Keys.SHIFT_LEFT);
        for (int i = 0; i < 5; i++) {
            nave.actualizar(null, 0.05f);
            barrera.actualizar(0.05f);
            barrera.comprobarImpacto(nave);
            nuevas.clear();
        }
        assertEquals(3, nave.getVidas());
        assertTrue(nave.getArea().x + nave.getArea().width < 216f);
    }
}
