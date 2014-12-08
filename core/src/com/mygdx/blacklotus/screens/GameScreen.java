package com.mygdx.blacklotus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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

    public static final float COOLDOWN_ENEMIES = 5;
    private float cooldown_enemies;

    //elementos del juego
    private Ninja blackLotus;
    private ArrayList<Shuriken> shurikens;
    private List<Enemy> enemies;

    Texture textureBlackLotus;

    public GameScreen (BlackLotusGame main) {
        super (main);
        shurikens = new ArrayList<Shuriken>();
        enemies = new ArrayList<Enemy>();
        cooldown_enemies = 0    ;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        fondo = new Texture(Gdx.files.internal("fondo.jpg"));

        //texturas de personajes
        textureBlackLotus = new Texture(Gdx.files.internal("padle.jpg"));
        Texture textureShuriken = new Texture(Gdx.files.internal("bola.png"));

        //dibujando elementos del juego
        blackLotus = new Ninja(80, Gdx.graphics.getHeight()/2-textureBlackLotus.getHeight()/2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        blackLotus.update(deltaTime, this.shurikens);


        if (cooldown_enemies > 0){
            cooldown_enemies -= deltaTime;
        }
        if (cooldown_enemies <= 0){
            Enemy nwenemy = new Enemy(Gdx.graphics.getWidth()-textureBlackLotus.getWidth()/2, (new Random()).nextFloat()*(Gdx.graphics.getHeight()-textureBlackLotus.getHeight()));
            enemies.add(nwenemy);
            cooldown_enemies = COOLDOWN_ENEMIES;
        }

        for (int i = 0; i < shurikens.size(); i++){
            Shuriken shuriken = shurikens.get(i);
            if (shuriken.isOver())
                shurikens.remove(shuriken);
            else
                shuriken.update(delta);
        }

        for (int i = 0; i < enemies.size(); i++){
            Enemy enemy = enemies.get(i);
            if (enemy.isOver() || enemy.hitByShuriken(shurikens))
                enemies.remove(enemy);
            else
                enemy.update(delta);
        }



        batch.begin();
            batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            //dibujando elementos
            blackLotus.draw(batch);

            for (int i = 0; i < enemies.size(); i++){
                Enemy enemy = enemies.get(i);
                enemy.draw(batch);
            }

            for (int i=0; i<shurikens.size(); i++){
                Shuriken shuriken = shurikens.get(i);
                shuriken.draw(batch);
            }
        batch.end();

    }
}