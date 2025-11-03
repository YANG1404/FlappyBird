package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {




    public Sprite sprite;
    public Vector2 position;
    public Vector2 velocity;
    public Rectangle bounds;


    private PairPipe parentPipe;

    public Enemy(Texture texture, PairPipe parent){
        this.sprite = new Sprite(texture);
        this.parentPipe = parent;



        this.sprite.setSize(100,150);

        float x = parent.initalX + (parent.bottomPipe.getWidth()/2) - (sprite.getWidth()/2);
        float y = parent.bottomPipe.getY() + parent.bottomPipe.getHeight();

        this.position = new Vector2(x,y);
        this.velocity = new Vector2(0,0);
        this.bounds = new Rectangle(x,y,sprite.getWidth(),sprite.getHeight());

    }

    public void takeHit(float knockbackX){
       velocity.x = knockbackX;
    }


    public void update(float delta){

        if(position.x > parentPipe.initalX + parentPipe.bottomPipe.getWidth())
        {
            velocity.y += GameWorld.WORLD_GRAVITY * delta;
            position.mulAdd(velocity,delta);
        }
        else
        {
            float targetX = parentPipe.initalX + (parentPipe.bottomPipe.getWidth()/2) - (sprite.getWidth()/2);
            float targetY = parentPipe.bottomPipe.getY() + parentPipe.bottomPipe.getHeight();

            float newPositionX = position.x + velocity.x * delta;


            position.set(newPositionX,targetY);
            bounds.setPosition(position);

            velocity.x *= 0.95f;
            if(velocity.x < 0.1f){
                velocity.x=0;
            }

        }

        // 3. 그래픽/충돌 영역 동기화
        sprite.setPosition(position.x, position.y);
        bounds.setPosition(position.x, position.y);
    }
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Rectangle getBounds() {
        return bounds;
    }
}


