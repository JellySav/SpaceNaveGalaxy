package puppy.code;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;

public class JefeSector extends Enemigo {
    public static final float INTERVALO_REFUERZOS = 5f;
    public static final float VELOCIDAD_GIRO=60f;
    private final SectorGalactico sector;
    private final float velocidadPersecucion;

    private final Texture texturaProyectil;
    private final AtaqueJefe ataque;
    private final AtaqueBarrera ataqueBarrera;
    private float tiempoAtaque;
    private int salvasNormales;
    private LaserJefe laserPrimerJefe;
    private float tiempoRefuerzos;
    private int refuerzosCreados;
    private final java.util.Random randomRefuerzos=new java.util.Random();

    public JefeSector(float x, float y, float tamano, float velocidad,
                      int resistencia, SectorGalactico sector,
                      Texture textura, Texture texturaProyectil) {
        super(textura, x, y, tamano, tamano, velocidad, 0f, resistencia);
        if (sector == null || texturaProyectil == null) {
            throw new IllegalArgumentException("Sector y proyectil del jefe son obligatorios");
        }
        this.sector = sector;
        this.velocidadPersecucion=Math.abs(velocidad);
        if(sector==SectorGalactico.CINTURON_DE_VELA)setVelocidad(0f,0f);
        this.ataqueBarrera = sector == SectorGalactico.NEBULOSA_DE_ION ? new AtaqueBarrera()
                : sector == SectorGalactico.FRONTERA_DE_KHEPRI ? new AtaqueBarrera(true) : null;
        this.texturaProyectil = texturaProyectil;
        if (sector == SectorGalactico.NEBULOSA_DE_ION) {
            ataque = new AtaqueAbanico();
            setColor(0.7f, 0.48f, 1f, 1f);
        } else if (sector == SectorGalactico.FRONTERA_DE_KHEPRI) {
            ataque = new AtaqueOleadas();
            setColor(1f, 0.47f, 0.28f, 1f);
        } else {
            ataque = new AtaqueDirigido();
            setColor(0.95f, 0.78f, 0.35f, 1f);
        }
        tiempoAtaque = ataque.getIntervalo();
        tiempoRefuerzos = getIntervaloRefuerzos();
    }

    @Override
    protected void actualizarComportamiento(float delta, float objetivoX, float objetivoY) {
        if(sector==SectorGalactico.NEBULOSA_DE_ION || sector==SectorGalactico.FRONTERA_DE_KHEPRI){
            if(ataqueBarrera.getBarrera()!=null){setVelocidad(0f,0f);return;}
            float dx=objetivoX-getCentroX(),dy=objetivoY-getCentroY();
            float distancia=(float)Math.hypot(dx,dy);
            if(distancia<0.001f){setVelocidad(0f,0f);return;}
            float velocidad=Math.min(velocidadPersecucion,distancia/delta);
            setVelocidad(dx/distancia*velocidad,dy/distancia*velocidad);
            return;
        }
        if (sector != SectorGalactico.CINTURON_DE_VELA) return;
        setVelocidad(0f,0f);
        if(laserPrimerJefe==null || !laserPrimerJefe.estaAvisando())
            girarAparienciaHacia(objetivoX,objetivoY,VELOCIDAD_GIRO,delta);
    }

    public void actualizarAtaque(float delta, float objetivoX, float objetivoY,
                                 Consumer<ProyectilHostil> crearProyectil) {
        if (!estaActivo()) {
            return;
        }
        if(delta<=0f)return;
        if(laserPrimerJefe!=null){
            laserPrimerJefe.actualizar(delta);
            if(laserPrimerJefe.termino())laserPrimerJefe=null;
            return;
        }
        tiempoAtaque -= delta;
        if (tiempoAtaque <= 0f) {
            float origenX=getCentroX(),origenY=getCentroY();
            if(sector==SectorGalactico.CINTURON_DE_VELA) {
                com.badlogic.gdx.math.Vector2 direccion=direccionApariencia();
                float dx=direccion.x,dy=direccion.y;
                com.badlogic.gdx.math.Vector2 boca=puntoApariencia(0.5f,0.988f);
                origenX=boca.x;origenY=boca.y;
                objetivoX=origenX+dx*100f;objetivoY=origenY+dy*100f;
                final float bocaX=origenX,bocaY=origenY;
                if(salvasNormales==4){
                    salvasNormales=0;
                    laserPrimerJefe=new LaserJefe(bocaX,bocaY,dx,dy);
                } else {
                    ataque.disparar(origenX,origenY,objetivoX,objetivoY,texturaProyectil,proyectil->{
                        proyectil.centrarEn(bocaX,bocaY);crearProyectil.accept(proyectil);
                    });
                    salvasNormales++;
                }
            } else {
                ataque.disparar(origenX, origenY, objetivoX, objetivoY,
                        texturaProyectil, crearProyectil);
            }
            tiempoAtaque = ataque.getIntervalo();
        }
    }

    public LaserJefe getLaserPrimerJefe(){return estaActivo()?laserPrimerJefe:null;}

    public SectorGalactico getSector() {
        return sector;
    }

    public void actualizarAtaqueEspecial(float delta, float jugadorX, float jugadorY,
                                         Consumer<CargaEnergia> soltarEnergia) {
        if (estaActivo() && ataqueBarrera != null) {
            ataqueBarrera.actualizar(delta, jugadorX, jugadorY, soltarEnergia);
        }
    }

    public BarreraEnergia getBarreraEnergia() {
        return estaActivo() && ataqueBarrera != null ? ataqueBarrera.getBarrera() : null;
    }

    public java.util.List<BarreraEnergia> getBarrerasEnergia() {
        return estaActivo() && ataqueBarrera != null ? ataqueBarrera.getBarreras()
                : java.util.Collections.emptyList();
    }

    /** Los primeros jefes invocan por debajo; el tercero genera dos naves aleatorias a los lados. */
    public void actualizarRefuerzos(float delta, float velocidadAlien, int resistenciaAlien,
                                    Texture texturaAlien, Consumer<AlienEnemigo> crearRefuerzo) {
        if (!estaActivo() || delta <= 0f) {
            return;
        }
        tiempoRefuerzos -= delta;
        while (tiempoRefuerzos <= 0f) {
            if(sector==SectorGalactico.FRONTERA_DE_KHEPRI) {
                float y=getCentroY()-24f;
                float izquierda=Math.max(0f,getArea().x-56f);
                float derecha=Math.min(MundoJuego.ANCHO-48f,getArea().x+getArea().width+8f);
                emitirRefuerzo(crearRefuerzoAleatorio(izquierda,y,velocidadAlien,resistenciaAlien,texturaAlien),crearRefuerzo);
                emitirRefuerzo(crearRefuerzoAleatorio(derecha,y,velocidadAlien,resistenciaAlien,texturaAlien),crearRefuerzo);
                refuerzosCreados+=2;
                tiempoRefuerzos+=getIntervaloRefuerzos();
                continue;
            }
            float x = Math.max(0f, Math.min(MundoJuego.ANCHO - 48f, getCentroX() - 24f));
            float y = Math.max(0f, Math.min(MundoJuego.ALTO - 48f, getArea().y - 48f));
            boolean segundo = sector == SectorGalactico.NEBULOSA_DE_ION;
            boolean naranja = segundo && refuerzosCreados % 3 == 2;
            boolean roja = segundo && refuerzosCreados % 3 == 1;
            AlienEnemigo refuerzo = naranja
                    ? new AlienArtillero(x,y,texturaAlien,new java.util.Random())
                    : roja
                    ? new AlienExplosivo(x, y, 48f, velocidadAlien, AlienExplosivo.RESISTENCIA, texturaAlien)
                    : new AlienEnemigo(x, y, velocidadAlien, resistenciaAlien, texturaAlien);
            emitirRefuerzo(refuerzo,crearRefuerzo);
            refuerzosCreados++;
            tiempoRefuerzos += getIntervaloRefuerzos();
        }
    }

    private void emitirRefuerzo(AlienEnemigo refuerzo,Consumer<AlienEnemigo> emitir) {
        refuerzo.configurarVidaParaRonda(getRondaDeVida());
        emitir.accept(refuerzo);
    }

    private AlienEnemigo crearRefuerzoAleatorio(float x,float y,float velocidad,int resistencia,Texture textura) {
        switch(randomRefuerzos.nextInt(5)) {
            case 1:return new AlienExplosivo(x,y,48f,velocidad,AlienExplosivo.RESISTENCIA,textura);
            case 2:return new AlienDivisible(x,y,velocidad,textura);
            case 3:return new AlienArtillero(x,y,textura,new java.util.Random(randomRefuerzos.nextLong()));
            case 4:return (AlienEnemigo)new EnemigoBuilder().tipo(EnemigoBuilder.Tipo.ALIEN_DETONADORA)
                    .posicion(x,y).velocidad(velocidad,velocidad).resistencia(AlienDetonadora.RESISTENCIA).construir(textura);
            default:return new AlienEnemigo(x,y,velocidad,resistencia,textura);
        }
    }

    public float getIntervaloRefuerzos() {
        if (sector == SectorGalactico.NEBULOSA_DE_ION) return 5f;
        if (sector == SectorGalactico.FRONTERA_DE_KHEPRI) return 3.5f;
        return INTERVALO_REFUERZOS;
    }

    public String getNombre() {
        return "GUARDIAN DE " + sector.getNombre().toUpperCase();
    }
}
