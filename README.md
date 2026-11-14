# Space Nave Galaxy

<img src="./lwjgl3/icons/logo.png" alt="Space Nave Galaxy" width="48" align="right">

Juego 2D de supervivencia espacial en Java y libGDX. Derrota enemigos y jefes,
consigue puntos y elige mejoras durante cada partida.

## Abrir y ejecutar

Clona el repositorio y abre la carpeta raíz como proyecto Gradle en Apache
NetBeans 21. IntelliJ IDEA también es compatible. NetBeans 21 puede ejecutarse
con JDK 11, 17 o 21; para mantener todo el flujo en JDK 11, instálalo y
configúralo como JVM de Gradle (y como JVM de NetBeans si así lo deseas). El
proyecto usa Gradle 8.10.1 y compila para Java 11: JDK 8 no es compatible. Una
versión anterior de NetBeans puede servir si ofrece soporte para proyectos
Gradle y puede usar JDK 11; NetBeans 21 es la opción recomendada.

En NetBeans, abre la vista **Gradle**, despliega las tareas de `lwjgl3` y ejecuta
`run` para iniciar el juego. También puedes ejecutar `test` bajo `core` o
`distZip` bajo `lwjgl3`. Si la vista no aparece o no se sincroniza, abre la
terminal integrada en la raíz y ejecuta el wrapper incluido. En Windows:

```powershell
.\gradlew.bat lwjgl3:run
.\gradlew.bat core:test
```

En Linux o macOS:

```bash
./gradlew :lwjgl3:run
./gradlew :core:test
```

El juego y las pruebas se validaron con OpenJDK 11; Oracle JDK 11 no se ha
probado por separado. Ambos son distribuciones de Java 11 y el proyecto no
depende de APIs exclusivas de OpenJDK. El empaquetamiento nativo
multiplataforma por sistema operativo no puede generarse con la configuración
actual usando solo JDK 11. Para crear una distribución portable de Java 11:

```bash
./gradlew :lwjgl3:distZip
```

En Windows también puedes ejecutar `.\gradlew.bat :lwjgl3:distZip`. El ZIP queda
en `lwjgl3/build/distributions/SpaceNav2024-1.0.0.zip` e incluye el JAR y scripts
de inicio para Windows y Unix. La máquina donde se ejecute debe tener Java 11.
Es una distribución portable multiplataforma, no un instalador nativo. El
empaquetamiento actual con Construo solo se activa con Gradle ejecutado en JDK 17
y descarga runtimes JDK 17; por eso no cumple el requisito de empaquetar y
ejecutar todo con JDK 11.

## Código y UML

El código del juego está en `core/src/main/java/puppy/code`, organizado por dominios:

- `enemigos`: aliens, jefes y constructores de jefes.
- `combate`: disparos, proyectiles, ataques y colisiones.
- `nave`: nave del jugador, drones y apoyos.
- `mejoras`: progresión y catálogo de mejoras.
- `pantallas`: menú, juego, pausa, mejoras y fin de partida.
- `apariencias`: recursos y apariencias visuales.

El punto de entrada de escritorio está en `lwjgl3/src/main/java/puppy/code/lwjgl3`.
Las pruebas están en `core/src/test/java/puppy/code` y los recursos en `assets`.

Para revisar el diagrama de clases y los patrones de diseño:

- **Singleton:** `mejoras/ProgresionMejoras` conserva el estado de mejoras de la partida.
- **Strategy:** `combate/EstrategiaDisparo` y sus implementaciones definen el disparo
  de la nave; `combate/AtaqueJefe` define estrategias de ataque para los jefes.
- **Template Method:** `enemigos/Enemigo.actualizar` y `Enemigo.dibujar` fijan el flujo
  común; las subclases especializan `actualizarComportamiento` y `dibujarSprite`.
- **Builder GoF:** `enemigos/ConstructorJefe` define los pasos; `DirectorJefes` los
  coordina y los constructores concretos ensamblan los jefes por sector.

`pantallas/PantallaJuego` coordina la partida. `nave/Nave4` representa al jugador;
los enemigos comparten la base `enemigos/Enemigo`. Las pantallas manejan la interfaz
principal. No hace falta ejecutar el juego para inspeccionar estas clases y preparar
el UML.

## Controles

- **WASD:** moverse. **Flechas:** disparar y orientar la nave.
- **Shift:** dash en la última dirección de movimiento.
- **R:** reparar con una batería.
- **P o Esc:** pausar la partida. Desde pausa puedes reiniciar o volver al menú.
- **1, 2, 3 o clic:** elegir una mejora.
- **Al perder:** Enter para volver a jugar; Esc o el botón para volver al menú.

El menú **Controles y reglas** explica las mecánicas dentro del juego. Durante la
reparación, la nave queda inmóvil; en la pausa puedes ajustar el volumen de música
y efectos.


## Partida

Al comenzar eliges una nave especializada en dash, drones o críticos. Derrotar
enemigos y recoger energía da puntos para subir de nivel y elegir mejoras
aleatorias. Las baterías reparan la nave; la energía recarga el dash.

Hay insectos verdes perseguidores, insectos rojos explosivos, células que se
dividen, platillos que disparan al detenerse y abejas que explotan lanzando
proyectiles. Los meteoritos cruzan la pantalla como recursos opcionales.

Los primeros jefes aparecen en las rondas 4, 8 y 12. El primero permanece en el
centro, gira y alterna salvas con un láser anunciado. El segundo y el tercero
persiguen al jugador y se detienen durante sus barreras. Después de la ronda 12
continúan las oleadas y los jefes periódicos.

<details>
<summary>Explora los sectores</summary>

| Sector | Comienza en | Característica |
|---|---:|---|
| Cinturón de Vela | Ronda 1 | Primer sector; su jefe permanece en el centro y apunta antes de disparar. |
| Nebulosa de Ion | Ronda 5 | Los enemigos refuerzan el bloqueo; el jefe usa barreras. |
| Frontera de Khepri | Ronda 9 | Enemigos más resistentes, recursos de salvamento y refuerzos del jefe. |

</details>

<details>
<summary>Descubre qué hacen las mejoras y los recursos</summary>

- **Mejora inicial:** especializa la nave en dash, drones o golpes críticos.
- **Mejoras de nivel:** pueden aumentar daño, cadencia, movimiento, vida, dash,
  críticos o capacidades de drones.
- **Cargas de energía:** dan puntos y recuperan energía del dash.
- **Baterías:** permiten reparar la nave; su capacidad es limitada.
- **Rarezas:** inicial, común, épica y legendaria.

</details>
