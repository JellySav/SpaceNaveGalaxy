package puppy.code;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class MejorasAvanzadasTest extends EntornoGdxPrueba {
    private final ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
    private void elegir(TipoMejora tipo){p.subirNivelPruebas();assertTrue(p.elegirMejora(tipo));}
    @Test public void masDashesReduceCosteEnDiezYNoSeRepite() {
        assertEquals(RarezaMejora.EPICA,TipoMejora.MAS_DASHES.rareza);
        elegir(TipoMejora.MAS_DASHES);EnergiaDash energia=new EnergiaDash();
        assertEquals(30f,energia.getCoste(),0f);assertTrue(energia.iniciar());assertEquals(70f,energia.getEnergia(),0f);
        p.subirNivelPruebas();assertFalse(p.elegirMejora(TipoMejora.MAS_DASHES));assertEquals(1,p.getPendientes());
        p.reiniciar();assertEquals(40f,energia.getCoste(),0f);assertTrue(TipoMejora.MAS_DASHES.disponible(p));
    }
    @Test public void avanzadoDashAcumulaDescuentosEnCualquierOrdenYDuplicaDano() {
        for(boolean epicaPrimero:new boolean[]{true,false}) {
            p.reiniciar();p.elegirMejora(TipoMejora.INICIAL_DASH);
            elegir(epicaPrimero?TipoMejora.MAS_DASHES:TipoMejora.AVANZADO);
            assertEquals(epicaPrimero?30f:20f,p.getCosteDash(),0f);
            elegir(epicaPrimero?TipoMejora.AVANZADO:TipoMejora.MAS_DASHES);
            assertEquals(10f,p.getCosteDash(),0f);assertEquals(40f,p.getDanoDash(),0f);
            elegir(TipoMejora.DASH);assertEquals(60f,p.getDanoDash(),0f);
            EnergiaDash energia=new EnergiaDash();assertTrue(energia.iniciar());
            assertEquals(90f,energia.getEnergia(),0f);energia.actualizar(0.17f);assertTrue(energia.tieneEscudo());
        }
    }
    @Test public void avanzadoCriticoTieneDieciseisPorCientoYDanoTripleSoloEnNuevasBalas() {
        p.elegirMejora(TipoMejora.INICIAL_CRITICO);
        Random acierto=new Random(){public float nextFloat(){return 0.079f;}};
        Bullet antigua=new Bullet(100,100,0,1,textura).configurarJugador(p,acierto);
        elegir(TipoMejora.AVANZADO);assertEquals(0.16f,p.getProbabilidadCritico(),0f);assertEquals(3f,p.getMultiplicadorCritico(),0f);
        Bullet critica=new Bullet(100,100,0,1,textura).configurarJugador(p,new Random(){public float nextFloat(){return 0.159f;}});
        Bullet normal=new Bullet(100,100,0,1,textura).configurarJugador(p,new Random(){public float nextFloat(){return 0.16f;}});
        assertTrue(critica.esCritico());assertEquals(30f,critica.getDano(),0f);
        assertFalse(normal.esCritico());assertEquals(10f,normal.getDano(),0f);assertEquals(20f,antigua.getDano(),0f);
        assertEquals(40f,p.getCosteDash(),0f);assertEquals(20f,p.getDanoDash(),0f);
    }
    @Test public void miniDronAvanzadoDisparaCincoSinAlterarCadenciaDanoNiOtrosDrones() {
        p.elegirMejora(TipoMejora.INICIAL_DRON);elegir(TipoMejora.AVANZADO);
        DronEscolta mini=new DronEscolta(textura,false,true);
        List<Enemigo> enemigos=Arrays.asList(new AlienEnemigo(600,600,100,6,textura));
        List<Bullet> balas=new ArrayList<>();mini.actualizar(0.2f,400,400,enemigos,textura,balas::add);
        assertEquals(5,balas.size());for(Bullet bala:balas){assertEquals(5f,bala.getDano(),0f);assertFalse(bala.esCritico());}
        for(Bullet bala:balas) {
            float x=bala.getArea().x,y=bala.getArea().y;bala.update(0.1f);
            assertEquals(42f,(float)Math.hypot(bala.getArea().x-x,bala.getArea().y-y),0.01f);
        }
        mini.actualizar(2.19f,400,400,enemigos,textura,balas::add);assertEquals(5,balas.size());
        mini.actualizar(0.02f,400,400,enemigos,textura,balas::add);assertEquals(10,balas.size());
        for(boolean explosivo:new boolean[]{false,true}) {
            List<Bullet> otras=new ArrayList<>();new DronEscolta(textura,explosivo).actualizar(0.2f,400,400,enemigos,textura,otras::add);
            assertEquals(1,otras.size());assertEquals(10f,otras.get(0).getDano(),0f);
        }
        assertEquals(0f,p.getProbabilidadCritico(),0f);assertEquals(40f,p.getCosteDash(),0f);
    }
    @Test public void avanzadoExigeInicialNoSeRepiteYSaleDelSorteoAlElegirlo() {
        p.subirNivelPruebas();assertFalse(p.elegirMejora(TipoMejora.AVANZADO));assertEquals(1,p.getPendientes());
        p.elegirMejora(TipoMejora.INICIAL_DRON);assertTrue(p.elegirMejora(TipoMejora.AVANZADO));
        assertEquals(RarezaMejora.LEGENDARIA,TipoMejora.AVANZADO.rareza);
        elegir(TipoMejora.MAS_DASHES);p.subirNivelPruebas();assertFalse(p.elegirMejora(TipoMejora.AVANZADO));
        Random random=new Random(9);
        for(int i=0;i<1000;i++)for(TipoMejora tipo:CatalogoMejoras.sortear(p,random)) {
            assertNotEquals(TipoMejora.AVANZADO,tipo);assertNotEquals(TipoMejora.MAS_DASHES,tipo);
        }
        p.reiniciar();assertFalse(p.tieneAvanzado());assertFalse(p.tieneMiniDronQuintuple());
    }
}
