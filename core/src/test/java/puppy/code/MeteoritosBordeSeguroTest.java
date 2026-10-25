package puppy.code;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import com.badlogic.gdx.math.Rectangle;
public class MeteoritosBordeSeguroTest extends EntornoGdxPrueba {
    private int borde(Enemigo enemigo) {
        Rectangle area=enemigo.getArea();
        if(area.x+area.width<0)return 0;if(area.x>MundoJuego.ANCHO)return 1;
        if(area.y>MundoJuego.ALTO)return 2;if(area.y+area.height<0)return 3;
        throw new AssertionError("No aparece desde fuera de la pantalla");
    }
    @Test public void cadaLadoCercanoSeExcluyeYLosOtrosTresSiguenDisponibles() {
        Rectangle[] posiciones={new Rectangle(10,350,45,45),new Rectangle(1145,350,45,45),
                new Rectangle(570,745,45,45),new Rectangle(570,10,45,45)};
        for(boolean jefe:new boolean[]{false,true})for(int prohibido=0;prohibido<4;prohibido++) {
            Set<Integer> vistos=new HashSet<>();
            for(int semilla=0;semilla<300;semilla++) {
                List<Enemigo> enemigos=new ArrayList<>();
                new LluviaMeteoritos(8,jefe,new Random(semilla)).actualizar(1.5f,enemigos,textura,posiciones[prohibido]);
                assertEquals(1,enemigos.size());int entrada=borde(enemigos.get(0));
                assertNotEquals(prohibido,entrada);vistos.add(entrada);
            }
            assertEquals(3,vistos.size());
        }
    }
    @Test public void empatesEnEsquinasYCentroExcluyenAmbosBordesCercanos() {
        Rectangle[] posiciones={new Rectangle(10,10,45,45),new Rectangle(1145,10,45,45),
                new Rectangle(10,745,45,45),new Rectangle(1145,745,45,45),new Rectangle(577.5f,377.5f,45,45)};
        int[][] prohibidos={{0,3},{1,3},{0,2},{1,2},{2,3}};
        for(int caso=0;caso<posiciones.length;caso++) {
            Set<Integer> vistos=new HashSet<>();
            for(int semilla=0;semilla<300;semilla++) {
                List<Enemigo> enemigos=new ArrayList<>();new LluviaMeteoritos(5,false,new Random(semilla)).actualizar(1.5f,enemigos,textura,posiciones[caso]);
                assertEquals(1,enemigos.size());int entrada=borde(enemigos.get(0));
                for(int prohibido:prohibidos[caso])assertNotEquals(prohibido,entrada);vistos.add(entrada);
            }
            assertEquals(2,vistos.size());
        }
    }
    @Test public void recalculaConLaPosicionActualSinAdelantarLaAparicion() {
        Rectangle[] posiciones={new Rectangle(10,350,45,45),new Rectangle(1145,350,45,45),
                new Rectangle(570,745,45,45),new Rectangle(570,10,45,45)};
        for(int semilla=0;semilla<100;semilla++) {
            LluviaMeteoritos lluvia=new LluviaMeteoritos(3,false,new Random(semilla));List<Enemigo> enemigos=new ArrayList<>();
            lluvia.actualizar(1.49f,enemigos,textura,posiciones[0]);assertTrue(enemigos.isEmpty());
            lluvia.actualizar(0.02f,enemigos,textura,posiciones[1]);assertNotEquals(1,borde(enemigos.get(0)));enemigos.clear();
            for(int i=2;i<20;i++) {
                lluvia.actualizar(3.5f,enemigos,textura,posiciones[i%4]);assertEquals(1,enemigos.size());
                assertNotEquals(i%4,borde(enemigos.get(0)));enemigos.clear();
            }
        }
    }
}
