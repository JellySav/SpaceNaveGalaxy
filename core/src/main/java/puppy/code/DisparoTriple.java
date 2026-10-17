package puppy.code;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;

public class DisparoTriple implements EstrategiaDisparo {
    @Override
    public void disparar(float x, float y, Texture textura, Consumer<Bullet> emitirBala) {
        emitirBala.accept(new Bullet(x, y, 0f, 560f, textura));
        emitirBala.accept(new Bullet(x - 12f, y, -130f, 500f, textura));
        emitirBala.accept(new Bullet(x + 12f, y, 130f, 500f, textura));
    }

    @Override
    public String getNombre() {
        return "TRIPLE";
    }
}