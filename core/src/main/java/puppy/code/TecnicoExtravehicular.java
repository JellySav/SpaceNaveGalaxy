package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TecnicoExtravehicular {
    private float x;
    private float y;
    private float tiempo;

    public TecnicoExtravehicular(float naveX, float naveY) {
        x = naveX + 32f;
        y = naveY - 8f;
    }

    public void actualizar(float delta, float naveX, float naveY) {
        tiempo += delta;
        float destinoX = naveX + 32f;
        float destinoY = naveY - 8f + (float) Math.sin(tiempo * 2.5f) * 4f;
        float seguimiento = Math.min(1f, delta * 7f);
        x += (destinoX - x) * seguimiento;
        y += (destinoY - y) * seguimiento;
    }

    public void dibujar(ShapeRenderer shapes, float naveX, float naveY) {
        shapes.setColor(0.35f, 0.88f, 0.96f, 0.8f);
        shapes.rectLine(naveX, naveY, x + 10f, y + 22f, 2f);

        shapes.setColor(0.92f, 0.48f, 0.25f, 1f);
        shapes.rect(x + 2f, y + 5f, 18f, 22f);
        shapes.rect(x + 4f, y, 6f, 8f);
        shapes.rect(x + 14f, y, 6f, 8f);

        shapes.setColor(0.56f, 0.92f, 0.48f, 1f);
        shapes.circle(x + 11f, y + 35f, 11f);
        shapes.setColor(0.08f, 0.22f, 0.28f, 1f);
        shapes.ellipse(x + 7f, y + 32f, 10f, 6f);

        shapes.setColor(0.94f, 0.72f, 0.34f, 1f);
        shapes.rect(x + 18f, y + 12f, 7f, 14f);
    }
}