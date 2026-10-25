package puppy.code;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PantallaJuego implements Screen {
    private static final int CANTIDAD_ESTRELLAS = 90;

    private final SpaceNavigation game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final Sound explosionSound;
    private final Music gameMusic;
    private final Texture naveTexture;
    private final AparienciasEnemigas aparienciasEnemigas;
    private final AparienciasNave aparienciasNave;
    private final AparienciasProyectiles aparienciasProyectiles;
    private final Texture asteroidTexture;
    private final Texture bulletTexture;
    private final Texture texturaProyectilHostil;
    private final TextureRegion regionProyectilHostil;
    private final Texture texturaCritica;
    private final Sound naveHitSound;
    private final Sound bulletSound;
    private final List<Enemigo> enemigos = new ArrayList<>();
    private final SistemaExplosiones explosiones = new SistemaExplosiones();
    private final ColisionesNaves colisionesNaves = new ColisionesNaves();
    private final List<Bullet> balas = new ArrayList<>();
    private final List<ProyectilHostil> proyectilesHostiles = new ArrayList<>();
    private final IndiceEspacial indiceEnemigos = new IndiceEspacial(128f);
    private final List<BateriaEnergia> baterias = new ArrayList<>();
    private final List<CargaEnergia> cargasEnergia = new ArrayList<>();
    private final ProgresionMejoras progresion = ProgresionMejoras.obtenerInstancia();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Random random = new Random();
    private final float[] estrellasX = new float[CANTIDAD_ESTRELLAS];
    private final float[] estrellasY = new float[CANTIDAD_ESTRELLAS];

    private final int ronda;
    private final SectorGalactico sector;
    private final LluviaMeteoritos lluviaMeteoritos;
    private final LlegadaNaves llegadaNaves;
    private int score;
    private Nave4 nave;
    private JefeSector jefeSector;
    private ApoyoNave aliado;
    private ApoyoNave aliadoExplosivo;
    private DronEscolta miniDron;
    private DronLaser dronLaser;
    private final List<ExplosionBala> explosionesBalas=new ArrayList<>();
    private TecnicoExtravehicular tecnico;
    private float tiempoMensajeMejora;
    private float tiempoMensajeSector;
    private float tiempoReparacion;
    private String mensajeMejora = "";

    public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score) {
        this(game, ronda, vidas, score, new EnergiaDash(), 0f, 0f, 0f, 0f);
    }

    private PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,
                         EnergiaDash energiaDash, float tiempoDronDesactivado, float tiempoDronExplosivo, float tiempoMiniDron, float tiempoDronLaser) {
        this.game = game;
        this.ronda = ronda;
        this.sector = SectorGalactico.desdeRonda(ronda);
        this.score = score;
        this.llegadaNaves = new LlegadaNaves(ronda, random);
        this.lluviaMeteoritos = new LluviaMeteoritos(ronda, sector.esRondaDeJefe(ronda), random);
        if (sector.iniciaEnRonda(ronda)) {
            tiempoMensajeSector = 6f;
        }

        batch = game.getBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(MundoJuego.ANCHO, MundoJuego.ALTO, camera);
        viewport.apply();
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(game.getPreferencias().getVolumenMusica());

        naveTexture = new Texture(Gdx.files.internal("MainShip3.png"));
        aparienciasEnemigas=new AparienciasEnemigas();
        aparienciasNave=new AparienciasNave();
        aparienciasProyectiles=new AparienciasProyectiles();
        bulletTexture = new Texture(Gdx.files.internal("Rocket2.png"));
        Pixmap imagenRayo=new Pixmap(Gdx.files.internal("rayoenemigo.png"));
        int minX=imagenRayo.getWidth(),minY=imagenRayo.getHeight(),maxX=-1,maxY=-1;
        for(int y=0;y<imagenRayo.getHeight();y++)for(int x=0;x<imagenRayo.getWidth();x++) {
            if((imagenRayo.getPixel(x,y)&255)==0)continue;
            minX=Math.min(minX,x);minY=Math.min(minY,y);maxX=Math.max(maxX,x);maxY=Math.max(maxY,y);
        }
        texturaProyectilHostil=new Texture(imagenRayo);
        regionProyectilHostil=maxX>=0?new TextureRegion(texturaProyectilHostil,minX,minY,maxX-minX+1,maxY-minY+1)
                :new TextureRegion(texturaProyectilHostil);
        imagenRayo.dispose();
        texturaCritica = crearTexturaCritica();
        asteroidTexture = new Texture(Gdx.files.internal("aGreyMedium4.png"));
        naveHitSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3"));

        crearCampoEstelar(MundoJuego.ANCHO, MundoJuego.ALTO);
        nave = new Nave4((int) (MundoJuego.ANCHO / 2f - 22f), 30, naveTexture,
                naveHitSound, bulletTexture, bulletSound);
        nave.setVidas(vidas);
        nave.setEnergiaDash(energiaDash);
        nave.setNivelArma(progresion.getNivelArma());
        sincronizarMejoras();
        if(aliado!=null)aliado.desactivar(tiempoDronDesactivado);
        if(aliadoExplosivo!=null)aliadoExplosivo.desactivar(tiempoDronExplosivo);
        if(miniDron!=null)miniDron.desactivar(tiempoMiniDron);
        if(dronLaser!=null)dronLaser.desactivar(tiempoDronLaser);
        crearEnemigos();
    }

    /** Conserva la silueta y transparencia sin que los colores originales apaguen el amarillo. */
    private Texture crearTexturaCritica() {
        Pixmap imagen = new Pixmap(Gdx.files.internal("Rocket2.png"));
        imagen.setBlending(Pixmap.Blending.None);
        for(int y=0;y<imagen.getHeight();y++)for(int x=0;x<imagen.getWidth();x++)
            imagen.drawPixel(x,y,0xffffff00 | (imagen.getPixel(x,y)&0xff));
        Texture textura = new Texture(imagen);
        imagen.dispose();return textura;
    }

    private void crearEnemigos() {
        float anchoMundo = MundoJuego.ANCHO;
        float altoMundo = MundoJuego.ALTO;
                if (sector.esRondaDeJefe(ronda)) {
                    float tamanoJefe = Math.min(150f, anchoMundo * 0.2f);
                    float xJefe = (anchoMundo - tamanoJefe) / 2f;
                    float yJefe = sector==SectorGalactico.CINTURON_DE_VELA
                            ? (altoMundo-tamanoJefe)/2f : altoMundo-tamanoJefe-105f;
                    int resistenciaJefe = sector.getResistenciaJefe(ronda);
                    jefeSector = (JefeSector) new EnemigoBuilder()
                        .tipo(EnemigoBuilder.Tipo.JEFE)
                        .posicion(xJefe, yJefe)
                        .tamano(tamanoJefe)
                        .velocidad(34f * sector.getFactorDificultad(), 0f)
                        .resistencia(resistenciaJefe)
                        .sector(sector)
                        .construir(naveTexture, bulletTexture);
                    jefeSector.configurarVidaParaRonda(ronda);
                    enemigos.add(jefeSector);
                    tiempoMensajeSector = 6f;
                    mensajeMejora = "JEFE DE SECTOR: " + jefeSector.getNombre();
                    tiempoMensajeMejora = 6f;
                }
    }

    private void dibujaEncabezado() {
        BitmapFont font = game.getFont();
        float alto = MundoJuego.ALTO;
        font.getData().setScale(1.25f);
        font.setColor(0.88f, 0.93f, 0.94f, 1f);
        font.draw(batch, "VIDAS " + nave.getVidas() + "/" + nave.getVidasMaximas() + "   RONDA " + ronda,
                18f, alto - 20f);
        font.draw(batch, "RECORD " + game.getHighScore(), MundoJuego.ANCHO - 315f, alto - 20f);
        font.draw(batch, "PUNTOS " + score, MundoJuego.ANCHO - 155f, alto - 20f);
        font.draw(batch, "BATERIAS " + progresion.getBaterias() + "   R: REPARAR",
            18f, alto - 70f);
        font.draw(batch, "NIVEL " + progresion.getNivel(), 18f, alto - 95f);
        font.draw(batch, "SECTOR " + sector.getNombre().toUpperCase(), 18f, alto - 120f);
        font.getData().setScale(1f);
        font.setColor(0.75f,0.84f,0.87f,1f);
        font.draw(batch,"DANO: " + numeroHud(progresion.getDano()),18f,alto-155f);
        font.draw(batch,"CADENCIA: " + numeroHud(progresion.getCadencia()),18f,alto-177f);
        font.draw(batch,"VELOCIDAD DE MOVIMIENTO: " + Math.round(progresion.getVelocidad()),18f,alto-199f);
        font.draw(batch,"PROB CRIT: " + Math.round(progresion.getProbabilidadCritico()*100f) + "%",18f,alto-221f);
        font.draw(batch,"DANO CRIT: " + Math.round((progresion.getMultiplicadorCritico()-1f)*100f) + "%",18f,alto-243f);
        font.getData().setScale(1.25f);
        font.setColor(0.88f,0.93f,0.94f,1f);
        if (jefeSector != null && jefeSector.estaActivo()) {
            font.setColor(1f, 0.55f, 0.42f, 1f);
            dibujarTextoCentrado(font, jefeSector.getNombre(), alto - 65f, 1.1f, 420f);
            font.setColor(0.88f, 0.93f, 0.94f, 1f);
        }
        if (tecnico != null) {
            font.draw(batch, "TECNICO EXTERIOR " + (int) Math.ceil(tiempoReparacion) + " s",
                18f, alto - 280f);
        }
        if (aliado != null && aliado.estaDesactivado()) {
            font.setColor(1f, 0.55f, 0.4f, 1f);
            font.draw(batch, "DRON DESACTIVADO "
                    + (int) Math.ceil(aliado.getTiempoDesactivado()) + " s", 18f, alto - 305f);
        }
        font.getData().setScale(1f);

        if (progresion.esInvulnerablePruebas()) {
            font.getData().setScale(0.85f);
            font.setColor(0.65f, 0.9f, 1f, 1f);
            font.draw(batch, "PRUEBA: INMORTAL", 18f, alto - 335f);
            font.getData().setScale(1f);
        }

        String progreso = "SIGUIENTE NIVEL: " + progresion.getExperiencia() + "/" + progresion.getRequisitoNivel() + " PUNTOS";
        font.setColor(0.49f, 0.85f, 0.79f, 1f);
        font.draw(batch, progreso, 18f, alto - 45f);
        font.setColor(1f, 1f, 1f, 1f);

        EnergiaDash energia = nave.getEnergiaDash();
        String estadoDash = nave.isReparando() ? "REPARANDO"
                : energia.puedeUsar() ? "LISTO" : energia.getEnergia() < energia.getCoste()
                ? "SIN ENERGIA" : "RECARGANDO";
        font.setColor(0.35f, 0.9f, 1f, 1f);
        font.draw(batch, "ENERGIA " + Math.round(energia.getEnergia()) + "/100  SHIFT: DASH (" + Math.round(energia.getCoste()) + ")  "
                + estadoDash, 18f, 58f);
        font.setColor(1f, 1f, 1f, 1f);

        if (tiempoMensajeMejora > 0f) {
            font.setColor(1f, 0.78f, 0.35f, 1f);
            dibujarTextoCentrado(font, mensajeMejora, alto * 0.55f, 1.6f,
                    MundoJuego.ANCHO - 72f);
        }
        if (tiempoMensajeSector > 0f) {
            font.setColor(0.55f, 0.9f, 1f, 1f);
            dibujarTextoCentrado(font, "NUEVA ETAPA: " + sector.getNombre(),
                    alto * 0.46f, 1.5f, MundoJuego.ANCHO - 72f);
            dibujarTextoCentrado(font, sector.getNarrativa(), alto * 0.41f,
                    1f, MundoJuego.ANCHO - 72f);
        }
        font.getData().setScale(1f);
        font.setColor(1f, 1f, 1f, 1f);
    }

    private String numeroHud(float valor) {
        String numero=String.format(java.util.Locale.ROOT,"%.2f",valor);
        while(numero.endsWith("0"))numero=numero.substring(0,numero.length()-1);
        if(numero.endsWith("."))numero=numero.substring(0,numero.length()-1);
        return numero.replace('.',',');
    }

    private void dibujarTextoCentrado(BitmapFont font, String texto, float y,
                                      float escala, float anchoMaximo) {
        float escalaAnteriorX = font.getData().scaleX;
        float escalaAnteriorY = font.getData().scaleY;
        font.getData().setScale(escala);
        GlyphLayout textoMedido = new GlyphLayout();
        textoMedido.setText(font, texto, font.getColor(), anchoMaximo, Align.center, true);
        font.draw(batch, textoMedido, (MundoJuego.ANCHO - anchoMaximo) / 2f, y);
        font.getData().setScale(escalaAnteriorX, escalaAnteriorY);
    }

    @Override
    public void render(float delta) {
        if(!progresion.tieneMejoraInicial()) {
            game.setScreen(new PantallaMejoras(game,this,true));return;
        }
        gestionarNivelPruebas();
        if(!nave.estaDestruido() && progresion.hayMejorasPendientes()) {
            game.setScreen(new PantallaMejoras(game,this));
            return;
        }
        // Pausar antes de simular evita consumir energia o avanzar tiempos al pausar.
        if (!nave.estaDestruido() && (Gdx.input.isKeyJustPressed(Input.Keys.P)
                || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))) {
            game.setScreen(new PantallaPausa(game, this));
            return;
        }
        float frameDelta = Math.min(Math.max(delta, 0f), 0.05f);
        tiempoMensajeSector = Math.max(0f, tiempoMensajeSector - frameDelta);
        Gdx.gl.glClearColor(sector.getFondoRojo(), sector.getFondoVerde(),
            sector.getFondoAzul(), 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        dibujarCampoEstelar();
        batch.begin();

        gestionarReparacion(frameDelta);
        nave.actualizar(this, frameDelta);
        llegadaNaves.actualizar(frameDelta, enemigos, naveTexture);
        for(Enemigo enemigo:enemigos)aparienciasEnemigas.aplicar(enemigo);
        actualizarEnemigos(frameDelta);
        actualizarDanoDash();
        lluviaMeteoritos.actualizar(frameDelta, enemigos, asteroidTexture,nave.getArea());
        explosiones.actualizar(frameDelta, enemigos, nave, apoyosCombinados(), this::resolverDerrota,
                () -> explosionSound.play(game.getPreferencias().getVolumenEfectos()),bulletTexture,this::agregarProyectilHostil);
        if (jefeSector != null && jefeSector.estaActivo()) {
            jefeSector.actualizarAtaqueEspecial(frameDelta, nave.getCentroX(), nave.getCentroY(),
                    cargasEnergia::add);
            jefeSector.actualizarAtaque(frameDelta, nave.getCentroX(), nave.getCentroY(),
                this::agregarProyectilHostil);
            float velocidadAlien = Math.min(240f,
                    (85f + ronda * 8f) * sector.getFactorDificultad());
            int resistenciaAlien = AlienEnemigo.resistenciaEnRonda(ronda);
            jefeSector.actualizarRefuerzos(frameDelta, velocidadAlien, resistenciaAlien,
                    naveTexture, this::agregarRefuerzo);
        }
        indiceEnemigos.reconstruir(enemigos);
        for (Enemigo enemigo : enemigos) {
            if (enemigo instanceof AlienArtillero) {
                ((AlienArtillero) enemigo).actualizarDisparo(frameDelta,
                        nave.getCentroX(), nave.getCentroY(), bulletTexture, this::agregarProyectilHostil);
            }
        }
        if(jefeSector!=null && jefeSector.getLaserPrimerJefe()!=null)
            jefeSector.getLaserPrimerJefe().comprobarImpacto(nave);
        actualizarProyectilesHostiles(frameDelta);
        actualizarApoyo(miniDron,frameDelta);
        actualizarApoyo(aliado,frameDelta);
        actualizarApoyo(aliadoExplosivo,frameDelta);
        actualizarApoyo(dronLaser,frameDelta);
        actualizarBalas(frameDelta);
        List<BarreraEnergia> barreras = jefeSector == null
                ? java.util.Collections.emptyList() : jefeSector.getBarrerasEnergia();
        if (!barreras.isEmpty()) {
            batch.end();
            for (BarreraEnergia barrera : barreras) {
                barrera.comprobarImpacto(nave);
                dibujarBarreraJefe(barrera);
            }
            batch.begin();
        }
        tiempoMensajeMejora = Math.max(0f, tiempoMensajeMejora - frameDelta);

        for (Bullet bala : balas) {
            bala.draw(batch);
        }
        for (ProyectilHostil proyectil : proyectilesHostiles) {
            proyectil.dibujar(batch);
        }
        actualizarCargasEnergia(frameDelta);
        if (!cargasEnergia.isEmpty()) {
            batch.end();
            dibujarCargasEnergia();
            batch.begin();
        }
        nave.dibujar(batch);
        actualizarBaterias(frameDelta);
        for (BateriaEnergia bateria : baterias) {
            bateria.dibujar(batch);
        }
        if(aliado!=null)aliado.dibujar(batch);
        if(aliadoExplosivo!=null)aliadoExplosivo.dibujar(batch);
        if(miniDron!=null)miniDron.dibujar(batch);
        if(dronLaser!=null)dronLaser.dibujar(batch);
        for (int i = enemigos.size() - 1; i >= 0; i--) {
            Enemigo enemigo = enemigos.get(i);
            enemigo.dibujar(batch);
            if (nave.checkCollision(enemigo)) {
                if (enemigo instanceof AlienDivisible) {
                    while (enemigo.estaActivo()) enemigo.recibirImpacto();
                    resolverDerrota(enemigo);
                } else if (enemigo instanceof AlienExplosivo) {
                    resolverDerrota(enemigo);
                } else if (!(enemigo instanceof JefeSector)) {
                    enemigos.remove(i);
                }
            }
        }
        dibujaEncabezado();
        batch.end();
        dibujarExplosiones();
        dibujarExplosionesBalas();
        dibujarRayosLaser();dibujarBarraEnergia();
        dibujarLaserPrimerJefe();
        dibujarBarraJefe();
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
        } else if(progresion.hayMejorasPendientes()) {
            game.setScreen(new PantallaMejoras(game,this));
            return;
        } else if (!llegadaNaves.hayPendientes() && OleadaNaves.estaSuperada(enemigos) && !explosiones.hayPendientes()) {
            nave.getEnergiaDash().detener();
            game.setScreen(new PantallaJuego(game, ronda + 1, nave.getVidas(), score,
                    nave.getEnergiaDash(),
                    aliado == null ? 0f : aliado.getTiempoDesactivado(),
                    aliadoExplosivo == null ? 0f : aliadoExplosivo.getTiempoDesactivado(),
                    miniDron == null ? 0f : miniDron.getTiempoDesactivado(),
                    dronLaser == null ? 0f : dronLaser.getTiempoDesactivado()));
            dispose();
        }
    }

    void gestionarNivelPruebas() {
        if(nave.estaDestruido() || progresion.hayMejorasPendientes())return;
        if(Gdx.input.isKeyPressed(Input.Keys.F6) && Gdx.input.isKeyPressed(Input.Keys.F7)
                && (Gdx.input.isKeyJustPressed(Input.Keys.F6) || Gdx.input.isKeyJustPressed(Input.Keys.F7))) {
            progresion.subirNivelPruebas();
        }
    }

    private void agregarProyectilHostil(ProyectilHostil proyectil) {
        proyectil.usarSpriteLaser(regionProyectilHostil);
        proyectilesHostiles.add(proyectil);
    }

    private void actualizarDanoDash() {
        if (!nave.getEnergiaDash().protegeEsteFrame()) return;
        for (Enemigo enemigo : new ArrayList<>(enemigos)) {
            if (nave.golpearConDash(enemigo)) {
                Rectangle area=enemigo.getArea();
                ExplosionBala explosion=progresion.getDanoExplosionDash()>0f
                        ?new ExplosionBala(area.x+area.width/2f,area.y+area.height/2f,
                                progresion.getDanoExplosionDash(),progresion.getRadioExplosionDash()):null;
                if (enemigo.estaDestruido()) resolverDerrotaDash(enemigo);
                if(explosion!=null) {
                    explosion.aplicar(enemigos,this::resolverDerrotaDash);
                    explosionesBalas.add(explosion);
                }
            }
        }
    }

    private void resolverDerrotaDash(Enemigo enemigo) {
        if(!enemigo.estaDestruido() || !enemigos.contains(enemigo))return;
        resolverDerrota(enemigo);
        nave.getEnergiaDash().recuperar(progresion.getEnergiaPorBajaDash());
        if(progresion.tieneCuerpoACuerpo() && !nave.estaDestruido())nave.reparar();
    }

    private void actualizarApoyo(ApoyoNave apoyo,float delta) {
        if(apoyo==null)return;
        apoyo.orientarDetras(nave.getDireccionFrenteX(),nave.getDireccionFrenteY());
        apoyo.actualizar(delta,nave.getCentroX(),nave.getCentroY(),enemigos,bulletTexture,this::agregarBala);
    }

    private void actualizarBalas(float delta) {
        List<Bullet> nuevosFragmentos=new ArrayList<>();
        for(ExplosionBala explosion:explosionesBalas)explosion.actualizar(delta);
        explosionesBalas.removeIf(ExplosionBala::termino);
        for(int i=balas.size()-1;i>=0;i--) {
            Bullet bala=balas.get(i);bala.update(delta);
            if(bala instanceof RayoLaser) {
                ((RayoLaser)bala).aplicar(enemigos,this::resolverDerrota);
            } else for(ObjetivoDisparo objetivo:indiceEnemigos.consultar(bala.getArea())) {
                if(!(objetivo instanceof Enemigo))continue;
                Enemigo enemigo=(Enemigo)objetivo;
                if(!bala.checkCollision(enemigo))continue;
                nuevosFragmentos.addAll(bala.extraerFragmentos());
                if(enemigo.estaDestruido())resolverDerrota(enemigo);
                for(ExplosionBala explosion:bala.extraerExplosiones()) {
                    explosion.aplicar(enemigos,this::resolverDerrota);
                    explosionesBalas.add(explosion);
                }
                if(bala.isDestroyed())break;
            }
            if(bala.isDestroyed())balas.remove(i);
        }
        for(Bullet fragmento:nuevosFragmentos)agregarBala(fragmento);
    }

    private void dibujarRayosLaser() {
        boolean hayRayos=false;
        for(Bullet bala:balas)if(bala instanceof RayoLaser){hayRayos=true;break;}
        if(!hayRayos)return;
        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(Bullet bala:balas)if(bala instanceof RayoLaser)((RayoLaser)bala).dibujar(shapeRenderer);
        shapeRenderer.end();Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void dibujarExplosionesBalas() {
        if(explosionesBalas.isEmpty())return;
        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(ExplosionBala explosion:explosionesBalas)explosion.dibujar(shapeRenderer);
        shapeRenderer.end();Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /** Un solo camino de recompensa para balas, explosiones y choque de una nave roja. */
    private void resolverDerrota(Enemigo enemigo) {
        if (!enemigos.remove(enemigo)) return;
        if (enemigo instanceof AlienDivisible) {
            enemigos.addAll(((AlienDivisible) enemigo).dividir());
        }
        registrarDerrota(enemigo);
        Rectangle area = enemigo.getArea();
        cargasEnergia.add(new CargaEnergia(area.x + area.width / 2f,
                area.y + area.height / 2f, enemigo instanceof JefeSector ? 40f : 20f));
        float probabilidad = enemigo instanceof AlienEnemigo ? 0.10f
                : enemigo instanceof AsteroideEnemigo ? 0.05f : 0f;
        if (random.nextFloat() < probabilidad) {
            baterias.add(new BateriaEnergia(area.x + area.width / 2f,
                    area.y + area.height / 2f, bulletTexture));
        }
        explosiones.registrarMuerte(enemigo);
        explosionSound.play(game.getPreferencias().getVolumenEfectos());
        if (enemigo == jefeSector) jefeSector = null;
    }

    private void dibujarExplosiones() {
        if (!explosiones.hayPendientes()) return;
        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (ExplosionAlien explosion : explosiones.getExplosiones()) {
            if (explosion.detono()) {
                shapeRenderer.setColor(1f, 0.35f, 0.12f, 0.55f * explosion.getBrillo());
                shapeRenderer.circle(explosion.getX(), explosion.getY(), ExplosionAlien.RADIO, 48);
            }
        }
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (ExplosionAlien explosion : explosiones.getExplosiones()) {
            if (!explosion.detono()) {
                shapeRenderer.setColor(1f, 0.15f, 0.18f, 1f);
                shapeRenderer.circle(explosion.getX(), explosion.getY(), ExplosionAlien.RADIO, 48);
                shapeRenderer.setColor(1f, 0.65f, 0.25f, 1f);
                shapeRenderer.circle(explosion.getX(), explosion.getY(),
                        Math.max(3f, ExplosionAlien.RADIO * explosion.getProgresoAviso()), 48);
                shapeRenderer.line(explosion.getX() - 8f, explosion.getY(),
                        explosion.getX() + 8f, explosion.getY());
                shapeRenderer.line(explosion.getX(), explosion.getY() - 8f,
                        explosion.getX(), explosion.getY() + 8f);
            }
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void registrarDerrota(Enemigo enemigo) {
        int puntos=enemigo instanceof JefeSector?255:enemigo instanceof AlienEnemigo?30:15;
        score+=puntos;
        progresion.sumarPuntos(puntos);
        if(enemigo instanceof JefeSector) {
            progresion.registrarBateria();
            mostrarMensaje("JEFE DERROTADO");
        }
    }

    private void actualizarProyectilesHostiles(float delta) {
        for (Iterator<ProyectilHostil> iterator = proyectilesHostiles.iterator(); iterator.hasNext();) {
            ProyectilHostil proyectil = iterator.next();
            proyectil.actualizar(delta, MundoJuego.ANCHO, MundoJuego.ALTO);
            if (!nave.getEnergiaDash().protegeEsteFrame() && proyectil.toca(nave.getArea())) {
                nave.recibirDano();
                proyectil.destruir();
            }
            if (!proyectil.estaActivo()) {
                iterator.remove();
            }
        }
    }

    private void dibujarBarreraJefe(BarreraEnergia barrera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        barrera.dibujar(shapeRenderer);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void dibujarLaserPrimerJefe(){
        if(jefeSector==null || jefeSector.getLaserPrimerJefe()==null)return;
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        jefeSector.getLaserPrimerJefe().dibujar(shapeRenderer);
        shapeRenderer.end();
    }

    private void dibujarBarraJefe() {
        if (jefeSector == null || !jefeSector.estaActivo()) {
            return;
        }
        float ancho = Math.min(420f, MundoJuego.ANCHO * 0.5f);
        float x = (MundoJuego.ANCHO - ancho) / 2f;
        float y = MundoJuego.ALTO - 88f;
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.1f, 0.14f, 1f);
        shapeRenderer.rect(x, y, ancho, 12f);
        shapeRenderer.setColor(0.91f, 0.24f, 0.31f, 1f);
        shapeRenderer.rect(x, y, ancho * jefeSector.getFraccionResistencia(), 12f);
        shapeRenderer.end();
    }

    private void actualizarCargasEnergia(float delta) {
        Iterator<CargaEnergia> iterator = cargasEnergia.iterator();
        while (iterator.hasNext()) {
            CargaEnergia carga = iterator.next();
            carga.actualizar(delta);
            if (!carga.estaActiva()) {
                iterator.remove();
            } else if (carga.toca(nave.getArea())) {
                nave.getEnergiaDash().recuperar(carga.getCantidad());
                score+=5;progresion.sumarPuntos(5);
                iterator.remove();
            }
        }
    }

    private void dibujarCargasEnergia() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (CargaEnergia carga : cargasEnergia) {
            carga.dibujar(shapeRenderer);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void dibujarBarraEnergia() {
        EnergiaDash energia = nave.getEnergiaDash();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.06f, 0.12f, 0.18f, 1f);
        shapeRenderer.rect(18f, 24f, 260f, 18f);
        if (energia.getEnergia() >= energia.getCoste()) {
            shapeRenderer.setColor(0.2f, 0.8f, 1f, 1f);
        } else {
            shapeRenderer.setColor(0.9f, 0.4f, 0.22f, 1f);
        }
        shapeRenderer.rect(18f, 24f, 260f * energia.getEnergia() / EnergiaDash.MAXIMA, 18f);
        shapeRenderer.setColor(0.9f, 0.95f, 1f, 1f);
        shapeRenderer.rect(18f + 260f * energia.getCoste() / EnergiaDash.MAXIMA, 24f, 2f, 18f);
        shapeRenderer.end();
    }

    void sincronizarMejoras() {
        nave.aplicarMejoras();
        aparienciasNave.aplicar(nave,progresion);
        if(progresion.tieneMiniDron() && miniDron==null)miniDron=new DronEscolta(naveTexture,false,true);
        if(progresion.tieneDronLaser() && dronLaser==null) {
            dronLaser=new DronLaser(naveTexture);
            aparienciasNave.aplicarDron(dronLaser,TipoMejora.DRON_LASER);
        }
        if(progresion.tieneDron() && aliado==null) {
            DronEscolta dron=new DronEscolta(naveTexture);
            aparienciasNave.aplicarDron(dron,TipoMejora.DRON);
            aliado=dron;
        }
        if(progresion.tieneDronExplosivo() && aliadoExplosivo==null) {
            DronEscolta dron=new DronEscolta(naveTexture,true);
            aparienciasNave.aplicarDron(dron,TipoMejora.DRON_EXPLOSIVO);
            aliadoExplosivo=dron;
        }
    }

    /** Agrupa apoyos para que una explosion roja desactive a cada uno segun su posicion. */
    private ApoyoNave apoyosCombinados() {
        if(aliadoExplosivo==null && miniDron==null && dronLaser==null)return aliado;
        return new GrupoApoyos(aliado,aliadoExplosivo,miniDron,dronLaser);
    }

    /** Solo dibuja: mantiene todos los temporizadores congelados durante la eleccion. */
    void dibujarEscenaCongelada() {
        Gdx.gl.glClearColor(sector.getFondoRojo(),sector.getFondoVerde(),sector.getFondoAzul(),1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();camera.update();batch.setProjectionMatrix(camera.combined);
        dibujarCampoEstelar();batch.begin();
        for(Enemigo enemigo:enemigos)enemigo.dibujar(batch);
        for(Bullet bala:balas)bala.draw(batch);
        for(ProyectilHostil bala:proyectilesHostiles)bala.dibujar(batch);
        if(aliado!=null)aliado.dibujar(batch);
        if(aliadoExplosivo!=null)aliadoExplosivo.dibujar(batch);
        if(miniDron!=null)miniDron.dibujar(batch);
        if(dronLaser!=null)dronLaser.dibujar(batch);
        nave.dibujar(batch);dibujaEncabezado();batch.end();
        dibujarRayosLaser();dibujarBarraEnergia();dibujarCargasEnergia();dibujarExplosionesBalas();
        if(jefeSector!=null)for(BarreraEnergia barrera:jefeSector.getBarrerasEnergia())dibujarBarreraJefe(barrera);
        dibujarLaserPrimerJefe();
    }

    private void gestionarReparacion(float delta) {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.R) && tecnico == null) {
            if (nave.getEnergiaDash().estaActivo()) {
                mostrarMensaje("ESPERA A TERMINAR EL DASH PARA REPARAR");
            } else if (!nave.puedeReparar()) {
                mostrarMensaje("NAVE INTEGRA: NO NECESITA REPARACION");
            } else if (!progresion.consumirBateria()) {
                mostrarMensaje("SIN BATERIAS: DESTRUYE ENEMIGOS PARA ENCONTRAR UNA");
            } else {
                tecnico = new TecnicoExtravehicular(nave.getCentroX(), nave.getCentroY());
                tiempoReparacion = progresion.getDuracionReparacion();
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
            }
        }
    }

    private void mostrarMensaje(String mensaje) {
        mensajeMejora = mensaje;
        tiempoMensajeMejora = 2.5f;
    }

    private void crearCampoEstelar(float ancho, float alto) {
        for (int i = 0; i < CANTIDAD_ESTRELLAS; i++) {
            estrellasX[i] = random.nextFloat() * ancho;
            estrellasY[i] = random.nextFloat() * alto;
        }
    }

    private void dibujarCampoEstelar() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
        shapeRenderer.setColor(sector.getEstrellasRojo(), sector.getEstrellasVerde(),
                sector.getEstrellasAzul(), 0.72f);
        for (int i = 0; i < CANTIDAD_ESTRELLAS; i++) {
            shapeRenderer.point(estrellasX[i], estrellasY[i], 0f);
        }
        shapeRenderer.end();
    }

    private void actualizarEnemigos(float delta) {
        Iterator<Enemigo> iterator = enemigos.iterator();
        while (iterator.hasNext()) {
            Enemigo enemigo = iterator.next();
            enemigo.actualizar(delta, MundoJuego.ANCHO, MundoJuego.ALTO,
                    nave.getCentroX(), nave.getCentroY());
            if (enemigo.estaDestruido()
                    || (enemigo instanceof AsteroideEnemigo
                        && ((AsteroideEnemigo) enemigo).haSalidoDelEscenario(MundoJuego.ANCHO, MundoJuego.ALTO))) {
                iterator.remove();
            }
        }
        colisionesNaves.resolver(enemigos, MundoJuego.ANCHO, MundoJuego.ALTO);
    }

    private void agregarRefuerzo(AlienEnemigo refuerzo) {
        aparienciasEnemigas.aplicar(refuerzo);
        enemigos.add(refuerzo);
        colisionesNaves.resolver(enemigos, MundoJuego.ANCHO, MundoJuego.ALTO);
    }

    public boolean agregarBala(Bullet bala) {
        bala.usarTexturaCritica(texturaCritica);
        aparienciasProyectiles.aplicar(bala);
        return balas.add(bala);
    }

    public void setVolumenMusica(float volumen) {
        gameMusic.setVolume(volumen);
    }

    @Override
    public void show() {
        gameMusic.play();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.update();
        crearCampoEstelar(MundoJuego.ANCHO, MundoJuego.ALTO);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        gameMusic.pause();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        explosionSound.dispose();
        gameMusic.dispose();
        naveTexture.dispose();
        aparienciasEnemigas.dispose();
        aparienciasNave.dispose();
        aparienciasProyectiles.dispose();
        asteroidTexture.dispose();
        bulletTexture.dispose();
        texturaProyectilHostil.dispose();
        texturaCritica.dispose();
        naveHitSound.dispose();
        bulletSound.dispose();
    }
}
