package puppy.code;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Bullet {
    private final float xSpeed,ySpeed;
    private final Sprite spr;
    private Sprite apariencia;
    public static final float RANGO_FRAGMENTO=140f;
    private boolean destroyed,soloExplosion,critico,racimo;
    private float rangoRestante=Float.POSITIVE_INFINITY;
    private final List<Bullet> fragmentos=new ArrayList<>();
    private float dano=10f,penetracion,explosion;
    private Random random=ThreadLocalRandom.current();
    private final Set<ObjetivoDisparo> impactados=Collections.newSetFromMap(new IdentityHashMap<>());
    private final List<ExplosionBala> explosiones=new ArrayList<>();
    public Bullet(float x,float y,float xSpeed,float ySpeed,Texture textura) {
        spr=new Sprite(textura);spr.setPosition(x,y);spr.setOriginCenter();
        spr.setRotation((float)Math.toDegrees(Math.atan2(ySpeed,xSpeed))-90f);
        this.xSpeed=xSpeed;this.ySpeed=ySpeed;
    }
    public Bullet configurar(float dano,float tamano,float penetracion,float explosion,Random random) {
        this.dano=dano;this.penetracion=penetracion;this.explosion=explosion;this.random=random;
        spr.setScale(tamano);return this;
    }
    public Bullet configurarJugador(ProgresionMejoras p,Random random) {
        configurar(p.getDano(),p.getTamano(),p.getPenetracion(),p.getExplosion(),random);
        racimo=p.tieneRacimo();
        if(p.getProbabilidadCritico()>0f && random.nextFloat()<p.getProbabilidadCritico()) {
            critico=true;dano*=p.getMultiplicadorCritico();spr.setColor(1f,1f,0f,1f);
        }
        return this;
    }
    public void usarTexturaCritica(Texture textura){if(critico && textura!=null)spr.setTexture(textura);}
    public boolean esCritico(){return critico;}
    public Bullet comoMisil(){soloExplosion=true;explosion=1f;return this;}
    public void update(float delta) {
        float paso=Math.max(0f,delta);
        float velocidad=(float)Math.hypot(xSpeed,ySpeed);
        if(velocidad>0f && Float.isFinite(rangoRestante)) {
            paso=Math.min(paso,rangoRestante/velocidad);
            rangoRestante=Math.max(0f,rangoRestante-velocidad*paso);
            if(rangoRestante<=0.001f)destroyed=true;
        }
        spr.setPosition(spr.getX()+xSpeed*paso,spr.getY()+ySpeed*paso);
        Rectangle a=getArea();
        if(a.x+a.width<0f||a.x>MundoJuego.ANCHO||a.y+a.height<0f||a.y>MundoJuego.ALTO)destroyed=true;
    }
    public void update(){update(1f/60f);}
    /** El nuevo grafico no modifica el tamano logico ni la trayectoria del proyectil. */
    public void usarApariencia(TextureRegion region) {
        apariencia=new Sprite(region);
        float escala=Math.min(spr.getWidth()/region.getRegionWidth(),spr.getHeight()/region.getRegionHeight());
        apariencia.setSize(region.getRegionWidth()*escala,region.getRegionHeight()*escala);
        apariencia.setOriginCenter();
    }
    public void draw(SpriteBatch batch){
        if(apariencia==null){spr.draw(batch);return;}
        apariencia.setCenter(spr.getX()+spr.getWidth()/2f,spr.getY()+spr.getHeight()/2f);
        apariencia.setRotation(spr.getRotation());apariencia.setScale(spr.getScaleX(),spr.getScaleY());
        apariencia.setColor(spr.getColor());apariencia.draw(batch);
    }
    public Rectangle getArea(){return spr.getBoundingRectangle();}
    public boolean checkCollision(ObjetivoDisparo objetivo) {
        if(destroyed||!objetivo.estaActivo()||impactados.contains(objetivo)||!getArea().overlaps(objetivo.getArea()))return false;
        impactados.add(objetivo);
        if(!soloExplosion)objetivo.recibirDano(dano);
        if(racimo)dividirEnRacimo(objetivo);
        if(explosion>0f && random.nextFloat()<explosion) {
            Rectangle a=getArea();
            explosiones.add(new ExplosionBala(a.x+a.width/2f,a.y+a.height/2f,dano*(soloExplosion?1f:2f)));
        }
        destroyed=soloExplosion || penetracion<=0f || random.nextFloat()>=penetracion;
        return true;
    }
    private void dividirEnRacimo(ObjetivoDisparo golpeado) {
        Rectangle area=getArea();
        float x=area.x+area.width/2f,y=area.y+area.height/2f;
        double direccion=Math.atan2(ySpeed,xSpeed);
        for(int i=0;i<4;i++) {
            double angulo=direccion+Math.toRadians(45f+i*90f);
            Bullet fragmento=new Bullet(x,y,(float)Math.cos(angulo)*360f,
                    (float)Math.sin(angulo)*360f,spr.getTexture())
                    .configurar(dano/4f,spr.getScaleX()*0.5f,penetracion,explosion,random);
            fragmento.spr.setCenter(x,y);
            fragmento.spr.setColor(spr.getColor());fragmento.critico=critico;
            fragmento.rangoRestante=RANGO_FRAGMENTO;
            fragmento.impactados.add(golpeado);
            fragmentos.add(fragmento);
        }
    }
    public List<Bullet> extraerFragmentos(){List<Bullet> lista=new ArrayList<>(fragmentos);fragmentos.clear();return lista;}
    public List<ExplosionBala> extraerExplosiones(){List<ExplosionBala> lista=new ArrayList<>(explosiones);explosiones.clear();return lista;}
    public float getDano(){return dano;}
    public boolean isDestroyed(){return destroyed;}
}
