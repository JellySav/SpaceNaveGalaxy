package puppy.code.enemigos;

import com.badlogic.gdx.graphics.Texture;
import java.util.function.Consumer;
import puppy.code.combate.LaserJefe;
import puppy.code.combate.ProyectilHostil;

public interface EstrategiaJefeSector {
    void actualizarMovimiento(JefeSector jefe,float delta,float objetivoX,float objetivoY);

    void actualizarAtaque(JefeSector jefe,float delta,float objetivoX,float objetivoY,
                          Consumer<ProyectilHostil> crearProyectil);

    void actualizarRefuerzos(JefeSector jefe,float delta,float velocidadAlien,int resistenciaAlien,
                             Texture texturaAlien,Consumer<AlienEnemigo> crearRefuerzo);

    float getIntervaloRefuerzos();

    LaserJefe getLaser();
}
