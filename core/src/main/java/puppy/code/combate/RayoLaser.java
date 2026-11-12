package puppy.code.combate;

import puppy.code.*;
import puppy.code.enemigos.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** Impacto instantaneo en toda la trayectoria, con una estela breve sin dano continuo. */
public final class RayoLaser {
    private final Vector2 inicio,fin;
    private final float dano;
    private float tiempo=0.16f;
    private boolean aplicado;
    public RayoLaser(float x,float y,float dx,float dy,float dano,Texture textura) {
        if(!Float.isFinite(x)||!Float.isFinite(y)||!Float.isFinite(dano)||dano<=0f||textura==null) {
            throw new IllegalArgumentException("Configuracion de rayo invalida");
        }
        this.dano=dano;
        inicio=new Vector2(x,y);
        Vector2 direccion=new Vector2(dx,dy);
        if(direccion.isZero())direccion.set(0f,1f);
        direccion.nor();
        float alcance=1600f;
        if(direccion.x>0f)alcance=Math.min(alcance,(MundoJuego.ANCHO-x)/direccion.x);
        if(direccion.x<0f)alcance=Math.min(alcance,-x/direccion.x);
        if(direccion.y>0f)alcance=Math.min(alcance,(MundoJuego.ALTO-y)/direccion.y);
        if(direccion.y<0f)alcance=Math.min(alcance,-y/direccion.y);
        fin=new Vector2(inicio).mulAdd(direccion,Math.max(0f,alcance));
    }
    public void aplicar(List<Enemigo> enemigos,Consumer<Enemigo> alDerrotar) {
        if(aplicado)return;
        aplicado=true;
        for(Enemigo enemigo:new ArrayList<>(enemigos)) {
            if(!enemigo.estaActivo())continue;
            Rectangle area=new Rectangle(enemigo.getArea());
            area.x-=3f;area.y-=3f;area.width+=6f;area.height+=6f;
            if(!Intersector.intersectSegmentRectangle(inicio,fin,area))continue;
            enemigo.recibirDano(dano);
            if(enemigo.estaDestruido())alDerrotar.accept(enemigo);
        }
    }
    public void update(float delta){tiempo=Math.max(0f,tiempo-Math.max(0f,delta));}
    public boolean isDestroyed(){return tiempo<=0f;}
    public float getDano(){return dano;}
    public void dibujar(ShapeRenderer formas) {
        if(isDestroyed())return;
        float alpha=Math.min(1f,tiempo/0.06f);
        formas.setColor(0.15f,0.85f,1f,alpha);formas.rectLine(inicio,fin,6f);
        formas.setColor(0.75f,1f,1f,alpha);formas.rectLine(inicio,fin,2f);
    }
}
