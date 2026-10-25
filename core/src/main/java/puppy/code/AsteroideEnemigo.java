package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class AsteroideEnemigo extends Enemigo {
    public AsteroideEnemigo(float x, float y, float tamano, float velocidadX,
                            float velocidadY, Texture textura) {
        super(textura, x, y, tamano, tamano, velocidadX, velocidadY, 1);
    }

    @Override
    protected void actualizarComportamiento(float delta, float objetivoX, float objetivoY) {
    }

    @Override
    protected boolean rebotaEnBordes() { return false; }

    /** Distingue estar entrando de haber salido por un borde en la direccion del viaje. */
    public boolean haSalidoDelEscenario(float anchoMundo, float altoMundo) {
        Rectangle area = getArea();
        return (getVelocidadX() > 0f && area.x >= anchoMundo)
                || (getVelocidadX() < 0f && area.x + area.width <= 0f)
                || (getVelocidadY() > 0f && area.y >= altoMundo)
                || (getVelocidadY() < 0f && area.y + area.height <= 0f);
    }
}
