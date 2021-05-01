package engine;

public class GameEngine implements Runnable {
    public static final int UPDATE_PER_SEC = 30;

    private final Window window;
    private final Timer timer;
    private final IGameLogic gameLogic;

    public GameEngine(String title, int width, int height, IGameLogic gameLogic) {
        this.window = new Window(title, width, height);
        this.timer = new Timer();
        this.gameLogic = gameLogic;
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanUp();
        }
    }

    protected void cleanUp() {
        gameLogic.cleanUp();
    }

    protected void init() throws Exception {
        window.init();
        timer.init();
        gameLogic.init(window);
    }

    protected void gameLoop() throws Exception {
        float interval = 1.f / UPDATE_PER_SEC;
        float timeStack = 0f;

        while (!window.shouldClose()) {
            timeStack += timer.getElapsedTime();

            input();

            while (timeStack >= interval) {
                update(interval);
                timeStack -= interval;
            }

            render();
        }
    }

    protected void input() {
        gameLogic.input(window);
    }

    protected void update(float interval) {
        gameLogic.update(interval);
    }

    protected void render() throws Exception{
        gameLogic.render(window);
        window.update();
    }
}
