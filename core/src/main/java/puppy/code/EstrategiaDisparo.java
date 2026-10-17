package puppy.code;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;

public interface EstrategiaDisparo {
    void disparar(float x, float y, Texture textura, Consumer<Bullet> emitirBala);

    String getNombre();
}