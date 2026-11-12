package puppy.code;

import com.badlogic.gdx.graphics.Texture;

/** Variante roja: persigue como el alien verde, con un 10% menos de velocidad. */
public final class AlienExplosivo extends AlienEnemigo {
    public static final float FACTOR_VELOCIDAD = 0.9f;
    public static final int RESISTENCIA = 3;

    public AlienExplosivo(float x, float y, float tamano, float velocidadVerde,
                         int resistencia, Texture textura) {
        super(x, y, tamano, velocidadVerde * FACTOR_VELOCIDAD, resistencia, textura);
        setColor(1f, 0.18f, 0.22f, 1f);
    }

    public static int cantidadEnRonda(int ronda) {
        return OleadaNaves.cantidadRojas(ronda);
    }
}
