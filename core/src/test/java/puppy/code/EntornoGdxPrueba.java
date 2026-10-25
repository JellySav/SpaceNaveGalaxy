package puppy.code;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;

/** Prueba el movimiento y el dano reales sin abrir ventana ni usar audio/OpenGL reales. */
public abstract class EntornoGdxPrueba {
    protected final Set<Integer> pulsadas = new HashSet<>();
    protected final Set<Integer> nuevas = new HashSet<>();
    private Application appAnterior;
    private Input inputAnterior;
    private Graphics graphicsAnterior;
    private GL20 glAnterior;
    private GL20 gl20Anterior;
    private Object preferenciasAnteriores;
    private Field campoPreferencias;
    protected Texture textura;
    protected Sound sonido;
    protected boolean wasd;

    @SuppressWarnings("unchecked")
    private static <T> T simular(Class<T> tipo, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, handler);
    }

    private static Object valorVacio(Class<?> tipo) {
        if (tipo == boolean.class) return false;
        if (tipo == int.class) return 0;
        if (tipo == long.class) return 0L;
        if (tipo == float.class) return 0f;
        return null;
    }

    @Before
    public void preparar() throws Exception {
        ProgresionMejoras.obtenerInstancia().reiniciar();
        appAnterior = Gdx.app;
        inputAnterior = Gdx.input;
        graphicsAnterior = Gdx.graphics;
        glAnterior = Gdx.gl;
        gl20Anterior = Gdx.gl20;
        campoPreferencias = PreferenciasJuego.class.getDeclaredField("preferencias");
        campoPreferencias.setAccessible(true);
        preferenciasAnteriores = campoPreferencias.get(PreferenciasJuego.obtenerInstancia());
        campoPreferencias.set(PreferenciasJuego.obtenerInstancia(), null);
        Preferences preferencias = simular(Preferences.class, (p, m, a) -> {
            if (m.getName().equals("getBoolean")) return wasd;
            if (a != null && a.length == 2 && m.getName().startsWith("get")) return a[1];
            return valorVacio(m.getReturnType());
        });
        Gdx.app = simular(Application.class, (p, m, a) ->
                m.getName().equals("getPreferences") ? preferencias : valorVacio(m.getReturnType()));
        Gdx.input = simular(Input.class, (p, m, a) -> {
            if (m.getName().equals("isKeyPressed")) return pulsadas.contains((Integer) a[0]);
            if (m.getName().equals("isKeyJustPressed")) return nuevas.contains((Integer) a[0]);
            return valorVacio(m.getReturnType());
        });
        Gdx.graphics = simular(Graphics.class, (p, m, a) -> valorVacio(m.getReturnType()));
        Gdx.gl = Gdx.gl20 = simular(GL20.class, (p, m, a) -> valorVacio(m.getReturnType()));
        TextureData datos = simular(TextureData.class, (p, m, a) -> {
            if (m.getName().equals("getType")) return TextureData.TextureDataType.Custom;
            if (m.getName().equals("isPrepared")) return true;
            if (m.getName().equals("getWidth") || m.getName().equals("getHeight")) return 32;
            return valorVacio(m.getReturnType());
        });
        textura = new Texture(datos);
        sonido = simular(Sound.class, (p, m, a) -> valorVacio(m.getReturnType()));
    }

    @After
    public void restaurar() throws Exception {
        ProgresionMejoras.obtenerInstancia().reiniciar();
        if (textura != null) textura.dispose();
        campoPreferencias.set(PreferenciasJuego.obtenerInstancia(), preferenciasAnteriores);
        Gdx.app = appAnterior;
        Gdx.input = inputAnterior;
        Gdx.graphics = graphicsAnterior;
        Gdx.gl = glAnterior;
        Gdx.gl20 = gl20Anterior;
    }

    protected Nave4 nave(int x, int y) { return new Nave4(x, y, textura, sonido, textura, sonido); }

}
