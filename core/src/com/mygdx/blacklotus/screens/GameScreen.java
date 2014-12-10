package com.mygdx.blacklotus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private Music ambientMusic;

    public static final float COOLDOWN_ENEMIES = 5;
    private float difficult;
    private float cooldown_enemies;

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
        shurikens = new ArrayList<Shuriken>();
        enemies = new ArrayList<Enemy>();
        cooldown_enemies = 0;
        score = 0;
        lifes = 10;
        durationGame =0;
        difficult = 1;
        atlas = new TextureAtlas("ninja.atlas");
    }

    @Override
    public void show() {
        batch = main.batch;
        TextureAtlas atlas = new TextureAtlas("ninja.atlas");
        lifeTexture = atlas.findRegion("hearth");
        lifeVoidTexture = atlas.findRegion("heartVoid");
        font = new BitmapFont(Gdx.files.internal("ninjaFont.fnt"), Gdx.files.internal("ninjaFont.png"), false);
        ambientMusic = Gdx.audio.newMusic(Gdx.files.internal("Ninjas_Music_No_Vocals.mp3"));
        fondo = new Texture(Gdx.files.internal("fondo.jpg"));

        //reproduciendo musica
        ambientMusic.setLooping(true);
        ambientMusic.play();

        //texturas de personajes
        textureBlackLotus = new Texture(Gdx.files.internal("padle.jpg"));

        //dibujando elementos del juego
        blackLotus = new Ninja(80, Gdx.graphics.getHeight()/2-textureBlackLotus.getHeight()/2, this.atlas);

        puntuaciones =Gdx.app.getPreferences("-scores");
        maxScore = puntuaciones.getInteger("maxScore");
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (lifes > 0){
            updateGaming(deltaTime);
        } else {
            updateLose();
        }

        durationGame+=deltaTime;

        batch.begin();
            //dibujando elementos comunes
            batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            font.draw(batch, "Score: "+Integer.toString(this.score), Gdx.graphics.getWidth()/4, 40);

            for (int i=0; i<this.lifes; i++){
                batch.draw(lifeTexture, Gdx.graphics.getWidth()/4 + lifeTexture.getRegionWidth()*i,
                        Gdx.graphics.getHeight()-lifeTexture.getRegionWidth()-5, lifeTexture.getRegionWidth(),
                        lifeTexture.getRegionHeight());
            }
            for (int i=this.lifes; i<10; i++){
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
            if (lifes<0)
                font.draw(batch,"YOU LOSE", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        batch.end();
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
        blackLotus.update(deltaTime, this.shurikens, this.difficult);

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

    public void updateLose(){
        if (Gdx.input.isTouched()) {
            main.setScreen( new MenuScreen(main));
            this.dispose();
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

}