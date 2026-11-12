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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PantallaMenu implements Screen {

    private enum Vista {
        INICIO,
        CONTROLES,
        SELECTOR
    }

    private static final Color COLOR_FONDO = new Color(0.025f, 0.055f, 0.09f, 1f);
    private static final Color COLOR_ACENTO = new Color(0.24f, 0.82f, 0.76f, 1f);
    private static final Color COLOR_SECUNDARIO = new Color(0.94f, 0.63f, 0.36f, 1f);
    private static final Color COLOR_TEXTO = new Color(0.88f, 0.93f, 0.94f, 1f);
    private static final Color COLOR_APAGADO = new Color(0.57f, 0.68f, 0.72f, 1f);

    private final SpaceNavigation game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final ShapeRenderer shapeRenderer;
    private final Texture shipTexture;
    private final Texture asteroidTexture;
    private final Rectangle playButton = new Rectangle();
    private final Rectangle controlsButton = new Rectangle();
    private final Rectangle backButton = new Rectangle();
    private final Vector3 pointer = new Vector3();
    private Vista vista = Vista.INICIO;
    private int paginaReglas;
    private int etapaSeleccionada=1;
    private final Rectangle[] etapas=crearBotonesEtapas();
    private static Rectangle[] crearBotonesEtapas(){
        Rectangle[] botones=new Rectangle[12];
        for(int i=0;i<botones.length;i++)botones[i]=new Rectangle(120f+(i%4)*245f,490f-(i/4)*130f,225f,105f);
        return botones;
    }
    private final Rectangle[] pestanas={new Rectangle(130f,618f,300f,42f),
            new Rectangle(450f,618f,300f,42f),new Rectangle(770f,618f,300f,42f)};
    private static final String[] TITULOS={"CONTROLES","MEJORAS Y RECURSOS","ENEMIGOS Y ETAPAS"};
    private static final String[][][] REGLAS={
        {
            {"WASD","Mueve la nave."},
            {"FLECHAS","Manten una flecha para disparar en esa direccion."},
            {"SHIFT","Haz un dash en la ultima direccion de movimiento para esquivar sin recibir dano."},
            {"R","Repara la nave con una bateria. Quedas inmovil mientras reparas."},
            {"1 / 2 / 3","Elige una mejora con estas teclas o haciendo clic."},
            {"P / ESC","Pausa la partida. Desde ahi puedes reiniciar o volver al menu."},
            {"AL PERDER","Vuelve a jugar con Enter o regresa al menu con Esc. Tambien puedes hacer clic."}
        },
        {
            {"INICIAL","Elige tu nave: dash, dron o criticos. Cada una tiene su especialidad."},
            {"PUNTOS","Derrota enemigos y recoge energia para subir de nivel."},
            {"MEJORAS","La partida se pausa al subir de nivel. Elige una de las tres mejoras."},
            {"ENERGIA","Las cargas celestes recargan el dash y dan puntos."},
            {"BATERIAS","Las baterias verdes permiten reparar la nave."},
            {"DASH","Esquiva ataques y golpea enemigos. Su energia se recupera con el tiempo."},
            {"RECURSOS","Los objetos que dejas en pantalla desaparecen al cambiar de etapa."}
        },
        {
            {"INSECTOS VERDES","Te persiguen para chocar contigo."},
            {"INSECTOS ROJOS","Explotan al morir. Su explosion tambien dana a otros enemigos."},
            {"CELULAS","Al morir se separan en dos celulas pequenas."},
            {"PLATILLOS","Patrullan y disparan al detenerse."},
            {"ABEJAS","Al morir explotan y lanzan proyectiles en todas direcciones."},
            {"METEORITOS","Destruyelos para obtener recursos. Evita chocar con ellos."},
            {"ETAPAS","Los enemigos llegan en grupos. Eliminalos para avanzar."},
            {"JEFES","Esquiva sus ataques y refuerzos. Cuidado con las barreras y los laseres."}
        }
    };

    public PantallaMenu(SpaceNavigation game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(MundoJuego.ANCHO, MundoJuego.ALTO, camera);
        viewport.apply();
        shapeRenderer = new ShapeRenderer();
        shipTexture = new Texture(Gdx.files.internal("MainShip3.png"));
        asteroidTexture = new Texture(Gdx.files.internal("aGreyMedium4.png"));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(COLOR_FONDO);
        viewport.apply();
        camera.update();
        updateButtonBounds();

        SpriteBatch batch = game.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if(vista==Vista.INICIO)drawDecorations(batch);
        batch.end();

        drawButtons();

        batch.begin();
        if (vista == Vista.INICIO) {
            drawHome(batch);
        } else if(vista==Vista.SELECTOR){
            drawStageSelector(batch);
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
        drawCentered(batch, "Derrota las naves, suma puntos y alcanza la siguiente ronda.",
                centerX, height * 0.64f, 1f, COLOR_APAGADO);

        drawButtonLabel(batch, playButton, "INICIAR PARTIDA", COLOR_TEXTO);
        drawButtonLabel(batch, controlsButton, "CONTROLES Y REGLAS", COLOR_TEXTO);
        drawCentered(batch, "WASD  MOVER   /   FLECHAS  DISPARAR   /   SHIFT  DASH", centerX,
                height * 0.09f, 0.95f, COLOR_APAGADO);

        if (game.getHighScore() > 0) {
            drawCentered(batch, "RECORD GUARDADO: " + game.getHighScore(), centerX,
                    height * 0.045f, 0.85f, COLOR_SECUNDARIO);
        }
    }

    private void drawControls(SpriteBatch batch) {
        float centerX=camera.viewportWidth/2f;
        drawCentered(batch,"CONTROLES Y REGLAS",centerX,720f,2.5f,COLOR_TEXTO);
        for(int i=0;i<pestanas.length;i++)
            drawCentered(batch,TITULOS[i],pestanas[i].x+pestanas[i].width/2f,644f,1.05f,
                    paginaReglas==i?COLOR_ACENTO:COLOR_APAGADO);
        String[][] filas=REGLAS[paginaReglas];
        for(int i=0;i<filas.length;i++) {
            float y=570f-i*54f;
            drawText(batch,filas[i][0],130f,y,1.05f,COLOR_ACENTO);
            BitmapFont font=game.getFont();font.getData().setScale(1f);font.setColor(COLOR_TEXTO);
            GlyphLayout layout=new GlyphLayout();
            layout.setText(font,filas[i][1],COLOR_TEXTO,730f,com.badlogic.gdx.utils.Align.left,true);
            font.draw(batch,layout,350f,y);font.setColor(Color.WHITE);
        }
        drawCentered(batch,"CLIC EN UNA PESTANA O IZQUIERDA/DERECHA PARA CAMBIAR",centerX,132f,0.9f,COLOR_APAGADO);
        drawButtonLabel(batch,playButton,"JUGAR",COLOR_TEXTO);
        drawButtonLabel(batch,backButton,"VOLVER",COLOR_TEXTO);
    }

    private void drawStageSelector(SpriteBatch batch){
        drawCentered(batch,"SELECCIONAR ETAPA",600f,710f,2.3f,COLOR_TEXTO);
        for(int i=0;i<etapas.length;i++){
            Rectangle r=etapas[i];int ronda=i+1;
            drawCentered(batch,"RONDA "+ronda,r.x+r.width/2,r.y+66f,1.3f,COLOR_TEXTO);
            String detalle=ronda%4==0?"JEFE "+(ronda/4):"OLEADA";
            drawCentered(batch,detalle,r.x+r.width/2,r.y+32f,1f,ronda%4==0?COLOR_SECUNDARIO:COLOR_APAGADO);
        }
        drawCentered(batch,"FLECHAS PARA ELEGIR / ENTER PARA JUGAR / CLIC DIRECTO",600f,160f,.95f,COLOR_APAGADO);
        drawButtonLabel(batch,backButton,"VOLVER",COLOR_TEXTO);
    }

    private void drawButtons() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if(vista==Vista.SELECTOR){
            for(int i=0;i<etapas.length;i++)drawButtonBackground(etapas[i],etapaSeleccionada==i+1?COLOR_ACENTO:COLOR_APAGADO);
            drawButtonBackground(backButton,COLOR_SECUNDARIO);
            shapeRenderer.end();return;
        }
        drawButtonBackground(playButton, COLOR_ACENTO);
        if (vista == Vista.INICIO) {
            drawButtonBackground(controlsButton, COLOR_APAGADO);
        } else {
            drawButtonBackground(backButton, COLOR_SECUNDARIO);
            for(int i=0;i<pestanas.length;i++)drawButtonBackground(pestanas[i],paginaReglas==i?COLOR_ACENTO:COLOR_APAGADO);
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
        if(vista==Vista.INICIO && Gdx.input.isKeyPressed(Input.Keys.F) && Gdx.input.isKeyPressed(Input.Keys.P)
                && (Gdx.input.isKeyJustPressed(Input.Keys.F)||Gdx.input.isKeyJustPressed(Input.Keys.P))){
            vista=Vista.SELECTOR;return;
        }
        if(vista==Vista.SELECTOR){handleStageInput();return;}
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
                viewport.unproject(pointer);
                if (playButton.contains(pointer.x, pointer.y)) {
                    startGame();
                } else if (controlsButton.contains(pointer.x, pointer.y)) {
                    vista = Vista.CONTROLES;
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            paginaReglas=(paginaReglas+TITULOS.length-1)%TITULOS.length;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            paginaReglas=(paginaReglas+1)%TITULOS.length;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            vista = Vista.INICIO;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startGame();
        } else if (Gdx.input.justTouched()) {
            pointer.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            viewport.unproject(pointer);
            if (playButton.contains(pointer.x, pointer.y)) {
                startGame();
            } else if (backButton.contains(pointer.x, pointer.y)) {
                vista = Vista.INICIO;
            } else {
                for(int i=0;i<pestanas.length;i++)if(pestanas[i].contains(pointer.x,pointer.y))paginaReglas=i;
            }
        }
    }

    private void handleStageInput(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)||Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)){
            vista=Vista.INICIO;return;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT))etapaSeleccionada=(etapaSeleccionada+10)%12+1;
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))etapaSeleccionada=etapaSeleccionada%12+1;
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP))etapaSeleccionada=(etapaSeleccionada+7)%12+1;
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN))etapaSeleccionada=(etapaSeleccionada+3)%12+1;
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){startGame(etapaSeleccionada);return;}
        if(Gdx.input.justTouched()){
            pointer.set(Gdx.input.getX(),Gdx.input.getY(),0f);viewport.unproject(pointer);
            if(backButton.contains(pointer.x,pointer.y)){vista=Vista.INICIO;return;}
            for(int i=0;i<etapas.length;i++)if(etapas[i].contains(pointer.x,pointer.y)){startGame(i+1);return;}
        }
    }

    private void startGame(){startGame(1);}
    private void startGame(int ronda) {
        ProgresionMejoras.obtenerInstancia().reiniciar();
        game.setScreen(new PantallaJuego(game, ronda, 3, 0));
        dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
