
package dreadnoughtTeam;

import dreadnoughtTeam.Leading;
import dreadnoughtTeam.StateTeam;
import dreadnoughtTeam.StateTeamInfo;
import dreadnoughtTeam.Following;
import dreadnoughtTeam.HandShake;
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
                        if (state.stateInfo.fi) {
                            setState(state);
			}
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
	
        private void setState(StateTeam state)
	{
		switch(state.stateInfo.id) {
			case 0 -> {
				this.state = new Leading(this.state.stateInfo, this);
                                //this.state = new Following(this.state.stateInfo, this);
			}
			default -> {
			}
		}
	}
	
	
}
