package puppy.code;

import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public interface ApoyoNave {
    default void orientarDetras(float direccionX,float direccionY) { }
    void actualizar(float delta, float naveX, float naveY, List<Enemigo> enemigos,
                    Texture texturaBala, Consumer<Bullet> emitirBala);

    void dibujar(SpriteBatch batch);

    Rectangle getArea();

    void desactivar(float segundos);

    float getTiempoDesactivado();

    default boolean estaDesactivado() { return getTiempoDesactivado() > 0f; }
}
