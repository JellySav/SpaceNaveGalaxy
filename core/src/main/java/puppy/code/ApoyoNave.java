package puppy.code;

import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface ApoyoNave {
    void actualizar(float delta, float naveX, float naveY, List<Enemigo> enemigos,
                    Texture texturaBala, Consumer<Bullet> emitirBala);

    void dibujar(SpriteBatch batch);
}