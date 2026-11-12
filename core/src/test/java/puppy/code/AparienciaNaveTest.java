package puppy.code;
import static org.junit.Assert.*;
import org.junit.Test;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
public class AparienciaNaveTest extends EntornoGdxPrueba {
    @Test public void aparienciaConPropulsionNoModificaColisionPosicionOVida() {
        Nave4 nave=nave(100,100);Rectangle antes=new Rectangle(nave.getArea());
        TextureRegion region=new TextureRegion(textura,0,0,955,1129);
        nave.usarApariencia(region);nave.usarApariencia(region);
        assertEquals(antes,nave.getArea());assertEquals(122.5f,nave.getCentroX(),0f);
        assertEquals(122.5f,nave.getCentroY(),0f);assertEquals(3,nave.getVidas());
        nave.usarApariencia(null);assertEquals(antes,nave.getArea());
    }
    @Test public void disenoNoCambiaDistanciaDeDashNiSuImpactoYLaEleccionSeReinicia() {
        ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
        assertNull(p.getMejoraInicial());assertTrue(p.elegirMejora(TipoMejora.INICIAL_DASH));
        assertEquals(TipoMejora.INICIAL_DASH,p.getMejoraInicial());
        Nave4 nave=nave(100,100);nave.usarApariencia(new TextureRegion(textura,0,0,955,1129));
        pulsadas.add(Input.Keys.D);nuevas.add(Input.Keys.SHIFT_LEFT);nave.actualizar(null,0.17f);
        assertEquals(270f,nave.getArea().x,0.001f);assertEquals(45f,nave.getArea().width,0f);
        Enemigo enemigo=new AlienEnemigo(160,100,1,3,textura);
        assertTrue(nave.golpearConDash(enemigo));assertEquals(10f,enemigo.getVidaActual(),0f);
        assertFalse(nave.golpearConDash(enemigo));p.reiniciar();assertNull(p.getMejoraInicial());
    }
}
