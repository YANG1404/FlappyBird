package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;



/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    public static enum GameState{
        READY,
        RUNNING,
        PAUSED,
        FAILED,
        CLEARED
    }
    GameWorld world;

    private Music backgroundMusic;
    private Sound jumpSound;
    private Sound shootSound;

    private Sound levelUpSound;





    private Sound clearSound;

    private BitmapFont font;
    private GlyphLayout layout;

    private GameState currentState;
    private SpriteBatch batch;
    private Texture background;
    private Texture base;

    private Texture birdTexture;
    private Texture pipeTexture;
    private Texture enemyTexture;
    private Texture beamTexture;
    private Texture gigachad;
    private Texture sadcat;


    private OrthographicCamera camera;

    private Viewport viewport;
    private int level;

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();

        viewport = new FitViewport(600,600, camera);
        Gdx.graphics.setWindowedMode(600, 600);


        background = new Texture("Sprite/background-day_mul4.png");
        base = new Texture("Sprite/base.png");
        birdTexture = new Texture("Sprite/yellowbird-midflap.png");
        pipeTexture = new Texture("Sprite/pipe-green.png");
        enemyTexture = new Texture("Sprite/enemy.png");
        beamTexture = new Texture("Sprite/beam.png");
        gigachad = new Texture("Sprite/GigaChad.png");
        sadcat = new Texture("Sprite/sadcat.png");





        level = 1;
        world = new GameWorld(birdTexture,pipeTexture,enemyTexture,beamTexture,level);



        camera.position.set(world.getBird().position.x+200, world.getBird().position.y, 0);

        //camera.position.set(world.getBird().position.x+700, 600, 0);

        currentState = GameState.READY;

        // --- 2. 폰트 및 레이아웃 초기화 ---
        font = new BitmapFont(); // LibGDX 기본 폰트 로드
        font.getData().setScale(1f); // 폰트 크기 2.5배
        layout = new GlyphLayout(); // 중앙 정렬 헬퍼 초기화

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/bgm.wav"));
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));
        shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.wav"));

        clearSound = Gdx.audio.newSound(Gdx.files.internal("sounds/clear.wav"));
        levelUpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/levelup.wav"));





        // --- 6. BGM 설정 및 재생 ---
        backgroundMusic.setLooping(true); // 반복 재생
        backgroundMusic.setVolume(0.2f);  // 볼륨 50%
        backgroundMusic.play();           // 재생 시작
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        this.input();
        this.logic();
        this.draw();

    }

    private void logic(){
        if(world.scoreCounting()){
            level++;
            levelUpSound.play(0.8f);
            if(level > 3){
                currentState = GameState.CLEARED;
                backgroundMusic.stop();

                clearSound.play(0.5f);
            }
            world = new GameWorld(birdTexture,pipeTexture,enemyTexture,beamTexture,level);
        }

        if(currentState == GameState.FAILED) {
            backgroundMusic.stop();
        }

        //)
        currentState =world.update(Gdx.graphics.getDeltaTime(),currentState);
        camera.position.set(world.getBird().position.x+200, world.getBird().position.y, 0);

        //camera.position.set(world.getBird().sprite.getX()+700,600,0);



        //camera.update();
    }

    private void draw(){
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(viewport.getCamera().combined);


        batch.begin();



        // 배경 및 바닥 구성
        batch.draw(background, -100, 0,3200f,1200f);
        batch.draw(base, 0, 0,800f,50f);
        batch.draw(base, 800, 0,800f,50f);
        batch.draw(base, 1600, 0,800f,50f);
        batch.draw(base, 2400, 0,800f,50f);


        world.getBird().draw(batch);
        world.draw(batch);
        if(currentState==GameState.FAILED){
            batch.draw(sadcat,camera.position.x-200, camera.position.y-200, 400, 400);
        }
        if(currentState==GameState.CLEARED) {
            batch.draw(gigachad, camera.position.x-250, camera.position.y-300, 500, 600);
        }

        batch.end();

        // --- 2. UI 그리기 (화면에 고정) ---

        // (중요) 렌더링 좌표계를 카메라가 아닌 *화면 픽셀* 기준으로 리셋합니다.
        // (0,0)은 이제 창의 '왼쪽 하단'이 됩니다.
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        // 별도의 헬퍼 함수를 호출하여 UI를 그립니다.
        drawUI(batch);
        batch.end();
    }

    private void input(){
        if(Gdx.input.justTouched()){
            if(currentState == GameState.RUNNING){

                world.getBird().jump();
                jumpSound.play(0.5f);
            }else if(currentState == GameState.READY){
                currentState = GameState.RUNNING;
                world.isStarted = true;
            }
        }
        if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
            if(level >= 3) {
                world.shootBeam();
                shootSound.play(0.5f);
            }
        }
        if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
            if(currentState == GameState.PAUSED){
                currentState = GameState.RUNNING;
            }else if(currentState == GameState.RUNNING){
                currentState = GameState.PAUSED;
            }else if(currentState == GameState.FAILED || currentState == GameState.CLEARED) {
                restart(1);
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        base.dispose();
        birdTexture.dispose();
        pipeTexture.dispose();
        enemyTexture.dispose();
        beamTexture.dispose();
        font.dispose();

        backgroundMusic.dispose();
        jumpSound.dispose();
        shootSound.dispose();
        clearSound.dispose();

    }

    @Override
    public void resize(int width, int height) {
        // 9. *** (필수) 뷰포트에 현재 실제 창 크기를 알려줍니다. ***
        // 400x400 월드 뷰를 이 크기에 맞게(Fit) 조절합니다.
        viewport.update(width, height);
    }

    public void restart(int level)
    {
        this.level = level;
        currentState = GameState.READY;
        world.dispose();
        clearSound.stop();
        backgroundMusic.setLooping(true); // 반복 재생
        backgroundMusic.setVolume(0.2f);  // 볼륨 50%
        backgroundMusic.play();
        world = new GameWorld(birdTexture,pipeTexture,enemyTexture,beamTexture,level);
    }

    private void drawUI(SpriteBatch batch) {
        String text;

        // (A) 항상 표시: 점수 (화면 왼쪽 상단)
        font.draw(batch,
            "Score: " + world.getScore(),
            20, // 왼쪽에서 20px
            Gdx.graphics.getHeight() - 20); // 위에서 20px

        font.draw(batch,
            "GoalScore: " + world.getGoalScore(),
            Gdx.graphics.getWidth() - "GoalScore: ".length()*9,
            Gdx.graphics.getHeight() - 20);

        font.draw(batch,
            "Level: " + level,
            Gdx.graphics.getWidth()/2 - "Level: ".length()*5,
            Gdx.graphics.getHeight() - 20);

        if (level >= 3) {
            font.draw(batch,
                "SPACE TO SHOOT",
                Gdx.graphics.getWidth()/2 - "SPACE TO SHOOT".length()*6,
                50f);
        }
        // (B) 상태별 UI (화면 중앙)
        switch (currentState) {
            case READY:
                text = "Touch to Start";
                break;
            case PAUSED:
                text = "PAUSED\n " + "\n(Continue : ESC)";
                break;
            case FAILED:
                text = "FAILED\n" +"\n(Retry: ESC)";
                break;
            case CLEARED:
                text = "FINISHED!\n(Retry: ESC)";
                break;
            default:
                text = ""; // RUNNING 중에는 아무것도 표시 안 함
        }

        if (!text.isEmpty()) {
            // 2. GlyphLayout으로 텍스트의 크기를 계산
            // (세 번째 파라미터 'true'는 \n 줄바꿈을 계산하라는 의미)
            layout.setText(font, text,
                com.badlogic.gdx.graphics.Color.WHITE, // 색상
                Gdx.graphics.getWidth() * 0.5f, // 최대 가로 폭 (예: 화면의 80%)
                Align.center, // 중앙 정렬
                true); // 줄바꿈 활성화

            // 3. 계산된 크기를 이용해 화면 정중앙에 배치
            float x = (Gdx.graphics.getWidth() * 0.5f) / 2;
            float y = (Gdx.graphics.getHeight() + layout.height) / 2;

            font.draw(batch, layout, x, y);
        }
    }
}
