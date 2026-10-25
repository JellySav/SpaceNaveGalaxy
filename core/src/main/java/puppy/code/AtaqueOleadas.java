package puppy.code;

import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;

/** Tres arcos dirigidos al jugador seguidos de una pausa de recuperacion. */
public final class AtaqueOleadas implements AtaqueJefe {
    public static final int PROYECTILES = 7;
    public static final float ENTRE_RAFAGAS = 0.35f;
    public static final float PAUSA = 3.5f;
    public static final float VELOCIDAD = 200f;
    private int rafagasLanzadas;

    @Override
    public void disparar(float origenX, float origenY, float objetivoX, float objetivoY,
                         Texture textura, Consumer<ProyectilHostil> crearProyectil) {
        float dx=objetivoX-origenX,dy=objetivoY-origenY;
        if(dx==0f && dy==0f)dy=-1f;
        double anguloBase=Math.atan2(dy,dx);
        for (int i = 0; i < PROYECTILES; i++) {
            double angulo = anguloBase + Math.toRadians(-65f + 130f * i / (PROYECTILES - 1));
            crearProyectil.accept(new ProyectilHostil(origenX, origenY,
                    (float) Math.cos(angulo), (float) Math.sin(angulo), VELOCIDAD, textura));
        }
        rafagasLanzadas = (rafagasLanzadas + 1) % 3;
    }

    @Override
    public float getIntervalo() {
        return rafagasLanzadas == 0 ? PAUSA : ENTRE_RAFAGAS;
    }
}
