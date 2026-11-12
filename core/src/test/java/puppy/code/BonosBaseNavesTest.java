package puppy.code;
import org.junit.Test;
import static org.junit.Assert.*;
public class BonosBaseNavesTest {
 private final ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
 private void elegir(TipoMejora tipo){p.subirNivelPruebas();assertTrue(p.elegirMejora(tipo));}
 @Test public void dashTieneBase299YMejorasSumanSobreEsaBase(){
  try{p.reiniciar();p.elegirMejora(TipoMejora.INICIAL_DASH);
   assertEquals(299f,p.getVelocidad(),.001f);assertEquals(2.5f,p.getCadencia(),.001f);
   elegir(TipoMejora.MOVIMIENTO);elegir(TipoMejora.MOVIMIENTO);assertEquals(328.9f,p.getVelocidad(),.001f);
   assertFalse(p.elegirMejora(TipoMejora.INICIAL_DASH));assertEquals(328.9f,p.getVelocidad(),.001f);
  }finally{p.reiniciar();}
 }
 @Test public void criticoTieneCadenciaBase275YDuplicacionDelActual(){
  try{p.reiniciar();p.elegirMejora(TipoMejora.INICIAL_CRITICO);
   assertEquals(2.75f,p.getCadencia(),.001f);assertEquals(260f,p.getVelocidad(),.001f);
   elegir(TipoMejora.CADENCIA);elegir(TipoMejora.CADENCIA_EPICA);assertEquals(3.9875f,p.getCadencia(),.001f);
   elegir(TipoMejora.CADENCIA_DOBLE);assertEquals(7.975f,p.getCadencia(),.001f);
  }finally{p.reiniciar();}
 }
 @Test public void dronYReinicioConservanValoresOriginales(){
  try{p.reiniciar();p.elegirMejora(TipoMejora.INICIAL_DASH);p.reiniciar();p.elegirMejora(TipoMejora.INICIAL_CRITICO);p.reiniciar();p.elegirMejora(TipoMejora.INICIAL_DRON);
   assertEquals(260f,p.getVelocidad(),.001f);assertEquals(2.5f,p.getCadencia(),.001f);
  }finally{p.reiniciar();}
 }
}
