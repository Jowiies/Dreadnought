
package dreadnoughtTeam;

import robocode.DeathEvent;
import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;


public class Dreadnoughts extends TeamRobot
{
	private StateTeam state;
	
	@Override
	public void run() 
	{
		out.println(this.getName());
		
		if (state == null) 
			state = new HandShake(new StateTeamInfo(), this);
	
		while (true) {
			if (state.stateInfo.fi)
				setState();
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
	
	@Override
	public void onHitRobot(HitRobotEvent e)
	{
		state.onHitRobot(e);
	}

        @Override
        public void onDeath(DeathEvent e)
        {
                state.onDeath(e);
        }
	
	private void setState()
	{
		state = (state.stateInfo.isLeader) ? 
			new Leading(state.stateInfo,this) 
			: new Following(state.stateInfo,this);
	}
}
