
package dreadnought;

import java.awt.geom.Rectangle2D;
import static java.lang.System.out;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;



public class Escaping extends State
{
	private double predictedX, predictedY;
	public Escaping(Dreadnought m_robot, StateInfo m_info) 
	{
		super(m_robot, m_info);
	}

	@Override
	public void turn() 
	{
		m_info.m_directionAngle = getAngleToPoint(m_info.m_coordX,m_info.m_coordY);
		
		double angleDiff = Utils.normalRelativeAngleDegrees(
			m_info.m_directionAngle - m_robot.getHeading()
		);
		
		double distance = Math.hypot(
			m_info.m_coordX - m_robot.getX(),
			m_info.m_coordY - m_robot.getY()
		);
		
		m_robot.setTurnRadarRight(Double.POSITIVE_INFINITY);

		switch(m_info.m_inerState) {
			
			case 0 -> {
				if (angleDiff > 0.1) {
					m_robot.setTurnRight(angleDiff);
				}
				else {
					m_info.m_inerState = 1;
				}
			}

			//case 1 -> Heading to the corner ...
			case 1 -> {
				if (distance <= 0.1) {	
					m_robot.stop();
					m_info.m_fi = true;
				}				
				else if (willCollideWithEnemy()) {
					out.println("modifying path...");
					m_robot.setTurnRight(45);
					m_robot.setAhead(30);
				}
				else {
					out.println("moving to the corner...");
					m_robot.setTurnRight(angleDiff);	
					m_robot.setAhead(distance);	
				}	
			}
		} 
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		m_info.m_enemyBearing = m_robot.getHeading() + e.getBearing();
		m_info.m_enemyDistance = e.getDistance();
		//m_info.m_inerState = 1;
	}

		// Method to calculate the angle from your robot to a given point (x, y)
	private double getAngleToPoint(double x, double y) 
	{
		return Math.toDegrees(Math.atan2(x - m_robot.getX(), y - m_robot.getY()));
	}
	
	private void futurePosition(double distance) {
		double angle = Math.toRadians(m_robot.getHeading());
		predictedX = m_robot.getX() + Math.sin(angle) * distance;
		predictedY = m_robot.getY() + Math.cos(angle) * distance;
	}
	
	private boolean willCollideWithEnemy() {
		futurePosition(50);
		/*
		Rectangle2D.Double myBoundingBox = new Rectangle2D.Double(
			predictedX - 20, predictedY - 20, 
			predictedX + 20, predictedY + 20
		);
		Rectangle2D.Double enemyBoundingBox = new Rectangle2D.Double(
			m_info.m_enemyX - 20, m_info.m_enemyY - 20, 
			m_info.m_enemyX + 20, m_info.m_enemyY + 20
		);
		*/
		
		Rectangle2D.Double myBoundingBox = new Rectangle2D.Double(
			predictedX - 25, predictedY - 25, 45, 45
		);
		Rectangle2D.Double enemyBoundingBox = new Rectangle2D.Double(
			m_info.m_enemyX - 25, m_info.m_enemyY - 25, 45, 45
		);

		return myBoundingBox.intersects(enemyBoundingBox);
	}
}
