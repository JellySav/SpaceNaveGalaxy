package puppy.code.mejoras;

import java.util.function.Consumer;
import java.util.function.Predicate;

/** Catalogo ampliable: rareza, texto, disponibilidad y efecto tienen una unica definicion. */
public enum TipoMejora {
    INICIAL_DASH(RarezaMejora.INICIAL,"Mejora dash","Escudo durante 0,75 segundos tras el dash",
            p -> !p.tieneMejoraInicial(), ProgresionMejoras::mejorarDashInicial),
    INICIAL_DRON(RarezaMejora.INICIAL,"Mejora dron","Un mini dron alterna su disparo con el de tus drones",
            p -> !p.tieneMejoraInicial(), ProgresionMejoras::mejorarDronInicial),
    INICIAL_CRITICO(RarezaMejora.INICIAL,"Mejora critico","Tus balas pueden ser criticas",
            p -> !p.tieneMejoraInicial(), ProgresionMejoras::mejorarCriticoInicial),
    CADENCIA(RarezaMejora.COMUN,"Disparo mas rapido","+ cadencia de disparo",
            p -> true, p -> p.aumentarCadencia(0.15f)),
    VIDA(RarezaMejora.COMUN,"Mas vida","+ vida y curacion",
            p -> true, p -> p.aumentarVidas(1)),
    MOVIMIENTO(RarezaMejora.COMUN,"Motores mejorados","Mayor velocidad de movimiento",
            p -> true, p -> p.aumentarVelocidad(0.05f)),
    DANO(RarezaMejora.COMUN,"Balas mas fuertes","+ dano",
            p -> true, p -> p.aumentarDano(0.15f)),
    PROBABILIDAD_CRITICO(RarezaMejora.COMUN,"Probabilidad de critico","Mayor probabilidad de critico",
            p -> p.getProbabilidadCritico() < 1f, ProgresionMejoras::aumentarProbabilidadCritico),
    DANO_CRITICO(RarezaMejora.COMUN,"Dano critico","+ dano critico",
            p -> true, ProgresionMejoras::aumentarDanoCritico),
    DASH(RarezaMejora.COMUN,"Dash de impacto","+ dano del dash",
            p -> true, p -> p.aumentarDanoDash(0.5f)),
    ROBAR_ENERGIA(RarezaMejora.COMUN,"Robar energia","Eliminar con el dash o su explosion recupera energia",
            p -> true, ProgresionMejoras::aumentarRoboEnergia),
    DASH_EXPLOSIVO(RarezaMejora.COMUN,"Dash explosivo","El dash provoca explosiones; repetir aumenta su alcance",
            p -> true, ProgresionMejoras::aumentarDashExplosivo),
    DISTANCIA_DASH(RarezaMejora.COMUN,"Mejor dash","Mayor distancia del dash",
            p -> true, ProgresionMejoras::aumentarDistanciaDash),
    DRON(RarezaMejora.COMUN,"Dron de apoyo","Un companero que dispara por ti",
            p -> true, ProgresionMejoras::mejorarDron),
    DANO_EPICO(RarezaMejora.EPICA,"Municion reforzada","Mucho mas dano",
            p -> true, p -> p.aumentarDano(0.3f)),
    VIDA_EPICA(RarezaMejora.EPICA,"Mas vida","Mas vida y curacion",
            p -> true, p -> p.aumentarVidas(2)),
    CADENCIA_EPICA(RarezaMejora.EPICA,"Disparo mucho mas rapido","Mucha mas cadencia de disparo",
            p -> true, p -> p.aumentarCadencia(0.3f)),
    MAS_DASHES(RarezaMejora.EPICA,"Mas dashes","Menor costo de energia del dash",
            p -> !p.tieneMasDashes(), ProgresionMejoras::activarMasDashes),
    REPARACION(RarezaMejora.EPICA,"Mejor reparacion","Repara la nave mas rapido",
            p -> !p.tieneReparacionMejorada(), ProgresionMejoras::activarReparacionMejorada),
    TAMANO(RarezaMejora.EPICA,"Balas mas grandes","Mayor tamano de las balas",
            p -> true, ProgresionMejoras::aumentarTamano),
    PENETRACION(RarezaMejora.EPICA,"Balas perforantes","Tus balas pueden atravesar enemigos",
            p -> p.getPenetracion() < 1f, ProgresionMejoras::aumentarPenetracion),
    RACIMO(RarezaMejora.EPICA,"Bombas de racimo","Tus balas se dividen al impactar",
            p -> !p.tieneRacimo(), ProgresionMejoras::activarRacimo),
    EXPLOSION(RarezaMejora.EPICA,"Balas explosivas","Tus balas pueden explotar al impactar",
            p -> p.getExplosion() < 1f, ProgresionMejoras::aumentarExplosion),
    APOYO_REFORZADO(RarezaMejora.EPICA,"Apoyo reforzado","+ dano y cadencia de todos tus drones",
            p -> true, ProgresionMejoras::aumentarApoyo),
    DRON_EXPLOSIVO(RarezaMejora.EPICA,"Dron de apoyo explosivo","Un companero que dispara misiles explosivos",
            p -> true, ProgresionMejoras::mejorarDronExplosivo),
    DRON_LASER(RarezaMejora.LEGENDARIA,"Dron laser","Un companero que dispara laseres celestes",
            p -> true, ProgresionMejoras::mejorarDronLaser),
    CUERPO_A_CUERPO(RarezaMejora.LEGENDARIA,"Cuerpo a cuerpo","+ vida, robo de vida con dash, mayor recarga y dash sin espera",
            p -> !p.tieneCuerpoACuerpo(), ProgresionMejoras::activarCuerpoACuerpo),
    AVANZADO(RarezaMejora.LEGENDARIA,"Avanzado","Potencia tu mejora inicial",
            p -> p.tieneMejoraInicial() && !p.tieneAvanzado(), ProgresionMejoras::activarAvanzado),
    DOBLE(RarezaMejora.LEGENDARIA,"Disparo doble","Dispara dos balas a la vez",
            p -> !p.tieneDoble(), ProgresionMejoras::activarDoble),
    TRIPLE(RarezaMejora.LEGENDARIA,"Disparo triple","Dispara tres balas en abanico",
            p -> !p.tieneTriple(), ProgresionMejoras::activarTriple),
    DANO_DOBLE(RarezaMejora.LEGENDARIA,"Potencia devastadora","Muchisimo mas dano",
            p -> true, ProgresionMejoras::duplicarDano),
    CADENCIA_DOBLE(RarezaMejora.LEGENDARIA,"Fuego extremo","Muchisima mas cadencia de disparo",
            p -> true, ProgresionMejoras::duplicarCadencia);

    public final RarezaMejora rareza;
    private final String titulo, descripcion;
    private final Predicate<ProgresionMejoras> disponibilidad;
    private final Consumer<ProgresionMejoras> efecto;

    TipoMejora(RarezaMejora rareza, String titulo, String descripcion,
               Predicate<ProgresionMejoras> disponibilidad, Consumer<ProgresionMejoras> efecto) {
        this.rareza=rareza;this.titulo=titulo;this.descripcion=descripcion;
        this.disponibilidad=disponibilidad;this.efecto=efecto;
    }

    public boolean disponible(ProgresionMejoras p) {
        return disponibilidad.test(p);
    }

    public void aplicar(ProgresionMejoras p) {
        efecto.accept(p);
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
