package puppy.code;

import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;

public interface EstrategiaDisparo {
    void disparar(float x, float y, float direccionX, float direccionY,
                  Texture textura, Consumer<Bullet> emitirBala);

    default void disparar(float x, float y, Texture textura, Consumer<Bullet> emitirBala) {
        disparar(x, y, 0f, 1f, textura, emitirBala);
    }

    /** Rota los desplazamientos y velocidades del arma alrededor de su boca de disparo. */
    static void emitir(float x, float y, float dx, float dy, float lateral,
                       float avance, float apertura, Texture textura, Consumer<Bullet> emitirBala) {
        float px = dy, py = -dx;
        emitirBala.accept(new Bullet(x + px * lateral - textura.getWidth() / 2f,
                y + py * lateral - textura.getHeight() / 2f,
                dx * avance + px * apertura, dy * avance + py * apertura, textura));
    }

    String getNombre();
}
