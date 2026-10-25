package puppy.code;

/** Catalogo ampliable: rareza, texto, disponibilidad y efecto tienen una unica definicion. */
public enum TipoMejora {
    INICIAL_DASH(RarezaMejora.INICIAL,"Mejora dash","Escudo durante 0,75 segundos tras el dash"),
    INICIAL_DRON(RarezaMejora.INICIAL,"Mejora dron","Un mini dron alterna su disparo con el de tus drones"),
    INICIAL_CRITICO(RarezaMejora.INICIAL,"Mejora critico","Tus balas pueden ser criticas"),
    CADENCIA(RarezaMejora.COMUN,"Disparo mas rapido","+ cadencia de disparo"),
    VIDA(RarezaMejora.COMUN,"Mas vida","+ vida y curacion"),
    MOVIMIENTO(RarezaMejora.COMUN,"Motores mejorados","Mayor velocidad de movimiento"),
    DANO(RarezaMejora.COMUN,"Balas mas fuertes","+ dano"),
    PROBABILIDAD_CRITICO(RarezaMejora.COMUN,"Probabilidad de critico","Mayor probabilidad de critico"),
    DANO_CRITICO(RarezaMejora.COMUN,"Dano critico","+ dano critico"),
    DASH(RarezaMejora.COMUN,"Dash de impacto","+ dano del dash"),
    ROBAR_ENERGIA(RarezaMejora.COMUN,"Robar energia","Eliminar con el dash o su explosion recupera energia"),
    DASH_EXPLOSIVO(RarezaMejora.COMUN,"Dash explosivo","El dash provoca explosiones; repetir aumenta su alcance"),
    DISTANCIA_DASH(RarezaMejora.COMUN,"Mejor dash","Mayor distancia del dash"),
    DRON(RarezaMejora.COMUN,"Dron de apoyo","Un companero que dispara por ti"),
    DANO_EPICO(RarezaMejora.EPICA,"Municion reforzada","Mucho mas dano"),
    VIDA_EPICA(RarezaMejora.EPICA,"Mas vida","Mas vida y curacion"),
    CADENCIA_EPICA(RarezaMejora.EPICA,"Disparo mucho mas rapido","Mucha mas cadencia de disparo"),
    MAS_DASHES(RarezaMejora.EPICA,"Mas dashes","Menor costo de energia del dash"),
    REPARACION(RarezaMejora.EPICA,"Mejor reparacion","Repara la nave mas rapido"),
    TAMANO(RarezaMejora.EPICA,"Balas mas grandes","Mayor tamano de las balas"),
    PENETRACION(RarezaMejora.EPICA,"Balas perforantes","Tus balas pueden atravesar enemigos"),
    RACIMO(RarezaMejora.EPICA,"Bombas de racimo","Tus balas se dividen al impactar"),
    EXPLOSION(RarezaMejora.EPICA,"Balas explosivas","Tus balas pueden explotar al impactar"),
    APOYO_REFORZADO(RarezaMejora.EPICA,"Apoyo reforzado","+ dano y cadencia de todos tus drones"),
    DRON_EXPLOSIVO(RarezaMejora.EPICA,"Dron de apoyo explosivo","Un companero que dispara misiles explosivos"),
    DRON_LASER(RarezaMejora.LEGENDARIA,"Dron laser","Un companero que dispara laseres celestes"),
    CUERPO_A_CUERPO(RarezaMejora.LEGENDARIA,"Cuerpo a cuerpo","+ vida, robo de vida con dash, mayor recarga y dash sin espera"),
    AVANZADO(RarezaMejora.LEGENDARIA,"Avanzado","Potencia tu mejora inicial"),
    DOBLE(RarezaMejora.LEGENDARIA,"Disparo doble","Dispara dos balas a la vez"),
    TRIPLE(RarezaMejora.LEGENDARIA,"Disparo triple","Dispara tres balas en abanico"),
    DANO_DOBLE(RarezaMejora.LEGENDARIA,"Potencia devastadora","Muchisimo mas dano"),
    CADENCIA_DOBLE(RarezaMejora.LEGENDARIA,"Fuego extremo","Muchisima mas cadencia de disparo");

    public final RarezaMejora rareza;
    private final String titulo, descripcion;
    TipoMejora(RarezaMejora rareza,String titulo,String descripcion) {
        this.rareza=rareza;this.titulo=titulo;this.descripcion=descripcion;
    }
    public boolean disponible(ProgresionMejoras p) {
        if(rareza==RarezaMejora.INICIAL)return !p.tieneMejoraInicial();
        switch(this) {
            case CUERPO_A_CUERPO:return !p.tieneCuerpoACuerpo();
            case MAS_DASHES:return !p.tieneMasDashes();
            case AVANZADO:return p.tieneMejoraInicial() && !p.tieneAvanzado();
            case REPARACION:return !p.tieneReparacionMejorada();
            case DOBLE:return !p.tieneDoble();
            case TRIPLE:return !p.tieneTriple();
            case PROBABILIDAD_CRITICO:return p.getProbabilidadCritico()<1f;
            case PENETRACION:return p.getPenetracion()<1f;
            case RACIMO:return !p.tieneRacimo();
            case EXPLOSION:return p.getExplosion()<1f;
            default:return true;
        }
    }
    public String titulo(ProgresionMejoras p) {
        if(this==DRON_LASER && p.tieneDronLaser())return "Mejora el dron laser";
        if(this==DRON && p.tieneDron())return "Mejora el dron de apoyo";
        if(this==DRON_EXPLOSIVO && p.tieneDronExplosivo())return "Mejora el dron explosivo";
        return titulo;
    }
    public String descripcion(ProgresionMejoras p) {
        if(this==DRON_LASER && p.tieneDronLaser())return "+ dano y cadencia del dron laser";
        if(this==DRON && p.tieneDron())return "+ dano y cadencia del dron";
        if(this==DRON_EXPLOSIVO && p.tieneDronExplosivo())return "+ dano y cadencia del dron explosivo";
        return descripcion;
    }
}
