package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PantallaPausa implements Screen {
    private static final int OPCIONES = 5;

    private final SpaceNavigation game;
    private final PantallaJuego juego;
    private final PreferenciasJuego preferencias = PreferenciasJuego.obtenerInstancia();
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(MundoJuego.ANCHO, MundoJuego.ALTO, camera);
    private final ShapeRenderer shapes = new ShapeRenderer();
    private int seleccion;
    private final Rectangle[] botones=new Rectangle[OPCIONES];
    private final Vector3 puntero=new Vector3();
    private final Rectangle[] bajarVolumen={new Rectangle(722f,451f,50f,44f),new Rectangle(722f,387f,50f,44f)};
    private final Rectangle[] subirVolumen={new Rectangle(784f,451f,50f,44f),new Rectangle(784f,387f,50f,44f)};

    public PantallaPausa(SpaceNavigation game, PantallaJuego juego) {
        this.game = game;
        this.juego = juego;
        for(int i=0;i<OPCIONES;i++)botones[i]=new Rectangle(350f,511f-i*64f,500f,52f);
        viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.015f, 0.035f, 0.055f, 1f);
        viewport.apply();
        camera.update();
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.04f, 0.09f, 0.12f, 1f);
        shapes.rect(300f, 145f, 600f, 510f);
        shapes.setColor(0.24f, 0.82f, 0.76f, 1f);
        shapes.rect(300f, 645f, 600f, 5f);
        for (int indice = 0; indice < OPCIONES; indice++) {
            shapes.setColor(indice == seleccion ? 0.1f : 0.06f,
                    indice == seleccion ? 0.19f : 0.12f,
                    indice == seleccion ? 0.22f : 0.16f, 1f);
            Rectangle boton=botones[indice];
            shapes.rect(boton.x,boton.y,boton.width,boton.height);
        }
        shapes.setColor(0.12f,0.28f,0.31f,1f);
        for(int i=0;i<2;i++) {
            Rectangle menos=bajarVolumen[i],mas=subirVolumen[i];
            shapes.rect(menos.x,menos.y,menos.width,menos.height);
            shapes.rect(mas.x,mas.y,mas.width,mas.height);
        }
        shapes.end();

        SpriteBatch batch = game.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        BitmapFont font = game.getFont();
        font.getData().setScale(2.4f);
        font.setColor(0.9f, 0.96f, 0.97f, 1f);
        font.draw(batch, "PAUSA", 535f, 610f);
        font.getData().setScale(1.2f);
        font.setColor(1f, 1f, 1f, 1f);
        font.draw(batch, "REANUDAR", 385f, 520f);
        font.draw(batch, "MUSICA   " + porcentaje(preferencias.getVolumenMusica()), 385f, 456f);
        font.draw(batch, "EFECTOS   " + porcentaje(preferencias.getVolumenEfectos()), 385f, 392f);
        font.draw(batch, "REINICIAR PARTIDA", 385f, 328f);
        font.draw(batch, "VOLVER AL MENU", 385f, 264f);
        for(int i=0;i<2;i++) {
            font.draw(batch,"-",bajarVolumen[i].x+18f,bajarVolumen[i].y+30f);
            font.draw(batch,"+",subirVolumen[i].x+18f,subirVolumen[i].y+30f);
        }
        font.getData().setScale(0.9f);
        font.setColor(0.58f, 0.72f, 0.75f, 1f);
        font.draw(batch, "WASD: MOVER   FLECHAS: DISPARAR   SHIFT: DASH", 365f, 190f);
        font.draw(batch, "CLIC O ARRIBA/ABAJO: ELEGIR   ENTER: CONFIRMAR",350f,115f);
        font.draw(batch, "CLIC EN - / + O IZQUIERDA/DERECHA: VOLUMEN",350f,90f);
        font.setColor(Color.WHITE);
        font.getData().setScale(1f);
        batch.end();

        gestionarEntrada();
    }

    private void gestionarEntrada() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)
                || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            reanudar();
            return;
        }
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            puntero.set(Gdx.input.getX(),Gdx.input.getY(),0f);viewport.unproject(puntero);
            for(int i=0;i<2;i++) {
                float ajuste=bajarVolumen[i].contains(puntero.x,puntero.y)?-0.1f
                        :subirVolumen[i].contains(puntero.x,puntero.y)?0.1f:0f;
                if(ajuste!=0f) {
                    seleccion=i+1;
                    if(i==0)cambiarMusica(ajuste);else cambiarEfectos(ajuste);
                    return;
                }
            }
            for(int i=0;i<OPCIONES;i++)if(botones[i].contains(puntero.x,puntero.y)) {
                seleccion=i;activarSeleccion();return;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            seleccion = (seleccion + OPCIONES - 1) % OPCIONES;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            seleccion = (seleccion + 1) % OPCIONES;
        } else if (seleccion == 1 && Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            cambiarMusica(-0.1f);
        } else if (seleccion == 1 && Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            cambiarMusica(0.1f);
        } else if (seleccion == 2 && Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            cambiarEfectos(-0.1f);
        } else if (seleccion == 2 && Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            cambiarEfectos(0.1f);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            activarSeleccion();
        }
    }

    private void cambiarMusica(float delta) {
        preferencias.setVolumenMusica(preferencias.getVolumenMusica() + delta);
        juego.setVolumenMusica(preferencias.getVolumenMusica());
    }

    private void cambiarEfectos(float delta) {
        preferencias.setVolumenEfectos(preferencias.getVolumenEfectos() + delta);
    }

    private void activarSeleccion() {
        if (seleccion == 0) {
            reanudar();
        } else if (seleccion == 3) {
            ProgresionMejoras.obtenerInstancia().reiniciar();
            game.setScreen(new PantallaJuego(game,1,3,0));
            juego.dispose();dispose();
        } else if (seleccion == 4) {
            juego.dispose();
            dispose();
            game.setScreen(new PantallaMenu(game));
        }
    }

    private void reanudar() {
        game.setScreen(juego);
        dispose();
    }

    private int porcentaje(float valor) {
        return Math.round(valor * 100f);
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        shapes.dispose();
    }
}