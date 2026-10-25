package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/** Persigue lentamente; su detonacion combina la explosion roja con un anillo de balas. */
public final class AlienDetonadora extends AlienEnemigo {
    public static final int RESISTENCIA=3;
    public static final float FACTOR_VELOCIDAD=0.8f;
    private final TextureRegion izquierda,derecha;
    private boolean aparienciaAbeja;
    public AlienDetonadora(float x,float y,float tamano,float velocidadVerde,int resistencia,Texture textura) {
        super(x,y,tamano,velocidadVerde*FACTOR_VELOCIDAD,resistencia,textura);
        int mitad=textura.getWidth()/2;
        izquierda=new TextureRegion(textura,0,0,mitad,textura.getHeight());
        derecha=new TextureRegion(textura,mitad,0,textura.getWidth()-mitad,textura.getHeight());
    }
    public static int cantidadEnRonda(int ronda){return OleadaNaves.cantidadDetonadoras(ronda);}
    public void usarAparienciaAbeja(TextureRegion region){
        usarApariencia(region,48f);aparienciaAbeja=true;
    }
    @Override protected void dibujarSprite(SpriteBatch batch) {
        if(aparienciaAbeja){super.dibujarSprite(batch);return;}
        Rectangle area=getArea();float color=batch.getPackedColor();
        batch.setColor(1f,0.12f,0.16f,1f);batch.draw(izquierda,area.x,area.y,area.width/2f,area.height);
        batch.setColor(1f,0.5f,0.05f,1f);batch.draw(derecha,area.x+area.width/2f,area.y,area.width/2f,area.height);
        batch.setPackedColor(color);
    }
}
