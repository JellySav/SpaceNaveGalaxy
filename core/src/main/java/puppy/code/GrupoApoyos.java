package puppy.code;

import java.util.List;
import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/** Vista de los apoyos para resolver cada explosion sin duplicar actualizaciones. */
final class GrupoApoyos implements ApoyoNave {
    private final ApoyoNave[] apoyos;
    GrupoApoyos(ApoyoNave... apoyos){this.apoyos=apoyos;}
    void recibirExplosion(ExplosionAlien explosion) {
        for(ApoyoNave apoyo:apoyos)
            if(apoyo!=null && explosion.alcanza(apoyo.getArea()))apoyo.desactivar(ExplosionAlien.DESACTIVACION_DRON);
    }
    public void actualizar(float d,float x,float y,List<Enemigo> e,Texture t,Consumer<Bullet> b) { }
    public void dibujar(SpriteBatch batch) { }
    public Rectangle getArea(){for(ApoyoNave apoyo:apoyos)if(apoyo!=null)return apoyo.getArea();return new Rectangle();}
    public void desactivar(float segundos){for(ApoyoNave apoyo:apoyos)if(apoyo!=null)apoyo.desactivar(segundos);}
    public float getTiempoDesactivado(){float tiempo=0f;for(ApoyoNave apoyo:apoyos)if(apoyo!=null)tiempo=Math.max(tiempo,apoyo.getTiempoDesactivado());return tiempo;}
}
