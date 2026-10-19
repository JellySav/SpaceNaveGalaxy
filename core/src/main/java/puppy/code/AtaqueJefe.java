package puppy.code;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;

public interface AtaqueJefe {
    void disparar(float origenX, float origenY, float objetivoX, float objetivoY,
                  Texture textura, Consumer<ProyectilHostil> crearProyectil);

    float getIntervalo();
}