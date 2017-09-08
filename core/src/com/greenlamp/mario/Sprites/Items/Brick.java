package com.greenlamp.mario.Sprites.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.greenlamp.mario.MarioBros;
import com.greenlamp.mario.Scenes.Hud;
import com.greenlamp.mario.Screens.PlayScreen;
import com.greenlamp.mario.Sprites.TileObjects.InteractiveTileObject;

public class Brick extends InteractiveTileObject {
    World world;
    TiledMap map;
    AssetManager manager;

    public Brick(PlayScreen screen, Rectangle bounds){
        super(screen, bounds);
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.manager = screen.getManager();
        this.fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Brick", "Collision");
        setCategoryFilter(MarioBros.DESTROYED_BIT);
        getCell().setTile(null);
        Hud.addScore(50);
        this.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
    }
}