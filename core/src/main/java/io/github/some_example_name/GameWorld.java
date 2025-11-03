package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameWorld {

    Array<PairPipe> pipes;
    Array<Enemy> enemies;
    Array<Beam> beams;
    private Texture beamTexture;

    private int pipesNum=5;
    private float firstPipeX=300;
    private float pipeGapX=400;
    private int goalScore=5;
    private int score;
    private float nextScoreThreshold;

    private int level;

    public static final float WORLD_GRAVITY = -9.8f * 200; // 중력

    //public boolean isRunning = false;
    public boolean isStarted = false;

    private Sound passSound;

    private Sound failedMusic;


    private Bird bird;
    private Texture enemyTexture;
    private Texture pipeTexture_Red;


    public GameWorld(Texture birdTexture, Texture pipeTexture, Texture enemyTexture, Texture beamTexture, int level) {
        this.bird = new Bird(birdTexture, 100, 600, 100+((level-1)*50f));
        this.pipes = new Array<>();
        this.enemies = new Array<>();
        this.enemyTexture = enemyTexture;
        this.beams = new Array<>();
        this.beamTexture = beamTexture;
        this.level = level;
        this.passSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pass.wav"));

        this.failedMusic = Gdx.audio.newSound(Gdx.files.internal("sounds/failedmusic.wav"));

        this.pipeTexture_Red = new Texture("Sprite/pipe-red.png");

        this.goalScore = 5;
        this.pipesNum = goalScore;
        this.score = 0;
        this.firstPipeX = 300;
        this.pipeGapX = 400;
        // 첫 번째 점수 목표는 첫 파이프의 시작 위치
        this.nextScoreThreshold = firstPipeX;

        // Pipe 및 enemy 생성
        placePipe(pipeTexture,level);

    }

    public Main.GameState update(float delta, Main.GameState currentState) {
        if(currentState==Main.GameState.RUNNING){
            bird.velocity.y += WORLD_GRAVITY * delta;
            bird.position.y += bird.velocity.y * delta;
            bird.position.x += bird.velocity.x * delta;
        }

        bird.sprite.setPosition(bird.position.x,bird.position.y);
        Rectangle birdBound = bird.sprite.getBoundingRectangle();

        //pipe 생성 및 충돌 검사
        for(PairPipe pair : pipes){
            if(currentState==Main.GameState.RUNNING){
                pair.update(delta);
            }
            if(birdBound.overlaps(pair.getTopBound())||birdBound.overlaps(pair.getBottomBound())||bird.sprite.getY()<50f){
                currentState= gameFailed(currentState);
            }
        }
        if(level >=3){
            //enemy 생성 및 충돌 검사
            for (Enemy enemy : enemies) {

                    enemy.update(delta);

                if (birdBound.overlaps(enemy.getBounds())) {
                    currentState= gameFailed(currentState);
                }
            }

            //beam 생성 및 충돌
            for (int i = beams.size - 1; i >= 0; i--) {
                Beam beam = beams.get(i);
                beam.update(delta);

                boolean beamHit = false; // 빔이 무언가에 맞았는지 여부

                // (A) 빔 vs 파이프 충돌
                for (PairPipe pipe : pipes) {
                    if (beam.getBounds().overlaps(pipe.getTopBound()) ||
                        beam.getBounds().overlaps(pipe.getBottomBound())) {

                        beamHit = true; // 파이프에 맞음
                        break; // 파이프 순회 중단
                    }
                }

                if (beamHit) {
                    beams.removeIndex(i); // 빔 제거
                    continue; // 다음 빔으로
                }

                // (B) 빔 vs 적 충돌
                for (Enemy enemy : enemies) {
                    if (beam.getBounds().overlaps(enemy.getBounds())) {

                        // (가정) Enemy.java에 takeHit(넉백힘) 메서드가 있음
                        enemy.takeHit(500f); // 왼쪽(-x)으로 150의 힘으로 넉백

                        beamHit = true; // 적에게 맞음
                        break; // 적 순회 중단
                    }
                }

                if (beamHit) {
                    beams.removeIndex(i); // 빔 제거
                    continue; // 다음 빔으로
                }

                // (C) 빔이 화면 밖으로 나갔는지 검사
                if (beam.position.x >  bird.position.x+ 1000) { // (경계 + 여유분 50px)
                    beams.removeIndex(i); // 빔 제거
                }
            }
        }




        if(scoreCounting()){
            currentState = Main.GameState.READY;
        }
        return currentState;
    }

    public Main.GameState gameFailed(Main.GameState currentState)
    {
        bird.velocity.x = 0;
        if(currentState == Main.GameState.RUNNING){
            failedMusic.play(0.6f);
        }
        return Main.GameState.FAILED;
    }
    public Bird getBird() {
        return bird;
    }

    public void draw(SpriteBatch batch){
        for(PairPipe pair : pipes){
            pair.draw(batch);
        }
        for (Enemy enemy : enemies) {
            enemy.draw(batch);
        }
        for(Beam beam : beams){
            beam.draw(batch);
        }
    }

    public int getScore(){
        return this.score;
    }
    public int getGoalScore(){
        return this.goalScore;
    }
    public boolean scoreCounting(){

        // 2. 새의 x좌표가 '다음 점수 기준선'을 넘었는지 확인
        if (bird.position.x > nextScoreThreshold) {

            // 3. 점수 1 증가
            score++;
            passSound.play(0.2f);
            System.out.println("Score: " + score); // 콘솔 로그

            // 4. (중요) '다음 점수 기준선'을 다음 파이프 위치로 업데이트
            // 예: score가 1이 되면, 다음 기준선은 firstPipeX + (1 * pipeGapX) = 600이 됨
            nextScoreThreshold = firstPipeX + (score * pipeGapX);
        }
        if (score >= pipesNum) {
            return true;
        }else{
            return false;
        }
    }

    public void shootBeam() {
        // 새의 오른쪽 중간에서 발사되도록 위치 계산
        float startX = bird.position.x + bird.sprite.getBoundingRectangle().getWidth();
        float startY = bird.position.y + bird.sprite.getBoundingRectangle().getHeight() / 2;

        Beam newBeam = new Beam(this.beamTexture, startX, startY);
        beams.add(newBeam);
    }

    public void dispose() {
        // GameWorld가 직접 생성한 리소스들을 해제합니다.
        passSound.dispose();

        pipeTexture_Red.dispose();
        failedMusic.dispose();
    }

    public void placePipe(Texture pipeTexture, int level){
        for(int i=0;i<pipesNum;i++){
            float x = firstPipeX + (i * pipeGapX);
            float centerY = MathUtils.random(400,800);
            pipes.add(new PairPipe(pipeTexture,centerY,x));
        }

        // 파이프 움직임 구현 level >= 2 일 때
        if(level >= 2){
            int movingPipeCount = MathUtils.random(1, pipesNum);
            //int movingPipeCount =5;
            pipes.shuffle();
            for (int i = 0; i < movingPipeCount; i++) {
                PairPipe pipeToMove = pipes.get(i);
                pipeToMove.setTexture(pipeTexture_Red);
                pipeToMove.enableMovement(100f, 50f);

            }
        }

        if(level >= 3){
            int enemyCount = MathUtils.random(1, pipesNum);
            pipes.shuffle(); // 파이프 순서 섞기

            for (int i = 0; i < enemyCount; i++) {
                // (1) 적을 배치할 '대상' 파이프 선택
                PairPipe targetPipe = pipes.get(i);

                // (2) Enemy 객체 생성 (이때 부모-자식 관계 '연결')
                Enemy newEnemy = new Enemy(enemyTexture, targetPipe);

                // (3) GameWorld의 'enemies' 리스트에 추가
                this.enemies.add(newEnemy);
            }
        }
    }
}
