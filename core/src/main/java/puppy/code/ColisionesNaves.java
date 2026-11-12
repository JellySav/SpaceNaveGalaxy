package puppy.code;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.Rectangle;

/** Contactos solidos entre aliens: se separan sin recibir dano ni cambiar su persecucion. */
public final class ColisionesNaves {
    private static final float MARGEN = 0.02f;

    public void resolver(List<Enemigo> enemigos, float anchoMundo, float altoMundo) {
        List<AlienEnemigo> naves = new ArrayList<>();
        for (Enemigo enemigo : enemigos) {
            if (enemigo instanceof AlienEnemigo && enemigo.estaActivo()
                    && !((AlienEnemigo) enemigo).estaEntrando()) {
                naves.add((AlienEnemigo) enemigo);
            }
        }
        // Repetir permite liberar tambien grupos apretados contra un borde.
        int pasadas = Math.min(128, Math.max(12, naves.size() * naves.size() * 2));
        for (int pasada = 0; pasada < pasadas; pasada++) {
            boolean contacto = false;
            for (int i = 0; i < naves.size(); i++) {
                for (int j = i + 1; j < naves.size(); j++) {
                    AlienEnemigo a = naves.get(i);
                    AlienEnemigo b = naves.get(j);
                    Rectangle areaA = a.getArea();
                    Rectangle areaB = b.getArea();
                    if (!areaA.overlaps(areaB)) continue;
                    contacto = true;
                    float penetracionX = Math.min(areaA.x + areaA.width, areaB.x + areaB.width)
                            - Math.max(areaA.x, areaB.x);
                    float penetracionY = Math.min(areaA.y + areaA.height, areaB.y + areaB.height)
                            - Math.max(areaA.y, areaB.y);
                    if (penetracionX <= penetracionY) {
                        separar(a, b, true, penetracionX, anchoMundo, altoMundo);
                    } else {
                        separar(a, b, false, penetracionY, anchoMundo, altoMundo);
                    }
                }
            }
            if (!contacto) return;
        }
    }

    private void separar(AlienEnemigo a, AlienEnemigo b, boolean horizontal,
                         float penetracion, float anchoMundo, float altoMundo) {
        Rectangle areaA = a.getArea();
        Rectangle areaB = b.getArea();
        float posicionA = horizontal ? areaA.x : areaA.y;
        float posicionB = horizontal ? areaB.x : areaB.y;
        float tamanoA = horizontal ? areaA.width : areaA.height;
        float tamanoB = horizontal ? areaB.width : areaB.height;
        float limite = horizontal ? anchoMundo : altoMundo;
        float signo = posicionA + tamanoA / 2f <= posicionB + tamanoB / 2f ? -1f : 1f;
        float espacioA = Math.max(0f, signo < 0f ? posicionA : limite - posicionA - tamanoA);
        float espacioB = Math.max(0f, signo < 0f ? limite - posicionB - tamanoB : posicionB);
        float distancia = penetracion + MARGEN;
        float moverA = Math.min(distancia / 2f, espacioA);
        float moverB = Math.min(distancia - moverA, espacioB);
        moverA = Math.min(distancia - moverB, espacioA);
        a.desplazar(horizontal ? signo * moverA : 0f, horizontal ? 0f : signo * moverA,
                anchoMundo, altoMundo);
        b.desplazar(horizontal ? -signo * moverB : 0f, horizontal ? 0f : -signo * moverB,
                anchoMundo, altoMundo);
    }
}
