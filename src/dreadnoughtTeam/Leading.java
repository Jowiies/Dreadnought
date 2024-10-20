
package dreadnoughtTeam;

import java.io.IOException;
import static java.lang.System.out;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;



public class Leading extends StateTeam
{

	final double corners[][];

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
		sendPosition();
		out.println("Im the leader, so i'm standing doing nothing");	
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
	
	private void sendPosition()
	{
		out.println("sending current position...");
		if (stateInfo.followed == -1) {
			return;
		}

		try {
			robot.sendMessage(FIRST_NAME +" (" + stateInfo.followed + ")",
				"My coords are: " + robot.getX() + "/" + robot.getY());
			out.println(FIRST_NAME +" (" + stateInfo.followed + ") My coords are: " + robot.getX() + "/" + robot.getY());
		} 
		catch (IOException ex) {
			Logger.getLogger(Following.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
