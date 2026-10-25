package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class Enemigo implements ObjetivoDisparo {
    private final Sprite sprite;
    private TextureRegion apariencia;
    private float anguloApariencia;
    private float pivoteAparienciaX=0.5f,pivoteAparienciaY=0.5f;
    private float ancho;
    private float alto;
    private float x;
    private float y;
    private float velocidadX;
    private float velocidadY;
    private float resistencia;
    private float resistenciaMaxima;
    private int rondaDeVida=1;
    private boolean activo = true;

    protected Enemigo(Texture textura, float x, float y, float ancho, float alto,
                      float velocidadX, float velocidadY, int resistencia) {
        if (textura == null || ancho <= 0f || alto <= 0f || resistencia <= 0) {
            throw new IllegalArgumentException("Configuracion de enemigo invalida");
        }
        this.sprite = new Sprite(textura);
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidadX = velocidadX;
        this.velocidadY = velocidadY;
        this.resistencia = resistencia * 10f;
        resistenciaMaxima = this.resistencia;
        sprite.setSize(ancho, alto);
        sprite.setPosition(x, y);
    }

    public final void actualizar(float delta, float anchoMundo, float altoMundo,
                                 float objetivoX, float objetivoY) {
        if (!activo || delta <= 0f) {
            return;
        }

        actualizarComportamiento(delta, objetivoX, objetivoY);
        x += velocidadX * delta;
        y += velocidadY * delta;

        if (rebotaEnBordes()) {
            float maxX = Math.max(0f, anchoMundo - ancho);
            float maxY = Math.max(0f, altoMundo - alto);
            if (x < 0f) {
                x = 0f;
                velocidadX = Math.abs(velocidadX);
            } else if (x > maxX) {
                x = maxX;
                velocidadX = -Math.abs(velocidadX);
            }
            if (y < 0f) {
                y = 0f;
                velocidadY = Math.abs(velocidadY);
            } else if (y > maxY) {
                y = maxY;
                velocidadY = -Math.abs(velocidadY);
            }
        }
        sprite.setPosition(x, y);
    }

    public final void update() {
        actualizar(1f / 60f, MundoJuego.ANCHO, MundoJuego.ALTO,
                getCentroX(), getCentroY());
    }

    /** Corrige una colision manteniendo sincronizados posicion logica y sprite. */
    final void desplazar(float dx, float dy, float anchoMundo, float altoMundo) {
        x = Math.max(0f, Math.min(Math.max(0f, anchoMundo - ancho), x + dx));
        y = Math.max(0f, Math.min(Math.max(0f, altoMundo - alto), y + dy));
        sprite.setPosition(x, y);
    }

    /** El tamano visible y la colision comparten la region sin margenes transparentes. */
    public final void usarApariencia(TextureRegion region) {
        usarApariencia(region,Math.max(ancho,alto));
    }
    public final void usarApariencia(TextureRegion region,float tamano) {
        if(apariencia==region && Math.abs(Math.max(ancho,alto)-tamano)<0.001f)return;
        float centroX=getCentroX(),centroY=getCentroY();
        apariencia=region;
        float factor=tamano/Math.max(region.getRegionWidth(),region.getRegionHeight());
        ancho=region.getRegionWidth()*factor;alto=region.getRegionHeight()*factor;
        x=centroX-ancho/2f;y=centroY-alto/2f;
        sprite.setRegion(region);sprite.setSize(ancho,alto);sprite.setOriginCenter();
        sprite.setPosition(x,y);sprite.setColor(1f,1f,1f,1f);
    }

    protected boolean rebotaEnBordes() { return true; }

    protected abstract void actualizarComportamiento(float delta, float objetivoX, float objetivoY);

    public final void dibujar(SpriteBatch batch) {
        if (activo) {
            dibujarSprite(batch);
        }
    }

    /** Gira solo la imagen; la colision conserva su tamano al cambiar de direccion. */
    protected final void orientarApariencia(float objetivoX,float objetivoY) {
        float dx=objetivoX-getCentroX(),dy=objetivoY-getCentroY();
        if(dx*dx+dy*dy>0.0001f)
            anguloApariencia=(float)Math.toDegrees(Math.atan2(dy,dx))-90f;
    }

    protected final void girarAparienciaHacia(float objetivoX,float objetivoY,float gradosPorSegundo,float delta){
        float dx=objetivoX-getCentroX(),dy=objetivoY-getCentroY();
        if(dx*dx+dy*dy<0.0001f || delta<=0f)return;
        float destino=(float)Math.toDegrees(Math.atan2(dy,dx))-90f;
        float diferencia=((destino-anguloApariencia)%360f+540f)%360f-180f;
        float paso=Math.max(0f,gradosPorSegundo)*delta;
        anguloApariencia+=Math.max(-paso,Math.min(paso,diferencia));
    }

    protected final com.badlogic.gdx.math.Vector2 direccionApariencia(){
        return new com.badlogic.gdx.math.Vector2(0f,1f).rotateDeg(anguloApariencia);
    }

    public final void usarPivoteApariencia(float xNormalizado,float yNormalizado) {
        pivoteAparienciaX=xNormalizado;pivoteAparienciaY=yNormalizado;
    }

    /** Coordenada local normalizada de la imagen, transformada igual que al dibujar. */
    protected final com.badlogic.gdx.math.Vector2 puntoApariencia(float xNormalizado,float yNormalizado) {
        return new com.badlogic.gdx.math.Vector2((xNormalizado-pivoteAparienciaX)*ancho,
                (yNormalizado-pivoteAparienciaY)*alto).rotateDeg(anguloApariencia)
                .add(getCentroX(),getCentroY());
    }

    protected void dibujarSprite(SpriteBatch batch) {
        if(apariencia==null){sprite.draw(batch);return;}
        float rotacionAnterior=sprite.getRotation(),xAnterior=sprite.getX(),yAnterior=sprite.getY();
        float origenX=sprite.getOriginX(),origenY=sprite.getOriginY();
        try {
            sprite.setOrigin(ancho*pivoteAparienciaX,alto*pivoteAparienciaY);
            sprite.setPosition(getCentroX()-sprite.getOriginX(),getCentroY()-sprite.getOriginY());
            sprite.setRotation(anguloApariencia);sprite.draw(batch);
        } finally {
            sprite.setRotation(rotacionAnterior);sprite.setOrigin(origenX,origenY);
            sprite.setPosition(xAnterior,yAnterior);
        }
    }

    protected final void setColor(float rojo, float verde, float azul, float alpha) {
        sprite.setColor(rojo, verde, azul, alpha);
    }

    public final void draw(SpriteBatch batch) {
        dibujar(batch);
    }

    @Override
    public final Rectangle getArea() {
        return sprite.getBoundingRectangle();
    }

    @Override
    public final void recibirImpacto() {
        recibirDano(10f);
    }

    @Override
    public final void recibirDano(float dano) {
        if(!Float.isFinite(dano) || dano<=0f || !activo)return;
        resistencia=Math.max(0f,resistencia-dano);
        if(resistencia<=0f)activo=false;
    }

    /** Aumento fijo desde la ronda 5; reaplicarlo no acumula ni rellena vida. */
    public final void configurarVidaParaRonda(int ronda) {
        if(rondaDeVida>=5 || ronda<5)return;
        rondaDeVida=ronda;
        resistencia+=resistencia*0.2f;
        resistenciaMaxima+=resistenciaMaxima*0.2f;
    }
    protected final int getRondaDeVida(){return rondaDeVida;}

    public final float getVidaActual(){return resistencia;}
    public final float getVidaMaxima(){return resistenciaMaxima;}

    @Override
    public final boolean estaActivo() {
        return activo;
    }

    public final boolean estaDestruido() {
        return !activo;
    }

    public final float getFraccionResistencia() {
        return Math.max(0f, (float) resistencia / resistenciaMaxima);
    }

    protected final float getVelocidadX() {
        return velocidadX;
    }

    protected final float getVelocidadY() {
        return velocidadY;
    }

    protected final void setVelocidad(float x, float y) {
        velocidadX = x;
        velocidadY = y;
    }

    protected final float getCentroX() {
        return x + ancho / 2f;
    }

    protected final float getCentroY() {
        return y + alto / 2f;
    }
}
