package dreadnought;

import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

public class TurretMode extends State{

	public TurretMode(Dreadnought m_robot, StateInfo m_info) {
		super(m_robot, m_info);
	}


	@Override
	void turn() {
		// TODO
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		// TODO
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public void onHitRobot(HitRobotEvent event) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	
}
