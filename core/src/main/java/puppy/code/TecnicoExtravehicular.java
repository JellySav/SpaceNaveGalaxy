package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TecnicoExtravehicular {
    private static final float ESCALA=0.6f;
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
        // Cable de seguridad y mochila de oxigeno, por detras del traje.
        shapes.setColor(0.4f,0.65f,0.72f,1f);
        shapes.rectLine(naveX,naveY,x+25f*ESCALA,y+20f*ESCALA,1f);
        com.badlogic.gdx.math.Matrix4 transformacionAnterior=new com.badlogic.gdx.math.Matrix4(shapes.getTransformMatrix());
        shapes.setTransformMatrix(new com.badlogic.gdx.math.Matrix4(transformacionAnterior)
                .translate(x,y,0f).scale(ESCALA,ESCALA,1f).translate(-x,-y,0f));
        try {
        shapes.setColor(0.12f,0.2f,0.27f,1f);shapes.rect(x+18f,y+11f,12f,22f);
        shapes.setColor(0.65f,0.74f,0.8f,1f);shapes.rect(x+20f,y+13f,8f,18f);
        shapes.setColor(0.86f,0.91f,0.94f,1f);shapes.rect(x+23f,y+14f,3f,16f);

        // Piernas y botas separadas para dar una silueta reconocible.
        shapes.setColor(0.12f,0.2f,0.27f,1f);
        shapes.rect(x+1f,y,10f,15f);shapes.rect(x+12f,y,10f,15f);
        shapes.setColor(0.9f,0.94f,0.97f,1f);
        shapes.rect(x+3f,y+3f,6f,12f);shapes.rect(x+14f,y+3f,6f,12f);
        shapes.setColor(0.85f,0.24f,0.18f,1f);
        shapes.rect(x+3f,y+8f,6f,2f);shapes.rect(x+14f,y+8f,6f,2f);
        shapes.setColor(0.23f,0.33f,0.42f,1f);
        shapes.rect(x+1f,y,9f,4f);shapes.rect(x+13f,y,10f,4f);

        // Brazos con guantes y herramienta de reparacion.
        shapes.setColor(0.12f,0.2f,0.27f,1f);
        shapes.rectLine(x+3f,y+25f,x-5f,y+18f,8f);
        shapes.rectLine(x-5f,y+18f,x-10f,y+25f,7f);
        shapes.rectLine(x+20f,y+25f,x+26f,y+18f,8f);
        shapes.setColor(0.88f,0.93f,0.96f,1f);
        shapes.rectLine(x+3f,y+25f,x-5f,y+18f,5.5f);
        shapes.rectLine(x-5f,y+18f,x-10f,y+25f,4.5f);
        shapes.rectLine(x+20f,y+25f,x+26f,y+18f,5.5f);
        shapes.setColor(0.62f,0.72f,0.8f,1f);
        shapes.circle(x-10f,y+25f,3f,12);shapes.circle(x+26f,y+18f,3f,12);
        shapes.setColor(0.72f,0.8f,0.85f,1f);
        shapes.rectLine(x-11f,y+26f,x-15f,y+33f,2f);
        shapes.rectLine(x-15f,y+33f,x-18f,y+34f,2f);
        shapes.rectLine(x-15f,y+33f,x-14f,y+36f,2f);

        // Traje blanco, cinturon y panel del pecho.
        shapes.setColor(0.12f,0.2f,0.27f,1f);shapes.rect(x,y+11f,23f,20f);
        shapes.setColor(0.9f,0.94f,0.97f,1f);shapes.rect(x+2f,y+13f,19f,16f);
        shapes.setColor(0.65f,0.74f,0.8f,1f);shapes.rect(x+2f,y+12f,19f,3f);
        shapes.setColor(0.18f,0.3f,0.4f,1f);shapes.rect(x+7f,y+18f,9f,8f);
        shapes.setColor(0.3f,0.85f,0.95f,1f);shapes.rect(x+9f,y+22f,5f,2f);
        shapes.setColor(0.95f,0.5f,0.2f,1f);shapes.rect(x+9f,y+19f,2f,1.5f);
        shapes.setColor(0.5f,0.85f,0.55f,1f);shapes.rect(x+12f,y+19f,2f,1.5f);

        // Casco grande con aro, visor dorado oscuro y reflejo.
        shapes.setColor(0.12f,0.2f,0.27f,1f);shapes.circle(x+11f,y+35f,12.5f,28);
        shapes.setColor(0.95f,0.97f,1f,1f);shapes.circle(x+11f,y+35f,11f,28);
        shapes.setColor(0.64f,0.72f,0.79f,1f);
        shapes.circle(x,y+35f,2.5f,12);shapes.circle(x+22f,y+35f,2.5f,12);
        shapes.setColor(0.93f,0.65f,0.18f,1f);shapes.ellipse(x+2f,y+29f,18f,13f,24);
        shapes.setColor(0.12f,0.22f,0.3f,1f);shapes.ellipse(x+4f,y+31f,14f,9f,24);
        shapes.setColor(0.66f,0.88f,0.95f,1f);shapes.rectLine(x+7f,y+37f,x+12f,y+38f,1.3f);
        shapes.setColor(0.84f,0.25f,0.18f,1f);shapes.rect(x+8f,y+44f,6f,2f);
        if(((int)(tiempo*10f)%3)==0) {
            shapes.setColor(1f,0.8f,0.28f,1f);
            shapes.rectLine(x-20f,y+37f,x-23f,y+39f,1f);
            shapes.rectLine(x-18f,y+39f,x-18f,y+42f,1f);
        }
        } finally {shapes.setTransformMatrix(transformacionAnterior);}
    }
}
