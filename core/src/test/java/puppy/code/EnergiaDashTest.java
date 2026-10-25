package puppy.code;

import static org.junit.Assert.*;
import org.junit.Test;
import com.badlogic.gdx.math.Rectangle;

public class EnergiaDashTest {
    @Test
    public void dashConsumeUnaVezYRespetaEspera() {
        EnergiaDash energia = new EnergiaDash();
        assertTrue(energia.iniciar());
        assertEquals(60f, energia.getEnergia(), 0.001f);
        assertFalse(energia.iniciar());
        assertEquals(0.17f, energia.actualizar(0.17f), 0.001f);
        assertTrue(energia.protegeEsteFrame());
        assertFalse(energia.estaActivo());
        assertFalse(energia.iniciar());
        energia.actualizar(0.29f);
        assertFalse(energia.protegeEsteFrame());
        assertTrue(energia.iniciar());
    }

    @Test
    public void energiaInsuficienteImpideDashHastaRecogerUnaCarga() {
        EnergiaDash energia = new EnergiaDash();
        assertTrue(energia.iniciar());
        energia.actualizar(0.5f);
        assertTrue(energia.iniciar());
        energia.actualizar(0.5f);
        float restante = energia.getEnergia();
        assertFalse(energia.iniciar());
        assertEquals(restante, energia.getEnergia(), 0.001f);
        energia.recuperar(20f);
        assertTrue(energia.iniciar());
    }

    @Test
    public void recargaSoloElTiempoFueraDelDashYLimitaElMaximo() {
        EnergiaDash energia = new EnergiaDash();
        energia.iniciar();
        energia.actualizar(0.3f);
        assertEquals(60.78f, energia.getEnergia(), 0.001f);
        energia.recuperar(200f);
        assertEquals(100f, energia.getEnergia(), 0f);
        energia.recuperar(-10f);
        assertEquals(100f, energia.getEnergia(), 0f);
    }

    @Test
    public void movimientoRapidoNoDependeDeLosFrames() {
        EnergiaDash corto = new EnergiaDash();
        EnergiaDash largo = new EnergiaDash();
        corto.iniciar();
        largo.iniciar();
        float duracion = 0f;
        for (int i = 0; i < 60; i++) duracion += corto.actualizar(1f / 60f);
        float duracionLarga = largo.actualizar(1f);
        assertEquals(170f, duracion * EnergiaDash.VELOCIDAD, 0.01f);
        assertEquals(duracion, duracionLarga, 0.001f);
        assertEquals(corto.getEnergia(), largo.getEnergia(), 0.001f);
    }

    @Test
    public void cambioDeRondaNoRellenaEnergiaNiEliminaEspera() {
        EnergiaDash energia = new EnergiaDash();
        energia.iniciar();
        energia.actualizar(0.05f);
        energia.detener();
        assertFalse(energia.estaActivo());
        assertFalse(energia.protegeEsteFrame());
        assertEquals(60f, energia.getEnergia(), 0.001f);
        assertFalse(energia.puedeUsar());
        energia.actualizar(0.5f);
        assertTrue(energia.puedeUsar());
        assertEquals(100f, new EnergiaDash().getEnergia(), 0f);
    }

    @Test
    public void cargaExpiraYPermiteRecogidaSoloMientrasEstaActiva() {
        CargaEnergia carga = new CargaEnergia(100f, 100f, 20f);
        Rectangle nave = new Rectangle(90f, 90f, 45f, 45f);
        assertTrue(carga.toca(nave));
        assertFalse(carga.toca(new Rectangle(500f, 500f, 45f, 45f)));
        carga.actualizar(7f);
        assertTrue(carga.estaActiva());
        carga.actualizar(1f);
        assertFalse(carga.toca(nave));
        assertFalse(carga.estaActiva());
        assertEquals(20f, carga.getCantidad(), 0f);
    }
}
