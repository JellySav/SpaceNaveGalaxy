package puppy.code;

public final class ProgresionMejoras {
    private static final ProgresionMejoras INSTANCIA = new ProgresionMejoras();
    private static final int[] UMBRALES = {3, 7};

    private int impactos;
    private int nivelArma = 1;

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
    }

    public int getImpactos() {
        return impactos;
    }

    public int getNivelArma() {
        return nivelArma;
    }

    public int getImpactosSiguienteMejora() {
        if (nivelArma > UMBRALES.length) {
            return 0;
        }
        return Math.max(0, UMBRALES[nivelArma - 1] - impactos);
    }
}