package puppy.code;

import static org.junit.Assert.*;
import org.junit.Test;

public class ReinicioPartidaTest {
    @Test public void esperaAntesDeAceptarUnaConfirmacionIntencional() {
        PantallaGameOver.ConfirmacionReinicio control=new PantallaGameOver.ConfirmacionReinicio();
        assertFalse(control.actualizar(0.2f,true,true));
        assertFalse(control.actualizar(0.3f,false,false));
        assertFalse(control.estaPreparada());
        assertFalse(control.actualizar(0.4f,false,false));
        assertTrue(control.estaPreparada());
        assertTrue(control.actualizar(0.01f,true,true));
    }
    @Test public void teclaOBotonMantenidoAlMorirExigeSoltarYPulsarDeNuevo() {
        PantallaGameOver.ConfirmacionReinicio control=new PantallaGameOver.ConfirmacionReinicio();
        assertFalse(control.actualizar(1f,true,true));
        for(int i=0;i<60;i++)assertFalse(control.actualizar(0.1f,true,true));
        assertFalse(control.estaPreparada());
        assertFalse(control.actualizar(0.1f,false,false));
        assertTrue(control.actualizar(0.1f,true,true));
    }
    @Test public void SinConfirmacionNoReiniciaAunqueSigaJugando() {
        PantallaGameOver.ConfirmacionReinicio control=new PantallaGameOver.ConfirmacionReinicio();
        for(int i=0;i<300;i++)assertFalse(control.actualizar(0.1f,false,false));
        assertFalse(control.actualizar(0.1f,true,false));
    }
}
