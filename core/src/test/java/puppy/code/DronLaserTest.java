package puppy.code;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class DronLaserTest extends EntornoGdxPrueba {
    private final ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
    private void elegir(TipoMejora tipo){p.subirNivelPruebas();assertTrue(p.elegirMejora(tipo));}
    @Test public void seAdquiereComoLegendarioYRepeticionesMejoranSoloSusBases() {
        assertEquals(RarezaMejora.LEGENDARIA,TipoMejora.DRON_LASER.rareza);
        elegir(TipoMejora.DRON_LASER);assertTrue(p.tieneDronLaser());assertTrue(TipoMejora.DRON_LASER.disponible(p));
        assertEquals(30f,p.getDanoLaser(),0f);assertEquals(ProgresionMejoras.CADENCIA_DRON_BASE,p.getCadenciaLaser(),0f);
        elegir(TipoMejora.DRON_LASER);elegir(TipoMejora.DRON_LASER);
        assertEquals(42f,p.getDanoLaser(),0.001f);assertEquals(ProgresionMejoras.CADENCIA_DRON_BASE*1.4f,p.getCadenciaLaser(),0.001f);
        assertEquals("Mejora el dron laser",TipoMejora.DRON_LASER.titulo(p));
        assertEquals(10f,p.getDanoDron(false),0f);assertEquals(10f,p.getDanoDron(true),0f);
        elegir(TipoMejora.DANO_DOBLE);assertEquals(42f,p.getDanoLaser(),0.001f);
        p.reiniciar();assertFalse(p.tieneDronLaser());assertEquals(30f,p.getDanoLaser(),0f);
    }
    @Test public void rayoAtraviesaTodosLosEnemigosYNoRepiteDanoDuranteElDestello() {
        Enemigo primero=new AlienEnemigo(200,80,100,6,textura);
        Enemigo segundo=new AlienEnemigo(500,80,100,6,textura);
        Enemigo fuera=new AlienEnemigo(350,160,100,6,textura);
        List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(primero,segundo,fuera));
        RayoLaser rayo=new RayoLaser(100,100,1,0,30f,textura);
        rayo.aplicar(enemigos,e->fail("Ningun enemigo deberia morir"));
        assertEquals(30f,primero.getVidaActual(),0f);assertEquals(30f,segundo.getVidaActual(),0f);assertEquals(60f,fuera.getVidaActual(),0f);
        for(int i=0;i<3;i++){rayo.update(0.05f);rayo.aplicar(enemigos,e->fail("Dano duplicado"));}
        assertFalse(rayo.isDestroyed());assertEquals(30f,primero.getVidaActual(),0f);
        rayo.update(0.02f);assertTrue(rayo.isDestroyed());
    }
    @Test public void variasBajasDanUnaNotificacionCadaUnaSinProblemasAlModificarLaLista() {
        List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(new AlienEnemigo(200,80,100,3,textura),new AlienEnemigo(500,80,100,3,textura)));
        List<Enemigo> bajas=new ArrayList<>();RayoLaser rayo=new RayoLaser(100,100,1,0,30f,textura);
        rayo.aplicar(enemigos,e->{bajas.add(e);enemigos.remove(e);});
        rayo.aplicar(enemigos,e->fail("Recompensa duplicada"));assertEquals(2,bajas.size());assertTrue(enemigos.isEmpty());
    }
    @Test public void dronEmiteRayosALaCadenciaNormalYRespetaDesactivacion() {
        elegir(TipoMejora.DRON_LASER);DronLaser dron=new DronLaser(textura);
        List<Enemigo> enemigos=Arrays.asList(new AlienEnemigo(600,600,100,6,textura));List<Bullet> rayos=new ArrayList<>();
        dron.actualizar(0.2f,400,400,enemigos,textura,rayos::add);assertEquals(1,rayos.size());assertTrue(rayos.get(0) instanceof RayoLaser);
        assertEquals(30f,rayos.get(0).getDano(),0f);assertEquals(384f,dron.getArea().x,0.001f);assertEquals(272f,dron.getArea().y,0.001f);
        dron.actualizar(1.09f,400,400,enemigos,textura,rayos::add);assertEquals(1,rayos.size());
        dron.actualizar(0.02f,400,400,enemigos,textura,rayos::add);assertEquals(2,rayos.size());
        dron.desactivar(3f);dron.actualizar(2f,400,400,enemigos,textura,rayos::add);assertEquals(2,rayos.size());
        dron.actualizar(1.01f,400,400,enemigos,textura,rayos::add);assertEquals(3,rayos.size());
        List<Bullet> ninguna=new ArrayList<>();dron.actualizar(2f,400,400,Collections.emptyList(),textura,ninguna::add);assertTrue(ninguna.isEmpty());
    }
    @Test public void laserConviveConTodosLosDronesYSeDesactivaConElGrupo() {
        DronEscolta normal=new DronEscolta(textura),explosivo=new DronEscolta(textura,true),mini=new DronEscolta(textura,false,true);
        DronLaser laser=new DronLaser(textura);GrupoApoyos grupo=new GrupoApoyos(normal,explosivo,mini,laser);
        grupo.desactivar(3f);for(ApoyoNave apoyo:new ApoyoNave[]{normal,explosivo,mini,laser})assertEquals(3f,apoyo.getTiempoDesactivado(),0f);
    }

    @Test public void todosLosDronesGiranDetrasDeLaNaveManteniendoLaSeparacion() {
        List<ApoyoNave> drones=Arrays.asList(new DronEscolta(textura),new DronEscolta(textura,true),
                new DronEscolta(textura,false,true),new DronLaser(textura));
        for(float[] direccion:new float[][]{{0,1},{1,0},{0,-1},{-1,0}}) {
            for(ApoyoNave dron:drones) {
                dron.orientarDetras(direccion[0],direccion[1]);
                dron.actualizar(0.2f,600f,400f,Collections.emptyList(),textura,b->fail("Sin enemigos"));
                com.badlogic.gdx.math.Rectangle area=dron.getArea();
                float dx=area.x+area.width/2f-600f,dy=area.y+area.height/2f-400f;
                assertTrue(dx*direccion[0]+dy*direccion[1]<0f);
            }
            for(int i=0;i<drones.size();i++)for(int j=0;j<i;j++)
                assertFalse(drones.get(i).getArea().overlaps(drones.get(j).getArea()));
            com.badlogic.gdx.math.Rectangle laser=drones.get(3).getArea();
            assertEquals(600f-direccion[0]*112f,laser.x+16f,0.001f);
            assertEquals(400f-direccion[1]*112f,laser.y+16f,0.001f);
        }
    }
}
