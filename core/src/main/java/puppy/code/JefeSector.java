package puppy.code;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;

public class JefeSector extends Enemigo {
    private final SectorGalactico sector;
    private final Texture texturaProyectil;
    private final AtaqueJefe ataque;
    private float tiempoAtaque;

    public JefeSector(float x, float y, float tamano, float velocidad,
                      int resistencia, SectorGalactico sector,
                      Texture textura, Texture texturaProyectil) {
        super(textura, x, y, tamano, tamano, velocidad, 0f, resistencia);
        if (sector == null || texturaProyectil == null) {
            throw new IllegalArgumentException("Sector y proyectil del jefe son obligatorios");
        }
        this.sector = sector;
        this.texturaProyectil = texturaProyectil;
        ataque = sector.crearAtaqueJefe();
        setColor(sector.getColorJefeRojo(), sector.getColorJefeVerde(),
                sector.getColorJefeAzul(), 1f);
        tiempoAtaque = ataque.getIntervalo();
    }

    @Override
    protected void actualizarComportamiento(float delta, float objetivoX, float objetivoY) {
    }

    public void actualizarAtaque(float delta, float objetivoX, float objetivoY,
                                 Consumer<ProyectilHostil> crearProyectil) {
        if (!estaActivo()) {
            return;
        }
        tiempoAtaque -= delta;
        if (tiempoAtaque <= 0f) {
            ataque.disparar(getCentroX(), getCentroY(), objetivoX, objetivoY,
                    texturaProyectil, crearProyectil);
            tiempoAtaque = ataque.getIntervalo();
        }
    }

    public SectorGalactico getSector() {
        return sector;
    }

    public String getNombre() {
        return "GUARDIAN DE " + sector.getNombre().toUpperCase();
    }

    @Override
    public int getPuntosDerrota() {
        return 250;
    }

    @Override
    public void registrarDerrota(ProgresionMejoras progresion) {
        progresion.registrarJefeDerrotado();
    }
}