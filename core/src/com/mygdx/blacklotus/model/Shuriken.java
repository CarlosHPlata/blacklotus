package com.mygdx.blacklotus.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Usuario on 06/12/2014.
 */
public class Shuriken {

    private static final float MOV_SPEED = 150;

    private float initx, initY, x, y;

    private Texture texture;
    private Rectangle bordes;

    public Shuriken(float initx, float initY, float x, float y) {
        this.initx = initx;
        this.initY = initY;
        this.x = x;
        this.y = y;

        this.texture = new Texture(Gdx.files.internal("bola.png"));
        this.bordes = new Rectangle(initx, initY, texture.getWidth(), texture.getHeight());
    }

    public void draw(SpriteBatch batch){
        batch.draw(texture, bordes.x, bordes.y, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta){
        float newX =  bordes.x + MOV_SPEED*delta;

        this.y = ( (newX - this.initx)/(this.x - this.initx) ) * (this.y - this.initY) + this.initY;
        this.x = newX;

        this.bordes.x = this.x;
        this.bordes.y = this.y;
    }

    public boolean isOver(){
        if ( bordes.x < 0 || bordes.x > Gdx.graphics.getWidth())
            return true;
        else
            return false;
    }
}
