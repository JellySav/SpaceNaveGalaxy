package puppy.code;

import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.*;
import org.junit.Test;
import com.badlogic.gdx.Input;

public class DashExplosivoTest extends EntornoGdxPrueba {
    private final ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
    private void elegir(TipoMejora tipo){p.subirNivelPruebas();assertTrue(p.elegirMejora(tipo));}
    @Test public void copiasAumentanSoloRadioYDanoSigueAlDashActual() {
        assertEquals(0f,p.getDanoExplosionDash(),0f);assertEquals(0f,p.getRadioExplosionDash(),0f);
        assertEquals(RarezaMejora.COMUN,TipoMejora.DASH_EXPLOSIVO.rareza);
        elegir(TipoMejora.DASH_EXPLOSIVO);
        assertEquals(ExplosionBala.RADIO,p.getRadioExplosionDash(),0f);
        assertEquals(10f,p.getDanoExplosionDash(),0f);
        elegir(TipoMejora.DASH_EXPLOSIVO);elegir(TipoMejora.DASH_EXPLOSIVO);
        assertEquals(120f,p.getRadioExplosionDash(),0f);assertEquals(10f,p.getDanoExplosionDash(),0f);
        elegir(TipoMejora.DASH);assertEquals(15f,p.getDanoExplosionDash(),0f);
        assertTrue(p.elegirMejora(TipoMejora.INICIAL_DASH));elegir(TipoMejora.AVANZADO);
        assertEquals(30f,p.getDanoExplosionDash(),0f);assertTrue(TipoMejora.DASH_EXPLOSIVO.disponible(p));
        p.reiniciar();assertEquals(0f,p.getDanoExplosionDash(),0f);assertEquals(0f,p.getRadioExplosionDash(),0f);
    }
    @Test public void impactoRealExplotaSobreObjetivoYAdyacentesUnaVez() throws Exception {
        elegir(TipoMejora.DASH_EXPLOSIVO);
        Nave4 nave=nave(100,100);
        Enemigo objetivo=new AlienEnemigo(160,100,1,10,textura);
        Enemigo adyacente=new AlienEnemigo(160,180,1,10,textura);
        Enemigo lejos=new AlienEnemigo(160,280,1,10,textura);
        List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(objetivo,adyacente,lejos));
        List<ExplosionBala> explosiones=new ArrayList<>();
        Class<?> tipo=Class.forName("sun.misc.Unsafe");Field unsafe=tipo.getDeclaredField("theUnsafe");unsafe.setAccessible(true);
        PantallaJuego pantalla=(PantallaJuego)tipo.getMethod("allocateInstance",Class.class).invoke(unsafe.get(null),PantallaJuego.class);
        String[] nombres={"nave","enemigos","progresion","explosionesBalas"};Object[] valores={nave,enemigos,p,explosiones};
        for(int i=0;i<nombres.length;i++){Field campo=PantallaJuego.class.getDeclaredField(nombres[i]);campo.setAccessible(true);campo.set(pantalla,valores[i]);}
        Method actualizar=PantallaJuego.class.getDeclaredMethod("actualizarDanoDash");actualizar.setAccessible(true);
        pulsadas.add(Input.Keys.D);nuevas.add(Input.Keys.SHIFT_LEFT);nave.actualizar(null,0.17f);nuevas.clear();
        actualizar.invoke(pantalla);actualizar.invoke(pantalla);
        assertEquals(70f,objetivo.getVidaActual(),0f);assertEquals(90f,adyacente.getVidaActual(),0f);
        assertEquals(100f,lejos.getVidaActual(),0f);assertEquals(1,explosiones.size());assertEquals(3,nave.getVidas());
    }
    @Test public void radioAmpliadoUsaElMismoDanoYNotificaLasBajasSoloUnaVez() {
        elegir(TipoMejora.DASH_EXPLOSIVO);elegir(TipoMejora.DASH_EXPLOSIVO);
        Enemigo objetivo=new AlienEnemigo(180,100,1,1,textura);
        List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(objetivo));List<Enemigo> bajas=new ArrayList<>();
        ExplosionBala normal=new ExplosionBala(100,100,10);
        normal.aplicar(enemigos,bajas::add);assertEquals(10f,objetivo.getVidaActual(),0f);
        ExplosionBala ampliada=new ExplosionBala(100,100,p.getDanoExplosionDash(),p.getRadioExplosionDash());
        ampliada.aplicar(enemigos,e->{bajas.add(e);enemigos.remove(e);});
        ampliada.aplicar(enemigos,bajas::add);assertEquals(1,bajas.size());assertTrue(enemigos.isEmpty());
    }
}
