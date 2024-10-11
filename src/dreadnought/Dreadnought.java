package dreadnought;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;


public class Dreadnought extends AdvancedRobot
{
	private State state;
	
	@Override
	public void run() 
	{
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
		double myX = getX();
		double myY = getY();
		double myHeading = getHeading();

		double distance = e.getDistance();

		double absBearing = Math.toRadians(myHeading + e.getBearing());

		double enemyX = myX + Math.sin(absBearing) * distance;
		double enemyY = myY + Math.cos(absBearing) * distance;
		
	}
	
	private void setState(State state)
	{
		switch(state.m_info.m_id) {
			case 0: {
				this.state = new Escaping(this,this.state.m_info);
				break;
			}
			case 1: {
				this.state = new TurretMode(this,this.state.m_info);
				break;
			}
			
			default:
				break;
		}
	}
}
