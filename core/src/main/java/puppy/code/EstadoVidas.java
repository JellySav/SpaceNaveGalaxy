package puppy.code;

public final class EstadoVidas {
    public static final int MAX_VIDAS = 3;

    private int vidas;
    private int maximas=MAX_VIDAS;

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
        return vidas < maximas;
    }

    public boolean estaDestruida() {
        return vidas == 0;
    }

    public void actualizarMaximo(int nuevoMaximo) {
        int aumento=Math.max(0,nuevoMaximo-maximas);
        maximas=Math.max(1,nuevoMaximo);
        vidas=Math.min(maximas,vidas+aumento);
    }

    public int getMaximas(){return maximas;}

    public int getVidas() {
        return vidas;
    }

    public void establecerVidas(int vidas) {
        this.vidas = Math.max(0, Math.min(maximas, vidas));
    }
}
