package puppy.code;

public enum RarezaMejora {
    COMUN("COMUN", 0.65f, 0.67f, 0.7f),
    EPICA("EPICA", 0.72f, 0.35f, 1f),
    LEGENDARIA("LEGENDARIA", 1f, 0.82f, 0.18f),
    INICIAL("INICIAL", 0.3f, 0.92f, 0.4f);
    public final String nombre;
    public final float r, g, b;
    RarezaMejora(String nombre, float r, float g, float b) {
        this.nombre=nombre; this.r=r; this.g=g; this.b=b;
    }
    public static RarezaMejora sortear(java.util.Random random) {
        double valor=random.nextDouble();
        return valor<0.7 ? COMUN : valor<0.92 ? EPICA : LEGENDARIA;
    }
}
