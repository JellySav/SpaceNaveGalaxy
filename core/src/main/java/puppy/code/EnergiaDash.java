package puppy.code;

/** Estado de la energia y los tiempos del dash, independiente de los graficos. */
public final class EnergiaDash {
    public static final float MAXIMA = 100f;
    public static final float COSTE = 40f;
    public static final float DURACION = 0.17f;
    public static final float RECARGA_POR_SEGUNDO = 6f;
    public static final float ESPERA = 0.45f;
    public static final float VELOCIDAD = 1000f;

    private float energia = MAXIMA;
    private float tiempoDash;
    private float tiempoEscudo;
    private float espera;
    private boolean protegidoEsteFrame;

    public boolean iniciar() {
        if (!puedeUsar()) return false;
        energia -= getCoste();
        tiempoDash = DURACION;
        if(ProgresionMejoras.obtenerInstancia().tieneEscudoDash())tiempoEscudo=DURACION+0.75f;
        espera = ProgresionMejoras.obtenerInstancia().getEsperaDash();
        return true;
    }

    /** Devuelve cuantos segundos de este frame corresponden al movimiento rapido. */
    public float actualizar(float delta) {
        delta = Math.max(0f, delta);
        float tramoDash = Math.min(delta, tiempoDash);
        protegidoEsteFrame = tiempoDash > 0f;
        tiempoDash = Math.max(0f, tiempoDash - delta);
        espera = Math.max(0f, espera - delta);
        tiempoEscudo = Math.max(0f, tiempoEscudo - delta);
        recuperar(ProgresionMejoras.obtenerInstancia().getRegeneracionEnergia() * (delta - tramoDash));
        return tramoDash;
    }

    public void recuperar(float cantidad) {
        energia = Math.min(MAXIMA, energia + Math.max(0f, cantidad));
    }

    public boolean puedeUsar() {
        return energia >= getCoste() && tiempoDash <= 0f && getEspera() <= 0f;
    }

    public boolean tieneEscudo() { return tiempoEscudo > 0f; }
    public boolean estaActivo() { return tiempoDash > 0f; }
    public boolean protegeEsteFrame() { return protegidoEsteFrame; }
    public float getCoste() { return ProgresionMejoras.obtenerInstancia().getCosteDash(); }
    public float getEnergia() { return energia; }
    public float getEspera() { return ProgresionMejoras.obtenerInstancia().tieneCuerpoACuerpo()?0f:espera; }

    /** Al cambiar de ronda se conserva energia y espera, pero no el movimiento. */
    public void detener() {
        tiempoDash = 0f;
        protegidoEsteFrame = false;
    }
}
