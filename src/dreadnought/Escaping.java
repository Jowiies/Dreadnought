
package dreadnought;

import robocode.ScannedRobotEvent;


public class Escaping extends State{

	public Escaping(Dreadnought m_robot, StateInfo m_info) {
		super(m_robot, m_info);
	}

	@Override
	public void turn() {
		switch(m_info.m_inerState) {
			//case 0 -> Heading to the corner ...
			case 0 -> {
				
			}
			//case 1 -> Enemy found, evading...
			case 1 -> {
				
			}
			//case 2 -> Correcting path ...
		} 
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
	}

	
}
