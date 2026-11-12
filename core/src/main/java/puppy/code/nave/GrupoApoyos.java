package puppy.code.nave;

import puppy.code.combate.*;
import puppy.code.enemigos.*;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/** Compuesto de apoyos que conserva el comportamiento de cada miembro. */
public final class GrupoApoyos implements ApoyoNave {
    private final List<ApoyoNave> apoyos;
    public GrupoApoyos(ApoyoNave... apoyos){
        this.apoyos=new ArrayList<>();
        if(apoyos!=null)for(ApoyoNave apoyo:apoyos)if(apoyo!=null)this.apoyos.add(apoyo);
    }
    @Override public void recibirExplosion(ExplosionAlien explosion) {
        for(ApoyoNave apoyo:apoyos)
            if(explosion.alcanza(apoyo.getArea()))apoyo.desactivar(ExplosionAlien.DESACTIVACION_DRON);
    }
    @Override public void orientarDetras(float direccionX,float direccionY) {
        for(ApoyoNave apoyo:apoyos)apoyo.orientarDetras(direccionX,direccionY);
    }
    @Override public void actualizar(float delta,float x,float y,List<Enemigo> enemigos,
                                     Texture textura,Consumer<Bullet> emitirBala) {
        for(ApoyoNave apoyo:apoyos)apoyo.actualizar(delta,x,y,enemigos,textura,emitirBala);
    }
    @Override public List<RayoLaser> extraerRayos() {
        List<RayoLaser> rayos=new ArrayList<>();
        for(ApoyoNave apoyo:apoyos)rayos.addAll(apoyo.extraerRayos());
        return rayos;
    }
    @Override public void dibujar(SpriteBatch batch) {
        for(ApoyoNave apoyo:apoyos)apoyo.dibujar(batch);
    }
    @Override public Rectangle getArea(){
        if(apoyos.isEmpty())return new Rectangle();
        Rectangle limites=new Rectangle(apoyos.get(0).getArea());
        for(int i=1;i<apoyos.size();i++)limites.merge(apoyos.get(i).getArea());
        return limites;
    }
    @Override public void desactivar(float segundos) {
        for(ApoyoNave apoyo:apoyos)apoyo.desactivar(segundos);
    }
    @Override public float getTiempoDesactivado(){
        float tiempo=0f;
        for(ApoyoNave apoyo:apoyos)tiempo=Math.max(tiempo,apoyo.getTiempoDesactivado());
        return tiempo;
    }
}
