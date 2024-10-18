
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
		if (state == null) { /*TODO*/}
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
