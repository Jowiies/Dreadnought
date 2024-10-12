package dreadnought;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;


public class Dreadnought extends AdvancedRobot
{
	private State state;
	
	@Override
	public void run() 
	{
		if (state == null) {
			state = new EnemyDetection(this, new StateInfo());
		}
		while(true) {
			if (state.m_info.m_fi) {
				setState(state);
			}
			state.turn();
			execute();
		}
	}
	

	@Override
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		state.onScannedRobot(e);
	}
	
	private void setState(State state)
	{
		switch(state.m_info.m_id) {
			case 0 -> {
				this.state = new Escaping(this, this.state.m_info);
			}
			case 1 ->{
				this.state = new TurretMode(this, this.state.m_info);
			}
			
			default -> {
			}
		}
	}	
    

}
