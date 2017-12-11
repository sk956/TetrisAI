package tetris;
/*source : https://rosettacode.org/wiki/Tetris/Java */
public class Scoreboard {
    static final int MAXLEVEL = 9;
 
    private int level;
    private int lines;
    private int score;
    private int topscore;
    private boolean gameOver = true;
 
    void reset() {
        setTopscore();
        level = lines = score = 0;
        gameOver = false;
    }
 
    void setGameOver() {
        gameOver = true;
    }
 
    boolean isGameOver() {
        return gameOver;
    }
 
    void setTopscore() {
        if (score > topscore)
            topscore = score;
    }
 
    int getTopscore() {
        return topscore;
    }
 
    int getSpeed() {
 
       return 50;
    }
 
    void addScore(int sc) {
        score += sc;
    }
 
    void addLines(int line) {
 
        switch (line) {
            case 1:
                addScore(10);
                break;
            case 2:
                addScore(20);
                break;
            case 3:
                addScore(30);
                break;
            case 4:
                addScore(40);
                break;
            default:
                return;
        }
 
        lines += line;
        if (lines > 10)
            addLevel();
    }
 
    void addLevel() {
        lines %= 10;
        if (level < MAXLEVEL)
            level++;
    }
 
    int getLevel() {
        return level;
    }
 
    int getLines() {
        return lines;
    }
 
    int getScore() {
        return score;
    }
}