package puppy.code;

import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.Field;
import org.junit.Test;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class MejorasInicialesTest extends EntornoGdxPrueba {
    private final ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
    @Test public void opcionesFijasInicialesSoloUnaSinConsumirNivelNiExperiencia() {
        assertEquals(Arrays.asList(TipoMejora.INICIAL_DASH,TipoMejora.INICIAL_DRON,TipoMejora.INICIAL_CRITICO),CatalogoMejoras.iniciales());
        for(TipoMejora inicial:CatalogoMejoras.iniciales()) {
            p.reiniciar();assertEquals(RarezaMejora.INICIAL,inicial.rareza);
            assertTrue(p.elegirMejora(inicial));assertTrue(p.tieneMejoraInicial());
            assertEquals(1,p.getNivel());assertEquals(0,p.getExperiencia());assertEquals(0,p.getPendientes());
            for(TipoMejora otra:CatalogoMejoras.iniciales())assertFalse(p.elegirMejora(otra));
            Random random=new Random(4);
            for(int i=0;i<1000;i++)for(TipoMejora opcion:CatalogoMejoras.sortear(p,random))assertNotEquals(RarezaMejora.INICIAL,opcion.rareza);
            p.reiniciar();assertFalse(p.tieneMejoraInicial());assertFalse(p.tieneMiniDron());assertFalse(p.tieneEscudoDash());assertEquals(0f,p.getProbabilidadCritico(),0f);
        }
    }
    @Test public void eleccionInicialNoConsumeUnaSubidaPendiente() {
        p.sumarPuntos(250);assertEquals(1,p.getPendientes());
        assertTrue(p.elegirMejora(TipoMejora.INICIAL_CRITICO));assertEquals(1,p.getPendientes());
        assertTrue(p.elegirMejora(TipoMejora.DANO));assertEquals(0,p.getPendientes());
    }
    @Test public void escudoProtegeTresCuartosDeSegundoDespuesDelDashYSobreviveAlCambioDeRonda() {
        p.elegirMejora(TipoMejora.INICIAL_DASH);Nave4 nave=nave(300,300);
        nuevas.add(Input.Keys.SHIFT_LEFT);nave.actualizar(null,0.17f);nuevas.clear();
        assertFalse(nave.getEnergiaDash().estaActivo());assertTrue(nave.getEnergiaDash().tieneEscudo());
        assertFalse(nave.recibirDano());
        nave.actualizar(null,0.74f);assertFalse(nave.recibirDano());
        EnergiaDash energia=nave.getEnergiaDash();energia.detener();
        Nave4 siguiente=nave(300,300);siguiente.setEnergiaDash(energia);
        assertFalse(siguiente.recibirDano());siguiente.actualizar(null,0.02f);
        assertFalse(energia.tieneEscudo());assertTrue(siguiente.recibirDano());assertEquals(2,siguiente.getVidas());
    }
    @Test public void dashFallidoNoActivaEscudo() {
        p.elegirMejora(TipoMejora.INICIAL_DASH);EnergiaDash energia=new EnergiaDash();
        energia.iniciar();energia.actualizar(0.5f);energia.iniciar();energia.actualizar(2f);
        assertFalse(energia.tieneEscudo());assertFalse(energia.iniciar());assertFalse(energia.tieneEscudo());
    }
    @Test public void criticosIndependientesPorBalaDoblanDanoActualYSonAmarillos() throws Exception {
        p.elegirMejora(TipoMejora.INICIAL_CRITICO);p.sumarPuntos(250);p.elegirMejora(TipoMejora.DANO);
        List<Bullet> balas=new ArrayList<>();Random random=new Random(){int indice;public float nextFloat(){return new float[]{0.099f,0.10f,0.9f}[indice++];}};
        new DisparoTriple().disparar(500f,400f,0f,1f,textura,b->balas.add(b.configurarJugador(p,random)));
        assertTrue(balas.get(0).esCritico());assertFalse(balas.get(1).esCritico());assertFalse(balas.get(2).esCritico());
        assertEquals(23f,balas.get(0).getDano(),0f);assertEquals(11.5f,balas.get(1).getDano(),0f);
        Field f=Bullet.class.getDeclaredField("spr");f.setAccessible(true);Sprite sprite=(Sprite)f.get(balas.get(0));
        assertEquals(1f,sprite.getColor().r,0f);assertEquals(1f,sprite.getColor().g,0f);
        p.sumarPuntos(275);p.elegirMejora(TipoMejora.DANO_DOBLE);assertEquals(23f,balas.get(0).getDano(),0f);
    }
    @Test public void miniDronTieneMitadDeDanoYCadenciaYSeColocaDetras() {
        DronEscolta mini=new DronEscolta(textura,false,true);List<Bullet> balas=new ArrayList<>();
        List<Enemigo> enemigos=Arrays.asList(new AlienEnemigo(600,600,100,6,textura));
        mini.orientarDetras(0f,1f);mini.actualizar(0.2f,400f,400f,enemigos,textura,balas::add);
        assertEquals(24f,mini.getArea().width,0f);assertEquals(388f,mini.getArea().x,0.001f);assertEquals(330f,mini.getArea().y,0.001f);
        assertEquals(1,balas.size());assertEquals(5f,balas.get(0).getDano(),0f);
        mini.actualizar(2.19f,400f,400f,enemigos,textura,balas::add);assertEquals(1,balas.size());
        mini.actualizar(0.02f,400f,400f,enemigos,textura,balas::add);assertEquals(2,balas.size());
        p.sumarPuntos(250);p.elegirMejora(TipoMejora.DRON);p.sumarPuntos(275);p.elegirMejora(TipoMejora.DRON);
        mini.orientarDetras(1f,0f);mini.actualizar(2.3f,400f,400f,enemigos,textura,balas::add);
        assertEquals(330f,mini.getArea().x,0.001f);assertEquals(388f,mini.getArea().y,0.001f);
        assertEquals(6f,balas.get(2).getDano(),0f);
        mini.desactivar(3f);int antes=balas.size();mini.actualizar(1f,400f,400f,enemigos,textura,balas::add);assertEquals(antes,balas.size());
    }
}
