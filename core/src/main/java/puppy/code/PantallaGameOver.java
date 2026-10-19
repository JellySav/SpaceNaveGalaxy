package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class PantallaGameOver implements Screen {

	private SpaceNavigation game;
	private OrthographicCamera camera;
	private Viewport viewport;

	public PantallaGameOver(SpaceNavigation game) {
		this.game = game;
        
		camera = new OrthographicCamera();
		viewport = new FitViewport(MundoJuego.ANCHO, MundoJuego.ALTO, camera);
		viewport.apply();
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);

		viewport.apply();
		camera.update();
		game.getBatch().setProjectionMatrix(camera.combined);

		game.getBatch().begin();
		game.getFont().draw(game.getBatch(), "Game Over !!! ", 120, 400,400,1,true);
		game.getFont().draw(game.getBatch(), "Pincha en cualquier lado para reiniciar ...", 100, 300);
	
		game.getBatch().end();

		if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
			ProgresionMejoras.obtenerInstancia().reiniciar();
			Screen ss = new PantallaJuego(game,1,3,0,1,1,10);
			ss.resize(1200, 800);
			game.setScreen(ss);
			dispose();
		}
	}
 
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		camera.update();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
   
}