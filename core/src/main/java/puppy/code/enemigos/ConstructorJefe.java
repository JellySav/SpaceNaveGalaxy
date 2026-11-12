package puppy.code.enemigos;

public interface ConstructorJefe {
    void reiniciar();

    void construirEstadisticas();

    void construirEstrategiaAtaque();

    void construirTextura();

    JefeSector obtenerResultado();
}
