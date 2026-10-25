package puppy.code;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import com.badlogic.gdx.graphics.Texture;

/** Distribuye la oleada obligatoria en grupos que entran desde arriba o los laterales. */
public final class LlegadaNaves {
    private final int ronda;
    private final int cantidad;
    private final int tamanoGrupo;
    private final boolean[] ordenRojas;
    private final boolean[] ordenMoradas;
    private final boolean[] ordenNaranjas;
    private final boolean[] ordenDetonadoras;
    private final Random random;
    private int emitidas;
    private float espera = 1f;

    public LlegadaNaves(int ronda, Random random) {
        this.ronda = ronda;
        tamanoGrupo = ronda >= 6 ? 3 : 2;
        this.random = random;
        cantidad = OleadaNaves.cantidadInicial(ronda);
        ordenRojas = new boolean[cantidad];
        int rojas = Math.min(cantidad, AlienExplosivo.cantidadEnRonda(ronda));
        if (rojas > 0) {
            // El segundo grupo incluye una roja, cuando ya hay otras naves en combate.
            ordenRojas[tamanoGrupo + random.nextInt(tamanoGrupo)] = true;
            // Las restantes se reparten al azar antes del ultimo grupo.
            for (int i = 1; i < rojas; i++) {
                int indice;
                do {
                    indice = tamanoGrupo * 2 + random.nextInt(cantidad - tamanoGrupo * 3);
                } while (ordenRojas[indice]);
                ordenRojas[indice] = true;
            }
        }
        ordenMoradas = new boolean[cantidad];
        int moradas = cantidad == 0 ? 0 : AlienDivisible.cantidadEnRonda(ronda);
        for (int i = 0; i < moradas; i++) {
            int indice;
            do {
                indice = 4 + random.nextInt(cantidad - 4);
            } while (ordenRojas[indice] || ordenMoradas[indice]);
            ordenMoradas[indice] = true;
        }

        ordenNaranjas = new boolean[cantidad];
        int naranjas = cantidad == 0 ? 0 : AlienArtillero.cantidadEnRonda(ronda);
        for (int i = 0; i < naranjas; i++) {
            int indice;
            do {
                indice = random.nextInt(cantidad);
            } while (ordenRojas[indice] || ordenMoradas[indice] || ordenNaranjas[indice]);
            ordenNaranjas[indice] = true;
        }

        ordenDetonadoras = new boolean[cantidad];
        int detonadoras = AlienDetonadora.cantidadEnRonda(ronda);
        for(int i=0;i<detonadoras;i++) {
            int indice;
            do { indice=random.nextInt(cantidad); }
            while(ordenRojas[indice] || ordenMoradas[indice] || ordenNaranjas[indice] || ordenDetonadoras[indice]);
            ordenDetonadoras[indice]=true;
        }
    }

    public void actualizar(float delta, List<Enemigo> enemigos, Texture textura) {
        if (delta <= 0f || !hayPendientes()) return;
        espera -= delta;
        if (espera > 0f) return;
        // Como maximo un grupo por cuadro, incluso tras una pausa del sistema.
        espera = ronda >= 9 ? 2.5f : 3f;
        float velocidad = Math.min(240f, (85f + ronda * 8f)
                * SectorGalactico.desdeRonda(ronda).getFactorDificultad());
        float margen = 60f;
        // Cada grupo comparte un borde; las rondas anteriores a la 6 solo usan arriba.
        int borde = ronda >= 6 ? random.nextInt(3) : 0;
        float maximo = (borde == 0 ? MundoJuego.ANCHO : MundoJuego.ALTO) - margen - 48f;
        List<Float> posiciones = new ArrayList<>();
        for (int i = 0; i < tamanoGrupo && hayPendientes(); i++) {
            boolean roja = ordenRojas[emitidas];
            float posicion = posicionLibre(margen, maximo, posiciones);
            posiciones.add(posicion);
            float x = borde == 0 ? posicion : borde == 1 ? -60f : MundoJuego.ANCHO + 12f;
            float y = borde == 0 ? MundoJuego.ALTO + 12f : posicion;
            if(ordenDetonadoras[emitidas]) {
                enemigos.add(new EnemigoBuilder().tipo(EnemigoBuilder.Tipo.ALIEN_DETONADORA)
                        .posicion(x,y).velocidad(velocidad,velocidad).resistencia(AlienDetonadora.RESISTENCIA).construir(textura));
            } else if (ordenNaranjas[emitidas]) {
                enemigos.add(new AlienArtillero(x, y,
                        textura, new Random(random.nextLong())));
            } else if (ordenMoradas[emitidas]) {
                enemigos.add(new AlienDivisible(x, y, velocidad, textura));
            } else {
            enemigos.add(new EnemigoBuilder()
                    .tipo(roja ? EnemigoBuilder.Tipo.ALIEN_EXPLOSIVO : EnemigoBuilder.Tipo.ALIEN)
                    .posicion(x, y)
                    .velocidad(velocidad, velocidad)
                    .resistencia(roja ? AlienExplosivo.RESISTENCIA : AlienEnemigo.resistenciaEnRonda(ronda))
                    .construir(textura));
            }
            enemigos.get(enemigos.size()-1).configurarVidaParaRonda(ronda);
            emitidas++;
        }
    }

    /** Muestrea el espacio libre para que todo el grupo entre sin solapamientos. */
    private float posicionLibre(float minimo, float maximo, List<Float> anteriores) {
        List<float[]> espacios = new ArrayList<>();
        espacios.add(new float[]{minimo, maximo});
        for (float x : anteriores) {
            List<float[]> libres = new ArrayList<>();
            for (float[] espacio : espacios) {
                if (x - 72f > espacio[0]) libres.add(new float[]{espacio[0], Math.min(espacio[1], x - 72f)});
                if (x + 72f < espacio[1]) libres.add(new float[]{Math.max(espacio[0], x + 72f), espacio[1]});
            }
            espacios = libres;
        }
        float total = 0f;
        for (float[] espacio : espacios) total += espacio[1] - espacio[0];
        float distancia = random.nextFloat() * total;
        for (float[] espacio : espacios) {
            float largo = espacio[1] - espacio[0];
            if (distancia <= largo) return espacio[0] + distancia;
            distancia -= largo;
        }
        return espacios.get(espacios.size() - 1)[1];
    }

    public boolean hayPendientes() {
        return emitidas < cantidad;
    }
}
