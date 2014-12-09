package com.mygdx.blacklotus.screens;

import com.badlogic.gdx.Screen;
import com.mygdx.blacklotus.BlackLotusGame;

/**
 * Created by Usuario on 06/12/2014.
 */
public class AbstractScreen implements Screen {

    protected BlackLotusGame main;

    public AbstractScreen(BlackLotusGame main) {
        this.main = main;
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
