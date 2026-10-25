package puppy.code;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import com.badlogic.gdx.math.Rectangle;

public class MeteoritosSecundariosTest extends EntornoGdxPrueba {
    @Before
    public void reiniciarPartida() { ProgresionMejoras.obtenerInstancia().reiniciar(); }

    @Test
    public void entranDesdeCuatroBordesYNoRebotanAlSalir() {
        AsteroideEnemigo[] meteoritos = {
            new AsteroideEnemigo(-60f, 100f, 48f, 150f, 0f, textura),
            new AsteroideEnemigo(1208f, 100f, 48f, -150f, 0f, textura),
            new AsteroideEnemigo(100f, -60f, 48f, 0f, 150f, textura),
            new AsteroideEnemigo(100f, 808f, 48f, 0f, -150f, textura)
        };
        for (AsteroideEnemigo meteorito : meteoritos) {
            float vx = meteorito.getVelocidadX();
            float vy = meteorito.getVelocidadY();
            assertFalse(meteorito.haSalidoDelEscenario(1200f, 800f));
            meteorito.actualizar(0.1f, 1200f, 800f, 0f, 0f);
            assertFalse(meteorito.haSalidoDelEscenario(1200f, 800f));
            meteorito.actualizar(10f, 1200f, 800f, 0f, 0f);
            assertTrue(meteorito.haSalidoDelEscenario(1200f, 800f));
            assertEquals(vx, meteorito.getVelocidadX(), 0f);
            assertEquals(vy, meteorito.getVelocidadY(), 0f);
        }
    }

    @Test
    public void tercerJefeNoSaleDelEscenarioAlPerseguir() {
        JefeSector jefe = new JefeSector(1100f, 600f, 100f, 30f, 10,
                SectorGalactico.FRONTERA_DE_KHEPRI, textura, textura);
        jefe.actualizar(1f, 1200f, 800f, 1300f, 650f);
        assertEquals(1100f, jefe.getArea().x, 0f);
        assertTrue(jefe.getVelocidadX() < 0f);
    }

    @Test
    public void generadorEspaciaEntradasYLimitaLaCantidadSinAfectarsePorLasNaves() {
        LluviaMeteoritos lluvia = new LluviaMeteoritos(3, false, new Random(0));
        List<Enemigo> enemigos = new ArrayList<>();
        enemigos.add(new AlienEnemigo(400f, 400f, 100f, 2, textura));
        lluvia.actualizar(1.49f, enemigos, textura);
        assertEquals(1, enemigos.size());
        lluvia.actualizar(0f, enemigos, textura);
        assertEquals(1, enemigos.size());
        lluvia.actualizar(0.02f, enemigos, textura);
        assertEquals(2, enemigos.size());
        assertFalse(enemigos.get(1).getArea().overlaps(new Rectangle(0f, 0f, 1200f, 800f)));
        for (int i = 0; i < 20; i++) lluvia.actualizar(3.5f, enemigos, textura);
        assertEquals(5, enemigos.size()); // Una nave y como maximo cuatro meteoritos.
    }

    @Test
    public void contraJefesHayMenosTraficoYSeRecuperaEspacioTrasUnaBaja() {
        LluviaMeteoritos lluvia = new LluviaMeteoritos(4, true, new Random(1));
        List<Enemigo> enemigos = new ArrayList<>();
        lluvia.actualizar(1.5f, enemigos, textura);
        lluvia.actualizar(3.5f, enemigos, textura);
        assertEquals(1, enemigos.size());
        lluvia.actualizar(1.5f, enemigos, textura);
        assertEquals(2, enemigos.size());
        for (int i = 0; i < 10; i++) lluvia.actualizar(5f, enemigos, textura);
        assertEquals(3, enemigos.size());
        enemigos.remove(0);
        lluvia.actualizar(5f, enemigos, textura);
        assertEquals(3, enemigos.size());
    }

    @Test
    public void trayectoriasAleatoriasCruzanHastaElBordeOpuesto() {
        Rectangle pantalla = new Rectangle(0f, 0f, 1200f, 800f);
        for (int semilla = 0; semilla < 40; semilla++) {
            List<Enemigo> enemigos = new ArrayList<>();
            new LluviaMeteoritos(3, false, new Random(semilla)).actualizar(1.5f, enemigos, textura);
            AsteroideEnemigo meteorito = (AsteroideEnemigo) enemigos.get(0);
            Rectangle inicio = new Rectangle(meteorito.getArea());
            boolean entro = false;
            for (int frame = 0; frame < 400 && !meteorito.haSalidoDelEscenario(1200f, 800f); frame++) {
                meteorito.actualizar(0.05f, 1200f, 800f, 0f, 0f);
                entro |= meteorito.getArea().overlaps(pantalla);
            }
            assertTrue(entro);
            assertTrue(meteorito.haSalidoDelEscenario(1200f, 800f));
            Rectangle fin = meteorito.getArea();
            if (inicio.x < 0f) assertTrue(fin.x >= 1200f);
            else if (inicio.x >= 1200f) assertTrue(fin.x + fin.width <= 0f);
            else if (inicio.y < 0f) assertTrue(fin.y >= 800f);
            else assertTrue(fin.y + fin.height <= 0f);
        }
    }

    @Test
    public void soloMeteoritosNoBloqueanLaRondaPeroUnaNaveOJefeSi() {
        AsteroideEnemigo meteorito = new AsteroideEnemigo(100f, 100f, 48f, 100f, 0f, textura);
        List<Enemigo> enemigos = new ArrayList<>(Arrays.asList(meteorito));
        assertTrue(OleadaNaves.estaSuperada(enemigos));
        AlienEnemigo nave = new AlienEnemigo(500f, 300f, 100f, 2, textura);
        enemigos.add(nave);
        assertFalse(OleadaNaves.estaSuperada(enemigos));
        nave.recibirImpacto();
        nave.recibirImpacto();
        assertTrue(OleadaNaves.estaSuperada(enemigos));
        enemigos.add(new JefeSector(500f, 600f, 100f, 30f, 40,
                SectorGalactico.CINTURON_DE_VELA, textura, textura));
        assertFalse(OleadaNaves.estaSuperada(enemigos));
        assertTrue(OleadaNaves.cantidadInicial(1) > 0); // La primera ronda tiene un objetivo real.
        assertEquals(0, OleadaNaves.cantidadInicial(4));
        assertTrue(OleadaNaves.cantidadInicial(5) > 3);
        assertTrue(OleadaNaves.cantidadInicial(6) > 6);
    }

    private void poner(PantallaJuego pantalla, String campo, Object valor) throws Exception {
        Field f = PantallaJuego.class.getDeclaredField(campo);
        f.setAccessible(true);
        f.set(pantalla, valor);
    }

    @SuppressWarnings("unchecked")
    private <T> T leer(PantallaJuego pantalla, String campo) throws Exception {
        Field f = PantallaJuego.class.getDeclaredField(campo);
        f.setAccessible(true);
        return (T) f.get(pantalla);
    }

    private void invocar(PantallaJuego pantalla, String nombre, Class<?> tipo, Object argumento) throws Exception {
        Method metodo = PantallaJuego.class.getDeclaredMethod(nombre, tipo);
        metodo.setAccessible(true);
        metodo.invoke(pantalla, argumento);
    }

    private PantallaJuego pantallaSinVentana(List<Enemigo> enemigos) throws Exception {
        // Solo en pruebas: evita abrir OpenGL para comprobar actualizacion y recompensas reales.
        Class<?> tipo = Class.forName("sun.misc.Unsafe");
        Field f = tipo.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        PantallaJuego pantalla = (PantallaJuego) tipo.getMethod("allocateInstance", Class.class)
                .invoke(f.get(null), PantallaJuego.class);
        poner(pantalla, "enemigos", enemigos);
        poner(pantalla, "nave", nave(800, 600));
        poner(pantalla, "colisionesNaves", new ColisionesNaves());
        poner(pantalla, "game", new SpaceNavigation());
        poner(pantalla, "cargasEnergia", new ArrayList<CargaEnergia>());
        poner(pantalla, "baterias", new ArrayList<BateriaEnergia>());
        poner(pantalla, "random", new Random() {
            @Override public float nextFloat() { return 0.2f; }
        });
        poner(pantalla, "sector", SectorGalactico.CINTURON_DE_VELA);
        poner(pantalla, "progresion", ProgresionMejoras.obtenerInstancia());
        poner(pantalla, "explosiones", new SistemaExplosiones());
        poner(pantalla, "explosionSound", sonido);
        poner(pantalla, "bulletTexture", textura);
        return pantalla;
    }

    @Test
    public void bateriasLlenasPermanecenSinDarPuntosYSeRecogenAlHaberEspacio() throws Exception {
        ProgresionMejoras progreso=ProgresionMejoras.obtenerInstancia();
        for(int i=0;i<3;i++)progreso.registrarBateria();
        progreso.sumarPuntos(240);PantallaJuego pantalla=pantallaSinVentana(new ArrayList<>());poner(pantalla,"score",240);
        List<BateriaEnergia> baterias=leer(pantalla,"baterias");
        baterias.add(new BateriaEnergia(800f,600f,textura));baterias.add(new BateriaEnergia(800f,600f,textura));
        invocar(pantalla,"actualizarBaterias",float.class,0.01f);
        assertEquals(2,baterias.size());assertEquals(3,progreso.getBaterias());
        assertEquals(Integer.valueOf(240),leer(pantalla,"score"));assertEquals(1,progreso.getNivel());assertEquals(240,progreso.getExperiencia());
        progreso.consumirBateria();invocar(pantalla,"actualizarBaterias",float.class,0.01f);
        assertEquals(1,baterias.size());assertEquals(3,progreso.getBaterias());assertEquals(Integer.valueOf(240),leer(pantalla,"score"));
        assertEquals(240,progreso.getExperiencia());
    }

    @Test
    public void bateriaConEspacioSeGuardaSinDarPuntos() throws Exception {
        ProgresionMejoras progreso=ProgresionMejoras.obtenerInstancia();
        progreso.registrarBateria();progreso.registrarBateria();
        PantallaJuego pantalla=pantallaSinVentana(new ArrayList<>());
        List<BateriaEnergia> baterias=leer(pantalla,"baterias");
        baterias.add(new BateriaEnergia(800f,600f,textura));
        invocar(pantalla,"actualizarBaterias",float.class,0.01f);
        assertTrue(baterias.isEmpty());assertEquals(3,progreso.getBaterias());
        assertEquals(Integer.valueOf(0),leer(pantalla,"score"));
        assertEquals(0,progreso.getExperiencia());
    }

    @Test
    public void meteoritoQueSaleSeRetiraSinPuntosNiRecursos() throws Exception {
        List<Enemigo> enemigos = new ArrayList<>();
        enemigos.add(new AsteroideEnemigo(1190f, 100f, 48f, 200f, 0f, textura));
        PantallaJuego pantalla = pantallaSinVentana(enemigos);
        invocar(pantalla, "actualizarEnemigos", float.class, 0.1f);
        assertTrue(enemigos.isEmpty());
        assertEquals(Integer.valueOf(0), leer(pantalla, "score"));
        assertTrue(((List<?>) leer(pantalla, "cargasEnergia")).isEmpty());
        assertTrue(((List<?>) leer(pantalla, "baterias")).isEmpty());
    }

    @Test
    public void destruirMeteoritoDaRecursosUnaSolaVezYChocarSigueDanando() throws Exception {
        AsteroideEnemigo meteorito = new AsteroideEnemigo(100f, 100f, 48f, 100f, 0f, textura);
        List<Enemigo> enemigos = new ArrayList<>(Arrays.asList(meteorito));
        PantallaJuego pantalla = pantallaSinVentana(enemigos);
        Bullet bala = new Bullet(100f, 100f, 0f, 200f, textura);
        assertTrue(bala.checkCollision(meteorito));
        assertTrue(meteorito.estaDestruido());
        invocar(pantalla, "resolverDerrota", Enemigo.class, meteorito);
        invocar(pantalla, "resolverDerrota", Enemigo.class, meteorito);
        assertEquals(Integer.valueOf(15), leer(pantalla, "score"));
        assertEquals(15,ProgresionMejoras.obtenerInstancia().getExperiencia());
        List<CargaEnergia> cargas = leer(pantalla, "cargasEnergia");
        assertEquals(1, cargas.size());
        assertEquals(20f, cargas.get(0).getCantidad(), 0f);
        assertEquals(0, ((List<?>) leer(pantalla, "baterias")).size());
        Nave4 nave = nave(100, 100);
        assertTrue(nave.checkCollision(new AsteroideEnemigo(100f, 100f, 48f, 100f, 0f, textura)));
        assertEquals(2, nave.getVidas());
    }

    @Test public void energiaDaCincoPuntosConBarraLlenaOIncompletaSinDuplicados() throws Exception {
        ProgresionMejoras progreso=ProgresionMejoras.obtenerInstancia();progreso.sumarPuntos(245);
        PantallaJuego pantalla=pantallaSinVentana(new ArrayList<>());poner(pantalla,"score",245);
        List<CargaEnergia> cargas=leer(pantalla,"cargasEnergia");Nave4 jugador=leer(pantalla,"nave");
        cargas.add(new CargaEnergia(jugador.getCentroX(),jugador.getCentroY(),20f));
        invocar(pantalla,"actualizarCargasEnergia",float.class,0f);
        assertTrue(cargas.isEmpty());assertEquals(100f,jugador.getEnergiaDash().getEnergia(),0f);
        assertEquals(Integer.valueOf(250),leer(pantalla,"score"));assertEquals(2,progreso.getNivel());assertEquals(0,progreso.getExperiencia());
        invocar(pantalla,"actualizarCargasEnergia",float.class,0f);assertEquals(Integer.valueOf(250),leer(pantalla,"score"));
        jugador.getEnergiaDash().iniciar();assertEquals(60f,jugador.getEnergiaDash().getEnergia(),0f);
        cargas.add(new CargaEnergia(jugador.getCentroX(),jugador.getCentroY(),20f));
        invocar(pantalla,"actualizarCargasEnergia",float.class,0f);
        assertEquals(80f,jugador.getEnergiaDash().getEnergia(),0f);assertEquals(Integer.valueOf(255),leer(pantalla,"score"));assertEquals(5,progreso.getExperiencia());
        CargaEnergia expirada=new CargaEnergia(jugador.getCentroX(),jugador.getCentroY(),20f);expirada.actualizar(8f);cargas.add(expirada);
        invocar(pantalla,"actualizarCargasEnergia",float.class,0f);assertTrue(cargas.isEmpty());assertEquals(Integer.valueOf(255),leer(pantalla,"score"));
    }
    @Test public void bateriasCaenAlDiezPorCientoEnNavesYCincoEnMeteoritosSinBonusDeSector() throws Exception {
        for(SectorGalactico sector:SectorGalactico.values())for(boolean nave:new boolean[]{true,false}) {
            float limite=nave?0.1f:0.05f;
            for(float valor:new float[]{limite-0.001f,limite,0.2f}) {
                Enemigo enemigo=nave?new AlienEnemigo(200,200,100,3,textura):new AsteroideEnemigo(200,200,48,100,0,textura);
                enemigo.recibirDano(1000f);PantallaJuego pantalla=pantallaSinVentana(new ArrayList<>(Arrays.asList(enemigo)));
                poner(pantalla,"sector",sector);poner(pantalla,"random",new Random(){public float nextFloat(){return valor;}});
                invocar(pantalla,"resolverDerrota",Enemigo.class,enemigo);
                assertEquals(valor<limite?1:0,((List<?>)leer(pantalla,"baterias")).size());
                assertEquals(Integer.valueOf(nave?30:15),leer(pantalla,"score"));
            }
        }
    }
    @Test public void jefeDa255PuntosYBateriaSoloConEspacioSinPuntajePorExcedente() throws Exception {
        ProgresionMejoras progreso=ProgresionMejoras.obtenerInstancia();
        for(int guardadas:new int[]{2,3}) {
            progreso.reiniciar();for(int i=0;i<guardadas;i++)progreso.registrarBateria();
            JefeSector jefe=new JefeSector(500,500,100,30,40,SectorGalactico.CINTURON_DE_VELA,textura,textura);jefe.recibirDano(400f);
            PantallaJuego pantalla=pantallaSinVentana(new ArrayList<>(Arrays.asList(jefe)));
            invocar(pantalla,"resolverDerrota",Enemigo.class,jefe);invocar(pantalla,"resolverDerrota",Enemigo.class,jefe);
            assertEquals(Integer.valueOf(255),leer(pantalla,"score"));assertEquals(3,progreso.getBaterias());
            assertEquals(2,progreso.getNivel());assertEquals(5,progreso.getExperiencia());assertTrue(((List<?>)leer(pantalla,"baterias")).isEmpty());
        }
    }
}
