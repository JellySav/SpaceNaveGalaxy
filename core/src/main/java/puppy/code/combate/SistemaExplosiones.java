package puppy.code.combate;

import puppy.code.enemigos.*;
import puppy.code.nave.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/** Resuelve las cadenas sin modificar las listas que se estan recorriendo. */
public final class SistemaExplosiones {
    private final List<ExplosionAlien> explosiones = new ArrayList<>();

    public void registrarMuerte(Enemigo enemigo) {
        ExplosionAlien explosion=enemigo.crearExplosionAlDerrotar();
        if(explosion!=null)explosiones.add(explosion);
    }

    public void actualizar(float delta, List<Enemigo> enemigos, Nave4 nave, ApoyoNave aliado,
                           Consumer<Enemigo> resolverDerrota, Runnable sonidoExplosion) {
        actualizar(delta,enemigos,nave,aliado,resolverDerrota,sonidoExplosion,null,p -> { });
    }

    public void actualizar(float delta,List<Enemigo> enemigos,Nave4 nave,ApoyoNave aliado,
                           Consumer<Enemigo> resolverDerrota,Runnable sonidoExplosion,
                           com.badlogic.gdx.graphics.Texture textura,Consumer<ProyectilHostil> crearProyectil) {
        // Una nave alcanzada inicia su propio aviso completo a partir del siguiente frame.
        for (ExplosionAlien explosion : new ArrayList<>(explosiones)) {
            if (!explosion.actualizar(delta)) continue;
            explosion.emitirProyectiles(textura,crearProyectil);
            sonidoExplosion.run();
            if (explosion.alcanza(nave.getArea())) nave.recibirDano();
            if(aliado!=null)aliado.recibirExplosion(explosion);
            for (Enemigo enemigo : new ArrayList<>(enemigos)) {
                if (!enemigo.estaActivo() || !explosion.alcanza(enemigo.getArea())) continue;
                enemigo.recibirExplosionAlien();
                if (enemigo.estaDestruido()) resolverDerrota.accept(enemigo);
            }
        }
        explosiones.removeIf(ExplosionAlien::termino);
    }

    public boolean hayPendientes() { return !explosiones.isEmpty(); }
    public List<ExplosionAlien> getExplosiones() { return Collections.unmodifiableList(explosiones); }
}
