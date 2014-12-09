package com.mygdx.blacklotus.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.blacklotus.BlackLotusGame;

/**
 * Created by Usuario on 09/12/2014.
 */
public class MenuScreen extends AbstractScreen {

    private Skin skin;
    private Table table;
    private SpriteBatch batch;
    private Stage stage;

    public MenuScreen(BlackLotusGame main) {
        super(main);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.stage = new Stage();

        this.table = new Table();
    }

    @Override
    public void show() {
        batch = main.batch;
        final TextButton play = new TextButton("Play", this.skin);
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final BlackLotusGame game = main;
                stage.dispose();
                game.setScreen(new GameScreen(main));
            }
        });

        TextButton options = new TextButton("Options", this.skin);
        TextButton exit = new TextButton("Exit", this.skin);
        TextButton board = new TextButton("LaderBoards", this.skin);

        super.show();
        table.setFillParent(true);

        table.add(play).prefWidth(300);

        table.row().padTop(10);
        table.add(board).prefWidth(300);

        table.row().padTop(10);
        table.add(options).prefWidth(300);

        table.row().padTop(30);
        table.add(exit).prefWidth(300);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
