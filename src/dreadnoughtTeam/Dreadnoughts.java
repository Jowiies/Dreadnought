
package dreadnoughtTeam;

import robocode.TeamRobot;


public class Dreadnoughts extends TeamRobot
{
	private StateTeam m_state;
	
	@Override
	public void run() 
	{
		if (m_state == null) { /*TODO*/}
		m_state.turn();
	}
	
	
	
	
}
