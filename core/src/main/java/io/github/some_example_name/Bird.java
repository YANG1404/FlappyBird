package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bird {
    public Sprite sprite;

    public Vector2 position;
    public Vector2 velocity;



    public Bird(Texture texture, float startX, float startY, float speed) {
        // 물리 상태 초기화
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(speed, 0); // 처음엔 정지

        this.sprite = new Sprite(texture);
        this.sprite.setSize(sprite.getWidth() * 1.5f, sprite.getHeight() * 1.5f);
        this.sprite.setPosition(position.x, position.y);

    }

    public void jump(){
        velocity.y = 400; // 점프
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
