package puppy.code;

import java.util.List;
import java.util.Random;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/** Trafico espaciado desde fuera de la pantalla hasta el borde opuesto. */
public final class LluviaMeteoritos {
    private final Random random;
    private final int ronda;
    private final float intervalo;
    private final int maximoPresentes;
    private float espera = 1.5f;

    public LluviaMeteoritos(int ronda, boolean hayJefe, Random random) {
        this.ronda = ronda;
        this.random = random;
        intervalo = hayJefe ? 5f : 3.5f;
        maximoPresentes = hayJefe ? 3 : 4;
    }

    public void actualizar(float delta, List<Enemigo> enemigos, Texture textura) {
        actualizar(delta,enemigos,textura,null);
    }
    public void actualizar(float delta,List<Enemigo> enemigos,Texture textura,Rectangle jugador) {
        if (delta <= 0f) return;
        espera -= delta;
        if (espera > 0f) return;
        espera = intervalo;
        int presentes = 0;
        for (Enemigo enemigo : enemigos) {
            if (enemigo instanceof AsteroideEnemigo && enemigo.estaActivo()) presentes++;
        }
        if (presentes < maximoPresentes) {
            AsteroideEnemigo meteorito=crearMeteorito(textura,jugador);
            if(meteorito!=null)enemigos.add(meteorito);
        }
    }

    /** Izquierda, derecha, arriba y abajo; excluye todos los bordes empatados como mas cercanos. */
    private int elegirBorde(Rectangle jugador) {
        if(jugador==null)return random.nextInt(4);
        float[] distancias={Math.max(0f,jugador.x),Math.max(0f,MundoJuego.ANCHO-jugador.x-jugador.width),
                Math.max(0f,MundoJuego.ALTO-jugador.y-jugador.height),Math.max(0f,jugador.y)};
        float minimo=Math.min(Math.min(distancias[0],distancias[1]),Math.min(distancias[2],distancias[3]));
        int[] permitidos=new int[4];int cantidad=0;
        for(int i=0;i<4;i++)if(distancias[i]>minimo+0.001f)permitidos[cantidad++]=i;
        return cantidad==0?-1:permitidos[random.nextInt(cantidad)];
    }

    private AsteroideEnemigo crearMeteorito(Texture textura,Rectangle jugador) {
        float tamano = 40f + random.nextFloat() * 18f;
        float x;
        float y;
        float destinoX;
        float destinoY;
        int borde=elegirBorde(jugador);
        if(borde<0)return null;
        if (borde < 2) {
            x = borde == 0 ? -tamano - 8f : MundoJuego.ANCHO + 8f;
            destinoX = borde == 0 ? MundoJuego.ANCHO + 8f : -tamano - 8f;
            y = 80f + random.nextFloat() * (MundoJuego.ALTO - tamano - 160f);
            destinoY = 80f + random.nextFloat() * (MundoJuego.ALTO - tamano - 160f);
        } else {
            y = borde == 2 ? MundoJuego.ALTO + 8f : -tamano - 8f;
            destinoY = borde == 2 ? -tamano - 8f : MundoJuego.ALTO + 8f;
            x = 80f + random.nextFloat() * (MundoJuego.ANCHO - tamano - 160f);
            destinoX = 80f + random.nextFloat() * (MundoJuego.ANCHO - tamano - 160f);
        }
        float dx = destinoX - x;
        float dy = destinoY - y;
        float longitud = (float) Math.sqrt(dx * dx + dy * dy);
        float velocidad = 110f + 4f * Math.min(12, Math.max(0, ronda - 1))
                + random.nextFloat() * 20f;
        AsteroideEnemigo meteorito=(AsteroideEnemigo) new EnemigoBuilder()
                .tipo(EnemigoBuilder.Tipo.ASTEROIDE)
                .posicion(x, y).tamano(tamano)
                .velocidad(dx / longitud * velocidad, dy / longitud * velocidad)
                .construir(textura);
        meteorito.configurarVidaParaRonda(ronda);
        return meteorito;
    }
}
