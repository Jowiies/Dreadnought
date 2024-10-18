package dreadnought;

import static java.lang.System.out;
import robocode.util.Utils;
import robocode.ScannedRobotEvent;

public class TurretMode extends State{

	public TurretMode(Dreadnought m_robot, StateInfo m_info) {
		super(m_robot, m_info);
	}


	@Override
	void turn() {
		m_robot.setAdjustRadarForRobotTurn(true);
		m_robot.setAdjustRadarForGunTurn(true);
                
                if (m_robot.getTime() - m_info.m_enemyLastSeenTime > 20) {
                    m_info.m_inerState = 0;  // Cambiar al estado de búsqueda
                }

		switch(m_info.m_inerState) {
			//case 0 -> Searching for the enemy ...
			case 0 -> {
				m_robot.setTurnRadarRight(25);
                                m_robot.setTurnGunRight(25);
			}
			
			//case 1 -> Enemy found, fire !!!	
			case 1 -> {
                            double radarTurn = m_robot.getHeading() - m_robot.getRadarHeading() + m_info.m_enemyBearing;
                            radarTurn = Utils.normalRelativeAngleDegrees(radarTurn);
                            m_robot.setTurnRadarRight(radarTurn);

                            double gunTurn = m_robot.getHeading() - m_robot.getGunHeading() + m_info.m_enemyBearing;
                            gunTurn = Utils.normalRelativeAngleDegrees(gunTurn);
                            m_robot.setTurnGunRight(gunTurn);
                            
                            double firePower = Math.min(500 / m_info.m_enemyDistance, 3);
                            m_robot.fire(firePower);
			}

		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		out.println("A robot has been scanned !!!");
		
		m_info.m_enemyBearing = e.getBearing();
		m_info.m_enemyDistance = e.getDistance();
                
                m_info.m_enemyLastSeenTime = m_robot.getTime(); 
		
		m_info.m_inerState = 1;
	}

	
}
