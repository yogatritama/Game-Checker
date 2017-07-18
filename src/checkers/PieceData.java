
package checkers;

public class PieceData {
    
    private final int x1, y1, x2, y2, pieceScore, jumpScore, kingScore, beKing;
    
    public PieceData(int x1, int y1, int x2, int y2, int pieceScore, int jumpScore, int kingScore, int beKing) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.pieceScore = pieceScore;
        this.jumpScore = jumpScore;
        this.kingScore = kingScore;
        this.beKing = beKing;
        //System.out.println(x1 + " " + y1 + " " + x2 + " " + y2 + " " + pieceScore + " " + jumpScore + " " + kingScore + " " + beKing);
    }
    
    public final boolean isJump() {
        return (x1 - x2 == 2 || x1 - x2 == -2);
    }
    
    public final int getX1() {
        return x1;
    }
    
    public final int getY1() {
        return y1;
    }
    
    public final int getX2() {
        return x2;
    }
    
    public final int getY2() {
        return y2;
    }

    public final int getPieceScore() {
        return pieceScore;
    }

    public final int getJumpScore() {
        return jumpScore;
    }

    public final int getKingScore() {
        return kingScore;
    }

    public final int getBeKing() {
        return beKing;
    }
    
}
