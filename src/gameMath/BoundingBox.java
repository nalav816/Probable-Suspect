package gameMath;

public class BoundingBox {
    //The boxes' vertices
    private Vector topCorner, bottomCorner;


    public BoundingBox(Vector topCorner, Vector bottomCorner){
        this.topCorner = topCorner;
        this.bottomCorner = bottomCorner;
    }

    public Vector getTopRightCorner(){
        return topCorner;
    }

    public Vector getBottomLeftCorner(){
        return bottomCorner;
    }

    public int getHeight(){
        return (int)bottomCorner.getY() - (int)topCorner.getY();
    }

    public int getWidth(){
        return (int)bottomCorner.getX() - (int)topCorner.getX();
    }

    public BoundingBox move(Vector offset){
        return new BoundingBox(topCorner.add(offset), bottomCorner.add(offset));
    }

    public boolean intersect(BoundingBox other){
        if(topCorner.getX() > other.getBottomLeftCorner().getX() ||
            other.getTopRightCorner().getX() > bottomCorner.getX()){ 
                return false; 
        }

        if(topCorner.getY() > other.getBottomLeftCorner().getY() ||
        other.getTopRightCorner().getY() > bottomCorner.getY()){
            return false;
        }

        return true;
    }
}
