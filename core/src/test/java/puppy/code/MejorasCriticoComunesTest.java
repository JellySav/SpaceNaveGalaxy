package puppy.code;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
public class MejorasCriticoComunesTest extends EntornoGdxPrueba {
    private final ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
    private void elegir(TipoMejora tipo){p.subirNivelPruebas();assertTrue(p.elegirMejora(tipo));}
    @Test public void permitenCriticosConCualquierInicialYCalculanCadaProyectilPorSeparado() {
        for(TipoMejora inicial:new TipoMejora[]{TipoMejora.INICIAL_DASH,TipoMejora.INICIAL_DRON}) {
            p.reiniciar();assertTrue(p.elegirMejora(inicial));
            elegir(TipoMejora.PROBABILIDAD_CRITICO);elegir(TipoMejora.DANO_CRITICO);elegir(TipoMejora.DANO);
            assertEquals(0.03f,p.getProbabilidadCritico(),0.0001f);assertEquals(2.5f,p.getMultiplicadorCritico(),0f);
            List<Bullet> balas=new ArrayList<>();Random random=new Random(){int i;@Override public float nextFloat(){return new float[]{0.029f,0.03f,0.5f}[i++];}};
            new DisparoTriple().disparar(500,400,0,1,textura,b->balas.add(b.configurarJugador(p,random)));
            assertTrue(balas.get(0).esCritico());assertFalse(balas.get(1).esCritico());assertFalse(balas.get(2).esCritico());
            assertEquals(28.75f,balas.get(0).getDano(),0.001f);assertEquals(11.5f,balas.get(1).getDano(),0.001f);
            elegir(TipoMejora.DANO_CRITICO);assertEquals(3f,p.getMultiplicadorCritico(),0f);
            assertEquals(28.75f,balas.get(0).getDano(),0.001f);
        }
    }
    @Test public void bonosSeSumanALaInicialYAvanzadoEnCualquierOrdenYSeReinician() {
        for(boolean antes:new boolean[]{true,false}) {
            p.reiniciar();assertTrue(p.elegirMejora(TipoMejora.INICIAL_CRITICO));
            if(antes)elegir(TipoMejora.AVANZADO);
            for(int i=0;i<2;i++){elegir(TipoMejora.PROBABILIDAD_CRITICO);elegir(TipoMejora.DANO_CRITICO);}
            if(!antes){assertEquals(0.16f,p.getProbabilidadCritico(),0.0001f);assertEquals(3f,p.getMultiplicadorCritico(),0f);elegir(TipoMejora.AVANZADO);}
            assertEquals(0.22f,p.getProbabilidadCritico(),0.0001f);assertEquals(4f,p.getMultiplicadorCritico(),0f);
        }
        p.reiniciar();assertEquals(0f,p.getProbabilidadCritico(),0f);assertEquals(2f,p.getMultiplicadorCritico(),0f);
    }
    @Test public void sonComunesRepetiblesYProbabilidadDesapareceSoloAlCienPorCiento() {
        assertEquals(RarezaMejora.COMUN,TipoMejora.PROBABILIDAD_CRITICO.rareza);
        assertEquals(RarezaMejora.COMUN,TipoMejora.DANO_CRITICO.rareza);
        Random random=new Random(6);boolean probabilidad=false,dano=false;
        for(int i=0;i<1000;i++){List<TipoMejora> opciones=CatalogoMejoras.sortear(p,random);probabilidad|=opciones.contains(TipoMejora.PROBABILIDAD_CRITICO);dano|=opciones.contains(TipoMejora.DANO_CRITICO);}
        assertTrue(probabilidad&&dano);
        for(int i=0;i<34;i++)elegir(TipoMejora.PROBABILIDAD_CRITICO);
        assertEquals(1f,p.getProbabilidadCritico(),0f);assertFalse(TipoMejora.PROBABILIDAD_CRITICO.disponible(p));
        p.subirNivelPruebas();int pendientes=p.getPendientes();assertFalse(p.elegirMejora(TipoMejora.PROBABILIDAD_CRITICO));assertEquals(pendientes,p.getPendientes());
        for(int i=0;i<10;i++)elegir(TipoMejora.DANO_CRITICO);
        assertEquals(7f,p.getMultiplicadorCritico(),0f);assertTrue(TipoMejora.DANO_CRITICO.disponible(p));
        for(int i=0;i<1000;i++)assertFalse(CatalogoMejoras.sortear(p,random).contains(TipoMejora.PROBABILIDAD_CRITICO));
    }
}
