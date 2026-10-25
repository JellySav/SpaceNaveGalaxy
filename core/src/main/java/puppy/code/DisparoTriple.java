package puppy.code;

import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;

public class DisparoTriple implements EstrategiaDisparo {
    @Override
    public void disparar(float x, float y, float dx, float dy,
                         Texture textura, Consumer<Bullet> emitirBala) {
        EstrategiaDisparo.emitir(x, y, dx, dy, 0f, 560f, 0f, textura, emitirBala);
        EstrategiaDisparo.emitir(x, y, dx, dy, -12f, 500f, -130f, textura, emitirBala);
        EstrategiaDisparo.emitir(x, y, dx, dy, 12f, 500f, 130f, textura, emitirBala);
    }

    @Override
    public String getNombre() { return "TRIPLE"; }
}
