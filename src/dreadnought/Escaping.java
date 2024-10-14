
package dreadnought;

import static java.lang.System.out;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;



public class Escaping extends State
{
	private boolean turnRadarRight = true;
	public Escaping(Dreadnought m_robot, StateInfo m_info) 
	{
		super(m_robot, m_info);
	}

	@Override
	public void turn() 
	{
		m_info.m_directionAngle = getAngleToPoint(m_info.m_coordX,m_info.m_coordY);
				
		switch(m_info.m_inerState) {

			//case 0 -> Heading to the corner ...
			case 0 -> {
				oscillate();
				out.println("moving to the corner...");
				if (goTo((int)m_info.m_coordX, (int)m_info.m_coordY)) {
					m_robot.stop();
					m_info.m_fi = true;
				}
			}
			//case 1 -> Evading the enemy if necessary ...
			
			case 1 -> {
				if (m_info.m_enemyDistance < 200) {  // Only evade if the enemy is close
					getEnemyCoords();
					oscillate();
					evade();
				} else {
					m_info.m_inerState = 0;  // Return to original behavior when the enemy is far enough
				}
			}
			
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		m_info.m_enemyBearing = m_robot.getHeading() + e.getBearing();
		m_info.m_enemyDistance = e.getDistance();
		m_info.m_inerState = 1;
	}

		// Method to calculate the angle from your robot to a given point (x, y)
	private double getAngleToPoint(double x, double y) 
	{
		return Math.toDegrees(Math.atan2(x - m_robot.getX(), y - m_robot.getY()));
	}
	
	private void getEnemyCoords() {
		double absoluteBearing = Math.toRadians(m_robot.getHeading() + m_info.m_enemyBearing);

		m_info.m_enemyX = m_robot.getX() + m_info.m_enemyDistance * Math.sin(absoluteBearing);
		m_info.m_enemyY = m_robot.getY() + m_info.m_enemyDistance * Math.cos(absoluteBearing);
	}

	
	
	private boolean goTo(double x, double y) 
	{
		//adapted code from https://robowiki.net/wiki/GoTo

		x = x - m_robot.getX();
		y = y - m_robot.getY();
		double goAngle = Utils.normalRelativeAngle(
			Math.atan2(x,y) - m_robot.getHeadingRadians()
		);
		
		m_robot.setTurnRightRadians(goAngle);

		double distance = Math.hypot(x, y);
		if (distance > 0.01) {
			m_robot.setAhead(Math.cos(goAngle) * distance);
			return false;
		}
		return true;
	}
	
	private void oscillate()
	{
		double radarAngle = m_robot.getHeadingRadians() - 
			m_robot.getRadarHeadingRadians();
		m_robot.setTurnRadarRightRadians(radarAngle);
		
		if (Math.abs(radarAngle) < 0.001) {
			if (turnRadarRight) {
				m_robot.setTurnRadarRight(10);
			}
			else {
				m_robot.setTurnRadarLeft(10);
			}
			turnRadarRight = !turnRadarRight;
		}
		
	}
	
	
	private void evade() {
		double absoluteBearing = Math.toRadians(m_robot.getHeading() + m_info.m_enemyBearing);

		double evadeAngle = Math.toDegrees(absoluteBearing + Math.toRadians(90));  
		m_robot.setTurnRight(Utils.normalRelativeAngle(evadeAngle - m_robot.getHeading()));

		m_robot.ahead(100 + Math.random() * 50);  
	}

}
