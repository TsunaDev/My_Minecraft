package game.minecraft;

import engine.GameEngine;
import engine.IGameLogic;

public class App 
{
    public static void main( String[] args )
    {
        try {
            IGameLogic gameLogic = new DummyGame();
            GameEngine gameEngine = new GameEngine("Minecraft", 1024, 768, gameLogic);
            gameEngine.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
