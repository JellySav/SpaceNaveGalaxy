package puppy.code;

import com.badlogic.gdx.math.Rectangle;

public interface ObjetivoDisparo {
    Rectangle getArea();

    void recibirImpacto();

    default void recibirDano(float dano) {
        if(dano>0f) recibirImpacto();
    }

    boolean estaActivo();
}
