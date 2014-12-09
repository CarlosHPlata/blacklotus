package com.mygdx.blacklotus.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Usuario on 06/12/2014.
 */
public class Enemy {

    public static float MOV_SPEED = 50;
    private float initX, initY, verticex, verticey;

    private Texture texture;
    private Rectangle bordes;

    public Enemy(float initX, float initY) {
        this.initX = initX;
        this.initY = initY;

        this.texture = new Texture(Gdx.files.internal("ninja1.jpg"));
        this.bordes = new Rectangle(initX, initY, texture.getWidth(), texture.getHeight());

        verticex = Gdx.graphics.getWidth()/2 + ((new Random()).nextFloat() * (Gdx.graphics.getWidth()/3));
        verticey = Gdx.graphics.getHeight() - texture.getHeight();
    }

    public void draw(SpriteBatch batch){
        batch.draw(texture, bordes.x, bordes.y, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta){
        float a = (this.initY - this.verticey) / ((this.initX - this.verticex) * (this.initX - this.verticex)); //calculo del foco de la parabola

        float nextX = bordes.x - MOV_SPEED*delta;
        float nextY = a * ((nextX - this.verticex) * (nextX - this.verticex)) + this.verticey; //formula calculo de la parabola

        this.bordes.x = nextX;
        this.bordes.y = nextY;
    }

   private boolean hitByShuriken(Shuriken shuriken){
       if(shuriken.getBordes().overlaps(this.bordes)){
           return true;
       }
    return false;
   }

    public boolean hitByShuriken(ArrayList<Shuriken> shurikens){
        for (int i = 0; i < shurikens.size(); i++){
            if(this.hitByShuriken(shurikens.get(i))) {
                shurikens.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean isOver(){

        if ( bordes.x < 0 || bordes.x > Gdx.graphics.getWidth())
            return true;
        else
            return false;

    }
}
