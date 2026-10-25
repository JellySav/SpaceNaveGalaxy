package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/** Salvamento celeste: distinto de las baterias verdes usadas para reparar. */
public final class CargaEnergia {
    public static final float VIDA = 8f;
    private final Rectangle area;
    private final float cantidad;
    private float restante = VIDA;

    public CargaEnergia(float centroX, float centroY, float cantidad) {
        area = new Rectangle(Math.max(0f, Math.min(MundoJuego.ANCHO - 24f, centroX - 12f)),
                Math.max(0f, Math.min(MundoJuego.ALTO - 24f, centroY - 12f)), 24f, 24f);
        this.cantidad = cantidad;
    }

    public void actualizar(float delta) { restante = Math.max(0f, restante - Math.max(0f, delta)); }
    public boolean estaActiva() { return restante > 0f; }
    public boolean toca(Rectangle nave) { return estaActiva() && area.overlaps(nave); }
    public float getCantidad() { return cantidad; }

    public void dibujar(ShapeRenderer formas) {
        if (!estaActiva()) return;
        float alpha = restante < 2f ? 0.35f + 0.65f * Math.abs((float) Math.sin(restante * 10f)) : 1f;
        float centroX = area.x + area.width / 2f;
        float centroY = area.y + area.height / 2f;
        // Color propio, independiente de los colores originales del sprite del cohete.
        formas.setColor(0.1f, 0.85f, 1f, alpha);
        formas.triangle(centroX, area.y + area.height, area.x, centroY, centroX, area.y);
        formas.triangle(centroX, area.y + area.height, centroX, area.y, area.x + area.width, centroY);
        formas.setColor(0.9f, 1f, 1f, alpha);
        formas.triangle(centroX, centroY + 5f, centroX - 5f, centroY, centroX, centroY - 5f);
        formas.triangle(centroX, centroY + 5f, centroX, centroY - 5f, centroX + 5f, centroY);
    }
}
