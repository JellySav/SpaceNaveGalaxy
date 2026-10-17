package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public class AsteroideEnemigo extends Enemigo implements ObjetivoDisparo {
    public AsteroideEnemigo(float x, float y, float tamano, float velocidadX,
                            float velocidadY, Texture textura) {
        super(textura, x, y, tamano, tamano, velocidadX, velocidadY, 1);
    }

    @Override
    protected void actualizarComportamiento(float delta, float objetivoX, float objetivoY) {
    }
}