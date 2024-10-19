
package dreadnoughtTeam;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;


public class Dreadnoughts extends TeamRobot
{
	private StateTeam state;
	
	@Override
	public void run() 
	{
		if (state == null) {
			state = new HandShake(new StateTeamInfo(), this);
		}
		while (true) {
			state.turn();
			this.execute();
		}
	}
	
	
	@Override
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		state.onScannedRobot(e);
	}
	
	
	@Override
	public void onMessageReceived(MessageEvent msg) 
	{
		state.onMessageReceived(msg);
	}
	
	
	
}
