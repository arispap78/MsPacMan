package entrants.pacman.username;

import java.util.ArrayList;
import java.util.Random;
import pacman.controllers.PacmanController;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Maze;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends PacmanController {
    private MOVE myMove = MOVE.NEUTRAL;
    private Maze currentMaze;  
    private Random random = new Random();
    
    
    @Override
    public MOVE getMove(Game game, long timeDue) 
    {
        // the position of pacman
        int current = game.getPacmanCurrentNodeIndex();
        //the coordinates of pacman
        int xo=game.getNodeXCood(current);
        int yo=game.getNodeYCood(current);
        //initialization of the minimum ghost distance
        double minDistanceGhost=1000;
        //the position of the nearest ghost
        int closerGhost=0;
        //the distance that pacman should avoid the ghost
        double danger=40;
        //the maximim distance that pacman should chase the ghost
        double hunt=80;
        //the initialization of the nearest ghost
        Constants.GHOST gasper=null; 
        //print the time and the score for each move
        System.out.println("The time is: "+game.getTotalTime()+", and the score is: "+game.getScore());
        
        //find the nearest ghost
        for (Constants.GHOST ghost : Constants.GHOST.values()) 
        {
            //if the ghost is not in the lair
            if (game.getGhostLairTime(ghost)==0)
            {
                //the position of the ghost
                int ghostLocation = game.getGhostCurrentNodeIndex(ghost);
                //if the ghost is observable
                if (ghostLocation != -1)
                {
                    //the distance between the pacman and the ghost
                    double distanceGhost=game.getDistance(current,ghostLocation,Constants.DM.PATH);
                    //the ghost with the minimum distance
                    if (distanceGhost<minDistanceGhost)
                    {
                        //the minimum distance
                        minDistanceGhost=distanceGhost;
                        //the position of the nearest ghost
                        closerGhost=ghostLocation;
                        //the nearest ghost
                        gasper=ghost;
                    }         
                }
            }
        }
        
        //get the indeces of the powerpills
        int[] powerPills = game.getPowerPillIndices();
        //the list with the non observable and the not eaten power pills
        ArrayList<Integer> targets = new ArrayList<Integer>();
        
        //if the ghost is not edible or the edible time is close to end
        if(gasper!=null&&game.getGhostEdibleTime(gasper)<=30)
        {
            //if the ghost is very close to pacman
            if(minDistanceGhost<danger)
                //runaway from the ghost
                return game.getNextMoveAwayFromTarget(current, closerGhost, Constants.DM.EUCLID);
            //if the ghost is not very close to pacman
            else if(minDistanceGhost>danger&& minDistanceGhost<hunt)
            {
                //for each power pill
                for (int i = 0; i < powerPills.length; i++) 
                { 
                    Boolean powerPillStillAvailable = game.isPowerPillStillAvailable(i);
                    //if power pill is not observable or is not eaten
                    if (powerPillStillAvailable == null||powerPillStillAvailable) 
                    {
                        {
                            //add to the list
                            targets.add(powerPills[i]);
                        }
                    }
                }
                //convert the list to array
                int[] arrayPP = targets.stream().mapToInt(i -> i).toArray();
                //go to the power pill
                return game.getNextMoveTowardsTarget(current, game.getClosestNodeIndexFromNodeIndex(current, arrayPP, Constants.DM.PATH), Constants.DM.PATH);
            }
        }
        //if there is an edible ghost near to the pacman
        else if(gasper!=null&&game.getGhostEdibleTime(gasper)>30)
            //go to the power pill
            return game.getNextMoveTowardsTarget(current, closerGhost, Constants.DM.PATH);
        
        //get the position of the pills
        int[] pills = game.getPillIndices();
        
        //if the ghosts are not near
        if(minDistanceGhost>hunt)
        {
            //initialization for each direction
            int up=0;
            int down=0;
            int right=0;
            int left=0;
            //the maximum number of pills
            int max=-1;
            for (int i = 0; i < pills.length; i++) 
            {
                //which pills which are available
                Boolean pillStillAvailable = game.isPillStillAvailable(i);
                //if pills are visible
                if (pillStillAvailable != null) 
                {
                    //if pills are available
                    if (pillStillAvailable) 
                    {
                        //get the coordinates of the position of each pill
                        int xp=game.getNodeXCood(pills[i]);
                        int yp=game.getNodeYCood(pills[i]);
                        //if y coordinate of the pill is equal to y coordinate of pacman position
                        if(yo==yp)
                        {
                            //if x of pill>x of pacman
                            if(xo>xp)
                            {
                                //the pill is on the left of the pacman
                                left+=1;
                                if(left>max)
                                    max=left;
                            } 
                            //if x of pill<x of pacman
                            else if(xo<xp)
                            {
                                //the pill is on the right of pacman
                                right+=1;
                                if(right>max)
                                    max=right;
                            }                       
                        }
                        ////if x coordinate of the pill is equal to x coordinate of pacman position
                        if (xo==xp)
                        {
                            //if y of pill>y of pacman
                            if(yo>yp)
                            {
                                //the pill is up of pacman
                                up+=1;
                                if(up>max)
                                    max=up;
                            } 
                            //if y of pill<y of pacman
                            else if(yo<yp)
                            {
                                //the pill is down of pacman
                                down+=1;
                                if(down>max)
                                    max=down;
                            }                           
                        }
                    }
                    
                }
            }
            //if more pills are on the right of pacman,go right
            if(right==max)
                return MOVE.RIGHT;
            //if more pills are on the left of pacman,go left
            else if(left==max)
                return MOVE.LEFT;            
            //if more pills are up of pacman,go up
            else if(up==max)
                return MOVE.UP;
            //if more pills are down of pacman,go down
            else if(down==max)
                return MOVE.DOWN;
            //if there are no pills in any direction
            else if(left==0&&right==0&&up==0&&down==0)
            {
                //make any of the possible moves except the opposite of the last move of pacman
                MOVE[] moves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());
                {
                    if (moves.length > 0) 
                        return moves[random.nextInt(moves.length)];
                }
                /*this part of code is a try to move pacman when there is not any observable pill 
                ArrayList<Integer> targetsPills = new ArrayList<Integer>();
                for (int i = 0; i < pills.length; i++) 
                { 
                    //check with power pills are available
                    Boolean PillStillAvailable = game.isPillStillAvailable(i);
                    if (PillStillAvailable == null||PillStillAvailable==true) 
                    {
                        {
                            targetsPills.add(pills[i]);
                        }
                    }
                }
                MOVE[] moves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());
                int[] arr1 = targetsPills.stream().mapToInt(i -> i).toArray();
                if(!game.isJunction(current))
                    return game.getNextMoveTowardsTarget(current, game.getFarthestNodeIndexFromNodeIndex(current, arr1, Constants.DM.PATH), Constants.DM.PATH);
                if(game.isJunction(current))
                {
                    if (moves.length > 0) 
                        return moves[random.nextInt(moves.length)];
                }
                */
            }    
        }
        //the next move will be the same as the previous one
        return myMove;
    }
}

