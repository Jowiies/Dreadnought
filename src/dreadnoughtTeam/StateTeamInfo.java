
package dreadnoughtTeam;


public class StateTeamInfo 
{
	
	byte id;
	
	byte innerState;
	
	byte leaderId, following, followed;
	
	boolean fi;
	
	boolean clockWise;
	
	boolean isLeader;
	
	double enemyDistance, enemyBearing;
	
	double followCoordX, followCoordY;
        
        double leaderCoordX, leaderCoordY;
        
        double leaderGoCoordX, leaderGoCoordY;
	
	Object msgObj;
	String msgSender;
	
	
}
