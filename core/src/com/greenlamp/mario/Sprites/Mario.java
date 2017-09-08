package com.greenlamp.mario.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.greenlamp.mario.MarioBros;
import com.greenlamp.mario.Screens.PlayScreen;

public class Mario extends Sprite {
    public enum State{FALLING, JUMPING, STANDING, RUNNING}
    public State currentState, previousState;
    public World world;
    public Body b2body;

    TextureRegion marioStand;
    Animation<TextureRegion> marioRun;
    Animation<TextureRegion> marioJump;
    float stateTimer;
    boolean runningRight;

    public Mario(PlayScreen screen){
        this.world = screen.getWorld();
        this.currentState = State.STANDING;
        this.previousState = State.STANDING;
        this.stateTimer = 0;
        this.runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i*16, 0, 16, 16));
        }
        marioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for(int i=4; i<6; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i*16, 0, 15, 16));
        }
        marioJump = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();


        this.defineMario();

        this.marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        this.setBounds(0, 0, 16/MarioBros.PPM, 16/MarioBros.PPM);
        this.setRegion(this.marioStand);
    }

    public void update(float dt){
        setPosition(this.b2body.getPosition().x - this.getWidth() / 2, this.b2body.getPosition().y - this.getHeight() / 2);
        setRegion(this.getFrame(dt));
    }

    public TextureRegion getFrame(float dt){
        currentState = this.getState();
        TextureRegion region;

        switch (currentState){
            case JUMPING:
                region = marioJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioStand;
                break;
        }

        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y <0 && previousState == State.JUMPING)){
            return State.JUMPING;
        }else if(b2body.getLinearVelocity().y < 0){
            return State.FALLING;
        }else if(b2body.getLinearVelocity().x != 0){
            return State.RUNNING;
        }else{
            return State.STANDING;
        }
    }

    private void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32/ MarioBros.PPM, 32/ MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/ MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);

        FixtureDef fdef2 = new FixtureDef();
        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-2/MarioBros.PPM, -6/MarioBros.PPM), new Vector2(2/MarioBros.PPM, -6/MarioBros.PPM));
        fdef2.shape = feet;
        fdef2.isSensor = false;
        fdef2.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef2.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef2);


        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/MarioBros.PPM, 6/MarioBros.PPM), new Vector2(2/MarioBros.PPM, 6/MarioBros.PPM));
        fdef.shape = head;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData("head");
    }
}
