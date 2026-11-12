package puppy.code;

import java.util.function.Supplier;

public enum SectorGalactico {
    CINTURON_DE_VELA(1, "Cinturon de Vela",
            "Una baliza de auxilio marca el primer salto. Mantengan la nave operativa.",
            1f, 0, 0, 0f, 0.012f, 0.035f, 0.055f, 0.38f, 0.72f, 0.92f,
            AtaqueDirigido::new, 0.95f, 0.78f, 0.35f),
    NEBULOSA_DE_ION(5, "Nebulosa de Ion",
            "La senal se pierde entre nubes ionizadas. Los aliens refuerzan el bloqueo.",
            1.12f, 1, 0, 0.05f, 0.034f, 0.018f, 0.062f, 0.72f, 0.52f, 0.94f,
            AtaqueAbanico::new, 0.7f, 0.48f, 1f),
    FRONTERA_DE_KHEPRI(9, "Frontera de Khepri",
            "La ruta atraviesa territorio hostil: mas resistencia y recursos de salvamento.",
            1.25f, 2, 1, 0.1f, 0.065f, 0.022f, 0.024f, 1f, 0.5f, 0.3f,
            AtaqueOleadas::new, 1f, 0.47f, 0.28f);

    private final int rondaInicial;
    private final String nombre;
    private final String narrativa;
    private final float factorDificultad;
    private final int aliensAdicionales;
    private final int resistenciaAlienAdicional;
    private final float probabilidadBateriaAdicional;
    private final float fondoRojo;
    private final float fondoVerde;
    private final float fondoAzul;
    private final float estrellasRojo;
    private final float estrellasVerde;
    private final float estrellasAzul;
    private final Supplier<AtaqueJefe> fabricaAtaqueJefe;
    private final float colorJefeRojo;
    private final float colorJefeVerde;
    private final float colorJefeAzul;

    SectorGalactico(int rondaInicial, String nombre, String narrativa, float factorDificultad,
                    int aliensAdicionales, int resistenciaAlienAdicional,
                    float probabilidadBateriaAdicional, float fondoRojo, float fondoVerde,
                    float fondoAzul, float estrellasRojo, float estrellasVerde, float estrellasAzul,
                    Supplier<AtaqueJefe> fabricaAtaqueJefe, float colorJefeRojo,
                    float colorJefeVerde, float colorJefeAzul) {
        this.rondaInicial = rondaInicial;
        this.nombre = nombre;
        this.narrativa = narrativa;
        this.factorDificultad = factorDificultad;
        this.aliensAdicionales = aliensAdicionales;
        this.resistenciaAlienAdicional = resistenciaAlienAdicional;
        this.probabilidadBateriaAdicional = probabilidadBateriaAdicional;
        this.fondoRojo = fondoRojo;
        this.fondoVerde = fondoVerde;
        this.fondoAzul = fondoAzul;
        this.estrellasRojo = estrellasRojo;
        this.estrellasVerde = estrellasVerde;
        this.estrellasAzul = estrellasAzul;
        this.fabricaAtaqueJefe = fabricaAtaqueJefe;
        this.colorJefeRojo = colorJefeRojo;
        this.colorJefeVerde = colorJefeVerde;
        this.colorJefeAzul = colorJefeAzul;
    }

    public static SectorGalactico desdeRonda(int ronda) {
        SectorGalactico sectorActual = CINTURON_DE_VELA;
        for (SectorGalactico sector : values()) {
            if (ronda >= sector.rondaInicial) {
                sectorActual = sector;
            }
        }
        return sectorActual;
    }

    public boolean iniciaEnRonda(int ronda) {
        return ronda == rondaInicial;
    }

    public boolean esRondaDeJefe(int ronda) {
        if (this == CINTURON_DE_VELA) {
            return ronda == 4;
        }
        if (this == NEBULOSA_DE_ION) {
            return ronda == 8;
        }
        return ronda >= 12 && ronda % 4 == 0;
    }

    /** Resistencia del constructor; Enemigo la convierte a vida multiplicando por diez. */
    public int getResistenciaJefe(int ronda) {
        if (this == CINTURON_DE_VELA) return 40;
        if (this == NEBULOSA_DE_ION) return 200;
        return 300 + Math.max(0, (ronda - 12) / 4) * 30;
    }

    public AtaqueJefe crearAtaqueJefe() {
        return fabricaAtaqueJefe.get();
    }

    public float getColorJefeRojo() {
        return colorJefeRojo;
    }

    public float getColorJefeVerde() {
        return colorJefeVerde;
    }

    public float getColorJefeAzul() {
        return colorJefeAzul;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNarrativa() {
        return narrativa;
    }

    public float getFactorDificultad() {
        return factorDificultad;
    }

    public int getAliensAdicionales() {
        return aliensAdicionales;
    }

    public int getResistenciaAlienAdicional() {
        return resistenciaAlienAdicional;
    }

    public float getProbabilidadBateriaAdicional() {
        return probabilidadBateriaAdicional;
    }

    public float getFondoRojo() {
        return fondoRojo;
    }

    public float getFondoVerde() {
        return fondoVerde;
    }

    public float getFondoAzul() {
        return fondoAzul;
    }

    public float getEstrellasRojo() {
        return estrellasRojo;
    }

    public float getEstrellasVerde() {
        return estrellasVerde;
    }

    public float getEstrellasAzul() {
        return estrellasAzul;
    }
}