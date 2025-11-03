package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PairPipe {

    public Sprite topPipe;
    public Sprite bottomPipe;

    public float gapHeight=200f;

    public float initalX;

    public Rectangle topBound;
    public Rectangle bottomBound;
    public Rectangle emptyBound;

    public boolean canMove = false; // 이 파이프가 움직이는 파이프인지?
    private float moveSpeed = 10f;   // 초당 100픽셀의 속도
    private float moveRange = 100f;   // 위/아래로 100픽셀씩 (총 200px)
    private float initialCenterY;     // 최초의 Y좌표 (기준점)
    private int moveDirection = 1;

    public PairPipe(Texture pipeTexture, float centerY,float x) {
        topPipe = new Sprite(pipeTexture);
        bottomPipe = new Sprite(pipeTexture);

        topPipe.flip(false, true);

        topPipe.setSize(topPipe.getWidth()*2.5f, topPipe.getHeight()*3.0f);
        bottomPipe.setSize(bottomPipe.getWidth()*2.5f, bottomPipe.getHeight()*3.0f);


        float topY = centerY + gapHeight / 2;
        float bottomY = centerY - gapHeight / 2 - bottomPipe.getHeight();

        initialCenterY = centerY;

        topPipe.setPosition(x,topY);
        bottomPipe.setPosition(x,bottomY);
        initalX=x;

        topBound = new Rectangle(x,topY,topPipe.getWidth(),topPipe.getHeight());
        bottomBound = new Rectangle(x,bottomY,bottomPipe.getWidth(),bottomPipe.getHeight());
        emptyBound = new Rectangle(x,centerY-gapHeight/2,topPipe.getWidth(),gapHeight);
    }

    private float getCenterY() {
        return emptyBound.y + emptyBound.height / 2;
    }

    public void draw(SpriteBatch batch) {
        topPipe.draw(batch);
        bottomPipe.draw(batch);
    }

    public void setX(float newX) {
        this.initalX = newX;
        topPipe.setX(initalX);
        bottomPipe.setX(initalX);
    }



    public Rectangle getTopBound() {
        return topBound;
    }

    public Rectangle getBottomBound() {
        return bottomBound;
    }

    public Rectangle getEmptyBound() {
        return emptyBound;
    }


    public void update(float delta){
// --- 4. 움직이는 로직 구현 ---
        if (!canMove) {
            return; // 움직일 수 없는 파이프면 아무것도 안 함
        }

        // 새 Y좌표 계산
        float newCenterY = getCenterY() + (moveSpeed * moveDirection * delta);

        // 범위(Range)를 벗어났는지 확인
        if (newCenterY > initialCenterY + moveRange) {
            // 위쪽 한계 도달
            newCenterY = initialCenterY + moveRange;
            moveDirection = -1; // 방향 전환 (아래로)
        } else if (newCenterY < initialCenterY - moveRange) {
            // 아래쪽 한계 도달
            newCenterY = initialCenterY - moveRange;
            moveDirection = 1; // 방향 전환 (위로)
        }

        // 계산된 새 Y좌표로 파이프와 충돌 영역 모두 업데이트
        setVerticalPosition(newCenterY);
    }

    public void enableMovement(float range, float speed) {
        this.canMove = true;
        this.moveRange = range;
        this.moveSpeed = speed;
        // 시작 방향을 무작위로 설정
        this.moveDirection = MathUtils.randomBoolean() ? 1 : -1;
    }

    private void setVerticalPosition(float centerY) {
        float topY = centerY + gapHeight / 2;
        float bottomY = centerY - gapHeight / 2 - bottomPipe.getHeight();

        topPipe.setPosition(initalX, topY);
        bottomPipe.setPosition(initalX, bottomY);

        topBound.setPosition(initalX, topY);
        bottomBound.setPosition(initalX, bottomY);
        emptyBound.setPosition(initalX, centerY - gapHeight / 2);
    }


    public void setTexture(Texture pipeTexture){ // 빨간 색 파이프로 변환
        topPipe.setTexture(pipeTexture);
        bottomPipe.setTexture(pipeTexture);
    }
}

