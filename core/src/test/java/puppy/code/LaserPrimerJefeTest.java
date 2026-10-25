package puppy.code;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.*;
import com.badlogic.gdx.math.Rectangle;
public class LaserPrimerJefeTest extends EntornoGdxPrueba {
 private JefeSector jefe(){return new JefeSector(550,350,100,34,40,SectorGalactico.CINTURON_DE_VELA,textura,textura);}
 private float angulo(JefeSector jefe)throws Exception{Field f=Enemigo.class.getDeclaredField("anguloApariencia");f.setAccessible(true);return f.getFloat(jefe);}
 @Test public void quintoTurnoCargaLaserSinSalvaSimultaneaYCongelaLaMira()throws Exception{
  JefeSector jefe=jefe();List<ProyectilHostil> balas=new ArrayList<>();
  jefe.actualizar(2f,1200,800,1000,400);
  for(int i=0;i<3;i++){jefe.actualizarAtaque(1.8f,1000,400,balas::add);assertNull(jefe.getLaserPrimerJefe());}
  jefe.actualizarAtaque(1.8f,1000,400,balas::add);assertEquals(12,balas.size());
  assertNull(jefe.getLaserPrimerJefe());
  jefe.actualizarAtaque(1.79f,1000,400,balas::add);assertNull(jefe.getLaserPrimerJefe());assertEquals(12,balas.size());
  jefe.actualizarAtaque(.02f,1000,400,balas::add);assertEquals(12,balas.size());
  LaserJefe laser=jefe.getLaserPrimerJefe();assertTrue(laser.estaAvisando());
  jefe.actualizar(.1f,1200,800,600,700);assertEquals(-90,angulo(jefe),.001f);
  jefe.actualizarAtaque(.99f,600,700,balas::add);assertTrue(laser.estaAvisando());assertEquals(12,balas.size());
  jefe.actualizarAtaque(.02f,600,700,balas::add);assertFalse(laser.estaAvisando());assertEquals(-90,angulo(jefe),.001f);
  jefe.actualizar(.5f,1200,800,600,700);assertEquals(-60,angulo(jefe),.001f);
  assertTrue(laser.toca(new Rectangle(900,398,4,4)));assertFalse(laser.toca(new Rectangle(598,650,4,4)));
  jefe.actualizarAtaque(.25f,600,700,balas::add);assertNull(jefe.getLaserPrimerJefe());
  jefe.actualizarAtaque(1.8f,600,700,balas::add);assertEquals(15,balas.size());assertNull(jefe.getLaserPrimerJefe());
 }
 @Test public void avisoNoDaniaYDisparoSoloQuitaUnaVida(){
  LaserJefe laser=new LaserJefe(50,120,1,0);Nave4 nave=nave(100,100);
  laser.comprobarImpacto(nave);assertEquals(3,nave.getVidas());
  laser.actualizar(1f);laser.comprobarImpacto(nave);laser.comprobarImpacto(nave);assertEquals(2,nave.getVidas());
  laser.actualizar(.26f);assertFalse(laser.toca(nave.getArea()));
 }
 @Test public void colisionDiagonalCoincideConLaLineaSinCajaGigante(){
  LaserJefe laser=new LaserJefe(100,100,1,1);laser.actualizar(1f);
  assertTrue(laser.toca(new Rectangle(400,400,5,5)));
  assertFalse(laser.toca(new Rectangle(400,500,5,5)));
  assertFalse(laser.toca(new Rectangle(50,50,5,5)));
 }
 @Test public void anchoDeDanoCoincideConTreintaPixeles(){
  LaserJefe laser=new LaserJefe(50,120,1,0);laser.actualizar(1f);
  assertTrue(laser.toca(new Rectangle(200,133,2,1)));
  assertFalse(laser.toca(new Rectangle(200,136,2,1)));
 }
 @Test public void sinTiempoSeCongelaYAlMorirDesaparece(){
  JefeSector jefe=jefe();for(int i=0;i<5;i++)jefe.actualizarAtaque(1.8f,1000,400,p->{});
  jefe.actualizarAtaque(0,0,0,p->{});jefe.actualizarAtaque(-1,0,0,p->{});assertTrue(jefe.getLaserPrimerJefe().estaAvisando());
  jefe.recibirDano(10000);assertNull(jefe.getLaserPrimerJefe());
 }
}
