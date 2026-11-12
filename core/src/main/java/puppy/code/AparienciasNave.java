package puppy.code;

import java.util.EnumMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/** Cada mejora inicial puede tener su propio diseno; las que faltan usan el original. */
public final class AparienciasNave implements Disposable {
    private final EnumMap<TipoMejora,TextureRegion> regiones=new EnumMap<>(TipoMejora.class);
    public AparienciasNave() {
        regiones.put(TipoMejora.INICIAL_DASH,cargar("nave-inicial-dash.png"));
        regiones.put(TipoMejora.INICIAL_CRITICO,cargar("nave-inicial-critico.png"));
        regiones.put(TipoMejora.INICIAL_DRON,cargar("nave-inicial-dron.png"));
        regiones.put(TipoMejora.DRON_EXPLOSIVO,cargar("dron-explosivo-calavera.png"));
        regiones.put(TipoMejora.DRON,cargar("dron-normal-compacto.png"));
        regiones.put(TipoMejora.DRON_LASER,cargar("dron-laser-interceptor.png"));
    }
    private TextureRegion cargar(String nombre) {
        Pixmap imagen=new Pixmap(Gdx.files.internal(nombre));
        try {
            int minX=imagen.getWidth(),minY=imagen.getHeight(),maxX=-1,maxY=-1;
            for(int y=0;y<imagen.getHeight();y++)for(int x=0;x<imagen.getWidth();x++) {
                // El halo casi transparente no debe reducir el tamano visible de la nave.
                if((imagen.getPixel(x,y)&255)<32)continue;
                minX=Math.min(minX,x);minY=Math.min(minY,y);maxX=Math.max(maxX,x);maxY=Math.max(maxY,y);
            }
            if(maxX<0)throw new IllegalArgumentException("Sprite vacio: "+nombre);
            Texture textura=new Texture(imagen);
            textura.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);
            return new TextureRegion(textura,minX,minY,maxX-minX+1,maxY-minY+1);
        } finally {imagen.dispose();}
    }
    public void aplicar(Nave4 nave,ProgresionMejoras progreso) {
        TipoMejora inicial=progreso.getMejoraInicial();
        nave.usarApariencia(inicial==null?null:regiones.get(inicial));
    }
    public void aplicarDron(DronEscolta dron,TipoMejora tipo) {
        dron.usarApariencia(regiones.get(tipo));
    }
    @Override public void dispose() {
        for(TextureRegion region:regiones.values())region.getTexture().dispose();
    }
}
