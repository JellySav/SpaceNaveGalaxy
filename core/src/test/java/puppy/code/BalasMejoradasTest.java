package puppy.code;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class BalasMejoradasTest extends EntornoGdxPrueba {
    private AlienEnemigo enemigo(float x,int vida){return new AlienEnemigo(x,100f,100f,vida,textura);}
    private Bullet bala(float dano,float perforar,float explotar){return new Bullet(100f,100f,0f,200f,textura).configurar(dano,1f,perforar,explotar,new Random(1));}

    @Test public void escalaDiezYDecimalesTienenEfectoReal() {
        AlienEnemigo enemigo=enemigo(100f,3);assertEquals(30f,enemigo.getVidaMaxima(),0f);
        assertTrue(bala(11.5f,0f,0f).checkCollision(enemigo));
        assertEquals(18.5f,enemigo.getVidaActual(),0f);
        bala(11.5f,0f,0f).checkCollision(enemigo);assertEquals(7f,enemigo.getVidaActual(),0f);
        bala(11.5f,0f,0f).checkCollision(enemigo);assertTrue(enemigo.estaDestruido());
        JefeSector jefe=new JefeSector(100f,100f,100f,30f,260,SectorGalactico.FRONTERA_DE_KHEPRI,textura,textura);
        assertEquals(2600f,jefe.getVidaMaxima(),0f);
    }
    @Test public void perforacionAtraviesaVariosEnemigosSinRepetirDanoAlMismo() {
        AlienEnemigo a=enemigo(100f,3),b=enemigo(110f,3);
        Bullet bala=bala(10f,1f,0f);
        assertTrue(bala.checkCollision(a));assertFalse(bala.isDestroyed());
        assertFalse(bala.checkCollision(a));assertEquals(20f,a.getVidaActual(),0f);
        assertTrue(bala.checkCollision(b));assertEquals(20f,b.getVidaActual(),0f);
        Bullet normal=bala(10f,0f,0f);normal.checkCollision(a);assertTrue(normal.isDestroyed());
        assertFalse(normal.checkCollision(b));
    }
    @Test public void explosionEsInstantaneaPequenaYDanaUnaSolaVez() {
        AlienEnemigo a=enemigo(100f,3),cerca=enemigo(160f,4),lejos=enemigo(230f,4);
        List<Enemigo> enemigos=new ArrayList<>(Arrays.asList(a,cerca,lejos));
        List<Enemigo> muertos=new ArrayList<>();Bullet bala=bala(10f,0f,1f);
        bala.checkCollision(a);assertEquals(20f,a.getVidaActual(),0f);
        List<ExplosionBala> explosiones=bala.extraerExplosiones();assertEquals(1,explosiones.size());
        assertTrue(bala.extraerExplosiones().isEmpty());
        ExplosionBala explosion=explosiones.get(0);explosion.aplicar(enemigos,muertos::add);
        assertTrue(a.estaDestruido());assertEquals(20f,cerca.getVidaActual(),0f);
        assertEquals(40f,lejos.getVidaActual(),0f);assertEquals(1,muertos.size());
        explosion.aplicar(enemigos,muertos::add);assertEquals(20f,cerca.getVidaActual(),0f);
        assertEquals(ExplosionAlien.RADIO/2f,ExplosionBala.RADIO,0f);
    }
    @Test public void misilSoloHaceDanoDeExplosionNormalSinDuplicarlo() {
        AlienEnemigo enemigo=enemigo(100f,3);Bullet misil=bala(10f,0f,0f).comoMisil();
        misil.checkCollision(enemigo);assertEquals(30f,enemigo.getVidaActual(),0f);
        misil.extraerExplosiones().get(0).aplicar(Arrays.asList(enemigo),e->fail());
        assertEquals(20f,enemigo.getVidaActual(),0f);assertTrue(misil.isDestroyed());
    }
    @Test public void probabilidadSeSorteaPorImpactoYTamanosEscalan() {
        AlienEnemigo enemigo=enemigo(100f,10);
        Bullet bala=new Bullet(100f,100f,0f,200f,textura).configurar(10f,1.4f,0f,0.05f,new Random(){@Override public float nextFloat(){return 0.04f;}});
        assertEquals(44.8f,bala.getArea().width,0.001f);bala.checkCollision(enemigo);
        assertEquals(1,bala.extraerExplosiones().size());
        Bullet otra=new Bullet(100f,100f,0f,200f,textura).configurar(10f,1f,0f,0.05f,new Random(){@Override public float nextFloat(){return 0.5f;}});
        otra.checkCollision(enemigo);assertTrue(otra.extraerExplosiones().isEmpty());
    }
    @Test public void ambosDronesDisparanConIgualBaseEnLadosOpuestos() {
        DronEscolta normal=new DronEscolta(textura),explosivo=new DronEscolta(textura,true);
        List<Enemigo> enemigos=Arrays.asList(enemigo(800f,5));
        List<Bullet> a=new ArrayList<>(),b=new ArrayList<>();
        for(int i=0;i<40;i++) {
            normal.actualizar(0.05f,600f,300f,enemigos,textura,a::add);
            explosivo.actualizar(0.05f,600f,300f,enemigos,textura,b::add);
        }
        assertTrue(normal.getArea().x>600f);assertTrue(explosivo.getArea().x<600f);
        assertEquals(a.size(),b.size());assertTrue(a.size()>0);
        assertEquals(10f,a.get(0).getDano(),0f);assertEquals(10f,b.get(0).getDano(),0f);
    }
}
