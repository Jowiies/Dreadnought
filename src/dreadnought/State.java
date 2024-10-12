package dreadnought;

import robocode.ScannedRobotEvent;

abstract class State
{

	public State(Dreadnought m_robot, StateInfo m_info) {
		this.m_robot = m_robot;
		this.m_info = m_info;
		this.m_info.m_fi = false;
		this.m_info.m_id++;
		this.m_info.m_inerState = 0;
	}
	

	protected Dreadnought m_robot;
	protected StateInfo m_info;
	
	abstract void turn();
	public abstract void onScannedRobot(ScannedRobotEvent e);
}
