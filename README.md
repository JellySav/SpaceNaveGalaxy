# Space Navigation

Prototipo 2D de supervivencia espacial hecho con [libGDX](https://libgdx.com/). El jugador mueve una nave, dispara a asteroides, pierde vidas al chocar y avanza de ronda cuando elimina todos los asteroides. Al perder todas las vidas aparece la pantalla de fin de partida.

## Controles y reglas

- Flechas: mantener pulsada una dirección mueve la nave de forma continua.
- Menú: `Enter` o clic en «Iniciar partida» comienza; `C` o «Controles y reglas» abre las instrucciones; `Esc` cierra la aplicación.
- Controles: `Flechas` mueven la nave, `Espacio` dispara; `Enter` o clic inicia la partida. `Esc` o `Retroceso` vuelve al menú.
- Game over: cualquier tecla o clic inicia una partida nueva.
- La nave comienza con 3 vidas. Los asteroides valen 10 puntos y los aliens 25; las rondas agregan asteroides y aumentan dificultad.
- Cada 3 impactos se desbloquea disparo doble; con 7 impactos, disparo triple y un dron aliado que ataca enemigos cercanos.
- Desde la ronda 2 aparecen aliens que persiguen la nave y requieren varios impactos. La progresión de armas se conserva entre rondas y reinicia al comenzar otra partida.
- El récord solo se conserva mientras la aplicación permanece abierta.

## Tecnología y ejecución

- `core`: lógica, entidades y pantallas compartidas del juego.
- `lwjgl3`: lanzador de escritorio mediante LWJGL3. No hay lanzadores configurados para Android, iOS o web.
- Java: código compatible con Java 11; se recomienda JDK 17 o 21 para ejecutar Gradle 8.10.1.
- Dependencias principales: libGDX 1.12.1 y backend LWJGL3; Gradle resuelve las demás dependencias del lanzador.
- Recursos: sprites PNG y audio OGG, WAV y MP3 ubicados en `assets/`.

Con un JDK compatible, ejecuta `./gradlew lwjgl3:run`. Para compilar todos los módulos usa `./gradlew build`; el JAR de escritorio se genera en `lwjgl3/build/libs`. El wrapper necesita conexión la primera vez para descargar Gradle y sus dependencias.

## Arquitectura y patrones

- **Clase abstracta y Template Method:** `Enemigo` comparte estado, impactos, límites y el ciclo final `actualizar`; `AsteroideEnemigo` y `AlienEnemigo` especializan su comportamiento.
- **Interfaz:** `ObjetivoDisparo` permite que ambos enemigos reciban impactos a través de `Bullet`, sin acoplar el proyectil a una clase concreta. `ApoyoNave` define además el contrato extensible de aliados.
- **Strategy:** `EstrategiaDisparo` intercambia disparo simple, doble o triple sin cambiar el control de la nave.
- **Singleton:** `ProgresionMejoras` conserva impactos y nivel del arma entre rondas; se reinicia al comenzar otra partida.
- **Builder:** `EnemigoBuilder` configura tipo, posición, velocidad y resistencia al crear enemigos por ronda.
- **Encapsulamiento:** entidades mantienen sus datos internos privados o protegidos y exponen operaciones de juego en vez de permitir cambios arbitrarios desde la pantalla.
- **Aliado:** `DronEscolta` implementa `ApoyoNave`; se desbloquea junto con el disparo triple y busca objetivos para atacar.

## Estado y límites

Es una base didáctica, no un juego listo para publicar. No hay pruebas automatizadas, persistencia del récord, pausa, ajustes ni soporte de mando. El movimiento de la nave, enemigos y proyectiles usa `delta`; las cámaras responden al cambio de tamaño, pero no mantienen una relación de aspecto fija. Las colisiones son simples y los impactos comparan proyectiles con enemigos, por lo que conviene añadir una estructura espacial si ambos grupos crecen mucho.

## Mejoras recomendadas

1. Basar movimiento, disparos y estados temporales en `delta`.
2. Definir una relación de aspecto fija con `Viewport` y probar ventanas de distintos tamaños.
3. Añadir pruebas para colisiones, vidas y progresión de rondas.
4. Persistir el récord y añadir pausa, opciones de audio y controles configurables.
5. Reducir el costo de colisiones si crece el número de asteroides.

Actualmente se puede reutilizar el núcleo del juego y el lanzador de escritorio. Android, iOS y web no están configurados; cada plataforma requiere su propio módulo de lanzamiento y validación de entradas, audio y empaquetado.
