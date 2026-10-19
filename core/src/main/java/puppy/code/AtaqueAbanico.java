package puppy.code;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;

public class AtaqueAbanico implements AtaqueJefe {
    @Override
    public void disparar(float origenX, float origenY, float objetivoX, float objetivoY,
                         Texture textura, Consumer<ProyectilHostil> crearProyectil) {
        float anguloBase = (float) Math.atan2(objetivoY - origenY, objetivoX - origenX);
        for (int indice = -2; indice <= 2; indice++) {
            float angulo = anguloBase + indice * 0.22f;
            crearProyectil.accept(new ProyectilHostil(origenX, origenY,
                    (float) Math.cos(angulo), (float) Math.sin(angulo), 210f, textura));
        }
    }

    @Override
    public float getIntervalo() {
        return 2.2f;
    }
}