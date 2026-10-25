package puppy.code;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class SistemaMejorasTest extends EntornoGdxPrueba {
    private final ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
    private void elegir(TipoMejora tipo){p.sumarPuntos(p.getRequisitoNivel()-p.getExperiencia());assertTrue(p.elegirMejora(tipo));}

    @Test public void umbralesCrecenVeinticincoConSobrantesYVariasElecciones() {
        p.sumarPuntos(249);assertEquals(1,p.getNivel());assertEquals(249,p.getExperiencia());
        assertEquals(250,p.getRequisitoNivel());
        p.sumarPuntos(326);assertEquals(3,p.getNivel());assertEquals(50,p.getExperiencia());
        assertEquals(300,p.getRequisitoNivel());assertEquals(2,p.getPendientes());
        assertTrue(p.elegirMejora(TipoMejora.DANO));assertEquals(1,p.getPendientes());
        assertEquals(50,p.getExperiencia());assertTrue(p.elegirMejora(TipoMejora.DASH));
        assertFalse(p.elegirMejora(TipoMejora.VIDA));assertEquals(0,p.getPendientes());
    }
    @Test public void porcentajesSobreBaseYMultiplicadoresSobreActualRespetanOrden() {
        elegir(TipoMejora.DANO);assertEquals(11.5f,p.getDano(),0.0001f);
        elegir(TipoMejora.DANO_DOBLE);assertEquals(23f,p.getDano(),0.0001f);
        elegir(TipoMejora.DANO);assertEquals(24.5f,p.getDano(),0.0001f);
        elegir(TipoMejora.DANO_EPICO);assertEquals(27.5f,p.getDano(),0.0001f);
        elegir(TipoMejora.CADENCIA);elegir(TipoMejora.CADENCIA_EPICA);
        assertEquals(3.625f,p.getCadencia(),0.0001f);
        elegir(TipoMejora.CADENCIA_DOBLE);elegir(TipoMejora.CADENCIA);
        assertEquals(7.625f,p.getCadencia(),0.0001f);
        elegir(TipoMejora.MOVIMIENTO);elegir(TipoMejora.MOVIMIENTO);
        assertEquals(286f,p.getVelocidad(),0.0001f);
        elegir(TipoMejora.DASH);elegir(TipoMejora.DASH);assertEquals(40f,p.getDanoDash(),0.0001f);
        elegir(TipoMejora.TAMANO);assertEquals(1.8f,p.getTamano(),0.0001f);
        elegir(TipoMejora.TAMANO);assertEquals(2.6f,p.getTamano(),0.0001f);
    }
    @Test public void vidaAmpliaMaximoYCuraSoloElIncrementoYReparacionRespetaMaximo() {
        Nave4 nave=nave(100,100);nave.setVidas(1);
        elegir(TipoMejora.VIDA);nave.aplicarMejoras();
        assertEquals(4,nave.getVidasMaximas());assertEquals(2,nave.getVidas());
        elegir(TipoMejora.VIDA_EPICA);nave.aplicarMejoras();
        assertEquals(6,nave.getVidasMaximas());assertEquals(4,nave.getVidas());
        nave.aplicarMejoras();assertEquals(4,nave.getVidas());
        nave.reparar();nave.reparar();nave.reparar();assertEquals(6,nave.getVidas());
        Nave4 siguiente=nave(100,100);siguiente.setVidas(4);
        assertEquals(6,siguiente.getVidasMaximas());assertEquals(4,siguiente.getVidas());
    }
    @Test public void probabilidadesSeLimitanYCatalogoExcluyeOpcionesAgotadas() {
        for(int i=1;i<=3;i++){elegir(TipoMejora.PENETRACION);assertEquals(Math.min(1f,i*0.4f),p.getPenetracion(),0.0001f);}
        for(int i=1;i<=10;i++){elegir(TipoMejora.EXPLOSION);assertEquals(Math.min(1f,i*0.1f),p.getExplosion(),0.0001f);}
        elegir(TipoMejora.DOBLE);elegir(TipoMejora.TRIPLE);
        assertEquals(1f,p.getPenetracion(),0f);assertEquals(1f,p.getExplosion(),0f);
        p.sumarPuntos(p.getRequisitoNivel());int antes=p.getPendientes();
        assertFalse(p.elegirMejora(TipoMejora.PENETRACION));assertEquals(antes,p.getPendientes());
        Random random=new Random(5);
        for(int i=0;i<1000;i++)for(TipoMejora tipo:CatalogoMejoras.sortear(p,random))
            assertTrue(tipo.disponible(p));
    }
    @Test public void sorteosIndependientesRespetanSetentaVeintidosOcho() {
        int[] cantidades=new int[3];Random random=new Random(15);
        for(int i=0;i<10000;i++) {
            List<TipoMejora> opciones=CatalogoMejoras.sortear(p,random);
            assertEquals(3,opciones.size());
            for(TipoMejora opcion:opciones)cantidades[opcion.rareza.ordinal()]++;
        }
        assertEquals(21000,cantidades[0],350);assertEquals(6600,cantidades[1],300);
        assertEquals(2400,cantidades[2],200);
    }
    @Test public void dronesSeAdquierenYMejoranIndependientementeSinHerenciaDelJugador() {
        elegir(TipoMejora.DRON);elegir(TipoMejora.DRON_EXPLOSIVO);
        assertTrue(p.tieneDron()&&p.tieneDronExplosivo());
        assertEquals(10f,p.getDanoDron(false),0f);assertEquals(10f,p.getDanoDron(true),0f);
        elegir(TipoMejora.DRON);assertEquals(12f,p.getDanoDron(false),0.0001f);
        assertEquals(10f,p.getDanoDron(true),0f);
        elegir(TipoMejora.DANO_DOBLE);assertEquals(12f,p.getDanoDron(false),0.0001f);
        assertTrue(TipoMejora.DRON.titulo(p).startsWith("Mejora"));
        assertEquals(ProgresionMejoras.CADENCIA_DRON_BASE*1.2f,p.getCadenciaDron(false),0.0001f);
        p.reiniciar();assertEquals(1,p.getNivel());assertEquals(3,p.getVidasMaximas());
        assertFalse(p.tieneDron()||p.tieneDronExplosivo()||p.tieneDoble()||p.tieneTriple());
        assertEquals(10f,p.getDano(),0f);assertEquals(2.5f,p.getCadencia(),0f);
    }
    @Test public void quintupleSeObtieneEnCualquierOrdenYNoConsumeBaterias() {
        for(boolean triplePrimero:new boolean[]{false,true}) {
            p.reiniciar();p.registrarBateria();
            elegir(triplePrimero?TipoMejora.TRIPLE:TipoMejora.DOBLE);
            elegir(triplePrimero?TipoMejora.DOBLE:TipoMejora.TRIPLE);
            assertEquals(4,p.getNivelArma());assertFalse(p.tieneDron());assertEquals(1,p.getBaterias());
            List<Bullet> balas=new ArrayList<>();
            new DisparoQuintuple().disparar(500f,400f,0f,1f,textura,balas::add);
            assertEquals(5,balas.size());
        }
    }

    @Test public void mejorDashAcumulaDistanciaBaseYConservaDuracionCosteYDano() {
        elegir(TipoMejora.DISTANCIA_DASH);elegir(TipoMejora.DISTANCIA_DASH);
        assertEquals(RarezaMejora.COMUN,TipoMejora.DISTANCIA_DASH.rareza);
        assertTrue(TipoMejora.DISTANCIA_DASH.disponible(p));
        Nave4 nave=nave(100,100);
        nuevas.add(com.badlogic.gdx.Input.Keys.SHIFT_LEFT);
        nave.actualizar(null,EnergiaDash.DURACION);
        assertEquals(304f,nave.getArea().y,0.01f);
        assertFalse(nave.getEnergiaDash().estaActivo());
        assertEquals(60f,nave.getEnergiaDash().getEnergia(),0.001f);
        assertEquals(20f,p.getDanoDash(),0f);
        p.reiniciar();assertEquals(1000f,p.getVelocidadDash(),0f);
    }

    @Test public void reparacionMejoradaSeObtieneUnaVezYDesapareceDelCatalogo() {
        assertEquals(3f,p.getDuracionReparacion(),0f);
        elegir(TipoMejora.REPARACION);
        assertEquals(RarezaMejora.EPICA,TipoMejora.REPARACION.rareza);
        assertEquals(2f,p.getDuracionReparacion(),0f);
        p.subirNivelPruebas();int pendientes=p.getPendientes();
        assertFalse(p.elegirMejora(TipoMejora.REPARACION));
        assertEquals(pendientes,p.getPendientes());
        Random random=new Random(7);
        for(int i=0;i<1000;i++)assertFalse(CatalogoMejoras.sortear(p,random).contains(TipoMejora.REPARACION));
        p.reiniciar();assertTrue(TipoMejora.REPARACION.disponible(p));
        assertEquals(3f,p.getDuracionReparacion(),0f);
    }

    @Test public void reparacionRealCuraAlCumplirDosSegundosConMejora() throws Exception {
        elegir(TipoMejora.REPARACION);p.registrarBateria();
        Class<?> unsafe=Class.forName("sun.misc.Unsafe");
        java.lang.reflect.Field instancia=unsafe.getDeclaredField("theUnsafe");instancia.setAccessible(true);
        PantallaJuego pantalla=(PantallaJuego)unsafe.getMethod("allocateInstance",Class.class)
                .invoke(instancia.get(null),PantallaJuego.class);
        Nave4 nave=nave(100,100);nave.setVidas(1);
        java.lang.reflect.Field campo=PantallaJuego.class.getDeclaredField("nave");campo.setAccessible(true);campo.set(pantalla,nave);
        campo=PantallaJuego.class.getDeclaredField("progresion");campo.setAccessible(true);campo.set(pantalla,p);
        java.lang.reflect.Method reparar=PantallaJuego.class.getDeclaredMethod("gestionarReparacion",float.class);reparar.setAccessible(true);
        nuevas.add(com.badlogic.gdx.Input.Keys.R);reparar.invoke(pantalla,0f);nuevas.clear();
        assertTrue(nave.isReparando());assertEquals(0,p.getBaterias());
        reparar.invoke(pantalla,1.99f);assertEquals(1,nave.getVidas());assertTrue(nave.isReparando());
        reparar.invoke(pantalla,0.02f);assertEquals(2,nave.getVidas());assertFalse(nave.isReparando());
    }
}
