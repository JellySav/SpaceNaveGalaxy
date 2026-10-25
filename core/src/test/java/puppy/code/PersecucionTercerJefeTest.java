package puppy.code;
import org.junit.Test;
import static org.junit.Assert.*;
import com.badlogic.gdx.Input;
public class PersecucionTercerJefeTest extends EntornoGdxPrueba {
 @Test public void terceroEsMasRapidoQueSegundoEnLaPartida(){
  JefeSector segundo=new JefeSector(400,400,100,34*SectorGalactico.NEBULOSA_DE_ION.getFactorDificultad(),200,SectorGalactico.NEBULOSA_DE_ION,textura,textura);
  JefeSector tercero=new JefeSector(400,400,100,34*SectorGalactico.FRONTERA_DE_KHEPRI.getFactorDificultad(),300,SectorGalactico.FRONTERA_DE_KHEPRI,textura,textura);
  segundo.actualizar(1,1200,800,900,450);tercero.actualizar(1,1200,800,900,450);
  assertEquals(38.08f,segundo.getVelocidadX(),.001f);assertEquals(42.5f,tercero.getVelocidadX(),.001f);
 }
 @Test public void quedaQuietoHastaQueAmbasBarrerasTerminan(){
  JefeSector jefe=new JefeSector(400,400,100,42.5f,300,SectorGalactico.FRONTERA_DE_KHEPRI,textura,textura);
  jefe.actualizarAtaqueEspecial(14,600,200,c->{});jefe.actualizar(.1f,1200,800,900,200);
  assertEquals(400,jefe.getArea().x,0);assertEquals(400,jefe.getArea().y,0);
  jefe.actualizarAtaqueEspecial(5,600,200,c->{});assertEquals(1,jefe.getBarrerasEnergia().size());
  jefe.actualizar(.5f,1200,800,900,200);assertEquals(400,jefe.getArea().x,0);
  jefe.actualizarAtaqueEspecial(2,600,200,c->{});assertTrue(jefe.getBarrerasEnergia().isEmpty());
  jefe.actualizar(1,1200,800,900,200);assertTrue(jefe.getArea().x>400);assertTrue(jefe.getArea().y<400);
 }
 @Test public void unDashDiagonalPuedeCruzarAmbasBarreras(){
  ProgresionMejoras.obtenerInstancia().reiniciar();
  AtaqueBarrera ataque=new AtaqueBarrera(true);ataque.actualizar(14,600,400,c->{});ataque.actualizar(2,600,400,c->{});
  Nave4 nave=nave(915,515);pulsadas.add(Input.Keys.W);pulsadas.add(Input.Keys.D);nuevas.add(Input.Keys.SHIFT_LEFT);
  for(int i=0;i<5;i++){
   nave.actualizar(null,.05f);ataque.actualizar(.05f,600,400,c->{});
   for(BarreraEnergia barrera:ataque.getBarreras())barrera.comprobarImpacto(nave);
   nuevas.clear();
  }
  assertEquals(3,nave.getVidas());assertTrue(nave.getArea().x>954);assertTrue(nave.getArea().y>554);
 }
}
