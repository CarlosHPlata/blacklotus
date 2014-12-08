package com.mygdx.blacklotus.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

/**
 * Created by Usuario on 06/12/2014.
 */
public class Ninja{

    public static final float COLD_DOWN = 1;
    private float x;
    private float y;

    private float coldDown = 0;

    private Texture texture;
    private Rectangle bordes;

    public Ninja (float x, float y) {
        this.x = x;
        this.y = y;

        this.texture = new Texture(Gdx.files.internal("ninja2.jpg"));
        this.bordes = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void draw(SpriteBatch batch){
        batch.draw(texture, bordes.x, bordes.y, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta, List<Shuriken> shurikens){

        if ( coldDown > 0) {
            coldDown = coldDown - delta;
        }

        if (Gdx.input.isTouched() && coldDown <= 0){
            float coordX = Gdx.input.getX();
            float coordY = Gdx.graphics.getHeight() - Gdx.input.getY();

            Shuriken shuriken = new Shuriken(bordes.getX(), bordes.getY(), coordX, coordY);
            shurikens.add(shuriken);

            coldDown = COLD_DOWN;
        }
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return  this.y;
    }

}
