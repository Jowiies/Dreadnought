
package dreadnought;

import static java.lang.System.out;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
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
		oscillate();
		switch(m_info.m_inerState) {

			//case 0 -> Heading to the corner ...
			case 0 -> {
				m_robot.setAdjustRadarForRobotTurn(true);
				m_robot.setAdjustRadarForGunTurn(true);
				
				out.println("moving to the corner...");
				if (goTo((int)m_info.m_coordX, (int)m_info.m_coordY)) {
					m_robot.stop();
					m_info.m_fi = true;
				}
			}
			//case 1 -> Evading the enemy if necessary ...
			
			case 1 ->  {
				m_robot.setAdjustRadarForRobotTurn(false);
				m_robot.setAdjustRadarForGunTurn(false);
				double angleToCorner = getAngleTankToPoint(m_info.m_coordX,m_info.m_coordY);
				double angleToEnemy = getAngleTankToPoint(m_info.m_enemyX, m_info.m_enemyY);
				//double turnAngle = (Utils.normalRelativeAngle(m_robot.getHeadingRadians()) >= 0) ? 45 : -45;
				double turnAngle = 45;
				
				if (angleToCorner >= angleToEnemy) {
					m_robot.turnRight(turnAngle);
				}
				else {
					m_robot.turnLeft(turnAngle);
				}
				out.println("EnemyAngle-> " + angleToEnemy);
				out.println("CornerAngle-> " + angleToCorner);
				m_info.m_inerState = 2;
			}
			
			case 2 -> {
				m_robot.ahead(50);
				m_info.m_inerState = 0;
			}
			
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		if (m_info.m_fi)
			return;
		
		m_info.m_enemyBearing = m_robot.getHeading() + e.getBearing();
		m_info.m_enemyDistance = e.getDistance();	
		getEnemyCoords();
				
		if (m_info.m_enemyDistance > 150)
			return;
		
		if (m_info.m_inerState == 2)
			m_robot.stop();
		
		if (m_info.m_inerState == 0 ) {
			m_info.m_inerState = 1;
		}

	}

		// Method to calculate the angle from your robot to a given point (x, y)
	private double getAngleToPoint(double x, double y) 
	{
		return Utils.normalRelativeAngle(
			Math.atan2(x,y) - m_robot.getHeadingRadians()
		);
	}
	
	private double getAngleTankToPoint(double x, double y) 
	{
		double m = (y - m_robot.getY())/ (x - m_robot.getX());
		return Math.atan(m);
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
		
		m_robot.setTurnRightRadians(getAngleToPoint(x,y));

		double distance = Math.hypot(x, y);
		if (distance > 0.01) {
			m_robot.setAhead(Math.cos(getAngleToPoint(x,y)) * distance);
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
				m_robot.setTurnRadarRight(20);
			}
			else {
				m_robot.setTurnRadarLeft(20);
			}
			turnRadarRight = !turnRadarRight;
		}
		
	}
	
	private double getDistanceToPoint(double x, double y) 
	{
		x = x - m_robot.getX();
		y = y - m_robot.getY();

		return Math.hypot(x, y);
	}
	/*
	private void evade(double x, double y) {
		
		x = x - m_robot.getX();
		y = y - m_robot.getY();
		
		m_robot.setTurnRightRadians(getAngleToPoint(x,y) + Math.PI/4);
		
		m_robot.setAhead(Math.cos(getAngleToPoint(x,y) + Math.PI/4) * 
			getDistanceToPoint(m_info.m_enemyX,m_info.m_enemyY));
	}
	*/
	
	
	
	
	@Override
	public void onHitWall(HitWallEvent event) {
		//m_robot.back(50);
		//m_robot.setTurnLeft(Math.toRadians(45));
	}

	@Override
	public void onHitRobot(HitRobotEvent event) {

		m_robot.back(100);
		//m_robot.setTurnLeft(Math.toRadians(45));
	}

}
