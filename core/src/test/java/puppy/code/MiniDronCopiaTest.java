package puppy.code;
import static org.junit.Assert.*;
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
    private void comprobar(Bullet bala,float dano,boolean misil,boolean laser)throws Exception {
        assertEquals(dano,bala.getDano(),0.001f);assertEquals(laser,bala instanceof RayoLaser);
        if(laser)return;
        Field campo=Bullet.class.getDeclaredField("soloExplosion");campo.setAccessible(true);assertEquals(misil,campo.getBoolean(bala));
    }
    @Test public void cicloCompletoIncluyePropioYCopiasConMitadDelDanoActual()throws Exception {
        assertTrue(p.elegirMejora(TipoMejora.INICIAL_DRON));
        elegir(TipoMejora.DRON);elegir(TipoMejora.DRON);
        elegir(TipoMejora.DRON_EXPLOSIVO);elegir(TipoMejora.DRON_EXPLOSIVO);elegir(TipoMejora.DRON_EXPLOSIVO);
        elegir(TipoMejora.DRON_LASER);elegir(TipoMejora.DRON_LASER);
        elegir(TipoMejora.APOYO_REFORZADO);elegir(TipoMejora.APOYO_REFORZADO);
        DronEscolta mini=new DronEscolta(textura,false,true);
        for(int i=0;i<12;i++) {
            List<Bullet> balas=disparar(mini);assertEquals(1,balas.size());int turno=i%4;
            comprobar(balas.get(0),new float[]{6f,7f,8f,21f}[turno],turno==2,turno==3);
        }
    }
    @Test public void desbloqueosDuranteLaPartidaSeIncorporanSinCopiarDronesBloqueados()throws Exception {
        p.elegirMejora(TipoMejora.INICIAL_DRON);DronEscolta mini=new DronEscolta(textura,false,true);
        comprobar(disparar(mini).get(0),5,false,false);
        elegir(TipoMejora.DRON_LASER);comprobar(disparar(mini).get(0),15,false,true);
        elegir(TipoMejora.DRON);elegir(TipoMejora.DRON);
        comprobar(disparar(mini).get(0),5,false,false);comprobar(disparar(mini).get(0),6,false,false);
        elegir(TipoMejora.DRON_EXPLOSIVO);comprobar(disparar(mini).get(0),5,true,false);
        comprobar(disparar(mini).get(0),15,false,true);
        elegir(TipoMejora.DRON_LASER);disparar(mini);disparar(mini);disparar(mini);
        comprobar(disparar(mini).get(0),18,false,true);
    }
    @Test public void noAvanzaSinObjetivoNiDesactivadoYConservaTurnoAlRecrearElDron()throws Exception {
        p.elegirMejora(TipoMejora.INICIAL_DRON);elegir(TipoMejora.DRON);elegir(TipoMejora.DRON);
        DronEscolta mini=new DronEscolta(textura,false,true);List<Bullet> ninguna=new ArrayList<>();
        mini.actualizar(3f,400,400,Collections.emptyList(),textura,ninguna::add);assertTrue(ninguna.isEmpty());
        comprobar(disparar(mini).get(0),5,false,false);
        mini.desactivar(3f);mini.actualizar(1f,400,400,objetivos(),textura,ninguna::add);assertTrue(ninguna.isEmpty());
        DronEscolta siguienteRonda=new DronEscolta(textura,false,true);
        comprobar(disparar(siguienteRonda).get(0),6,false,false);
        p.reiniciar();p.elegirMejora(TipoMejora.INICIAL_DRON);elegir(TipoMejora.DRON);elegir(TipoMejora.DRON);
        comprobar(disparar(new DronEscolta(textura,false,true)).get(0),5,false,false);
    }
    @Test public void cadenciaMiniNoCopiaLaCadenciaAjenaYOtrosDronesNoAlteranSuTurno()throws Exception {
        p.elegirMejora(TipoMejora.INICIAL_DRON);elegir(TipoMejora.DRON);elegir(TipoMejora.DRON);
        DronEscolta mini=new DronEscolta(textura,false,true);
        comprobar(disparar(mini).get(0),5,false,false);
        for(DronEscolta otro:new DronEscolta[]{new DronEscolta(textura),new DronEscolta(textura,true),new DronLaser(textura)})
            otro.actualizar(0.2f,400,400,objetivos(),textura,b->{});
        List<Bullet> balas=new ArrayList<>();mini.actualizar(2.19f,400,400,objetivos(),textura,balas::add);assertTrue(balas.isEmpty());
        mini.actualizar(0.02f,400,400,objetivos(),textura,balas::add);assertEquals(1,balas.size());comprobar(balas.get(0),6,false,false);
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
        RayoLaser laser=(RayoLaser)disparar(mini).get(0);
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
            List<Bullet> balas=disparar(mini);assertEquals(5,balas.size());
            for(Bullet bala:balas)comprobar(bala,i==3?15f:5f,i==2,i==3);
            if(i==3) {
                Field fin=RayoLaser.class.getDeclaredField("fin");fin.setAccessible(true);
                Set<String> destinos=new HashSet<>();for(Bullet bala:balas)destinos.add(fin.get(bala).toString());assertEquals(5,destinos.size());
            }
        }
    }
}
