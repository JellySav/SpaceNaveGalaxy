package puppy.code;

public final class DirectorJefes {
    public JefeSector construirJefeCompleto(ConstructorJefe constructor) {
        if (constructor == null) {
            throw new IllegalArgumentException("El constructor del jefe es obligatorio");
        }
        constructor.reiniciar();
        constructor.construirEstadisticas();
        constructor.construirEstrategiaAtaque();
        constructor.construirTextura();
        return constructor.obtenerResultado();
    }
}
