package puppy.code.enemigos;

import puppy.code.*;
import puppy.code.combate.*;
import puppy.code.mejoras.*;

import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;

public class JefeSector extends Enemigo {
    public static final float INTERVALO_REFUERZOS = 5f;
    public static final float VELOCIDAD_GIRO=60f;
    private final SectorGalactico sector;
    private final EstrategiaJefeSector estrategia;
    private final AtaqueEspecialJefe ataqueEspecial;
    private final java.util.Random randomRefuerzos=new java.util.Random();

    public JefeSector(float x,float y,float tamano,float velocidad,int resistencia,
                      SectorGalactico sector,AtaqueEspecialJefe ataqueEspecial,
                      EstrategiaJefeSector estrategia,Texture textura,Texture texturaProyectil) {
        super(textura,x,y,tamano,tamano,velocidad,0f,resistencia);
        if(sector==null||estrategia==null||texturaProyectil==null) {
            throw new IllegalArgumentException("Sector, estrategia y proyectil del jefe son obligatorios");
        }
        this.sector=sector;
        this.estrategia=estrategia;
        this.ataqueEspecial=ataqueEspecial;
        setVelocidad(0f,0f);
        setColor(sector.getColorJefeRojo(),sector.getColorJefeVerde(),sector.getColorJefeAzul(),1f);
    }

    public JefeSector(float x, float y, float tamano, float velocidad,
                      int resistencia, SectorGalactico sector,
                      AtaqueJefe ataque, AtaqueEspecialJefe ataqueEspecial,
                      Texture textura, Texture texturaProyectil) {
        this(x,y,tamano,velocidad,resistencia,sector,ataqueEspecial,
                crearEstrategia(sector,ataque,ataqueEspecial,velocidad,texturaProyectil),
                textura,texturaProyectil);
    }

    private static EstrategiaJefeSector crearEstrategia(SectorGalactico sector,AtaqueJefe ataque,
                                                         AtaqueEspecialJefe especial,float velocidad,
                                                         Texture texturaProyectil) {
        if(sector==null||ataque==null||texturaProyectil==null)
            throw new IllegalArgumentException("Sector, ataque y proyectil del jefe son obligatorios");
        return sector.crearEstrategiaJefe(ataque,especial,velocidad,texturaProyectil);
    }

    @Override
    protected void actualizarComportamiento(float delta, float objetivoX, float objetivoY) {
        estrategia.actualizarMovimiento(this,delta,objetivoX,objetivoY);
    }

    public void actualizarAtaque(float delta, float objetivoX, float objetivoY,
                                 Consumer<ProyectilHostil> crearProyectil) {
        estrategia.actualizarAtaque(this,delta,objetivoX,objetivoY,crearProyectil);
    }

    public LaserJefe getLaserPrimerJefe(){return estaActivo()?estrategia.getLaser():null;}

    public SectorGalactico getSector() {
        return sector;
    }

    public void actualizarAtaqueEspecial(float delta, float jugadorX, float jugadorY,
                                         Consumer<CargaEnergia> soltarEnergia) {
        if (estaActivo() && ataqueEspecial != null) {
            ataqueEspecial.actualizar(delta, jugadorX, jugadorY, soltarEnergia);
        }
    }

    public BarreraEnergia getBarreraEnergia() {
        return estaActivo() && ataqueEspecial != null ? ataqueEspecial.getBarrera() : null;
    }

    public java.util.List<BarreraEnergia> getBarrerasEnergia() {
        return estaActivo() && ataqueEspecial != null ? ataqueEspecial.getBarreras()
                : java.util.Collections.emptyList();
    }

    public void actualizarRefuerzos(float delta, float velocidadAlien, int resistenciaAlien,
                                    Texture texturaAlien, Consumer<AlienEnemigo> crearRefuerzo) {
        estrategia.actualizarRefuerzos(this,delta,velocidadAlien,resistenciaAlien,
                texturaAlien,crearRefuerzo);
    }

    public float getIntervaloRefuerzos() {
        return estrategia.getIntervaloRefuerzos();
    }

    java.util.Random getRandomRefuerzos(){return randomRefuerzos;}

    public String getNombre() {
        return "GUARDIAN DE " + sector.getNombre().toUpperCase();
    }

    @Override public boolean retirarTrasColisionNave(Consumer<Enemigo> resolverDerrota) {
        return false;
    }

    @Override
    public int getPuntosDerrota() {
        return 255;
    }

    @Override
    public float getCargaEnergiaAlDerrotar() {
        return 40f;
    }

    @Override
    public void registrarDerrota(ProgresionMejoras progresion) {
        progresion.registrarBateria();
    }
}
