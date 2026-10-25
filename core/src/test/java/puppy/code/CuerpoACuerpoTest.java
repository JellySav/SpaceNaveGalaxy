package puppy.code;
import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.*;
import org.junit.Test;
import com.badlogic.gdx.Input;
public class CuerpoACuerpoTest extends EntornoGdxPrueba {
    private final ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
    private void elegir(TipoMejora tipo){p.subirNivelPruebas();assertTrue(p.elegirMejora(tipo));}
    private void campo(Object objeto,String nombre,Object valor)throws Exception {
        Field campo=PantallaJuego.class.getDeclaredField(nombre);campo.setAccessible(true);campo.set(objeto,valor);
    }
    private PantallaJuego pantalla(Nave4 nave,List<Enemigo> enemigos)throws Exception {
        Class<?> tipo=Class.forName("sun.misc.Unsafe");Field unsafe=tipo.getDeclaredField("theUnsafe");unsafe.setAccessible(true);
        PantallaJuego pantalla=(PantallaJuego)tipo.getMethod("allocateInstance",Class.class).invoke(unsafe.get(null),PantallaJuego.class);
        campo(pantalla,"nave",nave);campo(pantalla,"enemigos",enemigos);campo(pantalla,"progresion",p);
        campo(pantalla,"explosionesBalas",new ArrayList<ExplosionBala>());
        campo(pantalla,"game",new SpaceNavigation());campo(pantalla,"explosionSound",sonido);
        campo(pantalla,"explosiones",new SistemaExplosiones());campo(pantalla,"cargasEnergia",new ArrayList<CargaEnergia>());
        campo(pantalla,"baterias",new ArrayList<BateriaEnergia>());campo(pantalla,"bulletTexture",textura);
        campo(pantalla,"random",new Random(1));return pantalla;
    }
    private void invocar(PantallaJuego pantalla,String nombre,Enemigo... objetivo)throws Exception {
        Method metodo=PantallaJuego.class.getDeclaredMethod(nombre,objetivo.length==0?new Class<?>[0]:new Class<?>[]{Enemigo.class});
        metodo.setAccessible(true);metodo.invoke(pantalla,(Object[])objetivo);
    }
    private void dash(Nave4 nave) {
        pulsadas.add(Input.Keys.D);nuevas.add(Input.Keys.SHIFT_LEFT);nave.actualizar(null,0.17f);nuevas.clear();
    }
    @Test public void legendariaUnicaDaVidaYCuraElIncrementoYSeReinicia() {
        Nave4 nave=nave(100,100);nave.setVidas(1);
        elegir(TipoMejora.CUERPO_A_CUERPO);nave.aplicarMejoras();
        assertEquals(RarezaMejora.LEGENDARIA,TipoMejora.CUERPO_A_CUERPO.rareza);
        assertEquals(6,nave.getVidasMaximas());assertEquals(4,nave.getVidas());
        nave.aplicarMejoras();assertEquals(4,nave.getVidas());
        p.subirNivelPruebas();int pendientes=p.getPendientes();
        assertFalse(p.elegirMejora(TipoMejora.CUERPO_A_CUERPO));assertEquals(pendientes,p.getPendientes());
        Random random=new Random(3);
        for(int i=0;i<1000;i++)assertFalse(CatalogoMejoras.sortear(p,random).contains(TipoMejora.CUERPO_A_CUERPO));
        p.reiniciar();assertFalse(p.tieneCuerpoACuerpo());assertEquals(3,p.getVidasMaximas());
        assertEquals(6f,p.getRegeneracionEnergia(),0f);assertEquals(EnergiaDash.ESPERA,p.getEsperaDash(),0f);
        assertTrue(TipoMejora.CUERPO_A_CUERPO.disponible(p));
    }
    @Test public void energiaRecargaTreintaPorCientoMasYEncadenaDashSinSuperponerlos() {
        EnergiaDash energia=new EnergiaDash();assertTrue(energia.iniciar());energia.actualizar(0.17f);
        assertFalse(energia.puedeUsar());elegir(TipoMejora.CUERPO_A_CUERPO);
        assertEquals(0f,energia.getEspera(),0f);assertTrue(energia.iniciar());
        assertFalse(energia.iniciar());energia.actualizar(0.17f);
        assertFalse(energia.iniciar()); // Sigue necesitando energia para pagar el tercer dash.
        energia.actualizar(1f);assertEquals(27.8f,energia.getEnergia(),0.001f);
        energia.recuperar(20f);assertTrue(energia.iniciar());
        energia.actualizar(0.17f);energia.recuperar(100f);assertEquals(100f,energia.getEnergia(),0f);
    }
    @Test public void entradaRealPermiteNuevoDashAlTerminarElAnterior() {
        elegir(TipoMejora.CUERPO_A_CUERPO);Nave4 nave=nave(100,100);
        dash(nave);assertEquals(60f,nave.getEnergiaDash().getEnergia(),0f);
        dash(nave);assertEquals(20f,nave.getEnergiaDash().getEnergia(),0f);
        assertEquals(440f,nave.getArea().x,0.01f);
    }
    @Test public void bajasDeExplosionCuranCadaVictimaYCombinanRobarEnergiaSinDuplicar()throws Exception {
        elegir(TipoMejora.CUERPO_A_CUERPO);elegir(TipoMejora.ROBAR_ENERGIA);elegir(TipoMejora.DASH_EXPLOSIVO);
        Nave4 nave=nave(100,100);nave.setVidas(1);
        Enemigo objetivo=new AlienEnemigo(160,100,1,3,textura),adyacente=new AlienEnemigo(160,180,1,1,textura);
        List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(objetivo,adyacente));PantallaJuego pantalla=pantalla(nave,enemigos);
        dash(nave);invocar(pantalla,"actualizarDanoDash");invocar(pantalla,"actualizarDanoDash");
        invocar(pantalla,"resolverDerrotaDash",objetivo);
        assertTrue(enemigos.isEmpty());assertEquals(3,nave.getVidas());assertEquals(66f,nave.getEnergiaDash().getEnergia(),0f);
    }
    @Test public void bajaDirectaCuraSinBateriaPeroNoSobrepasaMaximoYOtrasBajasNoCuran()throws Exception {
        elegir(TipoMejora.CUERPO_A_CUERPO);Nave4 nave=nave(100,100);nave.setVidas(5);
        List<Enemigo> enemigos=new ArrayList<>();PantallaJuego pantalla=pantalla(nave,enemigos);
        Enemigo otro=new AlienEnemigo(160,100,1,2,textura);enemigos.add(otro);otro.recibirDano(20f);
        invocar(pantalla,"resolverDerrota",otro);assertEquals(5,nave.getVidas());
        Enemigo directo=new AlienEnemigo(160,100,1,2,textura);enemigos.add(directo);
        dash(nave);invocar(pantalla,"actualizarDanoDash");assertEquals(6,nave.getVidas());assertEquals(0,p.getBaterias());
        Enemigo extra=new AlienEnemigo(160,100,1,2,textura);enemigos.add(extra);extra.recibirDano(20f);
        invocar(pantalla,"resolverDerrotaDash",extra);assertEquals(6,nave.getVidas());
        nave.setVidas(0);Enemigo muerto=new AlienEnemigo(160,100,1,2,textura);enemigos.add(muerto);muerto.recibirDano(20f);
        invocar(pantalla,"resolverDerrotaDash",muerto);assertEquals(0,nave.getVidas());
    }
}
