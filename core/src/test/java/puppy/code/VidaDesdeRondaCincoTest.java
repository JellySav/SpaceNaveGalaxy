package puppy.code;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
public class VidaDesdeRondaCincoTest extends EntornoGdxPrueba {
    @Test public void aumentoDesdeCincoEsFijoNoRellenaVidaNiSeAcumula() {
        Enemigo enemigo=new AlienEnemigo(100,100,100,5,textura);
        enemigo.configurarVidaParaRonda(4);assertEquals(50f,enemigo.getVidaMaxima(),0f);
        enemigo.recibirDano(10f);enemigo.configurarVidaParaRonda(5);
        assertEquals(60f,enemigo.getVidaMaxima(),0.001f);assertEquals(48f,enemigo.getVidaActual(),0.001f);
        assertEquals(0.8f,enemigo.getFraccionResistencia(),0.001f);
        enemigo.configurarVidaParaRonda(9);enemigo.configurarVidaParaRonda(5);
        assertEquals(48f,enemigo.getVidaActual(),0.001f);assertEquals(60f,enemigo.getVidaMaxima(),0.001f);
    }
    @Test public void meteoritosDePrimeraEtapaNoCambianYLosPosterioresTienenDoce() {
        for(int ronda:new int[]{1,4,5,9,20}) {
            List<Enemigo> enemigos=new ArrayList<>();
            new LluviaMeteoritos(ronda,false,new Random(7)).actualizar(1.5f,enemigos,textura);
            assertEquals(1,enemigos.size());assertEquals(ronda>=5?12f:10f,enemigos.get(0).getVidaActual(),0.001f);
        }
    }
    @Test public void fragmentosMoradosHeredanElAumentoUnaVez() {
        for(int ronda:new int[]{3,5,12}) {
            AlienDivisible morada=new AlienDivisible(100,100,100,textura);morada.configurarVidaParaRonda(ronda);
            assertEquals(ronda>=5?60f:50f,morada.getVidaMaxima(),0.001f);
            morada.recibirDano(1000f);List<AlienDivisible> hijos=morada.dividir();assertEquals(2,hijos.size());
            for(AlienDivisible hijo:hijos) {
                assertEquals(ronda>=5?24f:20f,hijo.getVidaActual(),0.001f);
                hijo.configurarVidaParaRonda(ronda);assertEquals(ronda>=5?24f:20f,hijo.getVidaMaxima(),0.001f);
            }
        }
    }
    @Test public void jefesYTodosSusRefuerzosRecibenElMismoFactorDeLaRonda() {
        SectorGalactico[] sectores={SectorGalactico.CINTURON_DE_VELA,SectorGalactico.NEBULOSA_DE_ION,SectorGalactico.FRONTERA_DE_KHEPRI};
        int[] rondas={4,8,12};
        for(int i=0;i<sectores.length;i++) {
            JefeSector jefe=new JefeSector(400,500,100,30,sectores[i].getResistenciaJefe(rondas[i]),sectores[i],textura,textura);
            jefe.configurarVidaParaRonda(rondas[i]);float factor=rondas[i]>=5?1.2f:1f;
            assertEquals(sectores[i].getResistenciaJefe(rondas[i])*10f*factor,jefe.getVidaMaxima(),0.01f);
            List<AlienEnemigo> refuerzos=new ArrayList<>();jefe.actualizarRefuerzos(100f,100,7,textura,refuerzos::add);
            assertFalse(refuerzos.isEmpty());
            for(AlienEnemigo nave:refuerzos) {
                int base=nave instanceof AlienArtillero?50:nave instanceof AlienDivisible?50:
                        nave instanceof AlienExplosivo||nave instanceof AlienDetonadora?30:70;
                assertEquals(base*factor,nave.getVidaMaxima(),0.001f);
            }
        }
    }
}
