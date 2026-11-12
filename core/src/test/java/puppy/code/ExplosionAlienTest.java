package puppy.code;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.Test;
import com.badlogic.gdx.math.Rectangle;

public class ExplosionAlienTest extends EntornoGdxPrueba {
    @Test
    public void explosionHaceSesentaYLasVerdesTardiasConservanDiezDeVida() {
        List<Enemigo> enemigos = new ArrayList<>();
        for (int ronda : new int[]{1, 3, 5, 7, 20}) {
            int vida = AlienEnemigo.resistenciaEnRonda(ronda);
            AlienEnemigo verde = new AlienEnemigo(100f, 100f, 100f, vida, textura);
            assertTrue(vida >= 3 && vida <= 7);
            enemigos.add(verde);
        }
        AlienExplosivo roja = new AlienExplosivo(100f, 100f, 48f, 100f,
                AlienExplosivo.RESISTENCIA, textura);
        roja.recibirImpacto();
        roja.recibirImpacto();
        assertTrue(roja.estaActivo());
        enemigos.add(roja);
        AlienExplosivo fuente = roja(100f, 100f);
        destruir(fuente);
        SistemaExplosiones sistema = new SistemaExplosiones();
        sistema.registrarMuerte(fuente);
        List<Enemigo> bajas = new ArrayList<>();
        sistema.actualizar(0.61f, enemigos, nave(800, 600), null, bajas::add, () -> {});
        assertEquals(enemigos.size()-1, bajas.size());
        for (Enemigo enemigo : enemigos) {
            if(enemigo.getVidaMaxima()>60f){assertTrue(enemigo.estaActivo());assertEquals(10f,enemigo.getVidaActual(),0f);}
            else assertTrue(enemigo.estaDestruido());
        }
    }

    @Test
    public void explosionCausaSeisImpactosSoloANavesYConservaDanoAJefes() {
        AlienExplosivo fuente = roja(100f, 100f);
        destruir(fuente);
        AlienEnemigo verdeSeis = new AlienEnemigo(150f, 100f, 100f, 6, textura);
        AlienEnemigo verdeSiete = new AlienEnemigo(100f, 150f, 100f, 7, textura);
        AlienExplosivo otraRoja = roja(150f, 150f);
        JefeSector jefe = new JefeSector(100f, 170f, 80f, 30f, 10,
                SectorGalactico.CINTURON_DE_VELA, textura, textura);
        List<Enemigo> enemigos = new ArrayList<>(Arrays.asList(verdeSeis, verdeSiete, otraRoja, jefe));
        List<Enemigo> bajas = new ArrayList<>();
        SistemaExplosiones sistema = new SistemaExplosiones();
        sistema.registrarMuerte(fuente);
        sistema.actualizar(0.61f, enemigos, nave(800, 600), null, enemigo -> {
            assertTrue(enemigos.remove(enemigo));
            bajas.add(enemigo);
            sistema.registrarMuerte(enemigo);
        }, () -> {});
        assertTrue(verdeSeis.estaDestruido());
        assertTrue(otraRoja.estaDestruido());
        assertTrue(verdeSiete.estaActivo());
        assertEquals(1f / 7f, verdeSiete.getFraccionResistencia(), 0.001f);
        assertEquals(0.8f, jefe.getFraccionResistencia(), 0.001f);
        assertEquals(Arrays.asList(verdeSeis, otraRoja), bajas);
    }

    private AlienExplosivo roja(float x, float y) {
        return (AlienExplosivo) new EnemigoBuilder().tipo(EnemigoBuilder.Tipo.ALIEN_EXPLOSIVO)
                .posicion(x, y).velocidad(100f, 100f).resistencia(2).construir(textura);
    }

    private void destruir(Enemigo enemigo) {
        while (enemigo.estaActivo()) enemigo.recibirImpacto();
    }

    @Test
    public void avisaAntesDeDetonarYElDestelloNoVuelveADanar() {
        ExplosionAlien explosion = new ExplosionAlien(0f, 0f);
        assertFalse(explosion.actualizar(0.59f));
        assertFalse(explosion.detono());
        assertTrue(explosion.actualizar(0.02f));
        assertTrue(explosion.detono());
        assertFalse(explosion.actualizar(0.1f));
        assertFalse(explosion.termino());
        assertFalse(explosion.actualizar(0.2f));
        assertTrue(explosion.termino());
    }

    @Test
    public void alcanceEsCircularYDetectaElBordeDeLaNave() {
        ExplosionAlien explosion = new ExplosionAlien(0f, 0f);
        assertTrue(explosion.alcanza(new Rectangle(120f, 0f, 10f, 10f)));
        assertFalse(explosion.alcanza(new Rectangle(121f, 0f, 10f, 10f)));
        assertFalse(explosion.alcanza(new Rectangle(119f, 119f, 10f, 10f)));
        assertTrue(explosion.alcanza(new Rectangle(-30f, -30f, 60f, 60f)));
    }

    @Test
    public void rojaPersigueDiezPorCientoMasLentoYResisteDosImpactos() {
        AlienEnemigo verde = new AlienEnemigo(100f, 100f, 100f, 2, textura);
        AlienExplosivo roja = roja(100f, 100f);
        verde.actualizar(0.1f, 1200f, 800f, 500f, 124f);
        roja.actualizar(0.1f, 1200f, 800f, 500f, 124f);
        assertEquals(10f, verde.getArea().x - 100f, 0.01f);
        assertEquals(9f, roja.getArea().x - 100f, 0.01f);
        roja.recibirImpacto();
        assertTrue(roja.estaActivo());
        roja.recibirImpacto();
        assertTrue(roja.estaDestruido());
    }

    @Test
    public void aparicionProgresivaDesdeRondaDosSinOleadasEnJefes() {
        assertEquals(0, AlienExplosivo.cantidadEnRonda(1));
        assertEquals(1, AlienExplosivo.cantidadEnRonda(2));
        assertEquals(2, AlienExplosivo.cantidadEnRonda(3));
        assertEquals(0, AlienExplosivo.cantidadEnRonda(4));
        assertEquals(3, AlienExplosivo.cantidadEnRonda(5));
        assertEquals(3, AlienExplosivo.cantidadEnRonda(7));
        assertEquals(4, AlienExplosivo.cantidadEnRonda(9));
        assertEquals(5, AlienExplosivo.cantidadEnRonda(11));
        assertEquals(6, AlienExplosivo.cantidadEnRonda(50));
    }

    @Test
    public void cadenaRespetaOtroAvisoYDanaAlJefeSinDuplicarBajas() {
        SistemaExplosiones sistema = new SistemaExplosiones();
        AlienExplosivo primera = roja(100f, 100f);
        AlienExplosivo segunda = roja(200f, 100f);
        AlienEnemigo verde = new AlienEnemigo(260f, 100f, 100f, 2, textura);
        JefeSector jefe = new JefeSector(100f, 170f, 80f, 30f, 10,
                SectorGalactico.CINTURON_DE_VELA, textura, textura);
        AlienEnemigo lejos = new AlienEnemigo(900f, 100f, 100f, 2, textura);
        List<Enemigo> enemigos = new ArrayList<>(Arrays.asList(segunda, verde, jefe, lejos));
        List<Enemigo> bajas = new ArrayList<>();
        Consumer<Enemigo> resolver = enemigo -> {
            assertTrue(enemigos.remove(enemigo));
            bajas.add(enemigo);
            sistema.registrarMuerte(enemigo);
        };
        AtomicInteger sonidos = new AtomicInteger();
        Nave4 nave = nave(800, 600);
        destruir(primera);
        sistema.registrarMuerte(primera);
        sistema.actualizar(0.61f, enemigos, nave, null, resolver, sonidos::incrementAndGet);
        assertEquals(Arrays.asList(segunda), bajas);
        assertTrue(verde.estaActivo());
        assertEquals(0.8f, jefe.getFraccionResistencia(), 0.001f);
        sistema.actualizar(0.59f, enemigos, nave, null, resolver, sonidos::incrementAndGet);
        assertTrue(verde.estaActivo());
        sistema.actualizar(0.02f, enemigos, nave, null, resolver, sonidos::incrementAndGet);
        assertEquals(Arrays.asList(segunda, verde), bajas);
        assertEquals(0.6f, jefe.getFraccionResistencia(), 0.001f);
        assertTrue(lejos.estaActivo());
        assertEquals(2, sonidos.get());
        sistema.actualizar(1f, enemigos, nave, null, resolver, sonidos::incrementAndGet);
        assertFalse(sistema.hayPendientes());
        assertEquals(2, bajas.size());
        assertEquals(2, sonidos.get());
    }

    @Test
    public void explosionQuitaUnaVidaYDashLaEvita() {
        AlienExplosivo fuente = roja(100f, 100f);
        destruir(fuente);
        Nave4 nave = nave(100, 100);
        SistemaExplosiones sistema = new SistemaExplosiones();
        sistema.registrarMuerte(fuente);
        sistema.actualizar(0.61f, new ArrayList<>(), nave, null, e -> fail(), () -> {});
        assertEquals(2, nave.getVidas());
        nave.actualizar(null, 1f);
        sistema.actualizar(0.1f, new ArrayList<>(), nave, null, e -> fail(), () -> {});
        assertEquals(2, nave.getVidas());

        Nave4 esquivando = nave(100, 100);
        esquivando.getEnergiaDash().iniciar();
        esquivando.getEnergiaDash().actualizar(0.05f);
        SistemaExplosiones otro = new SistemaExplosiones();
        otro.registrarMuerte(fuente);
        otro.actualizar(0.61f, new ArrayList<>(), esquivando, null, e -> fail(), () -> {});
        assertEquals(3, esquivando.getVidas());
    }

    @Test
    public void explosionDesactivaDronCercanoYTresSegundosDespuesDispara() {
        DronEscolta dron = new DronEscolta(textura);
        dron.actualizar(1f, 100f, 100f, new ArrayList<>(), textura, b -> fail());
        AlienExplosivo fuente = roja(100f, 100f);
        destruir(fuente);
        SistemaExplosiones sistema = new SistemaExplosiones();
        sistema.registrarMuerte(fuente);
        List<Enemigo> objetivos = new ArrayList<>();
        sistema.actualizar(0.61f, objetivos, nave(800, 600), dron, e -> fail(), () -> {});
        assertEquals(3f, dron.getTiempoDesactivado(), 0f);
        objetivos.add(new AlienEnemigo(400f, 100f, 100f, 2, textura));
        AtomicInteger disparos = new AtomicInteger();
        dron.actualizar(2.9f, 100f, 100f, objetivos, textura, b -> disparos.incrementAndGet());
        assertTrue(dron.estaDesactivado());
        assertEquals(0, disparos.get());
        dron.actualizar(0.11f, 100f, 100f, objetivos, textura, b -> disparos.incrementAndGet());
        assertFalse(dron.estaDesactivado());
        assertEquals(1, disparos.get());
    }

    @Test
    public void explosionNoDesactivaDronFueraDelRadio() {
        DronEscolta dron = new DronEscolta(textura);
        dron.actualizar(1f, 800f, 600f, new ArrayList<>(), textura, b -> fail());
        AlienExplosivo fuente = roja(100f, 100f);
        destruir(fuente);
        SistemaExplosiones sistema = new SistemaExplosiones();
        sistema.registrarMuerte(fuente);
        sistema.actualizar(0.61f, new ArrayList<>(), nave(800, 600), dron, e -> fail(), () -> {});
        assertFalse(dron.estaDesactivado());
    }

    @Test
    public void otraExplosionRenuevaDesactivacionSinSumarla() {
        DronEscolta dron = new DronEscolta(textura);
        dron.desactivar(3f);
        dron.actualizar(1f, 100f, 100f, new ArrayList<>(), textura, b -> fail());
        assertEquals(2f, dron.getTiempoDesactivado(), 0.001f);
        dron.desactivar(3f);
        assertEquals(3f, dron.getTiempoDesactivado(), 0.001f);
    }
}
