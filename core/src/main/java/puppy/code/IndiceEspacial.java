package puppy.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;

final class IndiceEspacial {
    private final float tamanoCelda;
    private final Map<Long, List<ObjetivoDisparo>> celdas = new HashMap<>();

    IndiceEspacial(float tamanoCelda) {
        if (!Float.isFinite(tamanoCelda) || tamanoCelda <= 0f) {
            throw new IllegalArgumentException("El tamano de celda debe ser positivo y finito");
        }
        this.tamanoCelda = tamanoCelda;
    }

    void reconstruir(Iterable<? extends ObjetivoDisparo> objetivos) {
        celdas.clear();
        for (ObjetivoDisparo objetivo : objetivos) {
            if (objetivo == null || !objetivo.estaActivo()) {
                continue;
            }
            Rectangle area = objetivo.getArea();
            int celdaMinX = celda(area.x);
            int celdaMaxX = celda(area.x + area.width);
            int celdaMinY = celda(area.y);
            int celdaMaxY = celda(area.y + area.height);
            for (int x = celdaMinX; x <= celdaMaxX; x++) {
                for (int y = celdaMinY; y <= celdaMaxY; y++) {
                    celdas.computeIfAbsent(clave(x, y), ignorado -> new ArrayList<>()).add(objetivo);
                }
            }
        }
    }

    List<ObjetivoDisparo> consultar(Rectangle area) {
        Set<ObjetivoDisparo> vistos = Collections.newSetFromMap(new IdentityHashMap<>());
        List<ObjetivoDisparo> colisiones = new ArrayList<>();
        int celdaMinX = celda(area.x);
        int celdaMaxX = celda(area.x + area.width);
        int celdaMinY = celda(area.y);
        int celdaMaxY = celda(area.y + area.height);
        for (int x = celdaMinX; x <= celdaMaxX; x++) {
            for (int y = celdaMinY; y <= celdaMaxY; y++) {
                List<ObjetivoDisparo> candidatos = celdas.get(clave(x, y));
                if (candidatos == null) {
                    continue;
                }
                for (ObjetivoDisparo candidato : candidatos) {
                    if (vistos.add(candidato) && candidato.estaActivo()
                            && candidato.getArea().overlaps(area)) {
                        colisiones.add(candidato);
                    }
                }
            }
        }
        return colisiones;
    }

    private int celda(float coordenada) {
        return (int) Math.floor(coordenada / tamanoCelda);
    }

    private long clave(int x, int y) {
        return ((long) x << 32) ^ (y & 0xffffffffL);
    }
}