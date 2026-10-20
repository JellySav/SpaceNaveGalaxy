# Space Navigation

Prototipo 2D de supervivencia espacial hecho con [libGDX](https://libgdx.com/). El jugador mueve una nave, dispara a asteroides, pierde vidas al chocar y avanza de ronda cuando elimina todos los asteroides. Al perder todas las vidas aparece la pantalla de fin de partida.

## Controles y reglas

- Flechas: mantener pulsada una dirección mueve la nave de forma continua.
- Menú: `Enter` o clic en «Iniciar partida» comienza; `C` o «Controles y reglas» abre las instrucciones; `Esc` cierra la aplicación.
- Controles: `Flechas` + `Espacio`, o `WASD` + `Ctrl izquierdo` según el esquema activo. Durante la partida, `R` inicia una reparación si tienes una batería.
- `P` o `Esc` pausa; en pausa puedes ajustar música/efectos, alternar flechas/WASD o volver al menú. La partida se congela al pausar.
- Game over: cualquier tecla o clic inicia una partida nueva.
- La nave comienza con 3 vidas. Los asteroides valen 10 puntos y los aliens 25; las rondas agregan asteroides y aumentan dificultad.
- Las mejoras ya no se activan automáticamente: el disparo doble exige ronda 2, 4 impactos, 35 segundos y 1 batería; el triple y el dron exigen ronda 3, 12 impactos, 90 segundos, 2 aliens derrotados, 2 baterías y un núcleo de jefe. Pulsa `U` para instalarlas.
- Desde la ronda 2 aparecen aliens que persiguen la nave y requieren varios impactos. La progresión de armas se conserva entre rondas y reinicia al comenzar otra partida.
- Jefes de sector: rondas 2, 4 y 5; en Khepri regresan cada tres rondas a partir de la 8. Sus patrones dependen del sector y dejan un núcleo y una batería; el disparo triple requiere un núcleo.
- Récord persistente, controles alternativos y volumen de música/efectos se guardan en preferencias locales. `P` o `Esc` abre pausa.
- Los enemigos pueden soltar baterías; se pueden guardar hasta 3. Pulsa `R` para salir de la nave durante 4 segundos y recuperar una vida. Mientras el técnico trabaja, la nave no se mueve ni dispara, pero los enemigos y aliados siguen activos.

## Recorrido galáctico

- Rondas 1-2: Cinturón de Vela. La misión comienza siguiendo una baliza de auxilio.
- Rondas 3-4: Nebulosa de Ion. Aumentan los alienígenas y la velocidad de la zona.
- Ronda 5 en adelante: Frontera de Khepri. Los aliens resisten más, aparecen en mayor número y dejan más baterías.

Cada sector muestra un breve mensaje de historia, cambia el fondo y el color de las estrellas, y modifica la dificultad y el salvamento. Las mejoras del arma y del dron se integran con el avance de las rondas.

## Tecnología y ejecución

- `core`: lógica, entidades y pantallas compartidas del juego.
- `lwjgl3`: lanzador de escritorio mediante LWJGL3. No hay lanzadores configurados para Android, iOS o web.
- Java: código compatible con Java 11; se recomienda JDK 17 o 21 para ejecutar Gradle 8.10.1.
- Dependencias principales: libGDX 1.12.1 y backend LWJGL3; Gradle resuelve las demás dependencias del lanzador.
- Recursos: sprites PNG y audio OGG, WAV y MP3 ubicados en `assets/`.

Con un JDK compatible, ejecuta `./gradlew lwjgl3:run`. Para compilar todos los módulos usa `./gradlew build`; el JAR de escritorio se genera en `lwjgl3/build/libs`. El wrapper necesita conexión la primera vez para descargar Gradle y sus dependencias.

## Arquitectura y patrones

- **Clase abstracta y Template Method:** `Enemigo` comparte estado, impactos, límites y el ciclo final `actualizar`; `AsteroideEnemigo`, `AlienEnemigo` y `JefeSector` especializan su comportamiento.
- **Interfaz:** `ObjetivoDisparo` permite que ambos enemigos reciban impactos a través de `Bullet`, sin acoplar el proyectil a una clase concreta. `ApoyoNave` define además el contrato extensible de aliados.
- **Strategy:** `EstrategiaDisparo` intercambia disparo simple, doble o triple sin cambiar el control de la nave.
- **Singleton:** `ProgresionMejoras` conserva progreso durante la partida; `PreferenciasJuego` conserva récord, volumen y controles entre ejecuciones.
- **Builder:** `EnemigoBuilder` configura tipo, posición, velocidad y resistencia para asteroides, aliens y jefes.
- **Strategy:** `AtaqueJefe` selecciona patrones dirigidos, en abanico o circulares según el sector.
- **Encapsulamiento:** entidades mantienen sus datos internos privados o protegidos y exponen operaciones de juego en vez de permitir cambios arbitrarios desde la pantalla.
- **Aliado:** `DronEscolta` implementa `ApoyoNave`; se desbloquea junto con el disparo triple y busca objetivos para atacar.

## Estado y límites

Es una base didáctica, no un juego listo para publicar. Tiene siete pruebas unitarias para vidas, progreso, sectores, Builder, grilla y cálculo de viewport; aún faltan pruebas integrales de jefes, daño por proyectiles hostiles y preferencias persistentes. El mundo lógico es 1200×800 y `FitViewport` mantiene la proporción en ventanas distintas. Las colisiones de disparo usan una grilla espacial; conviene medirla si aumenta mucho el número de enemigos.

## Mejoras recomendadas

1. Ampliar tests a ataques y recompensas de jefes, pausa y persistencia de preferencias.
2. Probar visualmente 16:9, 4:3 y ventanas pequeñas; el viewport mantiene proporción con bandas de letterboxing.
3. Extraer oleadas y progresión de `PantallaJuego` para reducir responsabilidades de la pantalla.
4. Agregar perfiles de dificultad y patrones de jefe configurables por sector.

Actualmente se puede reutilizar el núcleo del juego y el lanzador de escritorio. Android, iOS y web no están configurados; cada plataforma requiere su propio módulo de lanzamiento y validación de entradas, audio y empaquetado.
