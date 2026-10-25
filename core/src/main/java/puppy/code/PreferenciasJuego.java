package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

public final class PreferenciasJuego {
    private static final PreferenciasJuego INSTANCIA = new PreferenciasJuego();
    private static final String NOMBRE = "space-navigation";

    private Preferences preferencias;

    private PreferenciasJuego() {
    }

    public static PreferenciasJuego obtenerInstancia() {
        return INSTANCIA;
    }

    private Preferences datos() {
        if (preferencias == null) {
            preferencias = Gdx.app.getPreferences(NOMBRE);
        }
        return preferencias;
    }

    public int getRecord() {
        return datos().getInteger("record", 0);
    }

    public void guardarRecord(int score) {
        if (score > getRecord()) {
            datos().putInteger("record", score).flush();
        }
    }

    public float getVolumenMusica() {
        return datos().getFloat("volumenMusica", 0.5f);
    }

    public void setVolumenMusica(float volumen) {
        guardarVolumen("volumenMusica", volumen);
    }

    public float getVolumenEfectos() {
        return datos().getFloat("volumenEfectos", 0.75f);
    }

    public void setVolumenEfectos(float volumen) {
        guardarVolumen("volumenEfectos", volumen);
    }

    private void guardarVolumen(String clave, float volumen) {
        datos().putFloat(clave, Math.max(0f, Math.min(1f, volumen))).flush();
    }

    public int getTeclaIzquierda() { return Input.Keys.A; }
    public int getTeclaDerecha() { return Input.Keys.D; }
    public int getTeclaAbajo() { return Input.Keys.S; }
    public int getTeclaArriba() { return Input.Keys.W; }
}
