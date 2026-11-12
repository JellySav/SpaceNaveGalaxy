package puppy.code;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.junit.Test;

public class AtaqueOleadasTest extends EntornoGdxPrueba {
    private JefeSector jefe() {
        return new JefeSector(500f, 600f, 100f, 30f, 260,
                SectorGalactico.FRONTERA_DE_KHEPRI, textura, textura);
    }

    @Test public void tresRafagasRapidasPausaYRepite() {
        JefeSector jefe = jefe();
        List<ProyectilHostil> balas = new ArrayList<>();
        jefe.actualizarAtaque(3.4f, 600f, 100f, balas::add);
        assertTrue(balas.isEmpty());
        jefe.actualizarAtaque(0.11f, 600f, 100f, balas::add);
        assertEquals(7, balas.size());
        jefe.actualizarAtaque(0.3f, 600f, 100f, balas::add);
        assertEquals(7, balas.size());
        jefe.actualizarAtaque(0.06f, 600f, 100f, balas::add);
        assertEquals(14, balas.size());
        jefe.actualizarAtaque(0.36f, 600f, 100f, balas::add);
        assertEquals(21, balas.size());
        jefe.actualizarAtaque(3.4f, 600f, 100f, balas::add);
        assertEquals(21, balas.size());
        jefe.actualizarAtaque(0.11f, 600f, 100f, balas::add);
        assertEquals(28, balas.size());
        jefe.actualizarAtaque(0.36f, 600f, 100f, balas::add);
        jefe.actualizarAtaque(0.36f, 600f, 100f, balas::add);
        assertEquals(42, balas.size());
    }

    @Test public void arcoHaciaAbajoConEspaciosYVelocidadConstante() throws Exception {
        List<ProyectilHostil> balas = new ArrayList<>();
        new AtaqueOleadas().disparar(600f, 600f, 600f, 100f, textura, balas::add);
        assertEquals(7, balas.size());
        Field campo = ProyectilHostil.class.getDeclaredField("sprite");
        campo.setAccessible(true);
        for (int i = 0; i < balas.size(); i++) {
            ProyectilHostil bala = balas.get(i);
            bala.actualizar(0.5f, MundoJuego.ANCHO, MundoJuego.ALTO);
            Sprite sprite = (Sprite) campo.get(bala);
            assertTrue(sprite.getY() < 600f);
            float dx = sprite.getX() - 600f;
            float dy = sprite.getY() - 600f;
            assertEquals(100f, Math.sqrt(dx * dx + dy * dy), 0.01f);
            assertTrue(sprite.getColor().r > sprite.getColor().g);
            if (i < 3) assertTrue(dx < 0f);
            else if (i > 3) assertTrue(dx > 0f);
            else assertEquals(0f, dx, 0.01f);
            if (i > 0) {
                Sprite anterior = (Sprite) campo.get(balas.get(i - 1));
                assertTrue(sprite.getX() > anterior.getX() + sprite.getWidth());
            }
        }
    }

    @Test public void sinTiempoYMuertoNoDispara() {
        JefeSector jefe = jefe();
        List<ProyectilHostil> balas = new ArrayList<>();
        jefe.actualizarAtaque(0f, 600f, 100f, balas::add);
        assertTrue(balas.isEmpty());
        while (jefe.estaActivo()) jefe.recibirImpacto();
        jefe.actualizarAtaque(30f, 600f, 100f, balas::add);
        assertTrue(balas.isEmpty());
    }

    @Test public void cadaRafagaApuntaALaPosicionActualDelJugadorEnCualquierDireccion() throws Exception {
        AtaqueOleadas ataque=new AtaqueOleadas();Field campo=ProyectilHostil.class.getDeclaredField("sprite");campo.setAccessible(true);
        for(float[] objetivo:new float[][]{{1000f,400f},{600f,750f},{100f,400f},{600f,100f},{1000f,700f}}) {
            List<ProyectilHostil> balas=new ArrayList<>();ataque.disparar(600f,400f,objetivo[0],objetivo[1],textura,balas::add);
            assertEquals(7,balas.size());
            float dx=objetivo[0]-600f,dy=objetivo[1]-400f,distancia=(float)Math.hypot(dx,dy);
            for(int i=0;i<balas.size();i++) {
                balas.get(i).actualizar(0.1f,1200f,800f);Sprite sprite=(Sprite)campo.get(balas.get(i));
                float vx=sprite.getX()-600f,vy=sprite.getY()-400f;
                assertEquals(20f,(float)Math.hypot(vx,vy),0.001f);
                assertTrue(vx*dx+vy*dy>0f);
                if(i==3){assertEquals(dx/distancia*20f,vx,0.001f);assertEquals(dy/distancia*20f,vy,0.001f);}
            }
        }
    }
}
