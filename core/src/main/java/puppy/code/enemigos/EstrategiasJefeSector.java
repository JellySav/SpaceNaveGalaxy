package puppy.code.enemigos;

import puppy.code.*;
import puppy.code.combate.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public final class EstrategiasJefeSector {
    private EstrategiasJefeSector() { }

    public static final class Cinturon extends EstrategiaJefeBase {
        private int salvasNormales;

        public Cinturon(AtaqueJefe ataque,AtaqueEspecialJefe especial,float velocidad,Texture textura) {
            super(ataque,especial,velocidad,textura,5f);
        }

        @Override public void actualizarMovimiento(JefeSector jefe,float delta,float objetivoX,float objetivoY) {
            jefe.setVelocidad(0f,0f);
            if(laser==null || !laser.estaAvisando())
                jefe.girarAparienciaHacia(objetivoX,objetivoY,JefeSector.VELOCIDAD_GIRO,delta);
        }

        @Override protected void disparar(JefeSector jefe,float objetivoX,float objetivoY,
                                          Consumer<ProyectilHostil> crearProyectil) {
            Vector2 direccion=jefe.direccionApariencia();
            Vector2 boca=jefe.puntoApariencia(0.5f,0.988f);
            if(salvasNormales==4) {
                salvasNormales=0;
                laser=new LaserJefe(boca.x,boca.y,direccion.x,direccion.y);
                return;
            }
            float objetivoBocaX=boca.x+direccion.x*100f;
            float objetivoBocaY=boca.y+direccion.y*100f;
            ataque.disparar(boca.x,boca.y,objetivoBocaX,objetivoBocaY,texturaProyectil,proyectil->{
                proyectil.centrarEn(boca.x,boca.y);
                crearProyectil.accept(proyectil);
            });
            salvasNormales++;
        }

        @Override protected void crearRefuerzos(JefeSector jefe,float velocidadAlien,int resistenciaAlien,
                                                Texture texturaAlien,Consumer<AlienEnemigo> crearRefuerzo) {
            Rectangle area=jefe.getArea();
            float x=Math.max(0f,Math.min(MundoJuego.ANCHO-48f,jefe.getCentroX()-24f));
            float y=Math.max(0f,Math.min(MundoJuego.ALTO-48f,area.y-48f));
            emitirRefuerzo(jefe,new AlienEnemigo(x,y,velocidadAlien,resistenciaAlien,texturaAlien),crearRefuerzo);
        }
    }

    public static final class Nebulosa extends Perseguidor {
        public Nebulosa(AtaqueJefe ataque,AtaqueEspecialJefe especial,float velocidad,Texture textura) {
            super(ataque,especial,velocidad,textura,5f);
        }

        @Override protected void crearRefuerzos(JefeSector jefe,float velocidadAlien,int resistenciaAlien,
                                                Texture texturaAlien,Consumer<AlienEnemigo> crearRefuerzo) {
            Rectangle area=jefe.getArea();
            float x=Math.max(0f,Math.min(MundoJuego.ANCHO-48f,jefe.getCentroX()-24f));
            float y=Math.max(0f,Math.min(MundoJuego.ALTO-48f,area.y-48f));
            AlienEnemigo refuerzo;
            if(refuerzosCreados%3==2) {
                refuerzo=new AlienArtillero(x,y,texturaAlien,new Random(jefe.getRandomRefuerzos().nextLong()));
            } else if(refuerzosCreados%3==1) {
                refuerzo=new AlienExplosivo(x,y,48f,velocidadAlien,AlienExplosivo.RESISTENCIA,texturaAlien);
            } else {
                refuerzo=new AlienEnemigo(x,y,velocidadAlien,resistenciaAlien,texturaAlien);
            }
            emitirRefuerzo(jefe,refuerzo,crearRefuerzo);
            refuerzosCreados++;
        }
    }

    public static final class Frontera extends Perseguidor {
        private final List<FabricaRefuerzo> fabricas=Arrays.asList(
                (x,y,v,r,t,random)->new AlienEnemigo(x,y,v,r,t),
                (x,y,v,r,t,random)->new AlienExplosivo(x,y,48f,v,AlienExplosivo.RESISTENCIA,t),
                (x,y,v,r,t,random)->new AlienDivisible(x,y,v,t),
                (x,y,v,r,t,random)->new AlienArtillero(x,y,t,new Random(random.nextLong())),
                (x,y,v,r,t,random)->new AlienDetonadora(x,y,48f,v,AlienDetonadora.RESISTENCIA,t));

        public Frontera(AtaqueJefe ataque,AtaqueEspecialJefe especial,float velocidad,Texture textura) {
            super(ataque,especial,velocidad,textura,3.5f);
        }

        @Override protected void crearRefuerzos(JefeSector jefe,float velocidadAlien,int resistenciaAlien,
                                                Texture texturaAlien,Consumer<AlienEnemigo> crearRefuerzo) {
            Rectangle area=jefe.getArea();
            float y=jefe.getCentroY()-24f;
            float izquierda=Math.max(0f,area.x-56f);
            float derecha=Math.min(MundoJuego.ANCHO-48f,area.x+area.width+8f);
            Random random=jefe.getRandomRefuerzos();
            emitirRefuerzo(jefe,crearRefuerzoAleatorio(izquierda,y,velocidadAlien,resistenciaAlien,texturaAlien,random),crearRefuerzo);
            emitirRefuerzo(jefe,crearRefuerzoAleatorio(derecha,y,velocidadAlien,resistenciaAlien,texturaAlien,random),crearRefuerzo);
            refuerzosCreados+=2;
        }

        private AlienEnemigo crearRefuerzoAleatorio(float x,float y,float velocidad,int resistencia,
                                                    Texture textura,Random random) {
            return fabricas.get(random.nextInt(fabricas.size()))
                    .crear(x,y,velocidad,resistencia,textura,random);
        }
    }
}

abstract class EstrategiaJefeBase implements EstrategiaJefeSector {
    protected final AtaqueJefe ataque;
    protected final AtaqueEspecialJefe ataqueEspecial;
    protected final Texture texturaProyectil;
    protected final float velocidadPersecucion;
    protected float tiempoAtaque;
    protected float tiempoRefuerzos;
    protected int refuerzosCreados;
    protected LaserJefe laser;
    private final float intervaloRefuerzos;

    EstrategiaJefeBase(AtaqueJefe ataque,AtaqueEspecialJefe ataqueEspecial,float velocidad,
                       Texture texturaProyectil,float intervaloRefuerzos) {
        if(ataque==null||texturaProyectil==null||intervaloRefuerzos<=0f)
            throw new IllegalArgumentException("La estrategia del jefe debe estar completa");
        this.ataque=ataque;this.ataqueEspecial=ataqueEspecial;
        this.texturaProyectil=texturaProyectil;this.velocidadPersecucion=Math.abs(velocidad);
        this.intervaloRefuerzos=intervaloRefuerzos;
        tiempoAtaque=ataque.getIntervalo();
        tiempoRefuerzos=intervaloRefuerzos;
    }

    @Override public final void actualizarAtaque(JefeSector jefe,float delta,float objetivoX,float objetivoY,
                                                  Consumer<ProyectilHostil> crearProyectil) {
        if(!jefe.estaActivo()||delta<=0f)return;
        if(laser!=null) {
            laser.actualizar(delta);
            if(laser.termino())laser=null;
            return;
        }
        tiempoAtaque-=delta;
        if(tiempoAtaque<=0f) {
            disparar(jefe,objetivoX,objetivoY,crearProyectil);
            tiempoAtaque=ataque.getIntervalo();
        }
    }

    protected void disparar(JefeSector jefe,float objetivoX,float objetivoY,
                            Consumer<ProyectilHostil> crearProyectil) {
        ataque.disparar(jefe.getCentroX(),jefe.getCentroY(),objetivoX,objetivoY,
                texturaProyectil,crearProyectil);
    }

    @Override public final void actualizarRefuerzos(JefeSector jefe,float delta,float velocidadAlien,
                                                      int resistenciaAlien,Texture texturaAlien,
                                                      Consumer<AlienEnemigo> crearRefuerzo) {
        if(!jefe.estaActivo()||delta<=0f)return;
        tiempoRefuerzos-=delta;
        while(tiempoRefuerzos<=0f) {
            crearRefuerzos(jefe,velocidadAlien,resistenciaAlien,texturaAlien,crearRefuerzo);
            tiempoRefuerzos+=intervaloRefuerzos;
        }
    }

    protected abstract void crearRefuerzos(JefeSector jefe,float velocidadAlien,int resistenciaAlien,
                                           Texture texturaAlien,Consumer<AlienEnemigo> crearRefuerzo);

    protected final void emitirRefuerzo(JefeSector jefe,AlienEnemigo refuerzo,
                                        Consumer<AlienEnemigo> emitir) {
        refuerzo.configurarVidaParaRonda(jefe.getRondaDeVida());
        emitir.accept(refuerzo);
    }

    @Override public float getIntervaloRefuerzos(){return intervaloRefuerzos;}
    @Override public LaserJefe getLaser(){return laser;}
}

abstract class Perseguidor extends EstrategiaJefeBase {
    Perseguidor(AtaqueJefe ataque,AtaqueEspecialJefe especial,float velocidad,Texture textura,
                float intervaloRefuerzos) {
        super(ataque,especial,velocidad,textura,intervaloRefuerzos);
    }

    @Override public void actualizarMovimiento(JefeSector jefe,float delta,float objetivoX,float objetivoY) {
        if(ataqueEspecial!=null&&ataqueEspecial.getBarrera()!=null) {
            jefe.setVelocidad(0f,0f);
            return;
        }
        float dx=objetivoX-jefe.getCentroX(),dy=objetivoY-jefe.getCentroY();
        float distancia=(float)Math.hypot(dx,dy);
        if(distancia<0.001f) {
            jefe.setVelocidad(0f,0f);
            return;
        }
        float velocidad=Math.min(velocidadPersecucion,distancia/delta);
        jefe.setVelocidad(dx/distancia*velocidad,dy/distancia*velocidad);
    }
}

@FunctionalInterface
interface FabricaRefuerzo {
    AlienEnemigo crear(float x,float y,float velocidad,int resistencia,Texture textura,Random random);
}
