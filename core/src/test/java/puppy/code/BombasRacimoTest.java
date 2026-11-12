package puppy.code;

import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.*;
import org.junit.Test;

public class BombasRacimoTest extends EntornoGdxPrueba {
    private final ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
    private void elegir(TipoMejora tipo){p.subirNivelPruebas();assertTrue(p.elegirMejora(tipo));}
    private Bullet bala(Random random){return new Bullet(400f,300f,0f,520f,textura).configurarJugador(p,random);}
    private Enemigo objetivo(){return new AlienEnemigo(400f,300f,0f,10,textura);}
    @Test public void epicaNoRepetibleSeExcluyeTrasObtenerlaYSeReinicia() {
        assertEquals(RarezaMejora.EPICA,TipoMejora.RACIMO.rareza);elegir(TipoMejora.RACIMO);
        p.subirNivelPruebas();assertFalse(p.elegirMejora(TipoMejora.RACIMO));assertEquals(1,p.getPendientes());
        Random random=new Random(7);for(int i=0;i<1000;i++)assertFalse(CatalogoMejoras.sortear(p,random).contains(TipoMejora.RACIMO));
        p.reiniciar();assertFalse(p.tieneRacimo());assertTrue(TipoMejora.RACIMO.disponible(p));
    }
    @Test public void cuatroFragmentosTienenUnCuartoDelDanoYNoSeDividenNiRepitenElObjetivo() {
        elegir(TipoMejora.RACIMO);elegir(TipoMejora.DANO_DOBLE);elegir(TipoMejora.DANO_DOBLE);
        Bullet bala=bala(new Random(1));Enemigo enemigo=objetivo();assertTrue(bala.checkCollision(enemigo));
        assertEquals(60f,enemigo.getVidaActual(),0f);List<Bullet> fragmentos=bala.extraerFragmentos();assertEquals(4,fragmentos.size());
        assertTrue(bala.extraerFragmentos().isEmpty());
        for(Bullet fragmento:fragmentos) {
            assertEquals(10f,fragmento.getDano(),0f);assertFalse(fragmento.checkCollision(enemigo));
            Enemigo vecino=objetivo();assertTrue(fragmento.checkCollision(vecino));assertEquals(90f,vecino.getVidaActual(),0f);
            assertTrue(fragmento.extraerFragmentos().isEmpty());
        }
    }
    @Test public void fragmentosSalenEnCuatroDiagonalesSonMasPequenosYExpiranPorDistancia() throws Exception {
        elegir(TipoMejora.RACIMO);Bullet bala=bala(new Random(2));bala.checkCollision(objetivo());
        List<Bullet> fragmentos=bala.extraerFragmentos();Set<String> direcciones=new HashSet<>();
        Field campo=Bullet.class.getDeclaredField("spr");campo.setAccessible(true);
        for(Bullet fragmento:fragmentos) {
            com.badlogic.gdx.graphics.g2d.Sprite sprite=(com.badlogic.gdx.graphics.g2d.Sprite)campo.get(fragmento);
            assertEquals(0.5f,sprite.getScaleX(),0f);
            float x=fragmento.getArea().x,y=fragmento.getArea().y;fragmento.update(0.1f);
            float dx=fragmento.getArea().x-x,dy=fragmento.getArea().y-y;
            assertEquals(36f,(float)Math.hypot(dx,dy),0.001f);assertEquals(Math.abs(dx),Math.abs(dy),0.001f);
            direcciones.add((dx>0?"+":"-")+(dy>0?"+":"-"));
            fragmento.update(0.28f);assertFalse(fragmento.isDestroyed());fragmento.update(0.1f);assertTrue(fragmento.isDestroyed());
            assertEquals(140f,(float)Math.hypot(fragmento.getArea().x-x,fragmento.getArea().y-y),0.01f);
        }
        assertEquals(4,direcciones.size());
    }
    @Test public void fragmentosConservanDanoCriticoSinVolverASortearloYDronesNoSeDividen() {
        p.elegirMejora(TipoMejora.INICIAL_CRITICO);elegir(TipoMejora.RACIMO);
        int[] sorteos={0};Bullet bala=bala(new Random(){public float nextFloat(){sorteos[0]++;return 0f;}});
        assertTrue(bala.esCritico());bala.checkCollision(objetivo());
        for(Bullet fragmento:bala.extraerFragmentos()){assertTrue(fragmento.esCritico());assertEquals(5f,fragmento.getDano(),0f);}
        assertEquals(1,sorteos[0]);
        List<Bullet> dron=new ArrayList<>();new DronEscolta(textura).actualizar(0.2f,400,300,Arrays.asList(objetivo()),textura,dron::add);
        assertEquals(1,dron.size());dron.get(0).checkCollision(new AlienEnemigo(dron.get(0).getArea().x,dron.get(0).getArea().y,0f,10,textura));
        assertTrue(dron.get(0).extraerFragmentos().isEmpty());
    }
    @Test public void laPartidaIncorporaLosCuatroFragmentosSinProcesarlosComoBalasOriginales() throws Exception {
        elegir(TipoMejora.RACIMO);Class<?> tipo=Class.forName("sun.misc.Unsafe");Field unsafe=tipo.getDeclaredField("theUnsafe");unsafe.setAccessible(true);
        PantallaJuego pantalla=(PantallaJuego)tipo.getMethod("allocateInstance",Class.class).invoke(unsafe.get(null),PantallaJuego.class);
        List<Bullet> balas=new ArrayList<>(Arrays.asList(bala(new Random(1))));List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(objetivo()));
        IndiceEspacial indice=new IndiceEspacial(128f);indice.reconstruir(enemigos);
        Map<String,Object> campos=new HashMap<>();campos.put("balas",balas);campos.put("enemigos",enemigos);campos.put("indiceEnemigos",indice);campos.put("explosionesBalas",new ArrayList<ExplosionBala>());
        campos.put("aparienciasProyectiles",new AparienciasProyectiles(new com.badlogic.gdx.graphics.g2d.TextureRegion(textura),
                new com.badlogic.gdx.graphics.g2d.TextureRegion(textura)));
        for(Map.Entry<String,Object> entrada:campos.entrySet()){Field f=PantallaJuego.class.getDeclaredField(entrada.getKey());f.setAccessible(true);f.set(pantalla,entrada.getValue());}
        Method actualizar=PantallaJuego.class.getDeclaredMethod("actualizarBalas",float.class);actualizar.setAccessible(true);actualizar.invoke(pantalla,0f);
        assertEquals(4,balas.size());for(Bullet fragmento:balas)assertEquals(2.5f,fragmento.getDano(),0f);
        actualizar.invoke(pantalla,0f);assertEquals(4,balas.size());assertEquals(90f,enemigos.get(0).getVidaActual(),0f);
    }
}
