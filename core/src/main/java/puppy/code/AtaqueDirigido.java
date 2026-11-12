package puppy.code;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;

public class AtaqueDirigido implements AtaqueJefe {
    @Override
    public void disparar(float origenX, float origenY, float objetivoX, float objetivoY,
                         Texture textura, Consumer<ProyectilHostil> crearProyectil) {
        float dx = objetivoX - origenX, dy = objetivoY - origenY;
        if(dx==0f && dy==0f)dy=-1f;
        float anguloBase = (float) Math.atan2(dy, dx);
        for(int indice=-1;indice<=1;indice++) {
            float angulo=anguloBase+indice*0.22f;
            crearProyectil.accept(new ProyectilHostil(origenX,origenY,
                    (float)Math.cos(angulo),(float)Math.sin(angulo),230f,textura));
        }
    }

    @Override
    public float getIntervalo() {
        return 1.8f;
    }
}