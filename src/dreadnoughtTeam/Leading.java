
package dreadnoughtTeam;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;



public class Leading extends StateTeam
{

	double corners[][];
	

	public Leading(StateTeamInfo stateInfo, Dreadnoughts robot) {
		super(stateInfo,robot);
		
		this.corners = new double[][]{
			{robot.getBattleFieldWidth() * 0.1, robot.getBattleFieldHeight() * 0.1}, 
			{robot.getBattleFieldWidth() * 0.9, robot.getBattleFieldHeight() * 0.1}, 
			{robot.getBattleFieldWidth() * 0.1, robot.getBattleFieldHeight() * 0.9}, 
			{robot.getBattleFieldWidth() * 0.9, robot.getBattleFieldHeight() * 0.9}
		};
		
	}
	
	@Override
	public void turn() 
	{
		/*TODO*/	
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		/*TODO*/
	}

	@Override
	public void onMessageReceived(MessageEvent msg) 
	{
		/*TODO*/
	}
	
}
