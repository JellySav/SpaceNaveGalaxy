package puppy.code;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import com.badlogic.gdx.math.Rectangle;

public class ColisionesNavesTest extends EntornoGdxPrueba {
    private AlienEnemigo verde(float x, float y) {
        return new AlienEnemigo(x, y, 100f, 2, textura);
    }

    private AlienExplosivo roja(float x, float y) {
        return new AlienExplosivo(x, y, 48f, 100f, 2, textura);
    }

    private void resolver(List<Enemigo> enemigos) {
        new ColisionesNaves().resolver(enemigos, MundoJuego.ANCHO, MundoJuego.ALTO);
    }

    @Test
    public void verdesYRojasSeSeparanSinDano() {
        AlienEnemigo verde = verde(100f, 100f);
        AlienExplosivo roja = roja(140f, 100f);
        resolver(Arrays.asList(verde, roja));
        assertFalse(verde.getArea().overlaps(roja.getArea()));
        assertEquals(1f, verde.getFraccionResistencia(), 0f);
        assertEquals(1f, roja.getFraccionResistencia(), 0f);
        assertEquals(verde.getArea().x + roja.getArea().x, 240f, 0.001f);
    }

    @Test
    public void tambienColisionanNavesDelMismoTipoYEnPosicionIdentica() {
        for (boolean rojas : new boolean[]{false, true}) {
            AlienEnemigo a = rojas ? roja(100f, 100f) : verde(100f, 100f);
            AlienEnemigo b = rojas ? roja(100f, 100f) : verde(100f, 100f);
            resolver(Arrays.asList(a, b));
            assertFalse(a.getArea().overlaps(b.getArea()));
        }
    }

    @Test
    public void grupoEnUnaEsquinaNoSeSuperponeNiSaleDelMundo() {
        List<Enemigo> enemigos = new ArrayList<>();
        for (int i = 0; i < 6; i++) enemigos.add(i % 2 == 0 ? verde(0f, 0f) : roja(0f, 0f));
        resolver(enemigos);
        for (int i = 0; i < enemigos.size(); i++) {
            Rectangle area = enemigos.get(i).getArea();
            assertTrue(area.x >= 0f && area.y >= 0f);
            assertTrue(area.x + area.width <= MundoJuego.ANCHO);
            assertTrue(area.y + area.height <= MundoJuego.ALTO);
            for (int j = i + 1; j < enemigos.size(); j++) {
                assertFalse(enemigos.get(i).getArea().overlaps(enemigos.get(j).getArea()));
            }
        }
    }

    @Test
    public void meteoritosJefesYMuertosNoBloqueanAUnaNave() {
        AlienEnemigo verde = verde(100f, 100f);
        AlienExplosivo muerta = roja(100f, 100f);
        muerta.recibirImpacto();
        muerta.recibirImpacto();
        Enemigo asteroide = new AsteroideEnemigo(100f, 100f, 48f, 100f, 100f, textura);
        JefeSector jefe = new JefeSector(100f, 100f, 100f, 30f, 10,
                SectorGalactico.CINTURON_DE_VELA, textura, textura);
        resolver(Arrays.asList(verde, muerta, asteroide, jefe));
        assertEquals(100f, verde.getArea().x, 0f);
        assertEquals(100f, verde.getArea().y, 0f);
    }

    @Test
    public void correccionDePosicionSeConservaAlVolverAMoverse() {
        AlienEnemigo a = verde(100f, 100f);
        AlienEnemigo b = verde(140f, 100f);
        resolver(Arrays.asList(a, b));
        float despues = a.getArea().x;
        a.actualizar(0.01f, MundoJuego.ANCHO, MundoJuego.ALTO,
                despues + 500f, a.getArea().y + 24f);
        assertEquals(despues + 1f, a.getArea().x, 0.001f);
    }

    @Test
    public void persecucionDuranteVariosFramesMantieneLaSeparacion() {
        List<Enemigo> enemigos = Arrays.asList(verde(400f, 400f), roja(700f, 400f));
        for (int frame = 0; frame < 240; frame++) {
            for (Enemigo enemigo : enemigos) {
                enemigo.actualizar(0.05f, MundoJuego.ANCHO, MundoJuego.ALTO, 600f, 424f);
            }
            resolver(enemigos);
            assertFalse(enemigos.get(0).getArea().overlaps(enemigos.get(1).getArea()));
        }
    }
}
