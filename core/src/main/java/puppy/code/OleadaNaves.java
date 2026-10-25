package puppy.code;

import java.util.List;

/** Las naves son el objetivo obligatorio; los meteoritos son salvamento opcional. */
public final class OleadaNaves {
    // Verdes, rojas, moradas, naranjas y detonadoras. Los huecos corresponden a jefes.
    private static final int[][] COMPOSICIONES = {
        {8,0,0,0,0}, {11,1,0,3,0}, {10,2,0,4,0}, {0,0,0,0,0},
        {9,3,3,3,0}, {12,3,3,3,0}, {14,3,3,4,0}, {0,0,0,0,0},
        {18,4,4,4,2}, {20,4,4,5,3}, {20,5,5,6,5}
    };
    private static final int[] SIN_NAVES = {0,0,0,0,0};
    private static final int[] OLEADA_TARDIA = {21,6,6,6,6};
    private OleadaNaves() { }

    private static int[] composicion(int ronda) {
        if (ronda < 1 || SectorGalactico.desdeRonda(ronda).esRondaDeJefe(ronda)) return SIN_NAVES;
        return ronda <= COMPOSICIONES.length ? COMPOSICIONES[ronda-1] : OLEADA_TARDIA;
    }
    public static int cantidadDetonadoras(int ronda) { return composicion(ronda)[4]; }
    public static int cantidadRojas(int ronda) { return composicion(ronda)[1]; }
    public static int cantidadMoradas(int ronda) { return composicion(ronda)[2]; }
    public static int cantidadNaranjas(int ronda) { return composicion(ronda)[3]; }

    public static int cantidadInicial(int ronda) {
        int total = 0;
        for (int cantidad : composicion(ronda)) total += cantidad;
        return total;
    }

    public static boolean estaSuperada(List<Enemigo> enemigos) {
        for (Enemigo enemigo : enemigos) {
            if (enemigo.estaActivo() && !(enemigo instanceof AsteroideEnemigo)) return false;
        }
        return true;
    }
}
