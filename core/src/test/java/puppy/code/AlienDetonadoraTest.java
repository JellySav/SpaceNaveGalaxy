package puppy.code;

import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.*;
import org.junit.Test;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AlienDetonadoraTest extends EntornoGdxPrueba {
    @Test public void persigueMasLentoQueLaRojaYConservaTreintaDeVida() {
        AlienDetonadora nueva=new AlienDetonadora(100,100,48,200,3,textura);
        AlienExplosivo roja=new AlienExplosivo(100,100,48,200,3,textura);
        nueva.actualizar(0.1f,1200,800,600,124);roja.actualizar(0.1f,1200,800,600,124);
        assertEquals(116f,nueva.getArea().x,0.001f);assertEquals(118f,roja.getArea().x,0.001f);
        assertEquals(100f,nueva.getArea().y,0.001f);assertEquals(30f,nueva.getVidaActual(),0f);
    }
    @Test public void detonaConElMismoRadioYDanoYDisparaDiezSoloAlExplotar() throws Exception {
        AlienDetonadora nueva=new AlienDetonadora(476,376,48,100,3,textura);nueva.recibirDano(30f);
        SistemaExplosiones sistema=new SistemaExplosiones();sistema.registrarMuerte(nueva);
        Nave4 jugador=nave(480,380);DronEscolta apoyo=new DronEscolta(textura);apoyo.actualizar(0.2f,500,400,Collections.emptyList(),textura,b->{});
        Enemigo vecino=new AlienEnemigo(550,400,0,8,textura),fuera=new AlienEnemigo(800,400,0,8,textura);
        List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(vecino,fuera));List<ProyectilHostil> balas=new ArrayList<>();
        sistema.actualizar(0.59f,enemigos,jugador,apoyo,e->fail(),()->{},textura,balas::add);
        assertTrue(balas.isEmpty());assertEquals(3,jugador.getVidas());
        sistema.actualizar(0.02f,enemigos,jugador,apoyo,e->fail(),()->{},textura,balas::add);
        assertEquals(10,balas.size());assertEquals(2,jugador.getVidas());assertEquals(20f,vecino.getVidaActual(),0f);assertEquals(80f,fuera.getVidaActual(),0f);
        assertEquals(3f,apoyo.getTiempoDesactivado(),0f);
        Field sprite=ProyectilHostil.class.getDeclaredField("sprite");sprite.setAccessible(true);
        Set<Integer> angulos=new HashSet<>();
        for(ProyectilHostil bala:balas) {
            Sprite s=(Sprite)sprite.get(bala);float x=s.getX(),y=s.getY();bala.actualizar(0.1f,1200,800);
            float dx=s.getX()-x,dy=s.getY()-y;assertEquals(22f,(float)Math.hypot(dx,dy),0.001f);
            angulos.add(Math.round((float)Math.toDegrees(Math.atan2(dy,dx))/36f));
        }
        assertEquals(10,angulos.size());
        sistema.actualizar(0.1f,enemigos,jugador,apoyo,e->fail(),()->{},textura,balas::add);assertEquals(10,balas.size());assertEquals(20f,vecino.getVidaActual(),0f);
    }
    @Test public void cadenaConRojaEsperaElAvisoAntesDeEmitirElAnillo() {
        AlienExplosivo roja=new AlienExplosivo(476,376,48,100,3,textura);roja.recibirDano(30);
        AlienDetonadora nueva=new AlienDetonadora(530,376,48,100,3,textura);
        List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(nueva));SistemaExplosiones sistema=new SistemaExplosiones();sistema.registrarMuerte(roja);
        List<ProyectilHostil> balas=new ArrayList<>();Nave4 jugador=nave(900,100);
        java.util.function.Consumer<Enemigo> muerte=e->{enemigos.remove(e);sistema.registrarMuerte(e);};
        sistema.actualizar(0.61f,enemigos,jugador,null,muerte,()->{},textura,balas::add);
        assertTrue(nueva.estaDestruido());assertTrue(balas.isEmpty());
        sistema.actualizar(0.59f,enemigos,jugador,null,muerte,()->{},textura,balas::add);assertTrue(balas.isEmpty());
        sistema.actualizar(0.02f,enemigos,jugador,null,muerte,()->{},textura,balas::add);assertEquals(10,balas.size());
    }
    @Test public void rondasNueveDiezOnceAgreganDosTresYCincoSinReemplazarOtros() {
        for(int[] datos:new int[][]{{9,32,2},{10,36,3},{11,41,5}}) {
            List<Enemigo> enemigos=new ArrayList<>();LlegadaNaves llegada=new LlegadaNaves(datos[0],new Random(10));
            for(int i=0;i<20;i++)llegada.actualizar(3f,enemigos,textura);
            assertEquals(datos[1],enemigos.size());assertEquals(datos[2],enemigos.stream().filter(e->e instanceof AlienDetonadora).count());
        }
        assertEquals(0,AlienDetonadora.cantidadEnRonda(8));assertEquals(0,AlienDetonadora.cantidadEnRonda(12));assertEquals(6,AlienDetonadora.cantidadEnRonda(13));
    }
    @Test public void tercerJefeGeneraDosCadaTresComaCincoSegundosIndependientesYALosLados() throws Exception {
        JefeSector jefe=new JefeSector(400,550,150,30,260,SectorGalactico.FRONTERA_DE_KHEPRI,textura,textura);
        Field random=JefeSector.class.getDeclaredField("randomRefuerzos");random.setAccessible(true);((Random)random.get(jefe)).setSeed(20);
        List<AlienEnemigo> naves=new ArrayList<>();Set<Class<?>> tipos=new HashSet<>();boolean repetida=false;
        jefe.actualizarRefuerzos(3.4f,200,6,textura,naves::add);assertTrue(naves.isEmpty());
        jefe.actualizarRefuerzos(0.11f,200,6,textura,naves::add);assertEquals(2,naves.size());
        for(int pareja=0;pareja<100;pareja++) {
            if(pareja>0)jefe.actualizarRefuerzos(3.5f,200,6,textura,naves::add);
            assertEquals((pareja+1)*2,naves.size());
            AlienEnemigo izquierda=naves.get(pareja*2),derecha=naves.get(pareja*2+1);
            assertTrue(izquierda.getArea().x+48f<jefe.getArea().x);assertTrue(derecha.getArea().x>jefe.getArea().x+150f);
            tipos.add(izquierda.getClass());tipos.add(derecha.getClass());if(izquierda.getClass()==derecha.getClass())repetida=true;
        }
        assertEquals(5,tipos.size());assertTrue(repetida);jefe.recibirDano(2600f);
        jefe.actualizarRefuerzos(40f,200,6,textura,naves::add);assertEquals(200,naves.size());
    }
}
