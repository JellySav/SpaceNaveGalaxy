package puppy.code;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class PantallaJuego implements Screen {
    private final SpaceNavigation game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final Sound explosionSound;
    private final Music gameMusic;
    private final Texture naveTexture;
    private final Texture asteroidTexture;
    private final Texture bulletTexture;
    private final Sound naveHitSound;
    private final Sound bulletSound;
    private final List<Enemigo> enemigos = new ArrayList<>();
    private final List<Bullet> balas = new ArrayList<>();
    private final List<BateriaEnergia> baterias = new ArrayList<>();
    private final ProgresionMejoras progresion = ProgresionMejoras.obtenerInstancia();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Random random = new Random();

    private final int ronda;
    private final int velXAsteroides;
    private final int velYAsteroides;
    private final int cantAsteroides;
    private int score;
    private Nave4 nave;
    private ApoyoNave aliado;
    private TecnicoExtravehicular tecnico;
    private float tiempoMensajeMejora;
    private float tiempoReparacion;
    private String mensajeMejora = "";

    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,
                         int velXAsteroides, int velYAsteroides, int cantAsteroides) {
        this.game = game;
        this.ronda = ronda;
        this.score = score;
        this.velXAsteroides = velXAsteroides;
        this.velYAsteroides = velYAsteroides;
        this.cantAsteroides = cantAsteroides;

        batch = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.5f);

        naveTexture = new Texture(Gdx.files.internal("MainShip3.png"));
        bulletTexture = new Texture(Gdx.files.internal("Rocket2.png"));
        asteroidTexture = new Texture(Gdx.files.internal("aGreyMedium4.png"));
        naveHitSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3"));

        nave = new Nave4(Gdx.graphics.getWidth() / 2 - 22, 30, naveTexture,
                naveHitSound, bulletTexture, bulletSound);
        nave.setVidas(vidas);
        nave.setNivelArma(progresion.getNivelArma());
        if (nave.getNivelArma() >= 3) {
            aliado = new DronEscolta(naveTexture);
        }
        crearEnemigos();
    }

    private void crearEnemigos() {
        Random random = new Random();
        float anchoMundo = Gdx.graphics.getWidth();
        float altoMundo = Gdx.graphics.getHeight();
        for (int i = 0; i < cantAsteroides; i++) {
            float size = 20f + random.nextInt(10);
            float x = random.nextFloat() * Math.max(1f, anchoMundo - size * 2f);
            float y = 65f + random.nextFloat() * Math.max(1f, altoMundo - 65f - size * 2f);
            float speedX = (velXAsteroides + random.nextInt(4)) * 42f;
            float speedY = (velYAsteroides + random.nextInt(4)) * 42f;
            if (random.nextBoolean()) {
                speedX = -speedX;
            }
            enemigos.add(new EnemigoBuilder()
                    .tipo(EnemigoBuilder.Tipo.ASTEROIDE)
                    .posicion(x, y)
                    .tamano(size * 2f)
                    .velocidad(speedX, speedY)
                    .construir(asteroidTexture));
        }

        int cantidadAliens = ronda < 2 ? 0 : Math.min(4, ronda - 1);
        for (int i = 0; i < cantidadAliens; i++) {
            float x = random.nextFloat() * Math.max(1f, anchoMundo - 48f);
            float y = altoMundo * (0.55f + random.nextFloat() * 0.35f);
            float velocidad = Math.min(190f, 85f + ronda * 14f);
            int resistencia = Math.min(4, 1 + ronda / 2);
            enemigos.add(new EnemigoBuilder()
                    .tipo(EnemigoBuilder.Tipo.ALIEN)
                    .posicion(x, y)
                    .velocidad(velocidad, velocidad)
                    .resistencia(resistencia)
                    .construir(naveTexture));
        }
    }

    private void dibujaEncabezado() {
        BitmapFont font = game.getFont();
        float alto = Gdx.graphics.getHeight();
        font.getData().setScale(1.25f);
        font.setColor(0.88f, 0.93f, 0.94f, 1f);
        font.draw(batch, "VIDAS " + nave.getVidas() + "   RONDA " + ronda
                + "   ARMA " + nave.getNombreArma(), 18f, alto - 20f);
        font.draw(batch, "RECORD " + game.getHighScore(), Gdx.graphics.getWidth() - 315f, alto - 20f);
        font.draw(batch, "PUNTOS " + score, Gdx.graphics.getWidth() - 155f, alto - 20f);
        font.draw(batch, "BATERIAS " + progresion.getBaterias() + "   R: REPARAR",
            18f, alto - 70f);
        if (tecnico != null) {
            font.draw(batch, "TECNICO EXTERIOR " + (int) Math.ceil(tiempoReparacion) + " s",
                18f, alto - 95f);
        }
        font.getData().setScale(1f);

        int restantes = progresion.getImpactosSiguienteMejora();
        String progreso = restantes == 0
                ? "MEJORA MAXIMA | IMPACTOS " + progresion.getImpactos()
                : "IMPACTOS " + progresion.getImpactos() + " | SIGUIENTE MEJORA EN " + restantes;
        font.setColor(0.49f, 0.85f, 0.79f, 1f);
        font.draw(batch, progreso, 18f, alto - 45f);
        font.setColor(1f, 1f, 1f, 1f);

        if (tiempoMensajeMejora > 0f) {
            font.getData().setScale(2f);
            font.setColor(1f, 0.78f, 0.35f, 1f);
            font.draw(batch, mensajeMejora,
                    Gdx.graphics.getWidth() / 2f - 110f, Gdx.graphics.getHeight() * 0.72f);
            font.getData().setScale(1f);
            font.setColor(1f, 1f, 1f, 1f);
        }
    }

    @Override
    public void render(float delta) {
        float frameDelta = Math.min(Math.max(delta, 0f), 0.05f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        dibujaEncabezado();

        actualizarEnemigos(frameDelta);
        if (aliado != null) {
            aliado.actualizar(frameDelta, nave.getCentroX(), nave.getCentroY(), enemigos,
                    bulletTexture, this::agregarBala);
        }
        gestionarReparacion(frameDelta);
        actualizarBalas(frameDelta);
        tiempoMensajeMejora = Math.max(0f, tiempoMensajeMejora - frameDelta);

        for (Bullet bala : balas) {
            bala.draw(batch);
        }
        nave.draw(batch, this, frameDelta);
        actualizarBaterias(frameDelta);
        for (BateriaEnergia bateria : baterias) {
            bateria.dibujar(batch);
        }
        if (aliado != null) {
            aliado.dibujar(batch);
        }
        for (int i = enemigos.size() - 1; i >= 0; i--) {
            Enemigo enemigo = enemigos.get(i);
            enemigo.dibujar(batch);
            if (nave.checkCollision(enemigo)) {
                enemigos.remove(i);
            }
        }
        batch.end();
        if (tecnico != null) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            tecnico.dibujar(shapeRenderer, nave.getCentroX(), nave.getCentroY());
            shapeRenderer.end();
        }

        if (nave.estaDestruido()) {
            if (score > game.getHighScore()) {
                game.setHighScore(score);
            }
            game.setScreen(new PantallaGameOver(game));
            dispose();
        } else if (enemigos.isEmpty()) {
            game.setScreen(new PantallaJuego(game, ronda + 1, nave.getVidas(), score,
                    velXAsteroides + 2, velYAsteroides + 2, cantAsteroides + 10));
            dispose();
        }
    }

    private void actualizarBalas(float delta) {
        for (int i = balas.size() - 1; i >= 0; i--) {
            Bullet bala = balas.get(i);
            bala.update(delta);
            for (int j = enemigos.size() - 1; j >= 0; j--) {
                Enemigo enemigo = enemigos.get(j);
                if (bala.checkCollision(enemigo)) {
                    registrarImpacto();
                    if (enemigo.estaDestruido()) {
                        score += enemigo instanceof AlienEnemigo ? 25 : 10;
                        explosionSound.play(0.5f);
                        if (random.nextFloat() < (enemigo instanceof AlienEnemigo ? 0.55f : 0.12f)) {
                            Rectangle area = enemigo.getArea();
                            baterias.add(new BateriaEnergia(area.x + area.width / 2f,
                                    area.y + area.height / 2f, bulletTexture));
                        }
                        enemigos.remove(j);
                    }
                    break;
                }
            }
            if (bala.isDestroyed()) {
                balas.remove(i);
            }
        }
    }

    private void registrarImpacto() {
        int nivelAnterior = progresion.getNivelArma();
        int nuevoNivel = progresion.registrarImpacto();
        if (nuevoNivel > nivelAnterior) {
            nave.setNivelArma(nuevoNivel);
            if (nuevoNivel >= 3 && aliado == null) {
                aliado = new DronEscolta(naveTexture);
            }
            mensajeMejora = nuevoNivel >= 3
                    ? "NUEVA MEJORA: TRIPLE + DRON"
                    : "NUEVA MEJORA: " + nave.getNombreArma();
            tiempoMensajeMejora = 2.5f;
        }
    }

    private void gestionarReparacion(float delta) {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.R) && tecnico == null) {
            if (!nave.puedeReparar()) {
                mostrarMensaje("NAVE INTEGRA: NO NECESITA REPARACION");
            } else if (!progresion.consumirBateria()) {
                mostrarMensaje("SIN BATERIAS: DESTRUYE ENEMIGOS PARA ENCONTRAR UNA");
            } else {
                tecnico = new TecnicoExtravehicular(nave.getCentroX(), nave.getCentroY());
                tiempoReparacion = 4f;
                nave.setReparando(true);
                mostrarMensaje("TECNICO FUERA: LA NAVE NO PUEDE MOVERSE NI DISPARAR");
            }
        }

        if (tecnico != null) {
            tecnico.actualizar(delta, nave.getCentroX(), nave.getCentroY());
            tiempoReparacion = Math.max(0f, tiempoReparacion - delta);
            if (tiempoReparacion == 0f) {
                nave.reparar();
                nave.setReparando(false);
                tecnico = null;
                mostrarMensaje("REPARACION COMPLETA: +1 VIDA");
            }
        }
    }

    private void actualizarBaterias(float delta) {
        Iterator<BateriaEnergia> iterator = baterias.iterator();
        while (iterator.hasNext()) {
            BateriaEnergia bateria = iterator.next();
            bateria.actualizar(delta);
            if (bateria.toca(nave.getArea()) && progresion.registrarBateria()) {
                bateria.recoger();
                iterator.remove();
                mostrarMensaje("BATERIA RECARGADA");
            }
        }
    }

    private void mostrarMensaje(String mensaje) {
        mensajeMejora = mensaje;
        tiempoMensajeMejora = 2.5f;
    }

    private void actualizarEnemigos(float delta) {
        Iterator<Enemigo> iterator = enemigos.iterator();
        while (iterator.hasNext()) {
            Enemigo enemigo = iterator.next();
            enemigo.actualizar(delta, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                    nave.getCentroX(), nave.getCentroY());
            if (enemigo.estaDestruido()) {
                iterator.remove();
            }
        }
    }

    public boolean agregarBala(Bullet bala) {
        return balas.add(bala);
    }

    @Override
    public void show() {
        gameMusic.play();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        explosionSound.dispose();
        gameMusic.dispose();
        naveTexture.dispose();
        asteroidTexture.dispose();
        bulletTexture.dispose();
        naveHitSound.dispose();
        bulletSound.dispose();
    }
}