package puppy.code;

public enum SectorGalactico {
    CINTURON_DE_VELA(1, "Cinturon de Vela",
            "Una baliza de auxilio marca el primer salto. Mantengan la nave operativa.",
            1f, 0, 0, 0f, 0.012f, 0.035f, 0.055f, 0.38f, 0.72f, 0.92f),
    NEBULOSA_DE_ION(3, "Nebulosa de Ion",
            "La senal se pierde entre nubes ionizadas. Los aliens refuerzan el bloqueo.",
            1.12f, 1, 0, 0.05f, 0.034f, 0.018f, 0.062f, 0.72f, 0.52f, 0.94f),
    FRONTERA_DE_KHEPRI(5, "Frontera de Khepri",
            "La ruta atraviesa territorio hostil: mas resistencia y recursos de salvamento.",
            1.25f, 2, 1, 0.1f, 0.065f, 0.022f, 0.024f, 1f, 0.5f, 0.3f);

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

    SectorGalactico(int rondaInicial, String nombre, String narrativa, float factorDificultad,
                    int aliensAdicionales, int resistenciaAlienAdicional,
                    float probabilidadBateriaAdicional, float fondoRojo, float fondoVerde,
                    float fondoAzul, float estrellasRojo, float estrellasVerde, float estrellasAzul) {
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