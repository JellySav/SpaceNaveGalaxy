package puppy.code;

import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.Field;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.junit.Test;

public class AlienArtilleroTest extends EntornoGdxPrueba {
    private void entrar(AlienArtillero nave) {
        for (int i=0;i<100;i++) nave.actualizar(0.05f,1200f,800f,100f,100f);
        assertFalse(nave.estaEntrando());
    }

    @Test public void alturasVariablesYEntradaSuaveDesdeArriba() {
        Set<Integer> alturas=new HashSet<>();
        for(int semilla=0;semilla<20;semilla++) {
            AlienArtillero nave=new AlienArtillero(500f,812f,textura,new Random(semilla*100003L));
            nave.actualizar(0.05f,1200f,800f,0f,0f);
            assertEquals(805f,nave.getArea().y,0.01f);
            assertTrue(nave.estaEntrando());
            entrar(nave);
            float y=nave.getArea().y;
            assertTrue(y>=540f && y<=700f);
            alturas.add(Math.round(y));
        }
        assertTrue(alturas.size()>5);
    }

    @Test public void patrullaLocalConPausasSinSeguirAlJugador() {
        AlienArtillero a=new AlienArtillero(500f,812f,textura,new Random(12));
        AlienArtillero b=new AlienArtillero(500f,812f,textura,new Random(12));
        boolean pausa=false,movimiento=false;
        for(int i=0;i<1000;i++) {
            float antes=a.getArea().x;
            a.actualizar(0.05f,1200f,800f,0f,0f);
            b.actualizar(0.05f,1200f,800f,1200f,800f);
            assertEquals(a.getArea().x,b.getArea().x,0.001f);
            assertEquals(a.getArea().y,b.getArea().y,0.001f);
            assertTrue(a.getArea().x>=390f && a.getArea().x<=610f);
            if(!a.estaEntrando()) {
                if(Math.abs(a.getArea().x-antes)<0.01f) pausa=true;
                else movimiento=true;
            }
        }
        assertTrue(pausa && movimiento);
    }

    @Test public void unDisparoDirigidoEnCadaPausaYNingunoAlMoverse() throws Exception {
        AlienArtillero nave=new AlienArtillero(500f,812f,textura,new Random(3));
        List<ProyectilHostil> balas=new ArrayList<>();
        int disparosEnPausa=0,pausasConDisparo=0;
        boolean estabaEnPausa=false;
        Field campo=ProyectilHostil.class.getDeclaredField("sprite");campo.setAccessible(true);
        for(int i=0;i<1000;i++) {
            nave.actualizar(0.05f,1200f,800f,100f,100f);
            boolean enPausa=nave.estaEnPausa();
            if(enPausa && !estabaEnPausa) disparosEnPausa=0;
            int antes=balas.size();
            nave.actualizarDisparo(0.05f,100f,100f,textura,balas::add);
            if(balas.size()>antes) {
                assertTrue(enPausa);
                assertEquals(0f,nave.getVelocidadX(),0f);
                assertEquals(0f,nave.getVelocidadY(),0f);
                disparosEnPausa++;
                assertEquals(1,disparosEnPausa);
                pausasConDisparo++;
                ProyectilHostil bala=balas.get(balas.size()-1);
                Sprite sprite=(Sprite)campo.get(bala);
                float x=sprite.getX(),y=sprite.getY();
                bala.actualizar(0.1f,1200f,800f);
                assertTrue(sprite.getX()<x && sprite.getY()<y);
            }
            if(!enPausa) assertEquals(antes,balas.size());
            estabaEnPausa=enPausa;
        }
        assertTrue(pausasConDisparo>10);
        int antes=balas.size();
        while(nave.estaActivo()) nave.recibirImpacto();
        nave.actualizarDisparo(20f,100f,100f,textura,balas::add);
        assertEquals(antes,balas.size());
    }

    @Test public void esperaPreparacionYNoDisparaMasDeUnaVezEnLaMismaPausa() {
        AlienArtillero nave=new AlienArtillero(500f,812f,textura,new Random(8));
        List<ProyectilHostil> balas=new ArrayList<>();
        nave.actualizarDisparo(10f,100f,100f,textura,balas::add);
        assertTrue(balas.isEmpty());
        for(int i=0;i<100 && !nave.estaEnPausa();i++)
            nave.actualizar(0.05f,1200f,800f,100f,100f);
        assertTrue(nave.estaEnPausa());
        nave.actualizarDisparo(0f,100f,100f,textura,balas::add);
        nave.actualizarDisparo(0.1f,100f,100f,textura,balas::add);
        assertTrue(balas.isEmpty());
        nave.actualizarDisparo(0.06f,100f,100f,textura,balas::add);
        assertEquals(1,balas.size());
        nave.actualizarDisparo(1f,100f,100f,textura,balas::add);
        assertEquals(1,balas.size());
    }

    @Test public void oleadasMantienenTotalesYOtrasVariantes() {
        for(int ronda:new int[]{1,2,3,4,5,6,7,8,9,10,12,13}) {
            List<Enemigo> enemigos=new ArrayList<>();
            LlegadaNaves llegada=new LlegadaNaves(ronda,new Random(ronda));
            for(int i=0;i<20;i++)llegada.actualizar(3f,enemigos,textura);
            int naranjas=0,rojas=0,moradas=0;
            for(Enemigo e:enemigos) {
                if(e instanceof AlienArtillero)naranjas++;
                if(e instanceof AlienExplosivo)rojas++;
                if(e instanceof AlienDivisible)moradas++;
            }
            boolean jefe=SectorGalactico.desdeRonda(ronda).esRondaDeJefe(ronda);
            assertEquals(jefe?0:AlienArtillero.cantidadEnRonda(ronda),naranjas);
            assertEquals(jefe?0:AlienExplosivo.cantidadEnRonda(ronda),rojas);
            assertEquals(jefe?0:AlienDivisible.cantidadEnRonda(ronda),moradas);
            assertEquals(OleadaNaves.cantidadInicial(ronda),enemigos.size());
        }
    }

    @Test public void segundaRondaIncluyeOnceVerdesUnaRojaYTresNaranjas() {
        List<Enemigo> enemigos=new ArrayList<>();
        LlegadaNaves llegada=new LlegadaNaves(2,new Random(12));
        int verdes=0,naranjas=0,rojas=0;
        for(int i=0;i<8;i++) llegada.actualizar(3f,enemigos,textura);
        assertFalse(llegada.hayPendientes());
        assertEquals(15,enemigos.size());
        for(Enemigo e:enemigos) {
            if(e instanceof AlienArtillero) naranjas++;
            else if(e instanceof AlienExplosivo) rojas++;
            else { assertEquals(AlienEnemigo.class,e.getClass()); verdes++; }
        }
        assertEquals(11,verdes);assertEquals(1,rojas);
        assertEquals(3,naranjas);
    }

    @Test public void tieneCincoDeVidaYCuentaParaCompletarLaRonda() {
        AlienArtillero nave=new AlienArtillero(500f,812f,textura,new Random(1));
        List<Enemigo> enemigos=Arrays.asList(nave);
        assertFalse(OleadaNaves.estaSuperada(enemigos));
        for(int i=0;i<4;i++){nave.recibirImpacto();assertTrue(nave.estaActivo());}
        nave.recibirImpacto();assertTrue(OleadaNaves.estaSuperada(enemigos));
    }

    @Test public void lateralesPatrullanVerticalmenteSinSubirNiSeguirAlJugador() {
        for(float x:new float[]{-60f,1212f}) {
            AlienArtillero a=new AlienArtillero(x,300f,textura,new Random(20));
            AlienArtillero b=new AlienArtillero(x,300f,textura,new Random(20));
            boolean movimiento=false,pausa=false;
            for(int i=0;i<1000;i++) {
                a.actualizar(0.05f,1200f,800f,0f,0f);
                b.actualizar(0.05f,1200f,800f,1200f,800f);
                assertEquals(a.getArea().x,b.getArea().x,0.001f);
                assertEquals(a.getArea().y,b.getArea().y,0.001f);
                if(!a.estaEntrando()) {
                    assertTrue(x<0f ? a.getArea().x>=96f && a.getArea().x<=176f
                            : a.getArea().x>=976f && a.getArea().x<=1056f);
                    assertTrue(a.getArea().y>=190f && a.getArea().y<=410f);
                    assertEquals(0f,a.getVelocidadX(),0.001f);
                    if(a.estaEnPausa())pausa=true;
                    if(Math.abs(a.getVelocidadY())>0.01f)movimiento=true;
                }
            }
            assertTrue(movimiento && pausa);
        }
    }

    @Test public void lateralesDisparanUnaVezPorPausaYNuncaDuranteEntradaOMovimiento() {
        for(float x:new float[]{-60f,1212f}) {
            AlienArtillero nave=new AlienArtillero(x,300f,textura,new Random(20));
            List<ProyectilHostil> balas=new ArrayList<>();
            boolean vioEntrada=false,vioMovimiento=false;
            int pausasConDisparo=0,disparosPausa=0;boolean estabaEnPausa=false;
            nave.actualizarDisparo(10f,600f,100f,textura,balas::add);assertTrue(balas.isEmpty());
            for(int i=0;i<1000;i++) {
                nave.actualizar(0.05f,1200f,800f,600f,100f);
                boolean enPausa=nave.estaEnPausa();
                if(enPausa && !estabaEnPausa)disparosPausa=0;
                if(nave.estaEntrando())vioEntrada=true;
                else if(!enPausa)vioMovimiento=true;
                int antes=balas.size();nave.actualizarDisparo(0.05f,600f,100f,textura,balas::add);
                if(balas.size()>antes) {
                    assertTrue(enPausa);assertEquals(0f,nave.getVelocidadX(),0f);
                    assertEquals(0f,nave.getVelocidadY(),0f);
                    disparosPausa++;assertEquals(1,disparosPausa);pausasConDisparo++;
                }
                if(!enPausa)assertEquals(antes,balas.size());
                estabaEnPausa=enPausa;
            }
            assertTrue(vioEntrada && vioMovimiento);assertTrue(pausasConDisparo>10);
            int antes=balas.size();while(nave.estaActivo())nave.recibirImpacto();
            nave.actualizarDisparo(10f,600f,100f,textura,balas::add);assertEquals(antes,balas.size());
        }
    }

    @Test public void naranjasEnfrentadasDesbloqueanPausanDisparanYSeSeparan() throws Exception {
        for(boolean lateral:new boolean[]{false,true}) {
            AlienArtillero a=new AlienArtillero(lateral?-60f:500f,lateral?300f:812f,textura,new Random(20));
            AlienArtillero b=new AlienArtillero(lateral?-60f:548f,lateral?348f:812f,textura,new Random(20));
            Field altura=AlienArtillero.class.getDeclaredField("altura");altura.setAccessible(true);
            Field entradaX=AlienArtillero.class.getDeclaredField("entradaX");entradaX.setAccessible(true);
            float x=lateral?entradaX.getFloat(a):500f;
            float y=lateral?300f:altura.getFloat(a);
            a.desplazar(x-a.getArea().x,y-a.getArea().y,1200f,800f);
            b.desplazar((lateral?x:548f)-b.getArea().x,(lateral?348f:y)-b.getArea().y,1200f,800f);
            for(AlienArtillero nave:Arrays.asList(a,b)) {
                Field f=AlienArtillero.class.getDeclaredField("entrando");f.setAccessible(true);f.setBoolean(nave,false);
                f=AlienArtillero.class.getDeclaredField("moviendo");f.setAccessible(true);f.setBoolean(nave,true);
                f=AlienArtillero.class.getDeclaredField("destinoPatrulla");f.setAccessible(true);
                f.setFloat(nave,nave==a?(lateral?410f:610f):(lateral?238f:438f));
            }
            List<Enemigo> naves=Arrays.asList(a,b);ColisionesNaves colisiones=new ColisionesNaves();
            List<ProyectilHostil> balasA=new ArrayList<>(),balasB=new ArrayList<>();
            boolean pausaA=false,pausaB=false,separadas=false;
            for(int i=0;i<160;i++) {
                a.actualizar(0.05f,1200f,800f,600f,100f);b.actualizar(0.05f,1200f,800f,600f,100f);
                colisiones.resolver(naves,1200f,800f);
                assertFalse(a.getArea().overlaps(b.getArea()));
                pausaA|=a.estaEnPausa();pausaB|=b.estaEnPausa();
                a.actualizarDisparo(0.05f,600f,100f,textura,balasA::add);
                b.actualizarDisparo(0.05f,600f,100f,textura,balasB::add);
                float distancia=lateral?Math.abs(a.getArea().y-b.getArea().y):Math.abs(a.getArea().x-b.getArea().x);
                if(distancia>65f)separadas=true;
            }
            assertTrue(pausaA && pausaB);assertTrue(separadas);
            assertFalse(balasA.isEmpty());assertFalse(balasB.isEmpty());
        }
    }
}
