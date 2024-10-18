
package dreadnoughtTeam;


abstract class StateTeam 
{
	protected StateTeamInfo m_stateInfo;
	protected Dreadnoughts m_robot;
	
	public abstract void turn();
}
