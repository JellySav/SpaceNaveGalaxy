package puppy.code;
import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.util.*;
import org.junit.Test;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
public class AparienciaEnemigoTest extends EntornoGdxPrueba {
    private Sprite sprite(Enemigo enemigo)throws Exception {
        Field f=Enemigo.class.getDeclaredField("sprite");f.setAccessible(true);return (Sprite)f.get(enemigo);
    }
    @Test public void nuevaRegionMantieneProporcionCentroVidaYColorSinReaplicarTamano()throws Exception {
        AlienArtillero enemigo=new AlienArtillero(200,200,textura,new Random(3));
        TextureRegion region=new TextureRegion(textura,0,0,160,96);
        enemigo.configurarVidaParaRonda(5);enemigo.recibirDano(10);
        enemigo.usarApariencia(region);Rectangle area=new Rectangle(enemigo.getArea());
        assertEquals(48f,area.width,0.001f);assertEquals(28.8f,area.height,0.001f);
        assertEquals(224f,area.x+area.width/2f,0.001f);assertEquals(224f,area.y+area.height/2f,0.001f);
        assertEquals(50f,enemigo.getVidaActual(),0.001f);assertEquals(60f,enemigo.getVidaMaxima(),0.001f);
        assertEquals(1f,sprite(enemigo).getColor().g,0f);assertEquals(1f,sprite(enemigo).getColor().b,0f);
        enemigo.usarApariencia(region);assertEquals(area,enemigo.getArea());
        assertEquals(160,sprite(enemigo).getRegionWidth());assertEquals(96,sprite(enemigo).getRegionHeight());
    }
    @Test public void divisionUsaElSpriteDeFragmentoYConservaImpulsoVidaYTamano()throws Exception {
        AlienDivisible madre=new AlienDivisible(500,400,100,textura);
        TextureRegion normal=new TextureRegion(textura,0,0,264,255),pequeno=new TextureRegion(textura,0,0,126,123);
        madre.usarApariencias(normal,pequeno);madre.configurarVidaParaRonda(9);madre.recibirDano(100);
        List<AlienDivisible> hijos=madre.dividir();assertEquals(2,hijos.size());
        float izquierda=hijos.get(0).getArea().x,derecha=hijos.get(1).getArea().x;
        for(AlienDivisible hijo:hijos) {
            assertEquals(126,sprite(hijo).getRegionWidth());assertEquals(123,sprite(hijo).getRegionHeight());
            assertEquals(28f,hijo.getArea().width,0.001f);assertEquals(28f*123/126,hijo.getArea().height,0.001f);
            assertEquals(24f,hijo.getVidaActual(),0.001f);assertEquals(1f,sprite(hijo).getColor().r,0f);
            hijo.actualizar(0.1f,1200,800,600,100);assertTrue(hijo.esFragmento());
        }
        assertTrue(hijos.get(0).getArea().x<izquierda);assertTrue(hijos.get(1).getArea().x>derecha);
    }
    @Test public void platilloMasGrandeConservaCentroProporcionYNoCreceAlReaplicar() {
        AlienArtillero enemigo=new AlienArtillero(200,200,textura,new Random(3));
        TextureRegion region=new TextureRegion(textura,0,0,160,96);
        for(int i=0;i<10;i++)enemigo.usarApariencia(region,56f);
        assertEquals(56f,enemigo.getArea().width,0.001f);
        assertEquals(33.6f,enemigo.getArea().height,0.001f);
        assertEquals(224f,enemigo.getArea().x+enemigo.getArea().width/2f,0.001f);
        assertEquals(224f,enemigo.getArea().y+enemigo.getArea().height/2f,0.001f);
    }
}
