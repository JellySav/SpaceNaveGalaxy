package puppy.code;

import static org.junit.Assert.*;

import puppy.code.combate.*;
import puppy.code.enemigos.*;
import puppy.code.mejoras.*;
import puppy.code.nave.*;

import java.util.*;
import java.lang.reflect.Field;
import org.junit.Test;
import com.badlogic.gdx.math.Vector2;

public class MiniDronCopiaTest extends EntornoGdxPrueba {
    private final ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
    private void elegir(TipoMejora tipo){p.subirNivelPruebas();assertTrue(p.elegirMejora(tipo));}
    private List<Enemigo> objetivos(){return Arrays.asList(new AlienEnemigo(600,600,100,100,textura));}
    private List<Bullet> disparar(DronEscolta mini){
        List<Bullet> balas=new ArrayList<>();mini.actualizar(1f/p.getCadenciaMiniDron()+0.01f,400,400,objetivos(),textura,balas::add);return balas;
    }
    private List<RayoLaser> dispararRayos(DronEscolta mini){
        mini.actualizar(1f/p.getCadenciaMiniDron()+0.01f,400,400,objetivos(),textura,b->{});
        return mini.extraerRayos();
    }
    private void comprobar(Bullet bala,float dano,boolean misil)throws Exception {
        assertEquals(dano,bala.getDano(),0.001f);
        Field campo=Bullet.class.getDeclaredField("soloExplosion");campo.setAccessible(true);assertEquals(misil,campo.getBoolean(bala));
    }
    private void comprobarRayo(DronEscolta mini,float dano){
        List<RayoLaser> rayos=dispararRayos(mini);assertEquals(1,rayos.size());
        assertEquals(dano,rayos.get(0).getDano(),0.001f);
    }
    @Test public void cicloCompletoIncluyePropioYCopiasConMitadDelDanoActual()throws Exception {
        assertTrue(p.elegirMejora(TipoMejora.INICIAL_DRON));
        elegir(TipoMejora.DRON);elegir(TipoMejora.DRON);
        elegir(TipoMejora.DRON_EXPLOSIVO);elegir(TipoMejora.DRON_EXPLOSIVO);elegir(TipoMejora.DRON_EXPLOSIVO);
        elegir(TipoMejora.DRON_LASER);elegir(TipoMejora.DRON_LASER);
        elegir(TipoMejora.APOYO_REFORZADO);elegir(TipoMejora.APOYO_REFORZADO);
        DronEscolta mini=new DronEscolta(textura,false,true);
        for(int i=0;i<12;i++) {
            int turno=i%4;
            if(turno==3)comprobarRayo(mini,21f);
            else {
                List<Bullet> balas=disparar(mini);assertEquals(1,balas.size());
                comprobar(balas.get(0),new float[]{6f,7f,8f}[turno],turno==2);
            }
        }
    }
    @Test public void desbloqueosDuranteLaPartidaSeIncorporanSinCopiarDronesBloqueados()throws Exception {
        p.elegirMejora(TipoMejora.INICIAL_DRON);DronEscolta mini=new DronEscolta(textura,false,true);
        comprobar(disparar(mini).get(0),5,false);
        elegir(TipoMejora.DRON_LASER);comprobarRayo(mini,15);
        elegir(TipoMejora.DRON);elegir(TipoMejora.DRON);
        comprobar(disparar(mini).get(0),5,false);comprobar(disparar(mini).get(0),6,false);
        elegir(TipoMejora.DRON_EXPLOSIVO);comprobar(disparar(mini).get(0),5,true);
        comprobarRayo(mini,15);
        elegir(TipoMejora.DRON_LASER);disparar(mini);disparar(mini);disparar(mini);
        comprobarRayo(mini,18);
    }
    @Test public void noAvanzaSinObjetivoNiDesactivadoYConservaTurnoAlRecrearElDron()throws Exception {
        p.elegirMejora(TipoMejora.INICIAL_DRON);elegir(TipoMejora.DRON);elegir(TipoMejora.DRON);
        DronEscolta mini=new DronEscolta(textura,false,true);List<Bullet> ninguna=new ArrayList<>();
        mini.actualizar(3f,400,400,Collections.emptyList(),textura,ninguna::add);assertTrue(ninguna.isEmpty());
        comprobar(disparar(mini).get(0),5,false);
        mini.desactivar(3f);mini.actualizar(1f,400,400,objetivos(),textura,ninguna::add);assertTrue(ninguna.isEmpty());
        DronEscolta siguienteRonda=new DronEscolta(textura,false,true);
        comprobar(disparar(siguienteRonda).get(0),6,false);
        p.reiniciar();p.elegirMejora(TipoMejora.INICIAL_DRON);elegir(TipoMejora.DRON);elegir(TipoMejora.DRON);
        comprobar(disparar(new DronEscolta(textura,false,true)).get(0),5,false);
    }
    @Test public void cadenciaMiniNoCopiaLaCadenciaAjenaYOtrosDronesNoAlteranSuTurno()throws Exception {
        p.elegirMejora(TipoMejora.INICIAL_DRON);elegir(TipoMejora.DRON);elegir(TipoMejora.DRON);
        DronEscolta mini=new DronEscolta(textura,false,true);
        comprobar(disparar(mini).get(0),5,false);
        for(DronEscolta otro:new DronEscolta[]{new DronEscolta(textura),new DronEscolta(textura,true),new DronLaser(textura)})
            otro.actualizar(0.2f,400,400,objetivos(),textura,b->{});
        List<Bullet> balas=new ArrayList<>();mini.actualizar(2.19f,400,400,objetivos(),textura,balas::add);assertTrue(balas.isEmpty());
        mini.actualizar(0.02f,400,400,objetivos(),textura,balas::add);assertEquals(1,balas.size());comprobar(balas.get(0),6,false);
    }
    @Test public void copiasConservanExplosionYPerforacionDelLaser()throws Exception {
        p.elegirMejora(TipoMejora.INICIAL_DRON);elegir(TipoMejora.DRON_EXPLOSIVO);elegir(TipoMejora.DRON_LASER);
        DronEscolta mini=new DronEscolta(textura,false,true);disparar(mini);
        Bullet misil=disparar(mini).get(0);
        com.badlogic.gdx.math.Rectangle area=misil.getArea();
        Enemigo objetivo=new AlienEnemigo(area.x,area.y,100,10,textura);
        assertTrue(misil.checkCollision(objetivo));assertEquals(100f,objetivo.getVidaActual(),0f);
        List<ExplosionBala> explosiones=misil.extraerExplosiones();assertEquals(1,explosiones.size());
        explosiones.get(0).aplicar(Arrays.asList(objetivo),e->fail());assertEquals(95f,objetivo.getVidaActual(),0f);
        RayoLaser laser=dispararRayos(mini).get(0);
        Field inicio=RayoLaser.class.getDeclaredField("inicio"),fin=RayoLaser.class.getDeclaredField("fin");inicio.setAccessible(true);fin.setAccessible(true);
        Vector2 a=(Vector2)inicio.get(laser),b=(Vector2)fin.get(laser);List<Enemigo> enemigos=new ArrayList<>();
        for(float t:new float[]{0.3f,0.7f})enemigos.add(new AlienEnemigo(a.x+(b.x-a.x)*t-24,a.y+(b.y-a.y)*t-24,100,10,textura));
        laser.aplicar(enemigos,e->fail());laser.aplicar(enemigos,e->fail());
        for(Enemigo enemigo:enemigos)assertEquals(85f,enemigo.getVidaActual(),0.001f);
    }
    @Test public void avanzadoMantieneCincoDisparosOMisilesORayosPorTurno()throws Exception {
        p.elegirMejora(TipoMejora.INICIAL_DRON);elegir(TipoMejora.DRON);elegir(TipoMejora.DRON_EXPLOSIVO);elegir(TipoMejora.DRON_LASER);elegir(TipoMejora.AVANZADO);
        DronEscolta mini=new DronEscolta(textura,false,true);
        for(int i=0;i<4;i++) {
            if(i==3) {
                List<RayoLaser> rayos=dispararRayos(mini);assertEquals(5,rayos.size());
                for(RayoLaser rayo:rayos)assertEquals(15f,rayo.getDano(),0f);
                Field fin=RayoLaser.class.getDeclaredField("fin");fin.setAccessible(true);
                Set<String> destinos=new HashSet<>();for(RayoLaser rayo:rayos)destinos.add(fin.get(rayo).toString());assertEquals(5,destinos.size());
            } else {
                List<Bullet> balas=disparar(mini);assertEquals(5,balas.size());
                for(Bullet bala:balas)comprobar(bala,5f,i==2);
            }
        }
    }
}
