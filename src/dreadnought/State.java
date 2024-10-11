package dreadnought;


abstract class State extends Dreadnought
{

	public State(Dreadnought m_robot, StateInfo m_info) {
		this.m_robot = m_robot;
		this.m_info = m_info;
		this.m_info.m_fi = false;
		this.m_info.m_id++;
	}
	

	private Dreadnought m_robot;
	public StateInfo m_info;
	
	abstract void turn();
	abstract void onScannedRobot(StateInfo info);

	
}
