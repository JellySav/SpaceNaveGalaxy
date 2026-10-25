package puppy.code;

import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.*;
import org.junit.Test;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;

public class DisparoDireccionalTest extends EntornoGdxPrueba {
    private float cx(Bullet bala) { Rectangle a=bala.getArea();return a.x+a.width/2f; }
    private float cy(Bullet bala) { Rectangle a=bala.getArea();return a.y+a.height/2f; }
    private PantallaJuego pantalla(List<Bullet> balas) throws Exception {
        Class<?> tipo=Class.forName("sun.misc.Unsafe");
        Field unsafe=tipo.getDeclaredField("theUnsafe");unsafe.setAccessible(true);
        PantallaJuego pantalla=(PantallaJuego)tipo.getMethod("allocateInstance",Class.class)
                .invoke(unsafe.get(null),PantallaJuego.class);
        Field campo=PantallaJuego.class.getDeclaredField("balas");campo.setAccessible(true);
        campo.set(pantalla,balas);
        campo=PantallaJuego.class.getDeclaredField("aparienciasProyectiles");campo.setAccessible(true);
        campo.set(pantalla,new AparienciasProyectiles(new com.badlogic.gdx.graphics.g2d.TextureRegion(textura),
                new com.badlogic.gdx.graphics.g2d.TextureRegion(textura)));
        return pantalla;
    }

    @Test public void armasRotanVelocidadYSeparacionEnLasCuatroDirecciones() {
        EstrategiaDisparo[] armas={new DisparoSimple(),new DisparoDoble(),new DisparoTriple()};
        for(int arma=0;arma<3;arma++) {
            for(int[] dir:new int[][]{{0,1},{0,-1},{1,0},{-1,0}}) {
                List<Bullet> balas=new ArrayList<>();
                armas[arma].disparar(600f,400f,dir[0],dir[1],textura,balas::add);
                assertEquals(arma+1,balas.size());
                for(int i=0;i<balas.size();i++) {
                    Bullet bala=balas.get(i);
                    float x=cx(bala),y=cy(bala);
                    float lateral=(x-600f)*dir[1]-(y-400f)*dir[0];
                    float desplazamiento=arma==0||arma==2&&i==0?0f:i==balas.size()-1?12f:-12f;
                    assertEquals(desplazamiento,lateral,0.01f);
                    bala.update(0.1f);
                    float dx=cx(bala)-x,dy=cy(bala)-y;
                    float avance=dx*dir[0]+dy*dir[1];
                    assertEquals(arma==2?(i==0?56f:50f):52f,avance,0.01f);
                    float apertura=dx*dir[1]-dy*dir[0];
                    assertEquals(arma==2?(i==0?0f:i==1?-13f:13f):0f,apertura,0.01f);
                    assertFalse(bala.isDestroyed());
                }
            }
        }
    }

    @Test public void wasdMueveYFlechasDisparanContinuamenteSinMoverLaNave() throws Exception {
        List<Bullet> balas=new ArrayList<>();PantallaJuego juego=pantalla(balas);
        Nave4 nave=nave(500,300);
        pulsadas.add(Input.Keys.W);pulsadas.add(Input.Keys.RIGHT);nuevas.add(Input.Keys.RIGHT);
        nave.actualizar(juego,0.05f);nuevas.clear();
        assertEquals(500f,nave.getArea().x,0.01f);
        assertEquals(313f,nave.getArea().y,0.01f);
        assertEquals(1,balas.size());
        float x=cx(balas.get(0));float y=cy(balas.get(0));
        balas.get(0).update(0.1f);
        assertEquals(x+52f,cx(balas.get(0)),0.01f);
        assertEquals(y,cy(balas.get(0)),0.01f);
        for(int i=0;i<9;i++)nave.actualizar(juego,0.05f);
        assertEquals(2,balas.size());
        pulsadas.clear();int antes=balas.size();
        nave.actualizar(juego,0.5f);assertEquals(antes,balas.size());
        assertEquals(500f,nave.getArea().x,0.01f);
    }

    @Test public void ultimaFlechaTienePrioridadSinDiagonalesYRetomaLaAnterior() throws Exception {
        List<Bullet> balas=new ArrayList<>();PantallaJuego juego=pantalla(balas);
        Nave4 nave=nave(500,300);
        pulsadas.add(Input.Keys.UP);nuevas.add(Input.Keys.UP);
        nave.actualizar(juego,0.01f);nuevas.clear();
        pulsadas.add(Input.Keys.LEFT);nuevas.add(Input.Keys.LEFT);
        nave.actualizar(juego,0.41f);nuevas.clear();
        Bullet izquierda=balas.get(1);float x=cx(izquierda),y=cy(izquierda);
        izquierda.update(0.1f);
        assertEquals(x-52f,cx(izquierda),0.01f);assertEquals(y,cy(izquierda),0.01f);
        pulsadas.remove(Input.Keys.LEFT);
        nave.actualizar(juego,0.41f);
        Bullet arriba=balas.get(2);x=cx(arriba);y=cy(arriba);arriba.update(0.1f);
        assertEquals(x,cx(arriba),0.01f);assertEquals(y+52f,cy(arriba),0.01f);
        assertEquals(500f,nave.getArea().x,0.01f);assertEquals(300f,nave.getArea().y,0.01f);
    }

    @Test public void repararDestruirYSinTiempoBloqueanDisparo() throws Exception {
        List<Bullet> balas=new ArrayList<>();PantallaJuego juego=pantalla(balas);
        Nave4 nave=nave(500,300);pulsadas.add(Input.Keys.DOWN);
        nave.actualizar(juego,0f);assertTrue(balas.isEmpty());
        nave.setReparando(true);nave.actualizar(juego,0.5f);assertTrue(balas.isEmpty());
        nave.setReparando(false);nave.actualizar(juego,0.05f);assertEquals(1,balas.size());
        nave.setVidas(0);nave.actualizar(juego,1f);assertEquals(1,balas.size());
    }

    @Test public void dashSigueMovimientoAunqueSeDispareAlLadoOpuesto() throws Exception {
        List<Bullet> balas=new ArrayList<>();PantallaJuego juego=pantalla(balas);
        Nave4 nave=nave(500,300);
        pulsadas.add(Input.Keys.D);pulsadas.add(Input.Keys.LEFT);nuevas.add(Input.Keys.SHIFT_LEFT);
        nave.actualizar(juego,0.05f);
        assertEquals(550f,nave.getArea().x,0.01f);assertEquals(300f,nave.getArea().y,0.01f);
        assertEquals(1,balas.size());
        float x=cx(balas.get(0));balas.get(0).update(0.1f);
        assertTrue(cx(balas.get(0))<x);
    }

    @Test public void cadenciasAltasEmitenVariosDisparosEnUnCuadroSinPerderMejoras() throws Exception {
        ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
        for(int i=0;i<6;i++) {
            p.sumarPuntos(p.getRequisitoNivel()-p.getExperiencia());
            assertTrue(p.elegirMejora(TipoMejora.CADENCIA_DOBLE));
        }
        assertEquals(160f,p.getCadencia(),0f);
        List<Bullet> balas=new ArrayList<>();PantallaJuego juego=pantalla(balas);
        Nave4 nave=nave(500,300);pulsadas.add(Input.Keys.RIGHT);
        nave.actualizar(juego,0.05f);
        assertTrue(balas.size()>=8 && balas.size()<=9);
        pulsadas.clear();int antes=balas.size();nave.actualizar(juego,1f);
        assertEquals(antes,balas.size());
    }

    @Test public void balasSalenDelMundoPorCualquieraDeLosCuatroBordes() {
        for(int[] dir:new int[][]{{0,1},{0,-1},{1,0},{-1,0}}) {
            List<Bullet> balas=new ArrayList<>();
            new DisparoSimple().disparar(600f,400f,dir[0],dir[1],textura,balas::add);
            balas.get(0).update(3f);assertTrue(balas.get(0).isDestroyed());
        }
    }
}
