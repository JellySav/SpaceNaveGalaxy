package puppy.code;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class ApoyoReforzadoTest extends EntornoGdxPrueba {
    private final ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
    private void elegir(TipoMejora tipo){p.subirNivelPruebas();assertTrue(p.elegirMejora(tipo));}
    @Test public void acumulaSobreLaBaseSinDependerDelOrdenDeAdquisicion() {
        for(boolean antes:new boolean[]{true,false}) {
            p.reiniciar();assertTrue(p.elegirMejora(TipoMejora.INICIAL_DRON));
            if(antes){elegir(TipoMejora.APOYO_REFORZADO);elegir(TipoMejora.APOYO_REFORZADO);}
            for(TipoMejora tipo:new TipoMejora[]{TipoMejora.DRON,TipoMejora.DRON_EXPLOSIVO,TipoMejora.DRON_LASER}) {
                elegir(tipo);elegir(tipo);
            }
            if(!antes){elegir(TipoMejora.APOYO_REFORZADO);elegir(TipoMejora.APOYO_REFORZADO);}
            assertEquals(14f,p.getDanoDron(false),0.001f);assertEquals(14f,p.getDanoDron(true),0.001f);
            assertEquals(42f,p.getDanoLaser(),0.001f);assertEquals(6f,p.getDanoMiniDron(),0.001f);
            assertEquals(ProgresionMejoras.CADENCIA_DRON_BASE*1.4f,p.getCadenciaDron(false),0.001f);
            assertEquals(p.getCadenciaDron(false),p.getCadenciaDron(true),0.001f);
            assertEquals(p.getCadenciaDron(false),p.getCadenciaLaser(),0.001f);
            assertEquals(ProgresionMejoras.CADENCIA_DRON_BASE*0.6f,p.getCadenciaMiniDron(),0.001f);
            assertEquals(10f,p.getDano(),0f);assertEquals(2.5f,p.getCadencia(),0f);
        }
        p.reiniciar();assertEquals(10f,p.getDanoDron(false),0f);assertEquals(10f,p.getDanoDron(true),0f);
        assertEquals(30f,p.getDanoLaser(),0f);assertEquals(5f,p.getDanoMiniDron(),0f);
        assertEquals(ProgresionMejoras.CADENCIA_DRON_BASE,p.getCadenciaLaser(),0f);
    }
    @Test public void dronesExistentesDisparanConElBonoGlobalYLaNuevaCadencia() {
        assertTrue(p.elegirMejora(TipoMejora.INICIAL_DRON));elegir(TipoMejora.AVANZADO);
        DronEscolta[] drones={new DronEscolta(textura),new DronEscolta(textura,true),
                new DronEscolta(textura,false,true),new DronLaser(textura)};
        elegir(TipoMejora.APOYO_REFORZADO);
        List<Enemigo> enemigos=Arrays.asList(new AlienEnemigo(600,600,100,6,textura));
        float[] danos={11f,11f,5.5f,33f};int[] cantidades={1,1,5,1};
        for(int i=0;i<drones.length;i++) {
            List<Bullet> balas=new ArrayList<>();
            drones[i].actualizar(0.2f,400,400,enemigos,textura,balas::add);
            assertEquals(cantidades[i],balas.size());
            for(Bullet bala:balas)assertEquals(danos[i],bala.getDano(),0.001f);
            float intervalo=i==2?2f:1f;
            drones[i].actualizar(intervalo-0.01f,400,400,enemigos,textura,balas::add);
            assertEquals(cantidades[i],balas.size());
            drones[i].actualizar(0.02f,400,400,enemigos,textura,balas::add);
            assertEquals(cantidades[i]*2,balas.size());
        }
    }
    @Test public void esEpicaRepetibleYPermaneceEnElSorteo() {
        assertEquals(RarezaMejora.EPICA,TipoMejora.APOYO_REFORZADO.rareza);
        elegir(TipoMejora.APOYO_REFORZADO);elegir(TipoMejora.APOYO_REFORZADO);
        assertTrue(TipoMejora.APOYO_REFORZADO.disponible(p));
        Random random=new Random(27);boolean encontrada=false;
        for(int i=0;i<1000;i++)encontrada|=CatalogoMejoras.sortear(p,random).contains(TipoMejora.APOYO_REFORZADO);
        assertTrue(encontrada);
    }
}
