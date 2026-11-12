package puppy.code.enemigos;

import puppy.code.*;
import puppy.code.combate.*;

import com.badlogic.gdx.graphics.Texture;

abstract class ConstructorJefeSector implements ConstructorJefe {
    private final int ronda;
    private final Texture texturaJefeFuente;
    private final Texture texturaProyectilFuente;

    private float x;
    private float y;
    private float tamano;
    private float velocidad;
    private int resistencia;
    private AtaqueJefe ataque;
    private AtaqueEspecialJefe ataqueEspecial;
    private Texture texturaJefe;
    private Texture texturaProyectil;

    ConstructorJefeSector(int ronda, Texture texturaJefe, Texture texturaProyectil) {
        if (ronda <= 0) {
            throw new IllegalArgumentException("La ronda debe ser positiva");
        }
        this.ronda = ronda;
        this.texturaJefeFuente = texturaJefe;
        this.texturaProyectilFuente = texturaProyectil;
    }

    protected abstract SectorGalactico getSector();

    @Override
    public final void reiniciar() {
        x = 0f;
        y = 0f;
        tamano = 0f;
        velocidad = 0f;
        resistencia = 0;
        ataque = null;
        ataqueEspecial = null;
        texturaJefe = null;
        texturaProyectil = null;
    }

    @Override
    public final void construirEstadisticas() {
        SectorGalactico sector = getSector();
        tamano = Math.min(150f, MundoJuego.ANCHO * 0.2f);
        x = (MundoJuego.ANCHO - tamano) / 2f;
        y = sector == SectorGalactico.CINTURON_DE_VELA
                ? (MundoJuego.ALTO - tamano) / 2f : MundoJuego.ALTO - tamano - 105f;
        velocidad = 34f * sector.getFactorDificultad();
        resistencia = sector.getResistenciaJefe(ronda);
    }

    protected final void construirAtaquesDelSector() {
        SectorGalactico sector = getSector();
        ataque = sector.crearAtaqueJefe();
        ataqueEspecial = sector.crearAtaqueEspecialJefe();
    }

    @Override
    public final void construirTextura() {
        if (texturaJefeFuente == null || texturaProyectilFuente == null) {
            throw new IllegalArgumentException("Las texturas del jefe y del proyectil son obligatorias");
        }
        texturaJefe = texturaJefeFuente;
        texturaProyectil = texturaProyectilFuente;
    }

    @Override
    public final JefeSector obtenerResultado() {
        if (tamano <= 0f || resistencia <= 0 || ataque == null
                || texturaJefe == null || texturaProyectil == null) {
            throw new IllegalStateException("El jefe debe completar todos los pasos de construccion");
        }
        JefeSector jefe = new JefeSector(x, y, tamano, velocidad, resistencia,
                getSector(), ataque, ataqueEspecial, texturaJefe, texturaProyectil);
        jefe.configurarVidaParaRonda(ronda);
        return jefe;
    }
}
