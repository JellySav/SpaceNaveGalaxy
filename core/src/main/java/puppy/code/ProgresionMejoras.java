package puppy.code;

public final class ProgresionMejoras {
    private static final ProgresionMejoras INSTANCIA = new ProgresionMejoras();
    private static final int[] UMBRALES = {3, 7};
    private static final int MAX_BATERIAS = 3;

    private int impactos;
    private int nivelArma = 1;
    private int baterias;

    private ProgresionMejoras() {
    }

    public static ProgresionMejoras obtenerInstancia() {
        return INSTANCIA;
    }

    public int registrarImpacto() {
        impactos++;
        if (nivelArma <= UMBRALES.length && impactos >= UMBRALES[nivelArma - 1]) {
            nivelArma++;
        }
        return nivelArma;
    }

    public void reiniciar() {
        impactos = 0;
        nivelArma = 1;
        baterias = 0;
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
        if (nivelArma > UMBRALES.length) {
            return 0;
        }
        return Math.max(0, UMBRALES[nivelArma - 1] - impactos);
    }
}