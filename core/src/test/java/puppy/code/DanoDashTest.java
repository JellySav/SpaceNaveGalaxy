package puppy.code;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import com.badlogic.gdx.Input;

public class DanoDashTest extends EntornoGdxPrueba {
    @Before public void iniciar() { ProgresionMejoras.obtenerInstancia().reiniciar(); }
    @After public void limpiar() { ProgresionMejoras.obtenerInstancia().reiniciar(); }

    private void dash(Nave4 nave, float delta) {
        pulsadas.add(Input.Keys.D);
        nuevas.add(Input.Keys.SHIFT_LEFT);
        nave.actualizar(null, delta);
        nuevas.clear();
    }

    @Test public void atraviesaEnemigoEntreCuadrosYGolpeaSoloUnaVezPorDash() {
        Nave4 nave = nave(100, 100);
        AlienEnemigo enemigo = new AlienEnemigo(160f, 100f, 1f, 5, textura);
        dash(nave, 0.17f);
        assertFalse(nave.getArea().overlaps(enemigo.getArea()));
        assertTrue(nave.golpearConDash(enemigo));
        assertEquals(0.6f, enemigo.getFraccionResistencia(), 0.001f);
        assertFalse(nave.golpearConDash(enemigo));
        assertEquals(3, nave.getVidas());
        nave.actualizar(null, 0.3f);
        assertFalse(nave.golpearConDash(enemigo));
        pulsadas.clear();
        pulsadas.add(Input.Keys.A);
        nuevas.add(Input.Keys.SHIFT_LEFT);
        nave.actualizar(null, 0.17f);
        assertTrue(nave.golpearConDash(enemigo));
        assertEquals(0.2f, enemigo.getFraccionResistencia(), 0.001f);
    }

    @Test public void afectaVariosObjetivosPeroNoLosQueQuedanFueraDelRecorrido() {
        Nave4 nave = nave(100, 100);
        AlienExplosivo roja = new AlienExplosivo(160f, 100f, 48f, 100f, 3, textura);
        JefeSector jefe = new JefeSector(230f, 100f, 100f, 30f, 200,
                SectorGalactico.NEBULOSA_DE_ION, textura, textura);
        AlienEnemigo lejos = new AlienEnemigo(160f, 300f, 1f, 5, textura);
        dash(nave, 0.17f);
        assertTrue(nave.golpearConDash(roja));
        assertTrue(roja.estaActivo());
        assertTrue(nave.golpearConDash(jefe));
        assertEquals(0.99f, jefe.getFraccionResistencia(), 0.001f);
        assertFalse(nave.golpearConDash(lejos));
        assertEquals(1f, lejos.getFraccionResistencia(), 0f);
    }

    @Test public void movimientoNormalYReparacionNoHacenDanoDeDash() {
        Nave4 nave = nave(100, 100);
        AlienEnemigo enemigo = new AlienEnemigo(100f, 100f, 1f, 5, textura);
        nave.actualizar(null, 0.016f);
        assertFalse(nave.golpearConDash(enemigo));
        nave.setReparando(true);
        dash(nave, 0.05f);
        assertFalse(nave.golpearConDash(enemigo));
        assertEquals(1f, enemigo.getFraccionResistencia(), 0f);
    }

    @Test public void proyectilSobreviveAlDashYDespuesPuedeDanar() throws Exception {
        Nave4 nave = nave(100, 100);
        dash(nave, 0.05f);
        ProyectilHostil proyectil = new ProyectilHostil(160f, 110f, 1f, 0f, 50f, textura);
        List<ProyectilHostil> balas = new ArrayList<>();
        balas.add(proyectil);
        Class<?> tipo = Class.forName("sun.misc.Unsafe");
        Field unsafe = tipo.getDeclaredField("theUnsafe");
        unsafe.setAccessible(true);
        PantallaJuego pantalla = (PantallaJuego) tipo.getMethod("allocateInstance", Class.class)
                .invoke(unsafe.get(null), PantallaJuego.class);
        for (String nombre : new String[]{"nave", "proyectilesHostiles"}) {
            Field campo = PantallaJuego.class.getDeclaredField(nombre);
            campo.setAccessible(true);
            campo.set(pantalla, nombre.equals("nave") ? nave : balas);
        }
        Method actualizar = PantallaJuego.class.getDeclaredMethod("actualizarProyectilesHostiles", float.class);
        actualizar.setAccessible(true);
        actualizar.invoke(pantalla, 0.01f);
        assertEquals(1, balas.size());
        assertTrue(proyectil.estaActivo());
        assertEquals(3, nave.getVidas());
        nave.getEnergiaDash().detener();
        actualizar.invoke(pantalla, 0.01f);
        assertTrue(balas.isEmpty());
        assertEquals(2, nave.getVidas());
    }
}
