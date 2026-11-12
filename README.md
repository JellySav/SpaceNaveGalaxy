# Space Nave Galaxy

Juego 2D de supervivencia espacial en Java y libGDX. Derrota enemigos y jefes,
consigue puntos y elige mejoras durante cada partida.

## Abrir y ejecutar

Clona el repositorio y abre su carpeta raiz en IntelliJ IDEA como proyecto Gradle.
Usa un JDK compatible con Gradle 8.10.1, por ejemplo JDK 21 o el JDK 23 utilizado
para desarrollar esta version. Desde la carpeta del proyecto, en Windows:

```powershell
.\gradlew.bat lwjgl3:run
.\gradlew.bat core:test
```

## Codigo y UML

El codigo del juego esta en `core/src/main/java/puppy/code`.
El punto de entrada de escritorio esta en `lwjgl3/src/main/java/puppy/code/lwjgl3`.
Las pruebas estan en `core/src/test/java/puppy/code` y los recursos en `assets`.

Para revisar el diagrama de clases y los patrones de diseno:

- **Singleton:** `ProgresionMejoras` conserva el estado de mejoras de la partida.
- **Strategy:** `EstrategiaDisparo` y sus implementaciones definen el disparo de la
  nave; `AtaqueJefe` define estrategias de ataque para los jefes.
- **Template Method:** `Enemigo.actualizar` y `Enemigo.dibujar` fijan el flujo comun;
  las subclases especializan `actualizarComportamiento` y `dibujarSprite`.
- **Builder:** `EnemigoBuilder` construye enemigos y jefes.

`PantallaJuego` coordina la partida. `Nave4` representa al jugador; los enemigos
comparten la base `Enemigo`. `PantallaMenu`, `PantallaMejoras`, `PantallaPausa` y
`PantallaGameOver` manejan las pantallas principales. No hace falta ejecutar el
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
