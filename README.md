# Space Nave Galaxy

Juego 2D de supervivencia espacial en Java y libGDX. Derrota enemigos y jefes,
consigue puntos y elige mejoras durante cada partida.

## Abrir y ejecutar

Clona el repositorio y abre su carpeta raiz en IntelliJ IDEA como proyecto Gradle.
El proyecto usa Gradle 8.10.1, que puede ejecutarse con JDK 11, y compila el codigo
para Java 11. No es necesario instalar una version de JDK superior. Desde la carpeta
del proyecto, en Windows:

```powershell
.\gradlew.bat lwjgl3:run
.\gradlew.bat core:test
```
El empaquetado nativo multiplataforma es opcional y requiere JDK 17; la ejecucion,
las pruebas y la compilacion normal funcionan con JDK 11.

## Codigo y UML

El codigo del juego esta en `core/src/main/java/puppy/code`, organizado por dominios:

- `enemigos`: aliens, jefes y constructores de jefes.
- `combate`: disparos, proyectiles, ataques y colisiones.
- `nave`: nave del jugador, drones y apoyos.
- `mejoras`: progresion y catalogo de mejoras.
- `pantallas`: menu, juego, pausa, mejoras y fin de partida.
- `apariencias`: recursos y apariencias visuales.

El punto de entrada de escritorio esta en `lwjgl3/src/main/java/puppy/code/lwjgl3`.
Las pruebas estan en `core/src/test/java/puppy/code` y los recursos en `assets`.

Para revisar el diagrama de clases y los patrones de diseno:

- **Singleton:** `mejoras/ProgresionMejoras` conserva el estado de mejoras de la partida.
- **Strategy:** `combate/EstrategiaDisparo` y sus implementaciones definen el disparo
  de la nave; `combate/AtaqueJefe` define estrategias de ataque para los jefes.
- **Template Method:** `enemigos/Enemigo.actualizar` y `Enemigo.dibujar` fijan el flujo comun;
  las subclases especializan `actualizarComportamiento` y `dibujarSprite`.
- **Builder:** `enemigos/ConstructorJefe` define los pasos; `DirectorJefes` los coordina
  y los constructores concretos ensamblan los jefes por sector.

`pantallas/PantallaJuego` coordina la partida. `nave/Nave4` representa al jugador;
los enemigos comparten la base `enemigos/Enemigo`. Las pantallas manejan la interfaz
principal. No hace falta ejecutar el
juego para inspeccionar estas clases y preparar el UML.

## Controles

- WASD: moverse. Flechas: disparar y orientar la nave.
- Shift: dash en la ultima direccion de movimiento.
- R: reparar con una bateria.
- P o Esc: pausa, reinicio o regreso al menu.
- 1, 2, 3 o clic: elegir una mejora.
- Al perder: Enter para volver a jugar; Esc o el boton para volver al menu.

El menu **Controles y reglas** explica las mecanicas dentro del juego.

## Partida

Al comenzar eliges una nave especializada en dash, drones o criticos. Derrotar
enemigos y recoger energia da puntos para subir de nivel y elegir mejoras
aleatorias. Las baterias reparan la nave; la energia recarga el dash.

Hay insectos verdes perseguidores, insectos rojos explosivos, celulas que se
dividen, platillos que disparan al detenerse y abejas que explotan lanzando
proyectiles. Los meteoritos cruzan la pantalla como recursos opcionales.

Los primeros jefes aparecen en las rondas 4, 8 y 12. El primero permanece en el
centro, gira y alterna salvas con un laser anunciado. El segundo y el tercero
persiguen al jugador y se detienen durante sus barreras. Despues de la ronda 12
continuan las oleadas y los jefes periodicos.

Los archivos temporales, clases compiladas y ajustes locales del IDE se excluyen
del repositorio; Gradle los genera al compilar.
