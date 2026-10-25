package puppy.code;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class RefuerzosJefeTest extends EntornoGdxPrueba {
    private JefeSector jefe(SectorGalactico sector) {
        return new JefeSector(400f, 600f, 100f, 30f, 10, sector, textura, textura);
    }

    @Test
    public void esperaCincoSegundosYRepiteElIntervalo() {
        JefeSector jefe = jefe(SectorGalactico.CINTURON_DE_VELA);
        List<AlienEnemigo> refuerzos = new ArrayList<>();
        jefe.actualizarRefuerzos(4.9f, 113f, 2, textura, refuerzos::add);
        assertTrue(refuerzos.isEmpty());
        jefe.actualizarRefuerzos(0.11f, 113f, 2, textura, refuerzos::add);
        assertEquals(1, refuerzos.size());
        jefe.actualizarRefuerzos(4.98f, 113f, 2, textura, refuerzos::add);
        assertEquals(1, refuerzos.size());
        jefe.actualizarRefuerzos(0.02f, 113f, 2, textura, refuerzos::add);
        assertEquals(2, refuerzos.size());
    }

    @Test
    public void refuerzoSaleDelJefeEnSuPosicionActualYTieneEstadisticasDeLaRonda() {
        JefeSector jefe = jefe(SectorGalactico.CINTURON_DE_VELA);
        jefe.actualizar(1f, MundoJuego.ANCHO, MundoJuego.ALTO, 0f, 0f);
        List<AlienEnemigo> refuerzos = new ArrayList<>();
        jefe.actualizarRefuerzos(5f, 113f, 2, textura, refuerzos::add);
        AlienEnemigo alien = refuerzos.get(0);
        assertEquals(jefe.getArea().x + jefe.getArea().width / 2f - 24f, alien.getArea().x, 0.001f);
        assertEquals(jefe.getArea().y - 48f, alien.getArea().y, 0.001f);
        float inicio = alien.getArea().x;
        alien.actualizar(0.1f, MundoJuego.ANCHO, MundoJuego.ALTO,
                inicio + 500f, alien.getArea().y + 24f);
        assertEquals(11.3f, alien.getArea().x - inicio, 0.001f);
        alien.recibirImpacto();
        assertTrue(alien.estaActivo());
        alien.recibirImpacto();
        assertTrue(alien.estaDestruido());
    }

    @Test
    public void alMorirElJefeDejaDeCrearRefuerzos() {
        JefeSector jefe = jefe(SectorGalactico.CINTURON_DE_VELA);
        List<AlienEnemigo> refuerzos = new ArrayList<>();
        jefe.actualizarRefuerzos(5f, 113f, 2, textura, refuerzos::add);
        while (jefe.estaActivo()) jefe.recibirImpacto();
        jefe.actualizarRefuerzos(30f, 113f, 2, textura, refuerzos::add);
        assertEquals(1, refuerzos.size());
        assertTrue(refuerzos.get(0).estaActivo());
    }

    @Test
    public void otrosJefesConservanIntervaloYVarianSusRefuerzos() {
        for (SectorGalactico sector : new SectorGalactico[]{
                SectorGalactico.NEBULOSA_DE_ION}) {
            List<AlienEnemigo> refuerzos = new ArrayList<>();
            JefeSector jefe = jefe(sector);
            float intervalo = sector == SectorGalactico.NEBULOSA_DE_ION ? 5f : 4f;
            jefe.actualizarRefuerzos(intervalo - 0.1f, 180f, 4, textura, refuerzos::add);
            assertTrue(refuerzos.isEmpty());
            jefe.actualizarRefuerzos(0.11f, 180f, 4, textura, refuerzos::add);
            assertEquals(1, refuerzos.size());
            assertFalse(refuerzos.get(0) instanceof AlienExplosivo);
            jefe.actualizarRefuerzos(intervalo, 180f, 4, textura, refuerzos::add);
            assertEquals(2, refuerzos.size());
            assertTrue(refuerzos.get(1) instanceof AlienExplosivo);
            jefe.actualizarRefuerzos(intervalo, 180f, 4, textura, refuerzos::add);
            assertEquals(3, refuerzos.size());
            assertFalse(refuerzos.get(2) instanceof AlienExplosivo);
            assertEquals(sector==SectorGalactico.NEBULOSA_DE_ION,refuerzos.get(2) instanceof AlienArtillero);
            jefe.actualizarRefuerzos(intervalo,180f,4,textura,refuerzos::add);
            assertEquals(4,refuerzos.size());
            assertEquals(sector==SectorGalactico.FRONTERA_DE_KHEPRI,refuerzos.get(3) instanceof AlienExplosivo);
            if(sector==SectorGalactico.NEBULOSA_DE_ION)assertEquals(AlienEnemigo.class,refuerzos.get(3).getClass());
        }
    }

    @Test
    public void primerJefeSigueLanzandoSoloVerdes() {
        List<AlienEnemigo> refuerzos = new ArrayList<>();
        jefe(SectorGalactico.CINTURON_DE_VELA).actualizarRefuerzos(20f, 113f, 2,
                textura, refuerzos::add);
        assertEquals(4, refuerzos.size());
        for (AlienEnemigo refuerzo : refuerzos) assertFalse(refuerzo instanceof AlienExplosivo);
    }

    @Test
    public void rojaInvocadaConservaResistenciaYVelocidadPropias() {
        List<AlienEnemigo> refuerzos = new ArrayList<>();
        jefe(SectorGalactico.NEBULOSA_DE_ION).actualizarRefuerzos(10f, 200f, 6,
                textura, refuerzos::add);
        AlienEnemigo roja = refuerzos.get(1);
        assertTrue(roja instanceof AlienExplosivo);
        float x = roja.getArea().x;
        roja.actualizar(0.1f, MundoJuego.ANCHO, MundoJuego.ALTO,
                x + 500f, roja.getArea().y + 24f);
        assertEquals(18f, roja.getArea().x - x, 0.001f);
        roja.recibirImpacto();
        assertTrue(roja.estaActivo());
        roja.recibirImpacto();
        assertTrue(roja.estaActivo());
        roja.recibirImpacto();
        assertTrue(roja.estaDestruido());
    }

    @Test
    public void sinTiempoDeJuegoNoAvanzaLaInvocacion() {
        JefeSector jefe = jefe(SectorGalactico.CINTURON_DE_VELA);
        List<AlienEnemigo> refuerzos = new ArrayList<>();
        jefe.actualizarRefuerzos(4f, 113f, 2, textura, refuerzos::add);
        jefe.actualizarRefuerzos(0f, 113f, 2, textura, refuerzos::add);
        jefe.actualizarRefuerzos(-10f, 113f, 2, textura, refuerzos::add);
        assertTrue(refuerzos.isEmpty());
        jefe.actualizarRefuerzos(1f, 113f, 2, textura, refuerzos::add);
        assertEquals(1, refuerzos.size());
    }

    @Test
    public void primerJefePermaneceFijoYApuntaAlJugador() throws Exception {
        JefeSector jefe=jefe(SectorGalactico.CINTURON_DE_VELA);
        java.lang.reflect.Field angulo=Enemigo.class.getDeclaredField("anguloApariencia");angulo.setAccessible(true);
        float[][] objetivos={{750,650,-90},{450,750,0},{450,350,-180},{150,650,90}};
        for(float[] o:objetivos){
            jefe.actualizar(4f,1200f,800f,o[0],o[1]);
            assertEquals(400f,jefe.getArea().x,0.001f);assertEquals(600f,jefe.getArea().y,0.001f);
            assertEquals(0f,jefe.getVelocidadX(),0f);assertEquals(0f,jefe.getVelocidadY(),0f);
            assertEquals(0f,((angulo.getFloat(jefe)-o[2])%360f+540f)%360f-180f,0.001f);
        }
    }

    @Test
    public void primerJefeDisparaDesdeLaPuntaInclusoConJugadorMuyCerca() throws Exception {
        JefeSector jefe=jefe(SectorGalactico.CINTURON_DE_VELA);
        List<ProyectilHostil> balas=new ArrayList<>();
        jefe.actualizar(2f,1200,800,451f,650f);
        jefe.actualizarAtaque(1.8f,451f,650f,balas::add);
        java.lang.reflect.Field campo=ProyectilHostil.class.getDeclaredField("sprite");campo.setAccessible(true);
        com.badlogic.gdx.graphics.g2d.Sprite s=(com.badlogic.gdx.graphics.g2d.Sprite)campo.get(balas.get(1));
        assertEquals(490.8f,s.getX(),0.001f);assertEquals(638f,s.getY(),0.001f);
        balas.get(1).actualizar(.1f,1200,800);assertEquals(513.8f,s.getX(),0.001f);
    }

    @Test
    public void tercerJefePersigueAlJugador() {
        for (SectorGalactico sector : new SectorGalactico[]{
                SectorGalactico.FRONTERA_DE_KHEPRI}) {
            JefeSector jefe = jefe(sector);
            jefe.actualizar(1f, 1200f, 800f, 0f, 0f);
            assertTrue(jefe.getArea().x<400f);
            assertTrue(jefe.getArea().y<600f);
            assertEquals(30f,(float)Math.hypot(jefe.getVelocidadX(),jefe.getVelocidadY()),.001f);
        }
    }

    @Test public void primerJefeDisparaTresEnAbanicoCadaUnoComaOchoSegundos() throws Exception {
        JefeSector jefe=jefe(SectorGalactico.CINTURON_DE_VELA);
        List<ProyectilHostil> balas=new ArrayList<>();
        jefe.actualizar(3f,1200,800,450f,100f);
        jefe.actualizarAtaque(1.79f,450f,100f,balas::add);assertTrue(balas.isEmpty());
        jefe.actualizarAtaque(0.02f,450f,100f,balas::add);assertEquals(3,balas.size());
        java.lang.reflect.Field campo=ProyectilHostil.class.getDeclaredField("sprite");campo.setAccessible(true);
        float[] avances=new float[3];
        for(int i=0;i<3;i++) {
            com.badlogic.gdx.graphics.g2d.Sprite sprite=(com.badlogic.gdx.graphics.g2d.Sprite)campo.get(balas.get(i));
            float x=sprite.getX(),y=sprite.getY();balas.get(i).actualizar(0.1f,1200f,800f);
            avances[i]=sprite.getX()-x;
            assertTrue(sprite.getY()<y);
            assertEquals(23f,(float)Math.hypot(sprite.getX()-x,sprite.getY()-y),0.001f);
        }
        assertTrue(avances[0]<0f);assertEquals(0f,avances[1],0.001f);assertTrue(avances[2]>0f);
        assertEquals(-avances[0],avances[2],0.001f);
        jefe.actualizarAtaque(1.8f,450f,100f,balas::add);assertEquals(6,balas.size());
    }
}
