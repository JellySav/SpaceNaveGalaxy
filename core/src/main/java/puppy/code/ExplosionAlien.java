package puppy.code;

import com.badlogic.gdx.math.Rectangle;

/** Una sola detonacion tras el aviso; el destello posterior es solamente visual. */
public final class ExplosionAlien {
    public static final float AVISO = 0.6f;
    public static final float DESTELLO = 0.25f;
    public static final float RADIO = 120f;
    public static final int IMPACTOS_ENEMIGOS = 2;
    public static final int IMPACTOS_NAVES = 6;
    public static final float DESACTIVACION_DRON = 3f;

    private final float x;
    private final float y;
    private float tiempo;
    private boolean detonada;
    private final boolean radial;
    private boolean proyectilesEmitidos;

    public ExplosionAlien(float x, float y) { this(x,y,false); }
    public ExplosionAlien(float x,float y,boolean radial) {
        this.radial=radial;
        this.x = x;
        this.y = y;
    }

    public boolean actualizar(float delta) {
        tiempo += Math.max(0f, delta);
        if (!detonada && tiempo >= AVISO) {
            detonada = true;
            return true;
        }
        return false;
    }

    public void emitirProyectiles(com.badlogic.gdx.graphics.Texture textura,
                                  java.util.function.Consumer<ProyectilHostil> crear) {
        if(!radial || !detonada || proyectilesEmitidos || textura==null)return;
        proyectilesEmitidos=true;
        for(int i=0;i<10;i++) {
            double angulo=Math.PI*2*i/10;
            crear.accept(new ProyectilHostil(x-8f,y-12f,(float)Math.cos(angulo),(float)Math.sin(angulo),220f,textura));
        }
    }

    public boolean alcanza(Rectangle area) {
        float cercanoX = Math.max(area.x, Math.min(x, area.x + area.width));
        float cercanoY = Math.max(area.y, Math.min(y, area.y + area.height));
        float dx = x - cercanoX;
        float dy = y - cercanoY;
        return dx * dx + dy * dy <= RADIO * RADIO;
    }

    public boolean termino() { return tiempo >= AVISO + DESTELLO; }
    public boolean detono() { return detonada; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getProgresoAviso() { return Math.min(1f, tiempo / AVISO); }
    public float getBrillo() { return Math.max(0f, 1f - (tiempo - AVISO) / DESTELLO); }
}
