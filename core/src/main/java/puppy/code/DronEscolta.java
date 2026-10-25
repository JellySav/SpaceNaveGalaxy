package puppy.code;

import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class DronEscolta implements ApoyoNave {
    private final Sprite sprite;
    private Sprite apariencia;
    private final boolean explosivo;
    private final boolean mini;
    private float frenteX,frenteY=1f;
    private float cooldownDisparo;
    private float tiempoDesactivado;

    public DronEscolta(Texture textura) { this(textura,false); }
    public DronEscolta(Texture textura,boolean explosivo) { this(textura,explosivo,false); }
    public DronEscolta(Texture textura,boolean explosivo,boolean mini) {
        this.explosivo=explosivo;this.mini=mini;
        sprite = new Sprite(textura);
        sprite.setSize(mini?24f:32f, mini?24f:32f);
        sprite.setOriginCenter();
        sprite.setColor(0.4f, 0.92f, 1f, 1f);
    }

    public final void usarApariencia(TextureRegion region) {
        apariencia=region==null?null:new Sprite(region);
        if(apariencia!=null) {
            float escala=Math.max(sprite.getWidth(),sprite.getHeight())
                    /Math.max(region.getRegionWidth(),region.getRegionHeight());
            apariencia.setSize(region.getRegionWidth()*escala,region.getRegionHeight()*escala);
            apariencia.setOriginCenter();
        }
    }

    protected boolean esLaser(){return false;}

    @Override
    public void orientarDetras(float x,float y){
        if(x==0f && y==0f)return;
        frenteX=x;frenteY=y;
        sprite.setRotation((float)Math.toDegrees(Math.atan2(y,x))-90f);
    }

    @Override
    public void actualizar(float delta, float naveX, float naveY, List<Enemigo> enemigos,
                           Texture texturaBala, Consumer<Bullet> emitirBala) {
        float distanciaTrasera = esLaser() ? 112f : 58f;
        float desplazamientoLateral = mini || esLaser() ? 0f : explosivo ? -56f : 56f;
        float destinoX = naveX-frenteX*distanciaTrasera+frenteY*desplazamientoLateral-sprite.getWidth()/2f;
        float destinoY = naveY-frenteY*distanciaTrasera-frenteX*desplazamientoLateral-sprite.getHeight()/2f;
        float seguimiento = Math.min(1f, delta * 5f);
        sprite.setPosition(sprite.getX() + (destinoX - sprite.getX()) * seguimiento,
                sprite.getY() + (destinoY - sprite.getY()) * seguimiento);

        tiempoDesactivado = Math.max(0f, tiempoDesactivado - Math.max(0f, delta));
        if (estaDesactivado()) {
            sprite.setColor(0.38f, 0.4f, 0.44f, 0.65f);
            return;
        }
        sprite.setColor(0.4f, 0.92f, 1f, 1f);
        cooldownDisparo -= delta;
        if (cooldownDisparo > 0f) {
            return;
        }

        Enemigo objetivo = buscarObjetivo(enemigos);
        if (objetivo == null) {
            return;
        }
        Rectangle area = objetivo.getArea();
        float origenX = sprite.getX() + sprite.getWidth() / 2f;
        float origenY = sprite.getY() + sprite.getHeight() / 2f;
        float diferenciaX = area.x + area.width / 2f - origenX;
        float diferenciaY = area.y + area.height / 2f - origenY;
        float distancia = (float) Math.sqrt(diferenciaX * diferenciaX + diferenciaY * diferenciaY);
        if (distancia > 0f) {
            ProgresionMejoras p=ProgresionMejoras.obtenerInstancia();
            float dx=diferenciaX/distancia,dy=diferenciaY/distancia;
            if(mini) {
                TipoMejora turno=p.siguienteDisparoMiniDron();
                boolean quintuple=p.tieneMiniDronQuintuple();
                if(turno==TipoMejora.DRON_LASER) {
                    dispararLaser(origenX,origenY,dx,dy,p.getDanoLaser()*0.5f,quintuple,texturaBala,emitirBala);
                } else {
                    boolean misil=turno==TipoMejora.DRON_EXPLOSIVO;
                    float dano=turno==TipoMejora.INICIAL_DRON?p.getDanoMiniDron():p.getDanoDron(misil)*0.5f;
                    dispararProyectiles(origenX,origenY,dx,dy,dano,misil,quintuple,texturaBala,emitirBala);
                }
            } else if(esLaser()) {
                dispararLaser(origenX,origenY,dx,dy,p.getDanoLaser(),false,texturaBala,emitirBala);
            } else {
                dispararProyectiles(origenX,origenY,dx,dy,p.getDanoDron(explosivo),explosivo,false,texturaBala,emitirBala);
            }
            cooldownDisparo = 1f/(esLaser()?p.getCadenciaLaser():mini?p.getCadenciaMiniDron():p.getCadenciaDron(explosivo));
        }
    }

    private void dispararProyectiles(float x,float y,float dx,float dy,float dano,boolean misil,
                                     boolean quintuple,Texture textura,Consumer<Bullet> emitir) {
        Consumer<Bullet> configurar=bala->{
            bala.configurar(dano,1f,0f,0f,java.util.concurrent.ThreadLocalRandom.current());
            if(misil)bala.comoMisil();
            emitir.accept(bala);
        };
        if(quintuple)new DisparoQuintuple(420f).disparar(x,y,dx,dy,textura,configurar);
        else configurar.accept(new Bullet(x,y,dx*420f,dy*420f,textura));
    }
    private void dispararLaser(float x,float y,float dx,float dy,float dano,boolean quintuple,
                              Texture textura,Consumer<Bullet> emitir) {
        int apertura=quintuple?2:0;
        for(int i=-apertura;i<=apertura;i++) {
            float avance=i==0?560f:500f,lateral=i*115f;
            emitir.accept(new RayoLaser(x+dy*i*10f,y-dx*i*10f,
                    dx*avance+dy*lateral,dy*avance-dx*lateral,dano,textura));
        }
    }

    private Enemigo buscarObjetivo(List<Enemigo> enemigos) {
        Enemigo cercano = null;
        float distanciaMinima = Float.MAX_VALUE;
        for (Enemigo enemigo : enemigos) {
            if (!enemigo.estaActivo()) {
                continue;
            }
            Rectangle area = enemigo.getArea();
            float diferenciaX = area.x + area.width / 2f - sprite.getX();
            float diferenciaY = area.y + area.height / 2f - sprite.getY();
            float distancia = diferenciaX * diferenciaX + diferenciaY * diferenciaY;
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                cercano = enemigo;
            }
        }
        return cercano;
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        if(apariencia==null){sprite.draw(batch);return;}
        apariencia.setCenter(sprite.getX()+sprite.getWidth()/2f,sprite.getY()+sprite.getHeight()/2f);
        apariencia.setRotation(sprite.getRotation()+(explosivo?180f:0f));
        if(estaDesactivado())apariencia.setColor(0.38f,0.4f,0.44f,0.65f);
        else apariencia.setColor(1f,1f,1f,1f);
        apariencia.draw(batch);
    }

    @Override
    public Rectangle getArea() { return sprite.getBoundingRectangle(); }

    @Override
    public void desactivar(float segundos) {
        tiempoDesactivado = Math.max(tiempoDesactivado, Math.max(0f, segundos));
        cooldownDisparo = 0f;
        sprite.setColor(0.38f, 0.4f, 0.44f, 0.65f);
    }

    @Override
    public float getTiempoDesactivado() { return tiempoDesactivado; }
}
