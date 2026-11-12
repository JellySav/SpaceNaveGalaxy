package puppy.code;
import static org.junit.Assert.*;
import org.junit.Test;
public class NaveInvulnerabilidadTest extends EntornoGdxPrueba {
    @Test public void recibirDanoProtegeDuranteUnoComaCincoSegundosYSoloSeRenuevaConOtroImpacto() {
        Nave4 nave=nave(100,100);assertTrue(nave.recibirDano());assertEquals(2,nave.getVidas());
        nave.actualizar(null,1f);assertFalse(nave.recibirDano());assertTrue(nave.estaHerido());
        nave.actualizar(null,0.49f);assertFalse(nave.recibirDano());assertEquals(2,nave.getVidas());
        nave.actualizar(null,0.02f);assertFalse(nave.estaHerido());assertTrue(nave.recibirDano());assertEquals(1,nave.getVidas());
        assertFalse(nave.recibirDano());nave.actualizar(null,1.49f);assertFalse(nave.recibirDano());
        nave.actualizar(null,0.02f);assertTrue(nave.recibirDano());assertTrue(nave.estaDestruido());
    }
}
