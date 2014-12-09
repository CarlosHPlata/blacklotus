package com.mygdx.blacklotus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    public static final float COOLDOWN_ENEMIES = 5;
    private float cooldown_enemies;

    //elementos del juego
    private Ninja blackLotus;
    private ArrayList<Shuriken> shurikens;
    private List<Enemy> enemies;
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
    }

    @Override
    public void show() {
        batch = main.batch;
        font = new BitmapFont(Gdx.files.internal("ninjaFont.fnt"), Gdx.files.internal("ninjaFont.png"), false);

        fondo = new Texture(Gdx.files.internal("fondo.jpg"));

        //texturas de personajes
        textureBlackLotus = new Texture(Gdx.files.internal("padle.jpg"));

        //dibujando elementos del juego
        blackLotus = new Ninja(80, Gdx.graphics.getHeight()/2-textureBlackLotus.getHeight()/2);

        puntuaciones =Gdx.app.getPreferences("-scores");
        maxScore = puntuaciones.getInteger("maxScore");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        //update del personaje principal
        blackLotus.update(deltaTime, this.shurikens);

        //si el coldown es mayor a 0 se resta el delta al coldown para que baje
        if (cooldown_enemies > 0){
            cooldown_enemies -= score>0? deltaTime*(score/2) : deltaTime;
        }

        //Si l coldown llega a su limite de tiempo un nuevo enemigo se muestra en la pantalla
        if (cooldown_enemies <= 0){
            Enemy nwenemy = new Enemy(Gdx.graphics.getWidth()-textureBlackLotus.getWidth()/2, (new Random()).nextFloat()*(Gdx.graphics.getHeight()-textureBlackLotus.getHeight()));
            enemies.add(nwenemy);
            cooldown_enemies = COOLDOWN_ENEMIES; //reiniciando el coldown
        }

        //Update de los shurikens
        for (int i = 0; i < shurikens.size(); i++){
            Shuriken shuriken = shurikens.get(i);
            if (shuriken.isOver()) //si el shuriken sale de la pantalla hay que eliminarlo
                shurikens.remove(shuriken);
            else //update
                shuriken.update(delta);
        }

        //update de los enemigos
        for (int i = 0; i < enemies.size(); i++){
            Enemy enemy = enemies.get(i);
            if( enemy.hitByShuriken(shurikens)){ //si el enemigo es alcanzado por un shuriken se aumenta el score
                score++;
                enemies.remove(enemy);
                continue;
            }
            //SI el enemigo esta fuera de la pantalla hay que removerlo
            if (enemy.isOver())
                enemies.remove(enemy);
            else
                enemy.update(delta);
        }


        //si supera su puntuacion
        if (score >= maxScore){
            maxScore = score;
        }


        batch.begin();
            //dibujando elementos comunes
            batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            font.draw(batch, "Score: "+Integer.toString(this.score), Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()-5);

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
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();

        puntuaciones.putInteger("maxScore", maxScore);
        puntuaciones.flush();
        batch.dispose();
        font.dispose();
        textureBlackLotus.dispose();
    }
}