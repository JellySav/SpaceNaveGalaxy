package puppy.code;

import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;

public class DisparoSimple implements EstrategiaDisparo {
    @Override
    public void disparar(float x, float y, float dx, float dy,
                         Texture textura, Consumer<Bullet> emitirBala) {
        EstrategiaDisparo.emitir(x, y, dx, dy, 0f, 520f, 0f, textura, emitirBala);
    }

    @Override
    public String getNombre() { return "SIMPLE"; }
}
