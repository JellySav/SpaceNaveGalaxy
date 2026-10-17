package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public class AlienEnemigo extends Enemigo implements ObjetivoDisparo {
    private final float velocidadPersecucion;
    private float tiempoDireccion;

    public AlienEnemigo(float x, float y, float velocidadPersecucion,
                        int resistencia, Texture textura) {
        this(x, y, 48f, velocidadPersecucion, resistencia, textura);
    }

    public AlienEnemigo(float x, float y, float tamano, float velocidadPersecucion,
                        int resistencia, Texture textura) {
        super(textura, x, y, tamano, tamano, velocidadPersecucion, -velocidadPersecucion,
                resistencia);
        this.velocidadPersecucion = velocidadPersecucion;
        setColor(0.38f, 1f, 0.58f, 1f);
    }

    @Override
    protected void actualizarComportamiento(float delta, float objetivoX, float objetivoY) {
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
}