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
    private boolean posicionConfigurada;
    private boolean velocidadConfigurada;
    private boolean tamanoConfigurado;
    private boolean resistenciaConfigurada;

    public EnemigoBuilder tipo(Tipo tipo) {
        this.tipo = tipo;
        return this;
    }

    public EnemigoBuilder posicion(float x, float y) {
        this.x = x;
        this.y = y;
        posicionConfigurada = true;
        return this;
    }

    public EnemigoBuilder tamano(float tamano) {
        this.tamano = tamano;
        tamanoConfigurado = true;
        return this;
    }

    public EnemigoBuilder velocidad(float velocidadX, float velocidadY) {
        this.velocidadX = velocidadX;
        this.velocidadY = velocidadY;
        velocidadConfigurada = true;
        return this;
    }

    public EnemigoBuilder resistencia(int resistencia) {
        this.resistencia = resistencia;
        resistenciaConfigurada = true;
        return this;
    }

    public Enemigo construir(Texture textura) {
        validarConfiguracion();
        if (textura == null) {
            throw new IllegalArgumentException("La textura del enemigo es obligatoria");
        }
        if (tipo == Tipo.ASTEROIDE) {
            return new AsteroideEnemigo(x, y, tamano, velocidadX, velocidadY, textura);
        }
        return new AlienEnemigo(x, y, tamano,
                Math.max(Math.abs(velocidadX), Math.abs(velocidadY)), resistencia, textura);
    }

    private void validarConfiguracion() {
        if (tipo == null || !posicionConfigurada || !velocidadConfigurada) {
            throw new IllegalStateException("Tipo, posicion y velocidad son obligatorios");
        }
        if (!Float.isFinite(x) || !Float.isFinite(y)
                || !Float.isFinite(velocidadX) || !Float.isFinite(velocidadY)
                || (velocidadX == 0f && velocidadY == 0f)) {
            throw new IllegalArgumentException("Posicion y velocidad deben ser finitas; la velocidad no puede ser cero");
        }
        if (!Float.isFinite(tamano) || tamano <= 0f) {
            throw new IllegalArgumentException("El tamano debe ser positivo y finito");
        }
        if (tipo == Tipo.ASTEROIDE && !tamanoConfigurado) {
            throw new IllegalStateException("El tamano del asteroide es obligatorio");
        }
        if (tipo == Tipo.ALIEN && (!resistenciaConfigurada || resistencia <= 0)) {
            throw new IllegalStateException("La resistencia del alien es obligatoria y debe ser positiva");
        }
    }
}