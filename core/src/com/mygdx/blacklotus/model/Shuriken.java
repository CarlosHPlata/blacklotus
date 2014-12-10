package com.mygdx.blacklotus.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


/**
 * Created by Usuario on 06/12/2014.
 */
public class Shuriken {
    private static final int ANCHURA_ANIMATION = 56;
    private static final int ALTURA_ANIMATION = 18;

    private static final float MOV_SPEED = 150;

    private float initx, initY, x, y, time;

    private TextureRegion texture;

    private TextureRegion animationRegion;
    private TextureRegion[] animationFrames;
    private Animation animation;
    private Rectangle bordes;

    public Shuriken(float initx, float initY, float x, float y, TextureAtlas atlas) {
        this.initx = initx;
        this.initY = initY;
        this.x = x;
        this.y = y;
        this.time = 0;

        loadAnimation(atlas);

        this.bordes = new Rectangle(initx, initY, ANCHURA_ANIMATION/2, ALTURA_ANIMATION);

        this.texture = this.animation.getKeyFrame(0, true);
    }

    public void draw(SpriteBatch batch){
        this.texture = this.animation.getKeyFrame(time, true);
        batch.draw(texture, bordes.x, bordes.y, texture.getRegionWidth(), texture.getRegionHeight());
    }

    public void update(float delta, float durationGame){
        float newX =  bordes.x + MOV_SPEED*delta;

        this.y = ( (newX - this.initx)/(this.x - this.initx) ) * (this.y - this.initY) + this.initY;
        this.x = newX;

        this.bordes.x = this.x;
        this.bordes.y = this.y;

        time += delta;
    }

    public boolean isOver(){
        if ( bordes.x < 0 || bordes.x > Gdx.graphics.getWidth() || bordes.y < 0 || bordes.y > Gdx.graphics.getHeight())
            return true;
        else
            return false;
    }

    public Rectangle getBordes(){
        return this.bordes;
    }

    public void loadAnimation(TextureAtlas atlas){
        this.animationRegion = atlas.findRegion("ShurikenSpriteSheet");
        TextureRegion[][] temp = this.animationRegion.split(animationRegion.getRegionWidth()/2, animationRegion.getRegionHeight());
        this.animationFrames = new TextureRegion[2];

        for (int i=0; i<temp[0].length; i++){
            this.animationFrames[i] = temp[0][i];
        }

        this.animation = new Animation(0.05f, this.animationFrames);
    }
}
