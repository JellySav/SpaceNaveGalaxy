package puppy.code;

import com.badlogic.gdx.math.Rectangle;

public interface ObjetivoDisparo {
    Rectangle getArea();

    void recibirImpacto();

    boolean estaActivo();
}