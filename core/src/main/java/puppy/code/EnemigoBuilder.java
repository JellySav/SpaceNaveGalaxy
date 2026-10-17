package puppy.code;

import com.badlogic.gdx.graphics.Texture;

public class EnemigoBuilder {
    public enum Tipo {
        ASTEROIDE,
        ALIEN
    }

    private Tipo tipo;
    private float x;
    private float y;
    private float tamano = 48f;
    private float velocidadX;
    private float velocidadY;
    private int resistencia = 1;

    public EnemigoBuilder tipo(Tipo tipo) {
        this.tipo = tipo;
        return this;
    }

    public EnemigoBuilder posicion(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public EnemigoBuilder tamano(float tamano) {
        this.tamano = tamano;
        return this;
    }

    public EnemigoBuilder velocidad(float velocidadX, float velocidadY) {
        this.velocidadX = velocidadX;
        this.velocidadY = velocidadY;
        return this;
    }

    public EnemigoBuilder resistencia(int resistencia) {
        this.resistencia = resistencia;
        return this;
    }

    public Enemigo construir(Texture textura) {
        if (tipo == null || textura == null) {
            throw new IllegalStateException("El tipo y la textura del enemigo son obligatorios");
        }
        if (tipo == Tipo.ASTEROIDE) {
            return new AsteroideEnemigo(x, y, tamano, velocidadX, velocidadY, textura);
        }
        return new AlienEnemigo(x, y, Math.max(Math.abs(velocidadX), Math.abs(velocidadY)),
                resistencia, textura);
    }
}