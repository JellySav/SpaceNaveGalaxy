package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** Trayectoria fija: aviso inocuo y un disparo breve que alcanza hasta el borde. */
public final class LaserJefe {
    public static final float AVISO=1f,DURACION=0.25f,GROSOR=30f;
    private final Vector2 inicio,fin;
    private final float[] vertices=new float[8],objetivo=new float[8];
    private float tiempo;
    private boolean danoAplicado;

    public LaserJefe(float x,float y,float dx,float dy){
        inicio=new Vector2(x,y);
        Vector2 direccion=new Vector2(dx,dy);
        if(direccion.isZero())direccion.set(0,1);
        direccion.nor();
        float alcance=1600f;
        if(direccion.x>0)alcance=Math.min(alcance,(MundoJuego.ANCHO-x)/direccion.x);
        if(direccion.x<0)alcance=Math.min(alcance,-x/direccion.x);
        if(direccion.y>0)alcance=Math.min(alcance,(MundoJuego.ALTO-y)/direccion.y);
        if(direccion.y<0)alcance=Math.min(alcance,-y/direccion.y);
        fin=new Vector2(inicio).mulAdd(direccion,Math.max(0,alcance));
        float px=-direccion.y*GROSOR/2,py=direccion.x*GROSOR/2;
        vertices[0]=x+px;vertices[1]=y+py;vertices[2]=x-px;vertices[3]=y-py;
        vertices[4]=fin.x-px;vertices[5]=fin.y-py;vertices[6]=fin.x+px;vertices[7]=fin.y+py;
    }
    public void actualizar(float delta){tiempo+=Math.max(0,delta);}
    public boolean estaAvisando(){return tiempo<AVISO;}
    public boolean termino(){return tiempo>=AVISO+DURACION;}
    public boolean toca(Rectangle area){
        if(estaAvisando()||termino())return false;
        objetivo[0]=area.x;objetivo[1]=area.y;objetivo[2]=area.x+area.width;objetivo[3]=area.y;
        objetivo[4]=area.x+area.width;objetivo[5]=area.y+area.height;objetivo[6]=area.x;objetivo[7]=area.y+area.height;
        return Intersector.overlapConvexPolygons(vertices,objetivo,null);
    }
    public void comprobarImpacto(Nave4 nave){
        if(!danoAplicado && toca(nave.getArea()) && nave.recibirDano())danoAplicado=true;
    }
    public void dibujar(ShapeRenderer formas){
        if(termino())return;
        if(estaAvisando()){
            formas.setColor(1f,.06f,.1f,1f);formas.rectLine(inicio,fin,2f);
        }else{
            formas.setColor(.85f,.01f,.04f,1f);formas.rectLine(inicio,fin,GROSOR);
            formas.setColor(1f,.3f,.32f,1f);formas.rectLine(inicio,fin,10f);
        }
    }
}
