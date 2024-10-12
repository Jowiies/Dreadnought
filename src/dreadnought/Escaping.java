
package dreadnought;

import static java.lang.System.out;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;



public class Escaping extends State
{
	
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
		switch(m_info.m_inerState) {
			
			//case 0 -> Reseting radar bearing
			case 0 -> {
				double angleDiffRadar = Utils.normalRelativeAngleDegrees(
					m_robot.getHeading() - m_robot.getRadarHeading()
				);

				if (angleDiffRadar != 0) {
					m_robot.setTurnRadarRight(angleDiffRadar);
				}
				else {
					m_info.m_inerState++;
					m_robot.setAdjustRadarForRobotTurn(false);
				}
			}
			//case 1 -> Heading to the corner ...
			case 1 -> {
				double distance = Math.hypot(
					m_info.m_coordX - m_robot.getX(),
					m_info.m_coordY - m_robot.getY()
				);
				if (distance <= 0.1) {	
					m_robot.stop();
					m_info.m_fi = true;
				}
				else {
					m_robot.setTurnRight(angleDiff);	
					m_robot.setAhead(distance);	
				}	
			}
		} 
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		m_info.m_enemyBearing = e.getBearing();
		m_info.m_enemyDistance = e.getDistance();

	}

		// Method to calculate the angle from your robot to a given point (x, y)
	public double getAngleToPoint(double x, double y) 
	{
		return Math.toDegrees(Math.atan2(x - m_robot.getX(), y - m_robot.getY()));
	}
	
}
