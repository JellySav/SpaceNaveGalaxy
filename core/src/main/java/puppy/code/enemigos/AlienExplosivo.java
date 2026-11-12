package puppy.code.enemigos;

import puppy.code.*;
import puppy.code.combate.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import java.util.function.Consumer;

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

    @Override public ExplosionAlien crearExplosionAlDerrotar() {
        Rectangle area=getArea();
        return new ExplosionAlien(area.x+area.width/2f,area.y+area.height/2f);
    }

    @Override public boolean retirarTrasColisionNave(Consumer<Enemigo> resolverDerrota) {
        resolverDerrota.accept(this);
        return false;
    }
}
