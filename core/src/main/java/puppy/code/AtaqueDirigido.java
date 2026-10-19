package puppy.code;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;

public class AtaqueDirigido implements AtaqueJefe {
    @Override
    public void disparar(float origenX, float origenY, float objetivoX, float objetivoY,
                         Texture textura, Consumer<ProyectilHostil> crearProyectil) {
        crearProyectil.accept(new ProyectilHostil(origenX, origenY,
                objetivoX - origenX, objetivoY - origenY, 230f, textura));
    }

    @Override
    public float getIntervalo() {
        return 1.8f;
    }
}