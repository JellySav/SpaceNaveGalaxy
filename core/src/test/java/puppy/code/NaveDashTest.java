package puppy.code;

import static org.junit.Assert.*;
import org.junit.Test;
import com.badlogic.gdx.Input;

public class NaveDashTest extends EntornoGdxPrueba {
    @Test
    public void diagonalNormalizadaProtegeDelDanoYTermina() {
        Nave4 nave = nave(100, 100);
        pulsadas.add(Input.Keys.W);
        pulsadas.add(Input.Keys.D);
        nuevas.add(Input.Keys.SHIFT_LEFT);
        nave.actualizar(null, 0.05f);
        assertEquals(100f + 50f / (float) Math.sqrt(2), nave.getArea().x, 0.01f);
        assertEquals(nave.getArea().x, nave.getArea().y, 0.01f);
        assertFalse(nave.recibirDano());
        assertEquals(3, nave.getVidas());
        nuevas.clear();
        pulsadas.clear();
        for (int i = 0; i < 6; i++) nave.actualizar(null, 0.05f);
        assertTrue(nave.recibirDano());
        assertEquals(2, nave.getVidas());
    }

    @Test
    public void reparacionBloqueaDashSinGastarEnergia() {
        Nave4 nave = nave(100, 100);
        nave.setReparando(true);
        nuevas.add(Input.Keys.SHIFT_LEFT);
        pulsadas.add(Input.Keys.D);
        nave.actualizar(null, 0.05f);
        assertEquals(100f, nave.getArea().x, 0f);
        assertEquals(100f, nave.getEnergiaDash().getEnergia(), 0f);
    }

    @Test
    public void wasdYShiftDerechoRespetanLimitesDelMundo() {
        Nave4 nave = nave(1145, 100);
        pulsadas.add(Input.Keys.D);
        nuevas.add(Input.Keys.SHIFT_RIGHT);
        nave.actualizar(null, 0.05f);
        assertEquals(MundoJuego.ANCHO - 45f, nave.getArea().x, 0f);
        assertEquals(60f, nave.getEnergiaDash().getEnergia(), 0.001f);
    }

    @Test
    public void sinDireccionInicialElDashSube() {
        Nave4 nave = nave(100, 100);
        nuevas.add(Input.Keys.SHIFT_LEFT);
        nave.actualizar(null, 0.05f);
        assertEquals(100f, nave.getArea().x, 0f);
        assertEquals(150f, nave.getArea().y, 0.01f);
    }
}
