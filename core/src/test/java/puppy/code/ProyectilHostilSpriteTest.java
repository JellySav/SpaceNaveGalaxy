package puppy.code;
import static org.junit.Assert.*;
import org.junit.Test;
import java.lang.reflect.Field;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
public class ProyectilHostilSpriteTest extends EntornoGdxPrueba {
    private Sprite sprite(ProyectilHostil bala)throws Exception {
        Field campo=ProyectilHostil.class.getDeclaredField("sprite");campo.setAccessible(true);return (Sprite)campo.get(bala);
    }
    private ProyectilHostil laser(float dx,float dy) {
        ProyectilHostil bala=new ProyectilHostil(100,100,dx,dy,100,textura);
        bala.usarSpriteLaser(new TextureRegion(textura,0,0,11,77));return bala;
    }
    @Test public void usaSoloRegionVisibleConservaCentroYColisionEsDelgada()throws Exception {
        ProyectilHostil bala=laser(0,1);Sprite s=sprite(bala);
        assertEquals(108f,s.getX()+s.getWidth()/2f,0.001f);assertEquals(112f,s.getY()+s.getHeight()/2f,0.001f);
        assertEquals(32f,s.getHeight(),0.001f);assertEquals(32f/7f,s.getWidth(),0.001f);
        assertEquals(11,s.getRegionWidth());assertEquals(77,s.getRegionHeight());
        assertTrue(bala.toca(new Rectangle(107,111,2,2)));
        assertFalse(bala.toca(new Rectangle(111,111,2,2)));
        assertFalse(bala.toca(new Rectangle(100,96,2,2)));
        assertEquals(1f,s.getColor().g,0f);
    }
    @Test public void hitboxSigueLaRotacionSinLlenarEsquinasDelRectanguloEnvolvente()throws Exception {
        ProyectilHostil diagonal=laser(1,1);Sprite s=sprite(diagonal);
        assertEquals(-45f,s.getRotation(),0.001f);
        assertTrue(diagonal.toca(new Rectangle(107,111,2,2)));
        assertTrue(diagonal.toca(new Rectangle(117,121,1,1)));
        Rectangle caja=s.getBoundingRectangle();
        assertFalse(diagonal.toca(new Rectangle(caja.x+0.5f,caja.y+caja.height-1.5f,1,1)));
        ProyectilHostil horizontal=laser(1,0);
        assertTrue(horizontal.toca(new Rectangle(121,111,1,1)));
        assertFalse(horizontal.toca(new Rectangle(107,120,1,1)));
    }
    @Test public void trayectoriaYSalidaDePantallaRespetanElSpriteRotado()throws Exception {
        ProyectilHostil bala=laser(1,0);bala.actualizar(1f,200,200);
        assertTrue(bala.estaActivo()); // Su cola todavia es visible.
        assertEquals(208f,sprite(bala).getX()+sprite(bala).getWidth()/2f,0.001f);
        bala.actualizar(0.1f,200,200);assertFalse(bala.estaActivo());
        assertFalse(bala.toca(new Rectangle(210,110,20,20)));
    }
}
