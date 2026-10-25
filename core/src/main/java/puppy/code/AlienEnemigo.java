package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public class AlienEnemigo extends Enemigo {
    private final float velocidadPersecucion;
    private float tiempoDireccion;
    private boolean entrando;
    private final float entradaX, entradaY;

    public AlienEnemigo(float x, float y, float velocidadPersecucion,
                        int resistencia, Texture textura) {
        this(x, y, 48f, velocidadPersecucion, resistencia, textura);
    }

    public AlienEnemigo(float x, float y, float tamano, float velocidadPersecucion,
                        int resistencia, Texture textura) {
        super(textura, x, y, tamano, tamano, velocidadPersecucion, -velocidadPersecucion,
                resistencia);
        this.velocidadPersecucion = velocidadPersecucion;
        entrando = y >= MundoJuego.ALTO || x < 0f || x >= MundoJuego.ANCHO;
        entradaX = x < 0f ? 4f : x >= MundoJuego.ANCHO ? MundoJuego.ANCHO - tamano - 4f : x;
        entradaY = y >= MundoJuego.ALTO ? MundoJuego.ALTO - tamano - 4f : y;
        setColor(0.38f, 1f, 0.58f, 1f);
    }

    public static int resistenciaEnRonda(int ronda) {
        return ronda >= 9 ? 7 : Math.min(6, 3 + Math.max(0, ronda - 1) / 2);
    }

    @Override
    protected void actualizarComportamiento(float delta, float objetivoX, float objetivoY) {
        if(getClass()==AlienEnemigo.class)orientarApariencia(objetivoX,objetivoY);
        if (entrando) {
            float dx = entradaX - getArea().x;
            float dy = entradaY - getArea().y;
            float distancia = (float) Math.sqrt(dx * dx + dy * dy);
            if (distancia > 0.01f) {
                float velocidad = Math.min(velocidadPersecucion, distancia / delta);
                setVelocidad(dx / distancia * velocidad, dy / distancia * velocidad);
                return;
            }
            entrando = false;
        }
        tiempoDireccion -= delta;
        if (tiempoDireccion <= 0f) {
            float diferenciaX = objetivoX - getCentroX();
            float diferenciaY = objetivoY - getCentroY();
            float distancia = (float) Math.sqrt(diferenciaX * diferenciaX + diferenciaY * diferenciaY);
            if (distancia > 0f) {
                setVelocidad(diferenciaX / distancia * velocidadPersecucion,
                        diferenciaY / distancia * velocidadPersecucion);
            }
            tiempoDireccion = 0.7f;
        }
    }

    public boolean estaEntrando() {
        return entrando;
    }

    @Override
    protected boolean rebotaEnBordes() {
        return !entrando;
    }
}
