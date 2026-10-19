package puppy.code;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;

public class AtaqueCircular implements AtaqueJefe {
    private float anguloInicial;

    @Override
    public void disparar(float origenX, float origenY, float objetivoX, float objetivoY,
                         Texture textura, Consumer<ProyectilHostil> crearProyectil) {
        for (int indice = 0; indice < 10; indice++) {
            float angulo = anguloInicial + (float) (Math.PI * 2 * indice / 10);
            crearProyectil.accept(new ProyectilHostil(origenX, origenY,
                    (float) Math.cos(angulo), (float) Math.sin(angulo), 185f, textura));
        }
        anguloInicial += 0.18f;
    }

    @Override
    public float getIntervalo() {
        return 2.8f;
    }
}