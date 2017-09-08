package com.greenlamp.mario.Sprites.Items;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.greenlamp.mario.MarioBros;
import com.greenlamp.mario.Scenes.Hud;
import com.greenlamp.mario.Screens.PlayScreen;
import com.greenlamp.mario.Sprites.TileObjects.InteractiveTileObject;


public class Coin extends InteractiveTileObject {
    static TiledMapTileSet tileset;
    final int BLANK_COIN = 1+27;
    World world;
    TiledMap map;
    AssetManager manager;

    public Coin(PlayScreen screen, Rectangle bounds){
        super(screen, bounds);
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.manager = screen.getManager();
        tileset = map.getTileSets().getTileSet("tileset_gutter");
        this.fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
        this.manager = manager;
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Coin", "Collision");
        if(getCell().getTile().getId() == BLANK_COIN){
            this.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }else{
            this.manager.get("audio/sounds/coin.wav", Sound.class).play();
            screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM), Mushroom.class));
            getCell().setTile(tileset.getTile(BLANK_COIN));
            Hud.addScore(100);
        }
    }
}

part 22