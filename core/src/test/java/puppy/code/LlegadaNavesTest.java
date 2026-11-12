package puppy.code;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Test;

public class LlegadaNavesTest extends EntornoGdxPrueba {
    @Test public void posicionesAleatoriasSinLadosFijosNiSolapamientos() {
        boolean[] vioIzquierda = new boolean[2];
        boolean[] vioDerecha = new boolean[2];
        boolean mismaMitad = false;
        for (int semilla = 0; semilla < 100; semilla++) {
            LlegadaNaves llegada = new LlegadaNaves(5, new Random(semilla * 100003L));
            List<Enemigo> enemigos = new ArrayList<>();
            for (int pareja = 0; pareja < 5; pareja++) {
                enemigos.clear();
                llegada.actualizar(3f, enemigos, textura);
                assertEquals(2, enemigos.size());
                for (int i = 0; i < 2; i++) {
                    float x = enemigos.get(i).getArea().x;
                    assertTrue(x >= 60f && x + 48f <= MundoJuego.ANCHO - 60f);
                    if (x < MundoJuego.ANCHO / 2f) vioIzquierda[i] = true;
                    else vioDerecha[i] = true;
                }
                float a = enemigos.get(0).getArea().x;
                float b = enemigos.get(1).getArea().x;
                assertTrue(Math.abs(a - b) >= 71.99f);
                if ((a < MundoJuego.ANCHO / 2f) == (b < MundoJuego.ANCHO / 2f))
                    mismaMitad = true;
            }
        }
        assertTrue(vioIzquierda[0] && vioDerecha[0]);
        assertTrue(vioIzquierda[1] && vioDerecha[1]);
        assertTrue(mismaMitad);
    }

    @Test public void rojaTempranaYOtrasAntesDelFinalConOrdenVariable() {
        boolean[] posicionesRojas = new boolean[42];
        for (int ronda : new int[]{2, 3, 5, 9, 11, 13}) {
            for (int semilla = 0; semilla < 50; semilla++) {
                List<Enemigo> enemigos = new ArrayList<>();
                LlegadaNaves llegada = new LlegadaNaves(ronda, new Random(semilla * 100003L));
                for (int i = 0; i < 20; i++) llegada.actualizar(3f, enemigos, textura);
                assertFalse(enemigos.get(0) instanceof AlienExplosivo);
                assertFalse(enemigos.get(1) instanceof AlienExplosivo);
                int grupo = ronda >= 6 ? 3 : 2;
                int primerasRojas = 0;
                for (int i=0;i<grupo*2;i++) if(enemigos.get(i) instanceof AlienExplosivo) {
                    assertTrue(i>=grupo);primerasRojas++;
                }
                assertEquals(1,primerasRojas);
                int rojas = 0;
                for (int i = 0; i < enemigos.size(); i++) {
                    if (enemigos.get(i) instanceof AlienExplosivo) {
                        rojas++;
                        posicionesRojas[i] = true;
                        assertTrue(i < enemigos.size() - grupo);
                    }
                }
                assertEquals(AlienExplosivo.cantidadEnRonda(ronda), rojas);
            }
        }
        assertTrue(posicionesRojas[2] && posicionesRojas[3]);
        int variantes = 0;
        for (int i = 4; i < 16; i++) if (posicionesRojas[i]) variantes++;
        assertTrue(variantes > 2);
    }

    @Test public void aparecenEnParejasConEsperaYNoSeAdelantaLaRonda() {
        LlegadaNaves llegada = new LlegadaNaves(1, new Random(1));
        List<Enemigo> enemigos = new ArrayList<>();
        assertTrue(llegada.hayPendientes());
        llegada.actualizar(0f, enemigos, textura);
        llegada.actualizar(0.5f, enemigos, textura);
        assertTrue(enemigos.isEmpty());
        llegada.actualizar(0.5f, enemigos, textura);
        assertEquals(2, enemigos.size());
        enemigos.clear(); // El jugador puede matar la pareja antes de la siguiente.
        assertTrue(llegada.hayPendientes());
        llegada.actualizar(2.5f, enemigos, textura);
        assertTrue(enemigos.isEmpty());
        llegada.actualizar(0.5f, enemigos, textura);
        assertEquals(2, enemigos.size());
        llegada.actualizar(3f, enemigos, textura);
        assertEquals(4, enemigos.size());
        assertTrue(llegada.hayPendientes());
        llegada.actualizar(3f, enemigos, textura);
        assertEquals(6, enemigos.size());
        assertFalse(llegada.hayPendientes());
        llegada.actualizar(30f, enemigos, textura);
        assertEquals(6, enemigos.size());
    }

    @Test public void conservaTotalesTiposYResistencia() {
        for (int ronda : new int[]{1, 2, 4, 6, 8, 9, 11}) {
            LlegadaNaves llegada = new LlegadaNaves(ronda, new Random(3));
            List<Enemigo> enemigos = new ArrayList<>();
            for (int i = 0; i < 20; i++) llegada.actualizar(3f, enemigos, textura);
            assertEquals(OleadaNaves.cantidadInicial(ronda), enemigos.size());
            int rojas = 0;
            for (Enemigo enemigo : enemigos) {
                assertTrue(enemigo.getArea().y >= MundoJuego.ALTO
                        || enemigo.getArea().x + enemigo.getArea().width <= 0f
                        || enemigo.getArea().x >= MundoJuego.ANCHO);
                int vida = enemigo instanceof AlienArtillero ? AlienArtillero.RESISTENCIA
                        : enemigo instanceof AlienDivisible ? AlienDivisible.RESISTENCIA
                        : enemigo instanceof AlienDetonadora ? AlienDetonadora.RESISTENCIA
                        : enemigo instanceof AlienExplosivo ? AlienExplosivo.RESISTENCIA
                        : AlienEnemigo.resistenciaEnRonda(ronda);
                if (enemigo instanceof AlienExplosivo) rojas++;
                assertEquals(vida*10f*(ronda>=5?1.2f:1f),enemigo.getVidaMaxima(),0.001f);
                int impactos=(int)Math.ceil(vida*(ronda>=5?1.2f:1f)-0.0001f);
                for (int impacto = 1; impacto < impactos; impacto++) {
                    enemigo.recibirImpacto();
                    assertTrue(enemigo.estaActivo());
                }
                enemigo.recibirImpacto();
                assertFalse(enemigo.estaActivo());
            }
            assertEquals(AlienExplosivo.cantidadEnRonda(ronda), rojas);
        }
    }

    @Test public void rondasDeJefeNoGeneranOleadasExtra() {
        for (int ronda : new int[]{4, 8, 12, 16, 20, 24}) {
            LlegadaNaves llegada = new LlegadaNaves(ronda, new Random(2));
            List<Enemigo> enemigos = new ArrayList<>();
            llegada.actualizar(100f, enemigos, textura);
            assertFalse(llegada.hayPendientes());
            assertTrue(enemigos.isEmpty());
        }
    }

    @Test public void entraSinSaltarAlBordeYDespuesPersigue() {
        AlienEnemigo alien = new AlienEnemigo(100f, MundoJuego.ALTO + 12f, 100f, 3, textura);
        AlienEnemigo vecino = new AlienEnemigo(100f, MundoJuego.ALTO + 12f, 100f, 3, textura);
        ColisionesNaves colisiones = new ColisionesNaves();
        List<Enemigo> enemigos = Arrays.asList(alien, vecino);
        alien.actualizar(0.1f, MundoJuego.ANCHO, MundoJuego.ALTO, 900f, 100f);
        colisiones.resolver(enemigos, MundoJuego.ANCHO, MundoJuego.ALTO);
        assertEquals(MundoJuego.ALTO + 2f, alien.getArea().y, 0.01f);
        assertEquals(100f, alien.getArea().x, 0.01f);
        assertTrue(alien.estaEntrando());
        for (int i = 0; i < 8; i++)
            alien.actualizar(0.1f, MundoJuego.ANCHO, MundoJuego.ALTO, 900f, 100f);
        assertFalse(alien.estaEntrando());
        assertTrue(alien.getArea().x > 100f);
        assertTrue(alien.getArea().y + alien.getArea().height <= MundoJuego.ALTO);
    }

    @Test public void desdeRondaSeisEntranTresSeparadosConEsperaYTotalesMayores() {
        int[][] composiciones = {{1,8,0,0,0,0},{2,11,1,0,3,0},{3,10,2,0,4,0},{5,9,3,3,3,0},
                {6,12,3,3,3,0},{7,14,3,3,4,0},{9,18,4,4,4,2},{10,20,4,4,5,3},
                {11,20,5,5,6,5},{13,21,6,6,6,6},{14,21,6,6,6,6},{15,21,6,6,6,6},{50,21,6,6,6,6}};
        for(int[] c:composiciones)for(int semilla=0;semilla<30;semilla++) {
            LlegadaNaves llegada=new LlegadaNaves(c[0],new Random(semilla));
            List<Enemigo> enemigos=new ArrayList<>();int[] tipos=new int[5];
            int grupo=c[0]>=6?3:2;
            while(llegada.hayPendientes()) {
                enemigos.clear();llegada.actualizar(3f,enemigos,textura);
                assertTrue(enemigos.size()<=grupo);
                if(llegada.hayPendientes())assertEquals(grupo,enemigos.size());
                for(int i=0;i<enemigos.size();i++) {
                    Enemigo e=enemigos.get(i);
                    boolean arriba = e.getArea().y >= MundoJuego.ALTO;
                    float posicion = arriba ? e.getArea().x : e.getArea().y;
                    assertTrue(posicion>=60f && posicion <= (arriba ? 1092f : 692f));
                    for(int j=0;j<i;j++) {
                        Enemigo anterior=enemigos.get(j);
                        assertEquals(arriba, anterior.getArea().y>=MundoJuego.ALTO);
                        float otra=arriba ? anterior.getArea().x : anterior.getArea().y;
                        assertTrue(Math.abs(posicion-otra)>=71.99f);
                    }
                    tipos[e instanceof AlienDetonadora?4:e instanceof AlienExplosivo?1:e instanceof AlienDivisible?2:e instanceof AlienArtillero?3:0]++;
                }
                enemigos.clear();llegada.actualizar(c[0]>=9?2.4f:2.9f,enemigos,textura);assertTrue(enemigos.isEmpty());
            }
            for(int i=0;i<5;i++)assertEquals(c[i+1],tipos[i]);
        }
    }

    @Test public void tresBordesDesdeLaSeisYPrimerasRondasSoloArriba() {
        boolean[] vistos=new boolean[3];
        for(int ronda:new int[]{1,2,3,5,6,7,9,13})for(int semilla=0;semilla<40;semilla++) {
            LlegadaNaves llegada=new LlegadaNaves(ronda,new Random(semilla*100003L));
            while(llegada.hayPendientes()) {
                List<Enemigo> grupo=new ArrayList<>();llegada.actualizar(3f,grupo,textura);
                int borde=grupo.get(0).getArea().y>=800f?0:grupo.get(0).getArea().x<0f?1:2;
                if(ronda<6)assertEquals(0,borde);else vistos[borde]=true;
                for(Enemigo e:grupo) {
                    assertEquals(borde,e.getArea().y>=800f?0:e.getArea().x<0f?1:2);
                    assertTrue(((AlienEnemigo)e).estaEntrando());
                }
            }
        }
        assertTrue(vistos[0]&&vistos[1]&&vistos[2]);
    }

    @Test public void todosLosTiposEntranSuavementeDesdeAmbosLados() {
        for(float x:new float[]{-60f,1212f})for(int tipo=0;tipo<4;tipo++) {
            AlienEnemigo nave=tipo==0?new AlienEnemigo(x,300f,100f,3,textura)
                    :tipo==1?new AlienExplosivo(x,300f,48f,100f,3,textura)
                    :tipo==2?new AlienDivisible(x,300f,100f,textura)
                    :new AlienArtillero(x,300f,textura,new Random(2));
            List<Enemigo> grupo=Arrays.asList(nave);
            ColisionesNaves colisiones=new ColisionesNaves();
            float previo=x;
            for(int i=0;i<180 && nave.estaEntrando();i++) {
                nave.actualizar(0.05f,1200f,800f,600f,100f);
                colisiones.resolver(grupo,1200f,800f);
                assertTrue(Math.abs(nave.getArea().x-previo)<=7.01f);
                previo=nave.getArea().x;
            }
            assertFalse(nave.estaEntrando());
            assertTrue(nave.getArea().x>=0f && nave.getArea().x+48f<=1200f);
            if(tipo==3)assertTrue(x<0f ? nave.getArea().x<200f : nave.getArea().x>950f);
            else {
                float antes=nave.getArea().y;
                nave.actualizar(1f,1200f,800f,600f,100f);
                assertTrue(nave.getArea().y<antes);
            }
        }
    }

    @Test public void desdeLaNueveLleganGruposCadaDosComaCincoYAntesSeMantieneTres() {
        for(int ronda:new int[]{6,9,10,11,13}) {
            LlegadaNaves llegada=new LlegadaNaves(ronda,new Random(20));List<Enemigo> naves=new ArrayList<>();
            llegada.actualizar(0.99f,naves,textura);assertTrue(naves.isEmpty());
            llegada.actualizar(0.02f,naves,textura);assertEquals(3,naves.size());naves.clear();
            llegada.actualizar(2.49f,naves,textura);assertTrue(naves.isEmpty());
            llegada.actualizar(0.02f,naves,textura);assertEquals(ronda>=9?3:0,naves.size());
        }
        assertEquals(6,AlienEnemigo.resistenciaEnRonda(8));assertEquals(7,AlienEnemigo.resistenciaEnRonda(9));assertEquals(7,AlienEnemigo.resistenciaEnRonda(50));
    }
}
