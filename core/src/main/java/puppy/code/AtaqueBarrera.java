package puppy.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/** El tercer jefe alterna parejas de bordes adyacentes; el segundo mantiene su barrido simple. */
public final class AtaqueBarrera {
    public static final float INTERVALO = 14f;
    public static final float VELOCIDAD_TERCER_JEFE = BarreraEnergia.VELOCIDAD * 1.2f;
    private final boolean dobleAlterna;
    private final List<BarreraEnergia> barreras = new ArrayList<>();
    private final List<BarreraEnergia> vistaBarreras = Collections.unmodifiableList(barreras);
    private boolean arribaDerecha = true;
    private float espera = INTERVALO;

    public AtaqueBarrera() { this(false); }
    public AtaqueBarrera(boolean dobleAlterna) { this.dobleAlterna = dobleAlterna; }

    public void actualizar(float delta, float jugadorX, float jugadorY,
                           Consumer<CargaEnergia> soltarEnergia) {
        if (delta <= 0f) return;
        for (BarreraEnergia barrera : barreras) barrera.actualizar(delta);
        barreras.removeIf(BarreraEnergia::termino);
        espera -= delta;
        if (espera <= 0f && barreras.isEmpty()) {
            if (dobleAlterna) {
                barreras.add(new BarreraEnergia(arribaDerecha ? BarreraEnergia.Direccion.ARRIBA
                        : BarreraEnergia.Direccion.ABAJO, VELOCIDAD_TERCER_JEFE));
                barreras.add(new BarreraEnergia(arribaDerecha ? BarreraEnergia.Direccion.DERECHA
                        : BarreraEnergia.Direccion.IZQUIERDA, VELOCIDAD_TERCER_JEFE));
                arribaDerecha = !arribaDerecha;
            } else {
                barreras.add(new BarreraEnergia());
            }
            soltarEnergia.accept(new CargaEnergia(jugadorX, jugadorY, EnergiaDash.COSTE));
            espera = INTERVALO;
        }
    }

    public BarreraEnergia getBarrera() { return barreras.isEmpty() ? null : barreras.get(0); }
    public List<BarreraEnergia> getBarreras() { return vistaBarreras; }
}
