package com.mygdx.blacklotus.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;
import java.util.Random;

/**
 * Created by Usuario on 06/12/2014.
 */
public class Ninja{

    public static final float COLD_DOWN = 1;
    private float x;
    private float y;

    private float coordX=-2;
    private float coordY=-4;

    private float coldDown = 0;

    private TextureRegion texture;
    private TextureRegion ninjaNormal, throw1, throw2, throw3;
    private TextureAtlas atlas;
    private Rectangle bordes;
    private Sound hit1, hit2, hit3;

    public Ninja (float x, float y, TextureAtlas atlas) {
        this.x = x;
        this.y = y;

        hit1 = Gdx.audio.newSound(Gdx.files.internal("hit1.mp3"));
        hit2 = Gdx.audio.newSound(Gdx.files.internal("hit2.mp3"));
        hit3 = Gdx.audio.newSound(Gdx.files.internal("hit3.mp3"));

        this.atlas = atlas;

        this.ninjaNormal = atlas.findRegion("ninja");
        this.throw1 = atlas.findRegion("ninja-throw-1");
        this.throw2 = atlas.findRegion("ninja-throw-2");
        this.throw3 = atlas.findRegion("ninja-throw-3");

        this.texture = ninjaNormal;
        this.bordes = new Rectangle(x, y, texture.getRegionWidth(), texture.getRegionHeight());
    }

    public void draw(SpriteBatch batch){
        batch.draw(texture, bordes.x, bordes.y, texture.getRegionWidth(), texture.getRegionHeight());
    }

    public void update(float delta, List<Shuriken> shurikens, float difficult, boolean sound){

        if ( coldDown > 0) {
            coldDown = difficult<5? coldDown - delta : coldDown - (delta*2);
        } else {
            this.texture = ninjaNormal;
        }

        if (Gdx.input.isTouched() && coldDown <= 0){
            float newcoordX = Gdx.input.getX();
            float newcoordY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (newcoordX != coordX && newcoordY != coordY){
                coordX = newcoordX;
                coordY = newcoordY;
                Shuriken shuriken = new Shuriken(bordes.getX()+texture.getRegionWidth()/2, bordes.getY(), coordX, coordY, atlas);
                shurikens.add(shuriken);

                coldDown = COLD_DOWN;
                switch ((new Random()).nextInt(3)){
                    case 0:
                        this.texture = this.throw1;
                        if (sound)
                            hit1.play(0.3f);
                        break;
                    case 1:
                        this.texture = this.throw2;
                        if (sound)
                            hit2.play(0.3f);
                        break;
                    case 2:
                        this.texture = this.throw3;
                        if (sound)
                            hit3.play(0.3f);
                        break;
                    default:
                        this.texture = this.throw1;
                        hit2.play(0.3f);
                }
            }
        }
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return  this.y;
    }

}
