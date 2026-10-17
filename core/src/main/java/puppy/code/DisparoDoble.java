package puppy.code;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;

public class DisparoDoble implements EstrategiaDisparo {
    @Override
    public void disparar(float x, float y, Texture textura, Consumer<Bullet> emitirBala) {
        emitirBala.accept(new Bullet(x - 12f, y, 0f, 520f, textura));
        emitirBala.accept(new Bullet(x + 12f, y, 0f, 520f, textura));
    }

    @Override
    public String getNombre() {
        return "DOBLE";
    }
}