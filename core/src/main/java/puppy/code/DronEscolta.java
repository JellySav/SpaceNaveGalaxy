package puppy.code;

import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class DronEscolta implements ApoyoNave {
    private final Sprite sprite;
    private float tiempo;
    private float cooldownDisparo;

    public DronEscolta(Texture textura) {
        sprite = new Sprite(textura);
        sprite.setSize(32f, 32f);
        sprite.setColor(0.4f, 0.92f, 1f, 1f);
    }

    @Override
    public void actualizar(float delta, float naveX, float naveY, List<Enemigo> enemigos,
                           Texture texturaBala, Consumer<Bullet> emitirBala) {
        tiempo += delta;
        float destinoX = naveX + 42f;
        float destinoY = naveY + (float) Math.sin(tiempo * 3f) * 18f;
        float seguimiento = Math.min(1f, delta * 5f);
        sprite.setPosition(sprite.getX() + (destinoX - sprite.getX()) * seguimiento,
                sprite.getY() + (destinoY - sprite.getY()) * seguimiento);

        cooldownDisparo -= delta;
        if (cooldownDisparo > 0f) {
            return;
        }

        Enemigo objetivo = buscarObjetivo(enemigos);
        if (objetivo == null) {
            return;
        }
        Rectangle area = objetivo.getArea();
        float origenX = sprite.getX() + sprite.getWidth() / 2f;
        float origenY = sprite.getY() + sprite.getHeight() / 2f;
        float diferenciaX = area.x + area.width / 2f - origenX;
        float diferenciaY = area.y + area.height / 2f - origenY;
        float distancia = (float) Math.sqrt(diferenciaX * diferenciaX + diferenciaY * diferenciaY);
        if (distancia > 0f) {
            emitirBala.accept(new Bullet(origenX, origenY,
                    diferenciaX / distancia * 420f, diferenciaY / distancia * 420f,
                    texturaBala));
            cooldownDisparo = 1.1f;
        }
    }

    private Enemigo buscarObjetivo(List<Enemigo> enemigos) {
        Enemigo cercano = null;
        float distanciaMinima = Float.MAX_VALUE;
        for (Enemigo enemigo : enemigos) {
            if (!enemigo.estaActivo()) {
                continue;
            }
            Rectangle area = enemigo.getArea();
            float diferenciaX = area.x + area.width / 2f - sprite.getX();
            float diferenciaY = area.y + area.height / 2f - sprite.getY();
            float distancia = diferenciaX * diferenciaX + diferenciaY * diferenciaY;
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                cercano = enemigo;
            }
        }
        return cercano;
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        sprite.draw(batch);
    }
}