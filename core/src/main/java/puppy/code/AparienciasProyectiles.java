package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/** Texturas compartidas: misil aprobado y su silueta blanca para los criticos amarillos. */
public final class AparienciasProyectiles implements Disposable {
    private final TextureRegion normal,critico;
    AparienciasProyectiles(TextureRegion normal,TextureRegion critico){this.normal=normal;this.critico=critico;}
    public AparienciasProyectiles() {
        Pixmap imagen=new Pixmap(Gdx.files.internal("misil-normal.png"));
        try {
            int minX=imagen.getWidth(),minY=imagen.getHeight(),maxX=-1,maxY=-1;
            for(int y=0;y<imagen.getHeight();y++)for(int x=0;x<imagen.getWidth();x++) {
                if((imagen.getPixel(x,y)&255)<32)continue;
                minX=Math.min(minX,x);minY=Math.min(minY,y);maxX=Math.max(maxX,x);maxY=Math.max(maxY,y);
            }
            if(maxX<0)throw new IllegalArgumentException("Sprite de misil vacio");
            Texture textura=new Texture(imagen);textura.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);
            normal=new TextureRegion(textura,minX,minY,maxX-minX+1,maxY-minY+1);
            imagen.setBlending(Pixmap.Blending.None);
            for(int y=0;y<imagen.getHeight();y++)for(int x=0;x<imagen.getWidth();x++)
                imagen.drawPixel(x,y,0xffffff00|(imagen.getPixel(x,y)&255));
            Texture texturaCritica=new Texture(imagen);texturaCritica.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);
            critico=new TextureRegion(texturaCritica,minX,minY,maxX-minX+1,maxY-minY+1);
        } finally {imagen.dispose();}
    }
    public void aplicar(Bullet bala) {
        if(!(bala instanceof RayoLaser))bala.usarApariencia(bala.esCritico()?critico:normal);
    }
    @Override public void dispose(){normal.getTexture().dispose();critico.getTexture().dispose();}
}
