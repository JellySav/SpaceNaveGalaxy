package puppy.code.combate;

public final class FabricaEstrategiasDisparo {
    private FabricaEstrategiasDisparo() {
    }

    public static EstrategiaDisparo crear(int nivel) {
        int nivelLimitado = Math.max(1, Math.min(4, nivel));
        switch (nivelLimitado) {
            case 1:
                return new DisparoSimple();
            case 2:
                return new DisparoDoble();
            case 3:
                return new DisparoTriple();
            default:
                return new DisparoQuintuple();
        }
    }
}
