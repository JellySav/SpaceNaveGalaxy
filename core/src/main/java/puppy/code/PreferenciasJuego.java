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

    public boolean usaWASD() {
        return datos().getBoolean("controlesWASD", false);
    }

    public void alternarEsquemaControles() {
        datos().putBoolean("controlesWASD", !usaWASD()).flush();
    }

    public int getTeclaIzquierda() {
        return usaWASD() ? Input.Keys.A : Input.Keys.LEFT;
    }

    public int getTeclaDerecha() {
        return usaWASD() ? Input.Keys.D : Input.Keys.RIGHT;
    }

    public int getTeclaAbajo() {
        return usaWASD() ? Input.Keys.S : Input.Keys.DOWN;
    }

    public int getTeclaArriba() {
        return usaWASD() ? Input.Keys.W : Input.Keys.UP;
    }

    public int getTeclaDisparo() {
        return usaWASD() ? Input.Keys.CONTROL_LEFT : Input.Keys.SPACE;
    }
}