package puppy.code;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;
public class PersecucionSegundoJefeTest extends EntornoGdxPrueba {
 private JefeSector jefe(){return new JefeSector(400,400,100,30,200,SectorGalactico.NEBULOSA_DE_ION,textura,textura);}
 @Test public void persigueEnDiagonalConVelocidadConstante(){
  JefeSector jefe=jefe();jefe.actualizar(1,1200,800,750,150);
  assertEquals(400+30/Math.sqrt(2),jefe.getArea().x,.001);
  assertEquals(400-30/Math.sqrt(2),jefe.getArea().y,.001);
  float x=jefe.getArea().x;jefe.actualizar(1,1200,800,0,450);assertTrue(jefe.getArea().x<x);
 }
 @Test public void seDetieneDuranteAvisoYBarreraYRetomaDespues(){
  JefeSector jefe=jefe();jefe.actualizarAtaqueEspecial(14,600,100,c->{});
  float x=jefe.getArea().x,y=jefe.getArea().y;
  jefe.actualizar(.5f,1200,800,900,200);assertEquals(x,jefe.getArea().x,0);assertEquals(y,jefe.getArea().y,0);
  jefe.actualizarAtaqueEspecial(1.1f,600,100,c->{});assertFalse(jefe.getBarreraEnergia().estaAvisando());
  jefe.actualizar(1,1200,800,900,200);assertEquals(x,jefe.getArea().x,0);assertEquals(y,jefe.getArea().y,0);
  jefe.actualizarAtaqueEspecial(5,600,100,c->{});assertNull(jefe.getBarreraEnergia());
  jefe.actualizar(1,1200,800,900,200);assertTrue(jefe.getArea().x>x);assertTrue(jefe.getArea().y<y);
 }
 @Test public void quietoSigueDisparandoYGenerandoRefuerzos(){
  JefeSector jefe=jefe();jefe.actualizarAtaqueEspecial(14,600,100,c->{});jefe.actualizar(.1f,1200,800,900,200);
  List<ProyectilHostil> balas=new ArrayList<>();jefe.actualizarAtaque(2.3f,600,100,balas::add);assertEquals(5,balas.size());
  List<AlienEnemigo> aliens=new ArrayList<>();jefe.actualizarRefuerzos(5,100,3,textura,aliens::add);assertEquals(1,aliens.size());
  assertEquals(0,jefe.getVelocidadX(),0);assertEquals(0,jefe.getVelocidadY(),0);
 }
}
