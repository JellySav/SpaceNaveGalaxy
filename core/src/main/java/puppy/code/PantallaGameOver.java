package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PantallaGameOver extends ScreenAdapter {
    private final SpaceNavigation game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(MundoJuego.ANCHO, MundoJuego.ALTO, camera);
    private final ShapeRenderer formas = new ShapeRenderer();
    private final Rectangle boton = new Rectangle(390f, 300f, 420f, 64f);
    private final Rectangle botonMenu=new Rectangle(390f,210f,420f,64f);
    private final Vector3 puntero = new Vector3();
    private final ConfirmacionReinicio confirmacion = new ConfirmacionReinicio();

    public PantallaGameOver(SpaceNavigation game) { this.game = game; }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.015f, 0.025f, 0.045f, 1f);
        viewport.apply();camera.update();
        formas.setProjectionMatrix(camera.combined);
        formas.begin(ShapeRenderer.ShapeType.Filled);
        formas.setColor(0.06f, 0.09f, 0.13f, 1f);
        formas.rect(260f, 145f, 680f, 420f);
        formas.setColor(0.9f, 0.22f, 0.2f, 1f);
        formas.rect(260f, 560f, 680f, 5f);
        formas.setColor(confirmacion.estaPreparada() ? 0.12f : 0.08f, 0.18f, 0.23f, 1f);
        formas.rect(boton.x, boton.y, boton.width, boton.height);
        formas.rect(botonMenu.x,botonMenu.y,botonMenu.width,botonMenu.height);
        formas.end();

        SpriteBatch batch = game.getBatch();BitmapFont font = game.getFont();
        batch.setProjectionMatrix(camera.combined);batch.begin();
        font.setColor(1f, 0.35f, 0.3f, 1f);font.getData().setScale(2.6f);
        font.draw(batch, "NAVE DESTRUIDA", 0f, 485f, MundoJuego.ANCHO, Align.center, false);
        font.setColor(Color.WHITE);font.getData().setScale(1.3f);
        font.draw(batch, "La partida ha terminado", 0f, 420f, MundoJuego.ANCHO, Align.center, false);
        font.draw(batch, "VOLVER A JUGAR", boton.x, boton.y + 40f, boton.width, Align.center, false);
        font.draw(batch,"MENU PRINCIPAL",botonMenu.x,botonMenu.y+40f,botonMenu.width,Align.center,false);
        font.setColor(0.65f, 0.75f, 0.8f, 1f);font.getData().setScale(1f);
        font.draw(batch, confirmacion.estaPreparada() ? "ENTER: VOLVER A JUGAR / ESC: MENU PRINCIPAL" : "Espera un momento...",
                0f, 180f, MundoJuego.ANCHO, Align.center, false);
        font.setColor(Color.WHITE);font.getData().setScale(1f);batch.end();

        boolean enter = Gdx.input.isKeyPressed(Input.Keys.ENTER);
        boolean raton = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        puntero.set(Gdx.input.getX(), Gdx.input.getY(), 0f);viewport.unproject(puntero);
        boolean clicBoton = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && boton.contains(puntero.x, puntero.y);
        boolean clicMenu=Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && botonMenu.contains(puntero.x,puntero.y);
        boolean volverMenu=Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)||clicMenu;
        boolean nuevaConfirmacion = Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || clicBoton || volverMenu;
        if (confirmacion.actualizar(delta, enter || raton || Gdx.input.isKeyPressed(Input.Keys.ESCAPE), nuevaConfirmacion)) {
            ProgresionMejoras.obtenerInstancia().reiniciar();
            game.setScreen(volverMenu?new PantallaMenu(game):new PantallaJuego(game,1,3,0));
            dispose();
        }
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void dispose() { formas.dispose(); }

    /** Exige esperar y soltar la confirmacion antes de aceptar una pulsacion nueva. */
    static final class ConfirmacionReinicio {
        private float espera = 0.8f;
        private boolean preparada;
        boolean actualizar(float delta, boolean pulsado, boolean nuevaConfirmacion) {
            espera = Math.max(0f, espera - Math.max(0f, delta));
            if (espera > 0f) return false;
            if (!pulsado) preparada = true;
            return preparada && nuevaConfirmacion;
        }
        boolean estaPreparada() { return preparada; }
    }
}
