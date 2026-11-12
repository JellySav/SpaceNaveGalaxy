package puppy.code;

import com.badlogic.gdx.graphics.Texture;

/** Comparte seguimiento y desactivacion con los apoyos; su arma es un rayo penetrante. */
public final class DronLaser extends DronEscolta {
    public DronLaser(Texture textura){super(textura);}
    @Override protected boolean esLaser(){return true;}
}
