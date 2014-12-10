package com.mygdx.blacklotus.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Usuario on 06/12/2014.
 */
public class Enemy {
    private static final int ANCHURA_ANIMATION = 465;
    private static final int ALTURA_ANIMATION = 93;

    public static float MOV_SPEED = 50;
    private float initX, initY, verticex, verticey, time;

    private TextureRegion texture;
    private Rectangle bordes;
    private TextureRegion animationRegion;
    private TextureRegion[] animationFrames;
    private Animation animation;

    public Enemy(float initX, float initY, TextureAtlas atlas) {
        this.initX = initX;
        this.initY = initY;
        this.time = 0;

        loadAnimation(atlas);
        texture = this.animation.getKeyFrame(0, true);

        this.bordes = new Rectangle(initX, initY, texture.getRegionWidth(), texture.getRegionHeight());

        verticex = Gdx.graphics.getWidth()/2 + ((new Random()).nextFloat() * (Gdx.graphics.getWidth()/3));
        verticey = Gdx.graphics.getHeight() - texture.getRegionHeight();
    }

    public void draw(SpriteBatch batch){
        batch.draw(texture, bordes.x, bordes.y, texture.getRegionWidth(), texture.getRegionHeight());
    }

    public void update(float delta, float difficult){

        if (isOverY()){
            this.verticex = this.bordes.x;
            this.verticey = 0;
            this.initX = 80;
            this.initY = Gdx.graphics.getHeight()/2;
        }

        float movSpeed;

        if (difficult < 3)
           movSpeed = MOV_SPEED ;
        else{
            if (difficult -1 < 10)
             movSpeed = MOV_SPEED * (difficult -1);
            else
                movSpeed = (MOV_SPEED*10) + (difficult -1);
        }

        float a = (this.initY - this.verticey) / ((this.initX - this.verticex) * (this.initX - this.verticex)); //calculo del foco de la parabola

        float nextX = this.verticey==0? bordes.x - movSpeed*delta*3 : bordes.x - movSpeed*delta;
        float nextY = a * ((nextX - this.verticex) * (nextX - this.verticex)) + this.verticey; //formula calculo de la parabola

        this.bordes.x = nextX;
        this.bordes.y = nextY;

        this.time += delta;
        this.texture = this.animation.getKeyFrame(time, true);
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

        if ( bordes.x < 0 || bordes.x > Gdx.graphics.getWidth() || bordes.y < -40 || bordes.y > Gdx.graphics.getHeight())
            return true;
        else
            return false;

    }

    public boolean isOverY() {
        if (bordes.y < 0){
            return  true;
        } else {
            return false;
        }
    }

    public boolean hitWall(){
        if (bordes.x < Gdx.graphics.getHeight()/5)
            return true;
        else
            return false;
    }

    public void loadAnimation(TextureAtlas atlas){
        this.animationRegion = atlas.findRegion("enemieAnimation");
        TextureRegion[][] temp = this.animationRegion.split(animationRegion.getRegionWidth()/5, animationRegion.getRegionHeight());
        this.animationFrames = new TextureRegion[5];

        for (int i=0; i<temp[0].length; i++){
            this.animationFrames[i] = temp[0][i];
        }

        this.animation = new Animation(0.9f, this.animationFrames);
    }
}
