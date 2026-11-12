package puppy.code;

import java.util.*;

public final class CatalogoMejoras {
    private CatalogoMejoras() { }
    public static List<TipoMejora> iniciales() {
        return Collections.unmodifiableList(Arrays.asList(TipoMejora.INICIAL_DASH,
                TipoMejora.INICIAL_DRON,TipoMejora.INICIAL_CRITICO));
    }
    public static List<TipoMejora> sortear(ProgresionMejoras progreso, Random random) {
        List<TipoMejora> opciones=new ArrayList<>();
        for(int i=0;i<3;i++) {
            RarezaMejora rareza=RarezaMejora.sortear(random);
            List<TipoMejora> pool=new ArrayList<>();
            for(TipoMejora tipo:TipoMejora.values())
                if(tipo.rareza==rareza && tipo.disponible(progreso))pool.add(tipo);
            List<TipoMejora> distintas=new ArrayList<>(pool);
            distintas.removeAll(opciones);
            // Solo se repite una opcion si ya no quedan otras elegibles de esa rareza.
            if(!distintas.isEmpty())pool=distintas;
            opciones.add(pool.get(random.nextInt(pool.size())));
        }
        return Collections.unmodifiableList(opciones);
    }
}
