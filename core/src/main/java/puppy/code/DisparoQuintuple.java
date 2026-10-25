package puppy.code;

import java.util.function.Consumer;
import com.badlogic.gdx.graphics.Texture;

public final class DisparoQuintuple implements EstrategiaDisparo {
    private final float velocidad;
    public DisparoQuintuple(){this(0f);}
    public DisparoQuintuple(float velocidad){this.velocidad=velocidad;}
    @Override public void disparar(float x,float y,float dx,float dy,Texture textura,Consumer<Bullet> emitir) {
        for(int i=-2;i<=2;i++) {
            float avance=i==0?560f:500f, apertura=i*115f;
            if(velocidad>0f) {
                float factor=velocidad/(float)Math.sqrt(avance*avance+apertura*apertura);
                avance*=factor;apertura*=factor;
            }
            EstrategiaDisparo.emitir(x,y,dx,dy,i*10f,avance,apertura,textura,emitir);
        }
    }
    @Override public String getNombre(){return "QUINTUPLE";}
}
