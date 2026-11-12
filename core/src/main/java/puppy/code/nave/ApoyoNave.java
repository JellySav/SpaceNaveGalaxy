package puppy.code.nave;

import puppy.code.combate.*;
import puppy.code.enemigos.*;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public interface ApoyoNave {
    default void orientarDetras(float direccionX,float direccionY) { }
    void actualizar(float delta, float naveX, float naveY, List<Enemigo> enemigos,
                    Texture texturaBala, Consumer<Bullet> emitirBala);

    default List<RayoLaser> extraerRayos() { return Collections.emptyList(); }

    void dibujar(SpriteBatch batch);

    Rectangle getArea();

    void desactivar(float segundos);

    float getTiempoDesactivado();

    default void recibirExplosion(ExplosionAlien explosion) {
        if(explosion.alcanza(getArea()))desactivar(ExplosionAlien.DESACTIVACION_DRON);
    }

    default boolean estaDesactivado() { return getTiempoDesactivado() > 0f; }
}
