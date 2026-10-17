package puppy.code;

public final class ProgresionMejoras {
    private static final ProgresionMejoras INSTANCIA = new ProgresionMejoras();
    private static final int[] UMBRALES_IMPACTOS = {4, 12};
    private static final int[] UMBRALES_BATERIAS = {1, 2};
    private static final int[] UMBRALES_ALIENS = {0, 2};
    private static final int[] UMBRALES_RONDA = {2, 3};
    private static final int[] UMBRALES_TIEMPO = {35, 90};
    private static final int MAX_BATERIAS = 3;

    private int impactos;
    private int aliensDerrotados;
    private int nivelArma = 1;
    private int baterias;
    private int rondaMaxima = 1;
    private float tiempoSupervivencia;

    private ProgresionMejoras() {
    }

    public static ProgresionMejoras obtenerInstancia() {
        return INSTANCIA;
    }

    public int registrarImpacto() {
        impactos++;
        return nivelArma;
    }

    public void registrarAlienDerrotado() {
        aliensDerrotados++;
    }

    public void avanzarTiempo(float delta, int ronda) {
        if (delta > 0f) {
            tiempoSupervivencia += delta;
        }
        rondaMaxima = Math.max(rondaMaxima, ronda);
    }

    public boolean puedeInstalarMejora() {
        if (nivelArma > UMBRALES_IMPACTOS.length) {
            return false;
        }
        int indice = nivelArma - 1;
        return impactos >= UMBRALES_IMPACTOS[indice]
                && baterias >= UMBRALES_BATERIAS[indice]
                && aliensDerrotados >= UMBRALES_ALIENS[indice]
                && rondaMaxima >= UMBRALES_RONDA[indice]
                && tiempoSupervivencia >= UMBRALES_TIEMPO[indice];
    }

    public boolean instalarMejora() {
        if (!puedeInstalarMejora()) {
            return false;
        }
        baterias -= UMBRALES_BATERIAS[nivelArma - 1];
        nivelArma++;
        return true;
    }

    public void reiniciar() {
        impactos = 0;
        aliensDerrotados = 0;
        nivelArma = 1;
        baterias = 0;
        rondaMaxima = 1;
        tiempoSupervivencia = 0f;
    }

    public boolean registrarBateria() {
        if (baterias >= MAX_BATERIAS) {
            return false;
        }
        baterias++;
        return true;
    }

    public boolean consumirBateria() {
        if (baterias == 0) {
            return false;
        }
        baterias--;
        return true;
    }

    public int getImpactos() {
        return impactos;
    }

    public int getNivelArma() {
        return nivelArma;
    }

    public int getBaterias() {
        return baterias;
    }

    public int getImpactosSiguienteMejora() {
        if (nivelArma > UMBRALES_IMPACTOS.length) {
            return 0;
        }
        return Math.max(0, UMBRALES_IMPACTOS[nivelArma - 1] - impactos);
    }

    public int getAliensDerrotados() {
        return aliensDerrotados;
    }

    public int getRondaMaxima() {
        return rondaMaxima;
    }

    public int getTiempoSupervivencia() {
        return (int) tiempoSupervivencia;
    }

    public String getEstadoSiguienteMejora() {
        if (nivelArma > UMBRALES_IMPACTOS.length) {
            return "MEJORAS COMPLETAS";
        }
        int indice = nivelArma - 1;
        String nombre = nivelArma == 1 ? "DOBLE" : "TRIPLE + DRON";
        String estado = nombre + " | IMPACTOS " + Math.min(impactos, UMBRALES_IMPACTOS[indice])
                + "/" + UMBRALES_IMPACTOS[indice]
                + " | TIEMPO " + Math.min(getTiempoSupervivencia(), UMBRALES_TIEMPO[indice])
                + "/" + UMBRALES_TIEMPO[indice] + "s"
                + " | BATERIAS " + baterias + "/" + UMBRALES_BATERIAS[indice]
                + " | RONDA " + Math.min(rondaMaxima, UMBRALES_RONDA[indice])
                + "/" + UMBRALES_RONDA[indice];
        if (UMBRALES_ALIENS[indice] > 0) {
            estado += " | ALIENS " + Math.min(aliensDerrotados, UMBRALES_ALIENS[indice])
                    + "/" + UMBRALES_ALIENS[indice];
        }
        return estado + (puedeInstalarMejora() ? " | PULSA U PARA INSTALAR" : "");
    }
}