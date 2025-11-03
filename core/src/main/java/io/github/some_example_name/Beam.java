package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Beam {
    public Sprite sprite;
    public Vector2 position;
    public Vector2 velocity;
    public Rectangle bounds;

    public Beam(Texture texture, float startX, float startY) {
        this.sprite = new Sprite(texture);
        this.position = new Vector2(startX, startY);
        sprite.setSize(50,10);
        this.velocity = new Vector2(500, 0); // (예: 초당 500픽셀 x로 날아감)
        this.bounds = new Rectangle(startX, startY, sprite.getWidth(), sprite.getHeight());
    }

    public void update(float delta) {
        position.mulAdd(velocity, delta);
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

