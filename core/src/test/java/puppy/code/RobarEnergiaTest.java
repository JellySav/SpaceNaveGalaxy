package puppy.code;

import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.*;
import org.junit.Test;
import com.badlogic.gdx.Input;

public class RobarEnergiaTest extends EntornoGdxPrueba {
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
    @Test public void esComunAcumulableDisponibleEnSorteoYSeReinicia() {
        assertEquals(0f,p.getEnergiaPorBajaDash(),0f);
        assertEquals(RarezaMejora.COMUN,TipoMejora.ROBAR_ENERGIA.rareza);
        for(int i=1;i<=3;i++){elegir(TipoMejora.ROBAR_ENERGIA);assertEquals(i*3f,p.getEnergiaPorBajaDash(),0f);}
        assertTrue(TipoMejora.ROBAR_ENERGIA.disponible(p));boolean encontrada=false;Random random=new Random(7);
        for(int i=0;i<1000;i++)encontrada|=CatalogoMejoras.sortear(p,random).contains(TipoMejora.ROBAR_ENERGIA);
        assertTrue(encontrada);p.reiniciar();assertEquals(0f,p.getEnergiaPorBajaDash(),0f);
    }
    @Test public void bajaDirectaRecuperaUnaVezSinContarSupervivientes()throws Exception {
        elegir(TipoMejora.ROBAR_ENERGIA);elegir(TipoMejora.ROBAR_ENERGIA);
        Nave4 nave=nave(100,100);Enemigo debil=new AlienEnemigo(160,100,1,2,textura);
        Enemigo resistente=new AlienEnemigo(240,100,1,10,textura);
        List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(debil,resistente));PantallaJuego pantalla=pantalla(nave,enemigos);
        dash(nave);assertEquals(60f,nave.getEnergiaDash().getEnergia(),0f);
        invocar(pantalla,"actualizarDanoDash");invocar(pantalla,"actualizarDanoDash");
        invocar(pantalla,"resolverDerrotaDash",debil);invocar(pantalla,"resolverDerrotaDash",resistente);
        assertEquals(66f,nave.getEnergiaDash().getEnergia(),0f);assertEquals(1,enemigos.size());
        assertEquals(80f,resistente.getVidaActual(),0f);assertEquals(30,p.getExperiencia());
    }
    @Test public void explosionDelDashCuentaVictimaDirectaYAdyacenteSinDuplicar()throws Exception {
        elegir(TipoMejora.ROBAR_ENERGIA);elegir(TipoMejora.DASH_EXPLOSIVO);
        Nave4 nave=nave(100,100);Enemigo objetivo=new AlienEnemigo(160,100,1,3,textura);
        Enemigo adyacente=new AlienEnemigo(160,180,1,1,textura);
        List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(objetivo,adyacente));PantallaJuego pantalla=pantalla(nave,enemigos);
        dash(nave);invocar(pantalla,"actualizarDanoDash");invocar(pantalla,"actualizarDanoDash");
        assertEquals(66f,nave.getEnergiaDash().getEnergia(),0f);assertTrue(enemigos.isEmpty());assertEquals(60,p.getExperiencia());
    }
    @Test public void otrasBajasNoRobanEnergiaYLaRecuperacionNoSuperaElMaximo()throws Exception {
        elegir(TipoMejora.ROBAR_ENERGIA);
        Nave4 nave=nave(100,100);Enemigo objetivo=new AlienEnemigo(160,100,1,2,textura);
        List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(objetivo));PantallaJuego pantalla=pantalla(nave,enemigos);
        dash(nave);objetivo.recibirDano(20f);invocar(pantalla,"resolverDerrota",objetivo);
        assertEquals(60f,nave.getEnergiaDash().getEnergia(),0f);
        for(int i=0;i<14;i++)elegir(TipoMejora.ROBAR_ENERGIA);
        Enemigo otro=new AlienEnemigo(160,100,1,2,textura);enemigos.add(otro);otro.recibirDano(20f);
        invocar(pantalla,"resolverDerrotaDash",otro);assertEquals(100f,nave.getEnergiaDash().getEnergia(),0f);
    }
    @Test public void sinLaMejoraElDashMataSinRecuperarEnergia()throws Exception {
        Nave4 nave=nave(100,100);Enemigo objetivo=new AlienEnemigo(160,100,1,2,textura);
        List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(objetivo));PantallaJuego pantalla=pantalla(nave,enemigos);
        dash(nave);invocar(pantalla,"actualizarDanoDash");assertTrue(enemigos.isEmpty());
        assertEquals(60f,nave.getEnergiaDash().getEnergia(),0f);
    }
}
