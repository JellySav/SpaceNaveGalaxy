package puppy.code;

public final class EstadoVidas {
    public static final int MAX_VIDAS = 3;

    private int vidas;

    public EstadoVidas(int vidasIniciales) {
        establecerVidas(vidasIniciales);
    }

    public boolean recibirDano() {
        if (vidas == 0) {
            return false;
        }
        vidas--;
        return true;
    }

    public boolean reparar() {
        if (!puedeReparar()) {
            return false;
        }
        vidas++;
        return true;
    }

    public boolean puedeReparar() {
        return vidas < MAX_VIDAS;
    }

    public boolean estaDestruida() {
        return vidas == 0;
    }

    public int getVidas() {
        return vidas;
    }

    public void establecerVidas(int vidas) {
        this.vidas = Math.max(0, Math.min(MAX_VIDAS, vidas));
    }
}