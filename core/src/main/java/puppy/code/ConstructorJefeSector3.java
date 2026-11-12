package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public final class ConstructorJefeSector3 extends ConstructorJefeSector {
    public ConstructorJefeSector3(int ronda, Texture texturaJefe, Texture texturaProyectil) {
        super(ronda, texturaJefe, texturaProyectil);
    }

    @Override
    protected SectorGalactico getSector() {
        return SectorGalactico.FRONTERA_DE_KHEPRI;
    }

    @Override
    public void construirEstrategiaAtaque() {
        construirAtaquesDelSector();
    }
}
