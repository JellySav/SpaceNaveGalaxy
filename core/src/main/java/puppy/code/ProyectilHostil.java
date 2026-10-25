package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ProyectilHostil {
    private final Sprite sprite;
    private final float velocidadX;
    private final float velocidadY;
    private boolean activo = true;
    private final float[] rectanguloObjetivo=new float[8];
    private final float[] verticesLaser=new float[8];

    public ProyectilHostil(float x, float y, float direccionX, float direccionY,
                           float velocidad, Texture textura) {
        float longitud = (float) Math.sqrt(direccionX * direccionX + direccionY * direccionY);
        if (longitud == 0f || velocidad <= 0f || textura == null) {
            throw new IllegalArgumentException("Proyectil hostil invalido");
        }
        velocidadX = direccionX / longitud * velocidad;
        velocidadY = direccionY / longitud * velocidad;
        sprite = new Sprite(textura);
        sprite.setSize(16f, 24f);
        sprite.setColor(1f, 0.3f, 0.24f, 1f);
        sprite.setPosition(x, y);
        orientar();
    }

    /** Alinea el centro del proyectil con la boca del canon, antes de cambiar su textura. */
    final void centrarEn(float x,float y) {
        sprite.setCenter(x,y);
    }

    /** La region excluye el margen transparente del archivo original. */
    public void usarSpriteLaser(TextureRegion region) {
        float centroX=sprite.getX()+sprite.getWidth()/2f;
        float centroY=sprite.getY()+sprite.getHeight()/2f;
        sprite.setRegion(region);
        float alto=32f,ancho=alto*region.getRegionWidth()/region.getRegionHeight();
        sprite.setSize(ancho,alto);
        sprite.setPosition(centroX-ancho/2f,centroY-alto/2f);
        sprite.setColor(1f,1f,1f,1f);
        orientar();
    }
    private void orientar() {
        sprite.setOriginCenter();
        sprite.setRotation((float)Math.toDegrees(Math.atan2(velocidadY,velocidadX))-90f);
    }

    public void actualizar(float delta, float anchoMundo, float altoMundo) {
        sprite.setPosition(sprite.getX() + velocidadX * delta,
                sprite.getY() + velocidadY * delta);
        Rectangle limites=sprite.getBoundingRectangle();
        if (limites.x+limites.width<0f || limites.x>anchoMundo
                || limites.y+limites.height<0f || limites.y>altoMundo) {
            activo = false;
        }
    }

    public void dibujar(SpriteBatch batch) {
        if (activo) {
            sprite.draw(batch);
        }
    }

    public boolean toca(Rectangle area) {
        if(!activo || !sprite.getBoundingRectangle().overlaps(area))return false;
        // Usa el rectangulo orientado, no la caja grande que lo encierra en diagonal.
        float[] vertices=sprite.getVertices();
        for(int i=0;i<4;i++){verticesLaser[i*2]=vertices[i*5];verticesLaser[i*2+1]=vertices[i*5+1];}
        rectanguloObjetivo[0]=area.x;rectanguloObjetivo[1]=area.y;
        rectanguloObjetivo[2]=area.x+area.width;rectanguloObjetivo[3]=area.y;
        rectanguloObjetivo[4]=area.x+area.width;rectanguloObjetivo[5]=area.y+area.height;
        rectanguloObjetivo[6]=area.x;rectanguloObjetivo[7]=area.y+area.height;
        return Intersector.overlapConvexPolygons(verticesLaser,rectanguloObjetivo,null);
    }

    public boolean estaActivo() {
        return activo;
    }

    public void destruir() {
        activo = false;
    }
}