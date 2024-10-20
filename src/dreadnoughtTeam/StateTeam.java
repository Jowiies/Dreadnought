
package dreadnoughtTeam;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;


abstract class StateTeam 
{

	public StateTeam(StateTeamInfo stateInfo, Dreadnoughts robot) 
	{
		this.stateInfo = stateInfo;
		this.stateInfo.fi = false;
		this.stateInfo.id = 0;
		this.stateInfo.msgObj = null;
		this.stateInfo.msgSender = null;
		this.stateInfo.innerState = 0;
		this.robot = robot;
	}
	protected StateTeamInfo stateInfo;
	protected Dreadnoughts robot;
	
	protected final String FIRST_NAME = "dreadnoughtTeam.Dreadnoughts*";
	
	public abstract void turn();
	
	public abstract void onScannedRobot(ScannedRobotEvent e);
	
	public abstract void onMessageReceived(MessageEvent msg); 
}
