package puppy.code;

import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.*;
import org.junit.Test;

public class AlienDivisibleTest extends EntornoGdxPrueba {
    private void matar(Enemigo enemigo) { while (enemigo.estaActivo()) enemigo.recibirImpacto(); }

    @Test public void soloSeDivideAlMorirUnaVezYLasHijasNoSeDividen() {
        AlienDivisible madre = new AlienDivisible(500f, 400f, 100f, textura);
        assertTrue(madre.dividir().isEmpty());
        for (int i = 0; i < AlienDivisible.RESISTENCIA-1; i++) madre.recibirImpacto();
        assertTrue(madre.estaActivo());
        madre.recibirImpacto();
        List<AlienDivisible> hijas = madre.dividir();
        assertEquals(2, hijas.size());
        assertTrue(madre.dividir().isEmpty());
        for (AlienDivisible hija : hijas) {
            assertTrue(hija.esFragmento());
            assertEquals(28f, hija.getArea().width, 0f);
            hija.recibirImpacto(); assertTrue(hija.estaActivo());
            hija.recibirImpacto(); assertFalse(hija.estaActivo());
            assertTrue(hija.dividir().isEmpty());
        }
    }

    @Test public void salenExpulsadasFrenanYLuegoPersiguen() {
        AlienDivisible madre = new AlienDivisible(500f, 400f, 100f, textura);
        matar(madre);
        List<AlienDivisible> hijas = madre.dividir();
        AlienDivisible izq = hijas.get(0), der = hijas.get(1);
        float xIzq = izq.getArea().x, xDer = der.getArea().x;
        izq.actualizar(0.05f, 1200f, 800f, 1100f, 100f);
        der.actualizar(0.05f, 1200f, 800f, 100f, 100f);
        float primerTramo = xIzq - izq.getArea().x;
        assertTrue(primerTramo > 15f);
        assertTrue(der.getArea().x > xDer + 15f);
        for (int i = 0; i < 6; i++) izq.actualizar(0.05f, 1200f, 800f, 1100f, 100f);
        float antes = izq.getArea().x;
        izq.actualizar(0.05f, 1200f, 800f, 1100f, 100f);
        assertTrue(antes - izq.getArea().x < primerTramo);
        izq.actualizar(0.05f, 1200f, 800f, 1100f, 100f);
        antes = izq.getArea().x;
        izq.actualizar(0.05f, 1200f, 800f, 1100f, 100f);
        assertTrue(izq.getArea().x > antes);
    }

    @Test public void divisionEnBordesMantieneHijasDentroYSinSolaparse() {
        for (float x : new float[]{0f, 1152f}) {
            AlienDivisible madre = new AlienDivisible(x, 790f, 100f, textura);
            matar(madre);
            List<AlienDivisible> hijas = madre.dividir();
            assertFalse(hijas.get(0).getArea().overlaps(hijas.get(1).getArea()));
            for (AlienDivisible hija : hijas) {
                assertTrue(hija.getArea().x >= 0f);
                assertTrue(hija.getArea().x + 28f <= 1200f);
                assertTrue(hija.getArea().y + 28f <= 800f);
            }
        }
    }

    @Test public void impulsoRebotaHaciaDentroEnLosBordes() {
        for (float x : new float[]{0f, 1152f}) {
            AlienDivisible madre = new AlienDivisible(x, 400f, 100f, textura);
            matar(madre);
            List<AlienDivisible> hijas = madre.dividir();
            AlienDivisible hija = hijas.get(x == 0f ? 0 : 1);
            float antes = hija.getArea().x;
            hija.actualizar(0.05f, 1200f, 800f, 600f, 100f);
            assertTrue(x == 0f ? hija.getArea().x > antes : hija.getArea().x < antes);
        }
    }

    @Test public void oleadasPreservanRojasYCantidadYAgreganMoradas() {
        for (int ronda : new int[]{1,2,3,4,5,6,7,9,10}) {
            List<Enemigo> enemigos = new ArrayList<>();
            LlegadaNaves llegada = new LlegadaNaves(ronda, new Random(25));
            for (int i = 0; i < 20; i++) llegada.actualizar(3f, enemigos, textura);
            int moradas=0,rojas=0;
            for (Enemigo enemigo : enemigos) {
                if (enemigo instanceof AlienDivisible) moradas++;
                if (enemigo instanceof AlienExplosivo) rojas++;
            }
            boolean jefe = SectorGalactico.desdeRonda(ronda).esRondaDeJefe(ronda);
            assertEquals(jefe ? 0 : AlienDivisible.cantidadEnRonda(ronda),moradas);
            assertEquals(jefe ? 0 : AlienExplosivo.cantidadEnRonda(ronda),rojas);
            assertEquals(OleadaNaves.cantidadInicial(ronda),enemigos.size());
        }
    }

    private void poner(PantallaJuego pantalla, String campo, Object valor) throws Exception {
        Field f=PantallaJuego.class.getDeclaredField(campo); f.setAccessible(true); f.set(pantalla,valor);
    }

    @Test public void resolverMuerteRealCreaDosHijasYLaRondaLasEspera() throws Exception {
        Class<?> tipo=Class.forName("sun.misc.Unsafe");
        Field u=tipo.getDeclaredField("theUnsafe");u.setAccessible(true);
        PantallaJuego pantalla=(PantallaJuego)tipo.getMethod("allocateInstance",Class.class)
                .invoke(u.get(null),PantallaJuego.class);
        AlienDivisible madre=new AlienDivisible(500f,400f,100f,textura);
        List<Enemigo> enemigos=new ArrayList<>();enemigos.add(madre);
        poner(pantalla,"enemigos",enemigos);
        poner(pantalla,"game",new SpaceNavigation());
        poner(pantalla,"cargasEnergia",new ArrayList<CargaEnergia>());
        poner(pantalla,"baterias",new ArrayList<BateriaEnergia>());
        poner(pantalla,"random",new Random(5));
        poner(pantalla,"sector",SectorGalactico.CINTURON_DE_VELA);
        poner(pantalla,"progresion",ProgresionMejoras.obtenerInstancia());
        poner(pantalla,"explosiones",new SistemaExplosiones());
        poner(pantalla,"explosionSound",sonido);
        poner(pantalla,"bulletTexture",textura);
        matar(madre);
        Method resolver=PantallaJuego.class.getDeclaredMethod("resolverDerrota",Enemigo.class);
        resolver.setAccessible(true);resolver.invoke(pantalla,madre);resolver.invoke(pantalla,madre);
        assertEquals(2,enemigos.size());
        assertFalse(OleadaNaves.estaSuperada(enemigos));
        for (Enemigo hija : new ArrayList<>(enemigos)) {
            matar(hija);resolver.invoke(pantalla,hija);
        }
        assertTrue(enemigos.isEmpty());
        assertTrue(OleadaNaves.estaSuperada(enemigos));
    }
    @Test public void madreMasLentaYResistenteNoTransfiereElNerfDeVelocidadALasHijas() {
        AlienDivisible madre=new AlienDivisible(500,400,100,textura);
        madre.actualizar(0.1f,1200,800,1000,424);
        assertEquals(76f,(float)Math.hypot(madre.getVelocidadX(),madre.getVelocidadY()),0.001f);
        assertEquals(50f,madre.getVidaMaxima(),0f);
        madre.configurarVidaParaRonda(5);assertEquals(60f,madre.getVidaMaxima(),0f);
        matar(madre);
        List<AlienDivisible> hijas=madre.dividir();
        for(AlienDivisible hija:hijas) {
            assertEquals(24f,hija.getVidaMaxima(),0f);
            hija.actualizar(0.1f,1200,800,1000,100);
            assertEquals(420f,Math.abs(hija.getVelocidadX()),0.001f);
            hija.actualizar(0.4f,1200,800,1000,100);
            hija.actualizar(0.1f,1200,800,1000,100);
            assertEquals(110f,(float)Math.hypot(hija.getVelocidadX(),hija.getVelocidadY()),0.001f);
        }
    }
}
