package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class PantallaMenu implements Screen {

    private enum Vista {
        INICIO,
        CONTROLES
    }

    private static final Color COLOR_FONDO = new Color(0.025f, 0.055f, 0.09f, 1f);
    private static final Color COLOR_ACENTO = new Color(0.24f, 0.82f, 0.76f, 1f);
    private static final Color COLOR_SECUNDARIO = new Color(0.94f, 0.63f, 0.36f, 1f);
    private static final Color COLOR_TEXTO = new Color(0.88f, 0.93f, 0.94f, 1f);
    private static final Color COLOR_APAGADO = new Color(0.57f, 0.68f, 0.72f, 1f);

    private final SpaceNavigation game;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    private final Texture shipTexture;
    private final Texture asteroidTexture;
    private final Rectangle playButton = new Rectangle();
    private final Rectangle controlsButton = new Rectangle();
    private final Rectangle backButton = new Rectangle();
    private final Vector3 pointer = new Vector3();
    private Vista vista = Vista.INICIO;

    public PantallaMenu(SpaceNavigation game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer = new ShapeRenderer();
        shipTexture = new Texture(Gdx.files.internal("MainShip3.png"));
        asteroidTexture = new Texture(Gdx.files.internal("aGreyMedium4.png"));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(COLOR_FONDO);
        camera.update();
        updateButtonBounds();

        SpriteBatch batch = game.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawDecorations(batch);
        batch.end();

        drawButtons();

        batch.begin();
        if (vista == Vista.INICIO) {
            drawHome(batch);
        } else {
            drawControls(batch);
        }
        batch.end();

        handleInput();
    }

    private void drawDecorations(SpriteBatch batch) {
        float width = camera.viewportWidth;
        float height = camera.viewportHeight;
        float scale = Math.min(width / 1200f, height / 800f);

        batch.setColor(COLOR_ACENTO.r, COLOR_ACENTO.g, COLOR_ACENTO.b, 0.7f);
        batch.draw(shipTexture, width * 0.13f, height * 0.43f, 88f * scale, 74f * scale);
        batch.setColor(COLOR_SECUNDARIO.r, COLOR_SECUNDARIO.g, COLOR_SECUNDARIO.b, 0.52f);
        batch.draw(asteroidTexture, width * 0.75f, height * 0.42f, 132f * scale, 122f * scale);
        batch.setColor(Color.WHITE);
    }

    private void drawHome(SpriteBatch batch) {
        float centerX = camera.viewportWidth / 2f;
        float height = camera.viewportHeight;

        drawCentered(batch, "SPACE NAVIGATION", centerX, height * 0.78f, 3.2f, COLOR_TEXTO);
        drawCentered(batch, "SOBREVIVE A LA OLEADA", centerX, height * 0.70f, 1.25f, COLOR_ACENTO);
        drawCentered(batch, "Despeja el campo, suma puntos y alcanza la siguiente ronda.",
                centerX, height * 0.64f, 1f, COLOR_APAGADO);

        drawButtonLabel(batch, playButton, "INICIAR PARTIDA", COLOR_TEXTO);
        drawButtonLabel(batch, controlsButton, "CONTROLES Y REGLAS", COLOR_TEXTO);
        drawCentered(batch, "FLECHAS  MOVER   /   ESPACIO  DISPARAR", centerX,
                height * 0.09f, 0.95f, COLOR_APAGADO);

        if (game.getHighScore() > 0) {
            drawCentered(batch, "RECORD DE SESION: " + game.getHighScore(), centerX,
                    height * 0.045f, 0.85f, COLOR_SECUNDARIO);
        }
    }

    private void drawControls(SpriteBatch batch) {
        float centerX = camera.viewportWidth / 2f;
        float width = camera.viewportWidth;
        float height = camera.viewportHeight;

        drawCentered(batch, "CONTROLES Y REGLAS", centerX, height * 0.82f, 2.5f, COLOR_TEXTO);
        drawCentered(batch, "Controles sencillos, reflejos y una nave que no se detiene.",
                centerX, height * 0.74f, 1f, COLOR_APAGADO);

        drawControlRow(batch, "FLECHAS", "Mantener pulsado mueve la nave en esa direccion.",
                width * 0.25f, width * 0.43f, height * 0.61f);
        drawControlRow(batch, "ESPACIO", "Dispara hacia arriba; apunta y evita los asteroides.",
                width * 0.25f, width * 0.43f, height * 0.52f);
        drawControlRow(batch, "ENTER / CLIC", "Inicia la partida desde este menu.",
                width * 0.25f, width * 0.43f, height * 0.43f);

        drawText(batch, "OBJETIVO", width * 0.25f, height * 0.33f, 1.1f, COLOR_SECUNDARIO);
        drawText(batch, "Empiezas con 3 vidas; cada asteroide destruido suma 10 puntos.",
                width * 0.25f, height * 0.28f, 0.95f, COLOR_TEXTO);
        drawText(batch, "Al despejar la pantalla comienza otra ronda con mas asteroides y velocidad.",
                width * 0.25f, height * 0.23f, 0.95f, COLOR_TEXTO);
        drawText(batch, "Desde la ronda 2, los aliens persiguen la nave y requieren varios impactos.",
                width * 0.25f, height * 0.18f, 0.95f, COLOR_APAGADO);
        drawText(batch, "3 impactos: disparo doble. 7 impactos: triple y dron de apoyo.",
            width * 0.25f, height * 0.14f, 0.95f, COLOR_ACENTO);

        drawButtonLabel(batch, playButton, "JUGAR", COLOR_TEXTO);
        drawButtonLabel(batch, backButton, "VOLVER", COLOR_TEXTO);
    }

    private void drawControlRow(SpriteBatch batch, String key, String description,
                                float keyX, float descriptionX, float y) {
        drawText(batch, key, keyX, y, 1.15f, COLOR_ACENTO);
        drawText(batch, description, descriptionX, y, 1f, COLOR_TEXTO);
    }

    private void drawButtons() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawButtonBackground(playButton, COLOR_ACENTO);
        if (vista == Vista.INICIO) {
            drawButtonBackground(controlsButton, COLOR_APAGADO);
        } else {
            drawButtonBackground(backButton, COLOR_SECUNDARIO);
        }
        shapeRenderer.end();
    }

    private void drawButtonBackground(Rectangle bounds, Color accent) {
        shapeRenderer.setColor(0.10f, 0.17f, 0.21f, 1f);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.setColor(accent);
        shapeRenderer.rect(bounds.x, bounds.y, 5f, bounds.height);
    }

    private void drawButtonLabel(SpriteBatch batch, Rectangle bounds, String label, Color color) {
        drawCentered(batch, label, bounds.x + bounds.width / 2f,
                bounds.y + bounds.height / 2f - 7f, 1.15f, color);
    }

    private void drawCentered(SpriteBatch batch, String text, float centerX, float y,
                              float scale, Color color) {
        BitmapFont font = game.getFont();
        font.getData().setScale(scale);
        font.setColor(color);
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(batch, layout, centerX - layout.width / 2f, y);
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
    }

    private void drawText(SpriteBatch batch, String text, float x, float y,
                          float scale, Color color) {
        BitmapFont font = game.getFont();
        font.getData().setScale(scale);
        font.setColor(color);
        font.draw(batch, text, x, y);
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
    }

    private void updateButtonBounds() {
        float width = camera.viewportWidth;
        float height = camera.viewportHeight;
        float buttonWidth = Math.min(300f, width * 0.38f);
        float buttonHeight = Math.max(48f, height * 0.07f);
        float centerX = width / 2f;

        if (vista == Vista.INICIO) {
            playButton.set(centerX - buttonWidth / 2f, height * 0.30f, buttonWidth, buttonHeight);
            controlsButton.set(centerX - buttonWidth / 2f, height * 0.19f, buttonWidth, buttonHeight);
        } else {
            float gap = Math.min(24f, width * 0.03f);
            playButton.set(centerX - buttonWidth - gap / 2f, height * 0.055f, buttonWidth, buttonHeight);
            backButton.set(centerX + gap / 2f, height * 0.055f, buttonWidth, buttonHeight);
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (vista == Vista.CONTROLES) {
                vista = Vista.INICIO;
            } else {
                Gdx.app.exit();
            }
            return;
        }

        if (vista == Vista.INICIO) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
                vista = Vista.CONTROLES;
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                startGame();
            } else if (Gdx.input.justTouched()) {
                pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
                camera.unproject(pointer);
                if (playButton.contains(pointer.x, pointer.y)) {
                    startGame();
                } else if (controlsButton.contains(pointer.x, pointer.y)) {
                    vista = Vista.CONTROLES;
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            vista = Vista.INICIO;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startGame();
        } else if (Gdx.input.justTouched()) {
            pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            camera.unproject(pointer);
            if (playButton.contains(pointer.x, pointer.y)) {
                startGame();
            } else if (backButton.contains(pointer.x, pointer.y)) {
                vista = Vista.INICIO;
            }
        }
    }

    private void startGame() {
        ProgresionMejoras.obtenerInstancia().reiniciar();
        game.setScreen(new PantallaJuego(game, 1, 3, 0, 1, 1, 10));
        dispose();
    }

    @Override
    public void show() {
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
        shipTexture.dispose();
        asteroidTexture.dispose();
    }
}