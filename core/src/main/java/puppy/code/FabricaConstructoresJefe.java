package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public final class FabricaConstructoresJefe {
    private FabricaConstructoresJefe() {
    }

    public static ConstructorJefe crear(SectorGalactico sector, int ronda,
                                       Texture texturaJefe, Texture texturaProyectil) {
        if (sector == null) {
            throw new IllegalArgumentException("El sector del jefe es obligatorio");
        }
        switch (sector) {
            case CINTURON_DE_VELA:
                return new ConstructorJefeSector1(ronda, texturaJefe, texturaProyectil);
            case NEBULOSA_DE_ION:
                return new ConstructorJefeSector2(ronda, texturaJefe, texturaProyectil);
            case FRONTERA_DE_KHEPRI:
                return new ConstructorJefeSector3(ronda, texturaJefe, texturaProyectil);
            default:
                throw new IllegalArgumentException("Sector de jefe no soportado: " + sector);
        }
    }
}
