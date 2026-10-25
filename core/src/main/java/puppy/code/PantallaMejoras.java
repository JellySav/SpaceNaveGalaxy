package puppy.code;

import java.util.List;
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** No simula la partida: cada eleccion consume exactamente una subida pendiente. */
public final class PantallaMejoras extends ScreenAdapter {
    private final SpaceNavigation game;
    private final PantallaJuego juego;
    private final ProgresionMejoras progreso=ProgresionMejoras.obtenerInstancia();
    private final Random random=new Random();
    private final OrthographicCamera camera=new OrthographicCamera();
    private final FitViewport viewport=new FitViewport(MundoJuego.ANCHO,MundoJuego.ALTO,camera);
    private final ShapeRenderer formas=new ShapeRenderer();
    private List<TipoMejora> opciones;
    private boolean inicial;
    private final Rectangle[] tarjetas={new Rectangle(130f,155f,300f,385f),
            new Rectangle(450f,155f,300f,385f),new Rectangle(770f,155f,300f,385f)};
    private final Vector3 puntero=new Vector3();

    public PantallaMejoras(SpaceNavigation game,PantallaJuego juego) {
        this(game,juego,false);
    }
    public PantallaMejoras(SpaceNavigation game,PantallaJuego juego,boolean inicial) {
        this.game=game;this.juego=juego;this.inicial=inicial;
        opciones=inicial?CatalogoMejoras.iniciales():CatalogoMejoras.sortear(progreso,random);
        viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),true);
    }
    @Override public void render(float delta) {
        puntero.set(Gdx.input.getX(),Gdx.input.getY(),0f);viewport.unproject(puntero);
        int sobreTarjeta=tarjetaEn(puntero.x,puntero.y);
        juego.dibujarEscenaCongelada();
        viewport.apply();camera.update();formas.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeRenderer.ShapeType.Filled);
        formas.setColor(0f,0f,0f,0.78f);formas.rect(0f,0f,1200f,800f);
        formas.setColor(0.86f,0.88f,0.92f,1f);formas.rect(96f,95f,1008f,610f);
        formas.setColor(0.06f,0.075f,0.1f,1f);formas.rect(101f,100f,998f,600f);
        for(int i=0;i<3;i++) {
            RarezaMejora rareza=opciones.get(i).rareza;
            float x=tarjetas[i].x;
            formas.setColor(rareza.r,rareza.g,rareza.b,1f);formas.rect(x,155f,300f,385f);
            formas.setColor(i==sobreTarjeta?0.16f:0.09f,i==sobreTarjeta?0.18f:0.105f,i==sobreTarjeta?0.23f:0.14f,1f);formas.rect(x+4f,159f,292f,377f);
            formas.setColor(rareza.r,rareza.g,rareza.b,0.16f);formas.rect(x+4f,480f,292f,56f);
        }
        formas.end();Gdx.gl.glDisable(GL20.GL_BLEND);
        SpriteBatch batch=game.getBatch();batch.setProjectionMatrix(camera.combined);batch.begin();
        BitmapFont font=game.getFont();font.setColor(Color.WHITE);
        texto(font,batch,inicial?"Elige tu mejora inicial":"Subiste de nivel!",120f,660f,960f,2.1f);
        texto(font,batch,"Elige una mejora",120f,612f,960f,1.35f);
        for(int i=0;i<3;i++) {
            TipoMejora opcion=opciones.get(i);RarezaMejora rareza=opcion.rareza;
            float x=140f+i*320f;
            font.setColor(rareza.r,rareza.g,rareza.b,1f);
            texto(font,batch,rareza.nombre,x,514f,280f,1.2f);
            font.setColor(Color.WHITE);
            texto(font,batch,opcion.titulo(progreso),x+10f,437f,260f,1.4f);
            font.setColor(0.78f,0.82f,0.9f,1f);
            texto(font,batch,opcion.descripcion(progreso),x+14f,327f,252f,1.05f);
            font.setColor(rareza.r,rareza.g,rareza.b,1f);
            texto(font,batch,"[ "+(i+1)+" ]",x,210f,280f,1.65f);
        }
        font.setColor(0.7f,0.75f,0.82f,1f);
        texto(font,batch,"Haz clic o pulsa 1, 2 o 3  |  Partida en pausa",120f,126f,960f,0.95f);
        font.getData().setScale(1f);font.setColor(Color.WHITE);batch.end();
        int eleccion=Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)||Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)?0
                :Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)||Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)?1
                :Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)||Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)?2:-1;
        if(eleccion<0 && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))eleccion=sobreTarjeta;
        if(eleccion>=0 && progreso.elegirMejora(opciones.get(eleccion))) {
            juego.sincronizarMejoras();
            inicial=false;
            if(progreso.hayMejorasPendientes())opciones=CatalogoMejoras.sortear(progreso,random);
            else {game.setScreen(juego);dispose();}
        }
    }
    private int tarjetaEn(float x,float y) {
        for(int i=0;i<tarjetas.length;i++)if(tarjetas[i].contains(x,y))return i;
        return -1;
    }
    private void texto(BitmapFont font,SpriteBatch batch,String texto,float x,float y,float ancho,float escala) {
        font.getData().setScale(escala);
        GlyphLayout layout=new GlyphLayout();layout.setText(font,texto,font.getColor(),ancho,Align.center,true);
        font.draw(batch,layout,x,y);
    }
    @Override public void resize(int width,int height){viewport.update(width,height,true);}
    @Override public void dispose(){formas.dispose();}
}
