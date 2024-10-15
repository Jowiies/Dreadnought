
package dreadnought;

import static java.lang.System.out;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;



public class Escaping extends State
{
	private boolean turnRadarRight = true;
	private boolean readyToScan = false;
	
	public Escaping(Dreadnought m_robot, StateInfo m_info) 
	{
		super(m_robot, m_info);
	}

	@Override
	public void turn() 
	{
		m_robot.setAdjustRadarForRobotTurn(true);
		m_robot.setAdjustRadarForGunTurn(true);
		
		switch(m_info.m_inerState) {
			
			case 0 -> {
				readyToScan = false;
				if (turnToPoint(m_info.m_coordX, m_info.m_coordY)) {
					m_robot.stop();
					m_info.m_inerState = 1;
					readyToScan = true;
				}
			}
			
			//case 0 -> Heading to the corner ...
			case 1 -> {
				oscillate();
				
				out.println("moving to the corner...");
				if (goTo(m_info.m_coordX, m_info.m_coordY)) {
					m_robot.stop();
					m_info.m_fi = true;
				}
			}
			//case 1 -> Evading the enemy if necessary ...
			
			case 2 ->  {
				readyToScan = false;
				m_robot.stop();
				m_robot.setAdjustRadarForRobotTurn(false);
				m_robot.setAdjustRadarForGunTurn(false);
				
				
				m_robot.fire(Rules.MIN_BULLET_POWER);
				
				double turnAngle = (getDistanceToPoint(m_info.m_enemyX, m_info.m_enemyY) >=100)  ? 45 : 90;
				//double enemyAngle = Utils.normalRelativeAngle(
				//	m_robot.getHeading() + m_info.m_enemyBearing
				//);
				//Utils.normalRelativeAngle(enemyAngle - m_robot.getHeading())
				if (getAngleToPoint(m_info.m_enemyX, m_info.m_enemyY) > 0) {
					out.println("turning right...");
					m_robot.turnRight(turnAngle);
				}
				else {
					out.println("turning left...");
					m_robot.turnLeft(-turnAngle);
				}
				//out.println("EnemyAngle-> " + Math.toDegrees(angleToEnemy));
				//out.println("CornerAngle-> " + Math.toDegrees(angleToCorner));
				
				readyToScan = true;
				if (getDistanceToPoint(m_info.m_enemyX, m_info.m_enemyY) < 5) {
					m_robot.back(100);
				}
				else {
					m_robot.ahead(100);
				}
				
				m_info.m_inerState = 0;
			}
			
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		if (m_info.m_fi)
			return;
		
		if (!readyToScan)
			return;
		
		m_info.m_enemyBearing = m_robot.getHeading() + e.getBearing();
		m_info.m_enemyDistance = e.getDistance();
		getEnemyCoords();	
		
		if (getDistanceToPoint(m_info.m_enemyX, m_info.m_enemyY) > 200)
			return;
		
		if (m_info.m_inerState == 1 ) {
			m_info.m_inerState = 2;
		}

	}


	private double getAngleToPoint(double x, double y) 
	{
		x = x - m_robot.getX();
		y = y - m_robot.getY();
		
		return Utils.normalRelativeAngle(
			Math.atan2(x,y) - m_robot.getHeadingRadians()
		);
	}
	
	private void getEnemyCoords() {
		double absoluteBearing = Math.toRadians(m_robot.getHeading() + m_info.m_enemyBearing);

		m_info.m_enemyX = m_robot.getX() + m_info.m_enemyDistance * Math.sin(absoluteBearing);
		m_info.m_enemyY = m_robot.getY() + m_info.m_enemyDistance * Math.cos(absoluteBearing);
	}
	
	private boolean turnToPoint(double x, double y)
	{
		//adapted code from https://robowiki.net/wiki/GoTo
		
		
		double radarAngle = m_robot.getHeadingRadians() - 
			m_robot.getRadarHeadingRadians();
		
		
		if (Math.abs(getAngleToPoint(x,y)) > 0.1 || Math.abs(radarAngle) > 0.1) {
			m_robot.setTurnRightRadians(getAngleToPoint(x,y));
			m_robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(radarAngle));
			return false;
		}
		return true;
	}

	
	
	private boolean goTo(double x, double y) 
	{
		//adapted code from https://robowiki.net/wiki/GoTo
		
		double distance = getDistanceToPoint(x,y);
		
		if (Math.abs(distance) > 0.01) {
			m_robot.setAhead(distance);
			return false;
		
		}
		return true;
	}
	
	private void oscillate()
	{
		double radarAngle = m_robot.getHeadingRadians() - 
			m_robot.getRadarHeadingRadians();
		
		m_robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(radarAngle));
		
		double oscillation = Math.toRadians(15);
		
		if (Math.abs(radarAngle) < 0.01) {
			if (turnRadarRight) {
				m_robot.setTurnRadarRightRadians(oscillation);
			}
			else {
				m_robot.setTurnRadarLeftRadians(oscillation);
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
	
	@Override
	public void onHitWall(HitWallEvent event) 
	{
		//m_robot.back(50);
		//m_robot.setTurnLeft(Math.toRadians(45));
	}

	@Override
	public void onHitRobot(HitRobotEvent event) 
	{
		if (m_robot.getVelocity() > 0) {
			m_robot.back(100);
		}
		else {
			m_robot.ahead(100);
		}
		//m_robot.setTurnLeft(Math.toRadians(45));
	}

}
