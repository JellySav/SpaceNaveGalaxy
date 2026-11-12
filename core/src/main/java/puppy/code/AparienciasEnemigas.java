package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/** Comparte las texturas de una ronda y conserva los PNG originales sin modificarlos. */
public final class AparienciasEnemigas implements Disposable {
    private final TextureRegion artillero,divisible,fragmento,perseguidor,explosivo,primerJefe,detonadora;
    public AparienciasEnemigas() {
        detonadora=cargar("enemigo-abeja-detonadora.png",32);
        primerJefe=cargar("jefe-escarabajo-canon.png",32);
        explosivo=cargar("enemigo-rojo-insecto-robotico.png",32);
        perseguidor=cargar("enemigo-verde-insecto-robotico.png",32);
        artillero=cargar("artillero-platillo.png");
        divisible=cargarRegion("enemigo-celula-mecanica.png",308,51,786,611);
        fragmento=cargarRegion("enemigo-celula-mecanica.png",260,728,388,334);
    }
    /** Regiones independientes de la misma hoja: madre arriba y una hija abajo. */
    private TextureRegion cargarRegion(String nombre,int x,int y,int ancho,int alto) {
        Texture textura=new Texture(Gdx.files.internal(nombre));
        textura.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);
        return new TextureRegion(textura,x,y,ancho,alto);
    }
    private TextureRegion cargar(String nombre) { return cargar(nombre,1); }
    private TextureRegion cargar(String nombre,int alphaMinimo) {
        Pixmap imagen=new Pixmap(Gdx.files.internal(nombre));
        try {
            int minX=imagen.getWidth(),minY=imagen.getHeight(),maxX=-1,maxY=-1;
            for(int y=0;y<imagen.getHeight();y++)for(int x=0;x<imagen.getWidth();x++) {
                if((imagen.getPixel(x,y)&255)<alphaMinimo)continue;
                minX=Math.min(minX,x);minY=Math.min(minY,y);maxX=Math.max(maxX,x);maxY=Math.max(maxY,y);
            }
            if(maxX<0)throw new IllegalArgumentException("Sprite vacio: "+nombre);
            Texture textura=new Texture(imagen);
            textura.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);
            return new TextureRegion(textura,minX,minY,maxX-minX+1,maxY-minY+1);
        } finally {imagen.dispose();}
    }
    public void aplicar(Enemigo enemigo) {
        if(enemigo instanceof JefeSector && ((JefeSector)enemigo).getSector()==SectorGalactico.CINTURON_DE_VELA) {
            enemigo.usarApariencia(primerJefe,220f);
            enemigo.usarPivoteApariencia(0.5f,0.32f);
        }
        else if(enemigo.getClass()==AlienEnemigo.class)enemigo.usarApariencia(perseguidor,48f);
        else if(enemigo.getClass()==AlienExplosivo.class)enemigo.usarApariencia(explosivo,54f);
        else if(enemigo instanceof AlienDetonadora)((AlienDetonadora)enemigo).usarAparienciaAbeja(detonadora);
        else if(enemigo instanceof AlienArtillero)enemigo.usarApariencia(artillero,56f);
        else if(enemigo instanceof AlienDivisible)((AlienDivisible)enemigo).usarApariencias(divisible,fragmento);
    }
    @Override public void dispose() {
        detonadora.getTexture().dispose();primerJefe.getTexture().dispose();explosivo.getTexture().dispose();perseguidor.getTexture().dispose();artillero.getTexture().dispose();divisible.getTexture().dispose();fragmento.getTexture().dispose();
    }
}
