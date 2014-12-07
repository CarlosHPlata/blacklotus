package com.mygdx.blacklotus;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.blacklotus.screens.AbstractScreen;
import com.mygdx.blacklotus.screens.GameScreen;

public class BlackLotusGame extends Game {
    public AbstractScreen GAMESCREEN;
	
	@Override
	public void create () {
        GAMESCREEN = new GameScreen(this);
        setScreen(GAMESCREEN);
	}
}
