package puppy.code;

import java.util.List;
import java.util.function.Consumer;

public interface AtaqueEspecialJefe {
    void actualizar(float delta, float jugadorX, float jugadorY,
                    Consumer<CargaEnergia> soltarEnergia);

    BarreraEnergia getBarrera();

    List<BarreraEnergia> getBarreras();
}
