package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public final class ConstructorJefeSector2 extends ConstructorJefeSector {
    public ConstructorJefeSector2(int ronda, Texture texturaJefe, Texture texturaProyectil) {
        super(ronda, texturaJefe, texturaProyectil);
    }

    @Override
    protected SectorGalactico getSector() {
        return SectorGalactico.NEBULOSA_DE_ION;
    }

    @Override
    public void construirEstrategiaAtaque() {
        construirAtaquesDelSector();
    }
}
