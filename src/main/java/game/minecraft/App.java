package game.minecraft;

import engine.GameEngine;
import engine.IGameLogic;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        IGameLogic gameLogic = new DummyGame();
        GameEngine gameEngine = new GameEngine("Minecraft", 1024, 768, gameLogic);
        gameEngine.run();
    }
}
