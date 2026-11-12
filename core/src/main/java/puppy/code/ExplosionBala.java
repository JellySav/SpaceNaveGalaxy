package puppy.code;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** El dano se aplica una vez al impactar; el breve destello no retrasa la detonacion. */
public final class ExplosionBala {
    public static final float RADIO=60f;
    private final float x,y,dano,radio;
    private boolean aplicada;
    private float tiempo;
    public ExplosionBala(float x,float y,float dano){this(x,y,dano,RADIO);}
    public ExplosionBala(float x,float y,float dano,float radio){this.x=x;this.y=y;this.dano=dano;this.radio=radio;}
    public void aplicar(List<Enemigo> enemigos,Consumer<Enemigo> derrota) {
        if(aplicada)return;aplicada=true;
        for(Enemigo enemigo:new ArrayList<>(enemigos)) {
            if(!enemigo.estaActivo() || !alcanza(enemigo.getArea()))continue;
            enemigo.recibirDano(dano);
            if(enemigo.estaDestruido())derrota.accept(enemigo);
        }
    }
    public boolean alcanza(Rectangle a) {
        float dx=x-Math.max(a.x,Math.min(x,a.x+a.width));
        float dy=y-Math.max(a.y,Math.min(y,a.y+a.height));
        return dx*dx+dy*dy<=radio*radio;
    }
    public void actualizar(float delta){tiempo+=Math.max(0f,delta);}
    public boolean termino(){return tiempo>=0.18f;}
    public void dibujar(ShapeRenderer formas) {
        formas.setColor(1f,0.65f,0.2f,Math.max(0f,0.5f*(1f-tiempo/0.18f)));
        formas.circle(x,y,radio,32);
    }
}
