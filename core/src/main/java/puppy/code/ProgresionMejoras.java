package puppy.code;

/** Estado de una partida. Los porcentajes suman sobre la base; los x2 actuan al elegirlos. */
public final class ProgresionMejoras {
    private static final ProgresionMejoras INSTANCIA=new ProgresionMejoras();
    public static final float DANO_BASE=10f, CADENCIA_BASE=2.5f, VELOCIDAD_BASE=260f, DASH_BASE=20f;
    public static final float DANO_DRON_BASE=10f, CADENCIA_DRON_BASE=1f/1.1f;
    private int nivel=1, experiencia, pendientes, baterias;
    private int vidasMaximas=3;
    private float dano=DANO_BASE, cadencia=CADENCIA_BASE, velocidad=VELOCIDAD_BASE, danoDash=DASH_BASE;
    private float tamano=1f, penetracion, explosion;
    private int mejorasDistanciaDash;
    private int mejorasApoyo;
    private int mejorasDashExplosivo;
    private int mejorasRobarEnergia;
    private int mejorasProbabilidadCritico,mejorasDanoCritico;
    private int siguienteDisparoMini;
    private boolean reparacionMejorada;
    private boolean cuerpoACuerpo;
    private TipoMejora mejoraInicial;
    private boolean masDashes,avanzado,racimo;
    private boolean dronLaser;
    private float danoLaser=DANO_DRON_BASE*3f,cadenciaLaser=CADENCIA_DRON_BASE;
    private float danoDron=DANO_DRON_BASE,cadenciaDron=CADENCIA_DRON_BASE;
    private float danoDronExplosivo=DANO_DRON_BASE,cadenciaDronExplosivo=CADENCIA_DRON_BASE;
    private boolean doble,triple,dron,dronExplosivo,invulnerablePruebas;
    private ProgresionMejoras() { }
    public static ProgresionMejoras obtenerInstancia(){return INSTANCIA;}
    public void sumarPuntos(int puntos) {
        if(puntos<0)throw new IllegalArgumentException("Puntaje negativo");
        experiencia+=puntos;
        while(experiencia>=getRequisitoNivel()) {
            experiencia-=getRequisitoNivel();nivel++;pendientes++;
        }
    }
    void subirNivelPruebas() { nivel++; pendientes++; }

    public TipoMejora getMejoraInicial(){return mejoraInicial;}
    public boolean tieneMejoraInicial(){return mejoraInicial!=null;}
    public boolean tieneEscudoDash(){return mejoraInicial==TipoMejora.INICIAL_DASH;}
    public boolean tieneMiniDron(){return mejoraInicial==TipoMejora.INICIAL_DRON;}
    public float getProbabilidadCritico(){return Math.min(1f,(mejoraInicial==TipoMejora.INICIAL_CRITICO?(avanzado?0.16f:0.10f):0f)+mejorasProbabilidadCritico*0.03f);}
    public float getMultiplicadorCritico(){return (avanzado && mejoraInicial==TipoMejora.INICIAL_CRITICO?3f:2f)+mejorasDanoCritico*0.5f;}
    public boolean tieneRacimo(){return racimo;}
    public boolean tieneMasDashes(){return masDashes;}
    public boolean tieneAvanzado(){return avanzado;}
    public boolean tieneMiniDronQuintuple(){return avanzado && tieneMiniDron();}
    public float getCosteDash(){return EnergiaDash.COSTE-(masDashes?10f:0f)-(avanzado && tieneEscudoDash()?20f:0f);}
    public boolean elegirMejora(TipoMejora tipo) {
        if(tipo!=null && tipo.rareza==RarezaMejora.INICIAL) {
            if(tieneMejoraInicial())return false;
            mejoraInicial=tipo;
            if(tipo==TipoMejora.INICIAL_DASH)velocidad+=VELOCIDAD_BASE*0.15f;
            if(tipo==TipoMejora.INICIAL_CRITICO)cadencia+=CADENCIA_BASE*0.10f;
            return true;
        }
        if(pendientes==0 || tipo==null || !tipo.disponible(this))return false;
        switch(tipo) {
            case CADENCIA:cadencia+=getCadenciaBaseNave()*0.15f;break;
            case VIDA:vidasMaximas++;break;
            case MOVIMIENTO:velocidad+=getVelocidadBaseNave()*0.05f;break;
            case DANO:dano+=DANO_BASE*0.15f;break;
            case PROBABILIDAD_CRITICO:mejorasProbabilidadCritico++;break;
            case DANO_CRITICO:mejorasDanoCritico++;break;
            case DASH:danoDash+=DASH_BASE*0.5f;break;
            case ROBAR_ENERGIA:mejorasRobarEnergia++;break;
            case DASH_EXPLOSIVO:mejorasDashExplosivo++;break;
            case DISTANCIA_DASH:mejorasDistanciaDash++;break;
            case MAS_DASHES:masDashes=true;break;
            case AVANZADO:avanzado=true;break;
            case REPARACION:reparacionMejorada=true;break;
            case DRON:
                if(dron){danoDron+=DANO_DRON_BASE*0.2f;cadenciaDron+=CADENCIA_DRON_BASE*0.2f;}
                else dron=true;break;
            case DANO_EPICO:dano+=DANO_BASE*0.3f;break;
            case VIDA_EPICA:vidasMaximas+=2;break;
            case CADENCIA_EPICA:cadencia+=getCadenciaBaseNave()*0.3f;break;
            case TAMANO:tamano+=0.8f;break;
            case PENETRACION:penetracion=Math.min(1f,penetracion+0.4f);break;
            case RACIMO:racimo=true;break;
            case EXPLOSION:explosion=Math.min(1f,explosion+0.1f);break;
            case APOYO_REFORZADO:mejorasApoyo++;break;
            case DRON_EXPLOSIVO:
                if(dronExplosivo){danoDronExplosivo+=DANO_DRON_BASE*0.2f;cadenciaDronExplosivo+=CADENCIA_DRON_BASE*0.2f;}
                else dronExplosivo=true;break;
            case DRON_LASER:
                if(dronLaser){danoLaser+=DANO_DRON_BASE*3f*0.2f;cadenciaLaser+=CADENCIA_DRON_BASE*0.2f;}
                else dronLaser=true;break;
            case CUERPO_A_CUERPO:cuerpoACuerpo=true;vidasMaximas+=3;break;
            case DOBLE:doble=true;break;
            case TRIPLE:triple=true;break;
            case DANO_DOBLE:dano*=2f;break;
            case CADENCIA_DOBLE:cadencia*=2f;break;
        }
        pendientes--;return true;
    }
    public void reiniciar() {
        nivel=1;experiencia=pendientes=baterias=0;vidasMaximas=3;
        cuerpoACuerpo=false;mejoraInicial=null;masDashes=avanzado=racimo=false;
        dronLaser=false;danoLaser=DANO_DRON_BASE*3f;cadenciaLaser=CADENCIA_DRON_BASE;
        dano=DANO_BASE;cadencia=CADENCIA_BASE;velocidad=VELOCIDAD_BASE;danoDash=DASH_BASE;
        tamano=1f;penetracion=explosion=0f;
        siguienteDisparoMini=0;
        mejorasProbabilidadCritico=mejorasDanoCritico=0;
        mejorasRobarEnergia=0;mejorasDashExplosivo=0;mejorasApoyo=0;mejorasDistanciaDash=0;reparacionMejorada=false;
        danoDron=danoDronExplosivo=DANO_DRON_BASE;
        cadenciaDron=cadenciaDronExplosivo=CADENCIA_DRON_BASE;
        doble=triple=dron=dronExplosivo=invulnerablePruebas=false;
    }
    public int getNivel(){return nivel;}
    public int getExperiencia(){return experiencia;}
    public int getRequisitoNivel(){return 250+(nivel-1)*25;}
    public int getPendientes(){return pendientes;}
    public boolean hayMejorasPendientes(){return pendientes>0;}
    public int getNivelArma(){return doble&&triple?4:triple?3:doble?2:1;}
    public boolean tieneDoble(){return doble;}
    public boolean tieneTriple(){return triple;}
    public boolean tieneDronLaser(){return dronLaser;}
    public float getDanoLaser(){return danoLaser+DANO_DRON_BASE*3f*bonoApoyo();}
    public float getCadenciaLaser(){return cadenciaLaser+CADENCIA_DRON_BASE*bonoApoyo();}
    public boolean tieneDron(){return dron;}
    public boolean tieneDronExplosivo(){return dronExplosivo;}
    public int getVidasMaximas(){return vidasMaximas;}
    public float getDano(){return dano;}
    private float getCadenciaBaseNave(){return CADENCIA_BASE*(mejoraInicial==TipoMejora.INICIAL_CRITICO?1.10f:1f);}
    private float getVelocidadBaseNave(){return VELOCIDAD_BASE*(tieneEscudoDash()?1.15f:1f);}
    public float getCadencia(){return cadencia;}
    public float getVelocidad(){return velocidad;}
    public float getDanoDash(){return danoDash*(avanzado && tieneEscudoDash()?2f:1f);}
    public boolean tieneCuerpoACuerpo(){return cuerpoACuerpo;}
    public float getRegeneracionEnergia(){return EnergiaDash.RECARGA_POR_SEGUNDO*(cuerpoACuerpo?1.3f:1f);}
    public float getEsperaDash(){return cuerpoACuerpo?0f:EnergiaDash.ESPERA;}
    public float getEnergiaPorBajaDash(){return mejorasRobarEnergia*3f;}
    public float getDanoExplosionDash(){return mejorasDashExplosivo>0?getDanoDash()*0.5f:0f;}
    public float getRadioExplosionDash(){return mejorasDashExplosivo>0?ExplosionBala.RADIO*(1f+(mejorasDashExplosivo-1)*0.5f):0f;}
    public float getVelocidadDash(){return EnergiaDash.VELOCIDAD*(1f+mejorasDistanciaDash*0.1f);}
    public boolean tieneReparacionMejorada(){return reparacionMejorada;}
    public float getDuracionReparacion(){return reparacionMejorada?2f:3f;}
    public float getTamano(){return tamano;}
    public float getPenetracion(){return penetracion;}
    public float getExplosion(){return explosion;}
    public float getDanoDron(boolean explosivo){return (explosivo?danoDronExplosivo:danoDron)+DANO_DRON_BASE*bonoApoyo();}
    public float getCadenciaDron(boolean explosivo){return (explosivo?cadenciaDronExplosivo:cadenciaDron)+CADENCIA_DRON_BASE*bonoApoyo();}
    private float bonoApoyo(){return mejorasApoyo*0.1f;}
    /** Conserva el turno entre rondas; solo se avanza cuando el mini dron dispara. */
    public TipoMejora siguienteDisparoMiniDron() {
        for(int turno=siguienteDisparoMini;turno<4;turno++) {
            TipoMejora tipo=turno==0?TipoMejora.INICIAL_DRON:turno==1?TipoMejora.DRON
                    :turno==2?TipoMejora.DRON_EXPLOSIVO:TipoMejora.DRON_LASER;
            if(turno==0 || turno==1 && dron || turno==2 && dronExplosivo || turno==3 && dronLaser) {
                siguienteDisparoMini=turno+1;
                return tipo;
            }
        }
        siguienteDisparoMini=1;
        return TipoMejora.INICIAL_DRON;
    }
    public float getDanoMiniDron(){return DANO_DRON_BASE*0.5f*(1f+bonoApoyo());}
    public float getCadenciaMiniDron(){return CADENCIA_DRON_BASE*0.5f*(1f+bonoApoyo());}
    public boolean registrarBateria(){if(baterias>=3)return false;baterias++;return true;}
    public boolean consumirBateria(){if(baterias==0)return false;baterias--;return true;}
    public int getBaterias(){return baterias;}
    public boolean esInvulnerablePruebas(){return invulnerablePruebas;}
    public void alternarInvulnerabilidadPruebas(){invulnerablePruebas=!invulnerablePruebas;}
}
