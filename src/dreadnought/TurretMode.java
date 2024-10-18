package dreadnought;

import static java.lang.System.out;

import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class TurretMode extends State{
	
	private static boolean readyToScan = true;

	public TurretMode(Dreadnought m_robot, StateInfo m_info) {
		super(m_robot, m_info);
	}


	@Override
	void turn() {
		m_robot.setAdjustRadarForRobotTurn(true);
		m_robot.setAdjustRadarForGunTurn(true);

		switch(m_info.m_inerState) {
			//case 0 -> Searching for the enemy ...
			case 0 -> {
				m_robot.setTurnRadarRight(10);
                                m_robot.setTurnGunRight(10);
			}
			
			//case 1 -> Enemy found, fire !!!	
			case 1 -> {
				readyToScan = false;
				// Calcular el ángulo para el radar (corregido con la función de normalización)
				double radarTurn = m_robot.getHeading() - m_robot.getRadarHeading() + m_info.m_enemyBearing;
				radarTurn = Utils.normalRelativeAngleDegrees(radarTurn);
				m_robot.setTurnRadarRight(radarTurn);

				// Calcular el ángulo para el cañón (corregido con la función de normalización)
				double gunTurn = m_robot.getHeading() - m_robot.getGunHeading() + m_info.m_enemyBearing;
				gunTurn = Utils.normalRelativeAngleDegrees(gunTurn);
				m_robot.setTurnGunRight(gunTurn);

				double firePower = Math.min(500 / m_info.m_enemyDistance, 3);
				m_robot.setFire(firePower);
				readyToScan = true;
				m_info.m_inerState = 0;
			}

		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		if (!readyToScan) 
			return;
		m_robot.stop();
		out.println("A robot has been scanned !!!");
		
		m_info.m_enemyBearing = e.getBearing();
		m_info.m_enemyDistance = e.getDistance();
		
		m_info.m_inerState = 1;
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		//TODO
	}

	@Override
	public void onHitRobot(HitRobotEvent event) {
		//TODO
	}

	
}
