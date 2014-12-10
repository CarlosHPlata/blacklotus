package com.mygdx.blacklotus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.blacklotus.BlackLotusGame;
import com.mygdx.blacklotus.model.Enemy;
import com.mygdx.blacklotus.model.Ninja;
import com.mygdx.blacklotus.model.Shuriken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Carlos on 06/12/2014.
 */
public class GameScreen extends AbstractScreen {

    private SpriteBatch batch;
    private Texture fondo;
    private BitmapFont font;
    private Preferences puntuaciones;
    private float durationGame;
    private TextureAtlas atlas;
    private Skin skin;
    private Stage stagePause;
    private Stage buttonPause;

    private Sound loseSound;
    private Music ambientMusic;

    public static final float COOLDOWN_ENEMIES = 5;
    public static final int RUNNING = 1, PAUSE = 2, GAME_OVER = 3;
    private float difficult;
    private float cooldown_enemies;
    private int state;

    //elementos del juego
    private Ninja blackLotus;
    private ArrayList<Shuriken> shurikens;
    private List<Enemy> enemies;
    private TextureRegion lifeTexture;
    private TextureRegion lifeVoidTexture;
    private int score;
    private int maxScore;
    private int lifes;

    Texture textureBlackLotus;

    public GameScreen (BlackLotusGame main) {
        super (main);
        state = RUNNING;
        shurikens = new ArrayList<Shuriken>();
        enemies = new ArrayList<Enemy>();
        cooldown_enemies = 0;
        score = 0;
        lifes = 5;
        durationGame =0;
        difficult = 1;
        atlas = new TextureAtlas("ninja.atlas");
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.stagePause = new Stage();
        this.buttonPause = new Stage();
    }

    @Override
    public void show() {
        batch = main.batch;
        TextureAtlas atlas = new TextureAtlas("ninja.atlas");
        lifeTexture = atlas.findRegion("hearth");
        lifeVoidTexture = atlas.findRegion("heartVoid");
        font = new BitmapFont(Gdx.files.internal("ninjaFont.fnt"), Gdx.files.internal("ninjaFont.png"), false);
        ambientMusic = Gdx.audio.newMusic(Gdx.files.internal("Ninjas_Music_No_Vocals.mp3"));
        loseSound = Gdx.audio.newSound(Gdx.files.internal("YouLose.mp3"));
        fondo = new Texture(Gdx.files.internal("fondo.jpg"));

        //reproduciendo musica
        ambientMusic.setLooping(true);
        if (main.isSoundEnabled)
            ambientMusic.play();

        //texturas de personajes
        textureBlackLotus = new Texture(Gdx.files.internal("padle.jpg"));

        //dibujando elementos del juego
        blackLotus = new Ninja(80, Gdx.graphics.getHeight()/2, this.atlas);

        puntuaciones =Gdx.app.getPreferences("-scores");
        maxScore = puntuaciones.getInteger("maxScore");

        loadMenuScreen();
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (lifes == 0) this.state = GAME_OVER;

        switch (this.state){
            case RUNNING:
                updateGaming(deltaTime);
                break;
            case PAUSE:

                break;
            case GAME_OVER:
                updateLose();
                break;
        }

        durationGame+=deltaTime;

        batch.begin();
            //dibujando elementos comunes
            batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            font.draw(batch, "Score: "+Integer.toString(this.score), Gdx.graphics.getWidth()/5, 40);
            font.draw(batch, "Level: "+Integer.toString((int)this.difficult),Gdx.graphics.getWidth()/2+100,40);
            font.draw(batch, "MaxScore: "+Integer.toString(this.maxScore), Gdx.graphics.getWidth()/2+100, Gdx.graphics.getHeight()-5);

            for (int i=0; i<this.lifes; i++){
                batch.draw(lifeTexture, Gdx.graphics.getWidth()/4 + lifeTexture.getRegionWidth()*i,
                        Gdx.graphics.getHeight()-lifeTexture.getRegionWidth()-5, lifeTexture.getRegionWidth(),
                        lifeTexture.getRegionHeight());
            }
            for (int i=this.lifes; i<5; i++){
                batch.draw(lifeVoidTexture, Gdx.graphics.getWidth()/4 + lifeTexture.getRegionWidth()*i,
                        Gdx.graphics.getHeight()-lifeTexture.getRegionWidth()-5, lifeTexture.getRegionWidth(),
                        lifeTexture.getRegionHeight());
            }

            //dibujando a blackLotus
            blackLotus.draw(batch);

            //dibujando los enemigos
            for (int i = 0; i < enemies.size(); i++){
                Enemy enemy = enemies.get(i);
                enemy.draw(batch);
            }

            //dibujando los shurikens
            for (int i=0; i<shurikens.size(); i++){
                Shuriken shuriken = shurikens.get(i);
                shuriken.draw(batch);
            }

            //dibujando si perdiste
            if (lifes==0)
                font.draw(batch,"YOU LOSE", Gdx.graphics.getWidth()/2 - 100, Gdx.graphics.getHeight()/2+100);
        batch.end();

        buttonPause.act(deltaTime);
        buttonPause.draw();

        if (state == PAUSE){
            stagePause.act(deltaTime);
            stagePause.draw();
        }
    }

    @Override
    public void dispose() {
        puntuaciones.putInteger("maxScore", maxScore);
        puntuaciones.flush();
        batch.dispose();
        font.dispose();
        textureBlackLotus.dispose();
        ambientMusic.stop();
        ambientMusic.dispose();
    }


    public void updateGaming(float deltaTime) {
        //update del personaje principal
        blackLotus.update(deltaTime, this.shurikens, this.difficult, main.isSoundEnabled);

        //controla el cooldown de salida de los enemigos
        enemieCooldownController(deltaTime);

        //Update de los shurikens
        updateShurikens(deltaTime);

        //update de los enemigos
        updateEnemies(deltaTime);

        //Se encarga de elevar la dificultad
        diffcultController();

        //si supera su puntuacion maxima ahora sera el el mejor
        if (score >= maxScore){
            maxScore = score;
        }
    }



    public void updateEnemies(float delta) {
        //update de los enemigos
        for (int i = 0; i < enemies.size(); i++){
            Enemy enemy = enemies.get(i);
            if( enemy.hitByShuriken(shurikens)){ //si el enemigo es alcanzado por un shuriken se aumenta el score
                score++;
                enemies.remove(enemy);
                continue;
            }
            //SI el enemigo esta fuera de la pantalla hay que removerlo
            if (enemy.hitWall()){
                lifes--;
                enemies.remove(enemy);
                continue;
            }
            if (enemy.isOver())
                enemies.remove(enemy);
            else
                enemy.update(delta, this.difficult);
        }
    }

    public void updateShurikens(float delta) {
        //Update de los shurikens
        for (int i = 0; i < shurikens.size(); i++){
            Shuriken shuriken = shurikens.get(i);
            if (shuriken.isOver()) //si el shuriken sale de la pantalla hay que eliminarlo
                shurikens.remove(shuriken);
            else //update
                shuriken.update(delta, this.durationGame);
        }
    }

    public void enemieCooldownController(float deltaTime){
        //si el coldown es mayor a 0 se resta el delta al coldown para que baje
        if (cooldown_enemies > 0){
            cooldown_enemies -= deltaTime*difficult;
        }

        //Si l coldown llega a su limite de tiempo un nuevo enemigo se muestra en la pantalla
        if (cooldown_enemies <= 0 && enemies.size() < 20){
            Enemy nwenemy = new Enemy(Gdx.graphics.getWidth()-textureBlackLotus.getWidth()/2,
                    (new Random()).nextFloat()*(Gdx.graphics.getHeight()-textureBlackLotus.getHeight()),
                    this.atlas);
            enemies.add(nwenemy);
            cooldown_enemies = COOLDOWN_ENEMIES; //reiniciando el coldown
        }

    }

    private boolean loseSoundPlayed = false;
    public void updateLose(){
        if (!loseSoundPlayed) {
            if (main.isSoundEnabled)
                loseSound.play();
            loseSoundPlayed = true;
        }

        if (Gdx.input.isTouched()) {
            puntuaciones.putInteger("maxScore", maxScore);
            puntuaciones.flush();
            font.dispose();
            textureBlackLotus.dispose();
            ambientMusic.stop();
            ambientMusic.dispose();
            main.setScreen(new MenuScreen(main));
        }
    }

    public void diffcultController(){
        if (score > 10){
            difficult = 2;
        }

        if (score > 20){
            difficult = 3;
        }

        if (score > 40){
            difficult = 4;
        }

        if (score > 50){
            difficult = 5;
        }

        if (score > 100){
            difficult = 5 + (score/20);
        }
    }


    public void loadMenuScreen(){
        Table table = new Table();

        final CheckBox soundCheckbox = new CheckBox("  Sounds ", skin);
        soundCheckbox.setChecked(main.isSoundEnabled);
        soundCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final BlackLotusGame game = main;
                game.isSoundEnabled = !game.isSoundEnabled;
                if (game.isSoundEnabled)
                    ambientMusic.play();
                else
                    ambientMusic.stop();
            }
        });

        final TextButton resume = new TextButton("Resume", this.skin);
        resume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                state = RUNNING;
                Gdx.input.setInputProcessor(buttonPause);
            }
        });

        final TextButton backMenu = new TextButton("Back to menu", this.skin);
        backMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                  BlackLotusGame game = main;
                  puntuaciones.putInteger("maxScore", maxScore);
                  puntuaciones.flush();

                  main.setScreen(new MenuScreen(main));

                  fondo.dispose();
                  font.dispose();
                  stagePause.dispose();
                  buttonPause.dispose();
                  loseSound.stop();
                  ambientMusic.stop();
            }
        });

        Texture pauseImg = new Texture(Gdx.files.internal("pauseBut.png"));
        TextureRegion pauseImgR = new TextureRegion(pauseImg);

        final ImageButton pause = new ImageButton(new TextureRegionDrawable(pauseImgR));
        pause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                state = PAUSE;
                Gdx.input.setInputProcessor(stagePause);
            }
        });

        table.setFillParent(true);

        table.add(soundCheckbox).prefWidth(300);

        table.row().padTop(10);
        table.add(resume).prefWidth(300);

        table.row().padTop(30);
        table.add(backMenu).prefWidth(300);

        stagePause.addActor(table);

        //boton de pausa
        Table table2 = new Table();
        table2.setPosition(pauseImg.getWidth(), Gdx.graphics.getHeight()-pauseImg.getHeight());

        table2.add(pause);
        buttonPause.addActor(table2);
        Gdx.input.setInputProcessor(buttonPause);
    }

}