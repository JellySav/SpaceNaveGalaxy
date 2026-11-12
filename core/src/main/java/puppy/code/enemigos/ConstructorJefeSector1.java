package puppy.code.enemigos;

import puppy.code.*;

import com.badlogic.gdx.graphics.Texture;

public final class ConstructorJefeSector1 extends ConstructorJefeSector {
    public ConstructorJefeSector1(int ronda, Texture texturaJefe, Texture texturaProyectil) {
        super(ronda, texturaJefe, texturaProyectil);
    }

    @Override
    protected SectorGalactico getSector() {
        return SectorGalactico.CINTURON_DE_VELA;
    }

    @Override
    public void construirEstrategiaAtaque() {
        construirAtaquesDelSector();
    }
}
