package puppy.code;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.badlogic.gdx.Input;

public class ModoPruebasTest extends EntornoGdxPrueba {
    private final ProgresionMejoras progreso = ProgresionMejoras.obtenerInstancia();

    @Before
    public void iniciarPartida() { progreso.reiniciar(); }

    @After
    public void limpiarPartida() { progreso.reiniciar(); }

    private void combinar() {
        pulsadas.add(Input.Keys.F8);
        pulsadas.add(Input.Keys.F9);
        nuevas.add(Input.Keys.F9);
    }

    @Test
    public void combinacionAlternaUnaSolaVezPorPulsacion() {
        Nave4 nave = nave(100, 100);
        combinar();
        nave.actualizar(null, 0.016f);
        assertTrue(progreso.esInvulnerablePruebas());
        nuevas.clear();
        for (int i = 0; i < 10; i++) nave.actualizar(null, 0.016f);
        assertTrue(progreso.esInvulnerablePruebas());
        nuevas.add(Input.Keys.F8);
        nave.actualizar(null, 0.016f);
        assertFalse(progreso.esInvulnerablePruebas());
    }

    @Test
    public void teclasPorSeparadoNoLoActivan() {
        Nave4 nave = nave(100, 100);
        for (int tecla : new int[]{Input.Keys.F8, Input.Keys.F9}) {
            pulsadas.clear();
            nuevas.clear();
            pulsadas.add(tecla);
            nuevas.add(tecla);
            nave.actualizar(null, 0.016f);
            assertFalse(progreso.esInvulnerablePruebas());
        }
    }

    @Test
    public void inmortalidadBloqueaDanoYNoDestruyeEnemigosPorContacto() {
        Nave4 nave = nave(100, 100);
        progreso.alternarInvulnerabilidadPruebas();
        AlienEnemigo enemigo = new AlienEnemigo(100f, 100f, 100f, 2, textura);
        for (int i = 0; i < 100; i++) assertFalse(nave.recibirDano());
        assertFalse(nave.checkCollision(enemigo));
        assertTrue(enemigo.estaActivo());
        assertEquals(3, nave.getVidas());
        assertEquals(100f, nave.getEnergiaDash().getEnergia(), 0f);
        progreso.alternarInvulnerabilidadPruebas();
        assertTrue(nave.recibirDano());
        assertEquals(2, nave.getVidas());
    }

    @Test
    public void explosionRojaTampocoDanaAlJugadorInmortal() {
        Nave4 nave = nave(100, 100);
        progreso.alternarInvulnerabilidadPruebas();
        AlienExplosivo roja = new AlienExplosivo(100f, 100f, 48f, 100f, 2, textura);
        roja.recibirImpacto();
        roja.recibirImpacto();
        SistemaExplosiones sistema = new SistemaExplosiones();
        sistema.registrarMuerte(roja);
        sistema.actualizar(0.61f, new ArrayList<>(), nave, null, e -> fail(), () -> {});
        assertEquals(3, nave.getVidas());
    }

    @Test
    public void duraEntreRondasPeroSeBorraAlIniciarOtraPartida() {
        progreso.alternarInvulnerabilidadPruebas();
        Nave4 naveNuevaRonda = nave(100, 100);
        assertFalse(naveNuevaRonda.recibirDano());
        progreso.reiniciar();
        assertFalse(progreso.esInvulnerablePruebas());
        assertTrue(naveNuevaRonda.recibirDano());
    }

    @Test
    public void noSeActivaCuandoLaNaveYaEstaDestruida() {
        Nave4 nave = nave(100, 100);
        nave.setVidas(0);
        combinar();
        nave.actualizar(null, 0.016f);
        assertFalse(progreso.esInvulnerablePruebas());
    }
    private PantallaJuego pantallaParaAtajo(Nave4 nave) throws Exception {
        Class<?> tipo=Class.forName("sun.misc.Unsafe");
        java.lang.reflect.Field u=tipo.getDeclaredField("theUnsafe");u.setAccessible(true);
        PantallaJuego pantalla=(PantallaJuego)tipo.getMethod("allocateInstance",Class.class)
                .invoke(u.get(null),PantallaJuego.class);
        for(String nombre:new String[]{"nave","progresion"}) {
            java.lang.reflect.Field campo=PantallaJuego.class.getDeclaredField(nombre);
            campo.setAccessible(true);campo.set(pantalla,nombre.equals("nave")?nave:progreso);
        }
        return pantalla;
    }

    @Test public void atajoSubeUnNivelConservaExperienciaYNoSeRepiteAlMantener() throws Exception {
        PantallaJuego pantalla=pantallaParaAtajo(nave(100,100));
        progreso.sumarPuntos(240);
        pulsadas.add(Input.Keys.F6);pulsadas.add(Input.Keys.F7);nuevas.add(Input.Keys.F7);
        pantalla.gestionarNivelPruebas();
        assertEquals(2,progreso.getNivel());assertEquals(1,progreso.getPendientes());
        assertEquals(240,progreso.getExperiencia());
        java.lang.reflect.Field score=PantallaJuego.class.getDeclaredField("score");score.setAccessible(true);
        assertEquals(0,score.getInt(pantalla));
        nuevas.clear();
        assertTrue(progreso.elegirMejora(TipoMejora.DANO));
        for(int i=0;i<10;i++)pantalla.gestionarNivelPruebas();
        assertEquals(2,progreso.getNivel());
        nuevas.add(Input.Keys.F6);pantalla.gestionarNivelPruebas();assertEquals(3,progreso.getNivel());
    }

    @Test public void atajoNoFuncionaConUnaSolaTeclaNiConNaveDestruida() throws Exception {
        Nave4 nave=nave(100,100);PantallaJuego pantalla=pantallaParaAtajo(nave);
        for(int tecla:new int[]{Input.Keys.F6,Input.Keys.F7}) {
            pulsadas.clear();nuevas.clear();pulsadas.add(tecla);nuevas.add(tecla);
            pantalla.gestionarNivelPruebas();assertEquals(1,progreso.getNivel());
        }
        nave.setVidas(0);pulsadas.add(Input.Keys.F6);
        pantalla.gestionarNivelPruebas();assertEquals(1,progreso.getNivel());
    }

    @Test public void atajoNoAgregaEleccionesMientrasYaHayUnaPendienteYSeReinicia() throws Exception {
        PantallaJuego pantalla=pantallaParaAtajo(nave(100,100));progreso.sumarPuntos(350);
        pulsadas.add(Input.Keys.F6);pulsadas.add(Input.Keys.F7);nuevas.add(Input.Keys.F6);
        pantalla.gestionarNivelPruebas();assertEquals(2,progreso.getNivel());assertEquals(1,progreso.getPendientes());
        progreso.reiniciar();assertEquals(1,progreso.getNivel());assertEquals(0,progreso.getPendientes());
    }

}
