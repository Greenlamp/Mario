package com.greenlamp.mario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.greenlamp.mario.MarioBros;
import com.greenlamp.mario.Scenes.Hud;
import com.greenlamp.mario.Sprites.Enemies.Enemy;
import com.greenlamp.mario.Sprites.Items.Item;
import com.greenlamp.mario.Sprites.Items.ItemDef;
import com.greenlamp.mario.Sprites.Items.Mushroom;
import com.greenlamp.mario.Sprites.Mario;
import com.greenlamp.mario.Tools.B2WorldCreator;
import com.greenlamp.mario.Tools.WorldContactListener;

import java.util.PriorityQueue;

public class PlayScreen implements Screen{
    MarioBros game;

    TextureAtlas atlas;

    OrthographicCamera gameCam;
    Viewport gamePort;
    Hud hud;

    TmxMapLoader mapLoader;
    TiledMap map;
    OrthogonalTiledMapRenderer renderer;

    World world;
    Box2DDebugRenderer b2dr;
    B2WorldCreator creator;

    Mario player;

    Music music;
    AssetManager manager;

    Array<Item> items;
    PriorityQueue<ItemDef> itemsToSpawn;


    public PlayScreen(MarioBros game, AssetManager manager){
        this.game = game;
        this.atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.gameCam = new OrthographicCamera();
        this.gamePort = new FitViewport(MarioBros.V_WIDTH/ MarioBros.PPM, MarioBros.V_HEIGHT/ MarioBros.PPM, this.gameCam);
        this.hud = new Hud(this.game.batch);

        this.mapLoader = new TmxMapLoader();
        this.map = this.mapLoader.load("level1.tmx");
        this.renderer = new OrthogonalTiledMapRenderer(this.map, 1/ MarioBros.PPM);

        this.gameCam.position.set(this.gamePort.getWorldWidth() / 2, this.gamePort.getWorldHeight() / 2, 0);

        this.world = new World(new Vector2(0, -10), true);
        this.b2dr = new Box2DDebugRenderer();

        this.manager = manager;
        creator = new B2WorldCreator(this);

        this.player = new Mario(this);

        world.setContactListener(new WorldContactListener());

        this.music = this.manager.get("audio/music/mario_music.ogg", Music.class);
        this.music.setLooping(true);
        //this.music.play();

        items = new Array<Item>();
        itemsToSpawn = new PriorityQueue<ItemDef>();

    }

    public void spawnItem(ItemDef iDef){
        itemsToSpawn.add(iDef);
    }

    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef iDef = itemsToSpawn.poll();
            if(iDef.type == Mushroom.class){
                items.add(new Mushroom(this, iDef.position.x, iDef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt){
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)){
            this.player.b2body.applyLinearImpulse(new Vector2(0, 4f), this.player.b2body.getWorldCenter(), true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D) && player.b2body.getLinearVelocity().x <= 2){
            this.player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), this.player.b2body.getWorldCenter(), true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Q) && player.b2body.getLinearVelocity().x >= -2){
            this.player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), this.player.b2body.getWorldCenter(), true);
        }
    }

    public void update(float dt){
        handleInput(dt);
        handleSpawningItems();

        world.step(1/60f, 6, 2);
        this.player.update(dt);
        for(Enemy enemy: creator.getGoombas()){
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 16 * 14 / MarioBros.PPM){
                enemy.b2body.setActive(true);
            }
        }

        for(Item item: items){
            item.update(dt);
        }



        this.hud.update(dt);

        this.gameCam.position.x = this.player.b2body.getPosition().x;

        this.gameCam.update();

        this.renderer.setView(this.gameCam);
    }

    @Override
    public void render(float delta) {
        this.update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.renderer.render();

        b2dr.render(this.world, this.gameCam.combined);
        game.batch.setProjectionMatrix(this.gameCam.combined);
        game.batch.begin();
        this.player.draw(this.game.batch);
        for(Enemy enemy: creator.getGoombas()){
            enemy.draw(this.game.batch);
        }

        for(Item item: items){
            item.draw(this.game.batch);
        }
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld(){
        return world;
    }

    public AssetManager getManager(){
        return manager;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.map.dispose();
        this.renderer.dispose();
        this.world.dispose();
        this.b2dr.dispose();
        this.hud.dispose();
    }

}
