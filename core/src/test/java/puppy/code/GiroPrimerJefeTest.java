package puppy.code;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import java.util.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
public class GiroPrimerJefeTest extends EntornoGdxPrueba {
 private JefeSector jefe(){return new JefeSector(550,350,100,34,40,SectorGalactico.CINTURON_DE_VELA,textura,textura);}
 private Field angle()throws Exception{Field f=Enemigo.class.getDeclaredField("anguloApariencia");f.setAccessible(true);return f;}
 @Test public void giraSesentaPorSegundoSinSobrepasarElObjetivo()throws Exception{
  JefeSector jefe=jefe();jefe.actualizar(.25f,1200,800,1000,400);assertEquals(-15f,angle().getFloat(jefe),.001f);
  jefe.actualizar(.5f,1200,800,1000,400);assertEquals(-45f,angle().getFloat(jefe),.001f);
  jefe.actualizar(1f,1200,800,1000,400);assertEquals(-90f,angle().getFloat(jefe),.001f);
 }
 @Test public void cruzaElLimiteAngularPorElCaminoCorto()throws Exception{
  JefeSector jefe=jefe();angle().setFloat(jefe,179f);
  double target=Math.toRadians(-179+90);float x=600+(float)Math.cos(target)*300,y=400+(float)Math.sin(target)*300;
  jefe.actualizar(.01f,1200,800,x,y);assertEquals(179.6f,angle().getFloat(jefe),.001f);
  jefe.actualizar(.1f,1200,800,x,y);assertEquals(181f,angle().getFloat(jefe),.001f);
 }
 @Test public void disparaPorElCanonSinSaltarHaciaElJugador()throws Exception{
  JefeSector jefe=jefe();jefe.actualizar(.5f,1200,800,1000,400);assertEquals(-30f,angle().getFloat(jefe),.001f);
  List<ProyectilHostil> balas=new ArrayList<>();jefe.actualizarAtaque(1.8f,1000,400,balas::add);
  assertEquals(-30f,angle().getFloat(jefe),.001f);
  Field field=ProyectilHostil.class.getDeclaredField("sprite");field.setAccessible(true);Sprite s=(Sprite)field.get(balas.get(1));
  float x=s.getX(),y=s.getY();balas.get(1).actualizar(.1f,1200,800);
  assertTrue(s.getX()>x);assertTrue(s.getY()>y);assertEquals((float)Math.sqrt(3)*(s.getX()-x),s.getY()-y,.001f);
 }
}
