package dreadnought;

import static java.lang.Math.pow;
import static java.lang.System.out;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;


public class EnemyDetection extends State{
	
	private static final double MARGIN = 25;

	public EnemyDetection(Dreadnought m_robot, StateInfo m_info) {
		super(m_robot, m_info);
	}

	@Override
	public void turn() 
	{
		m_robot.setAdjustRadarForRobotTurn(true);
		switch (m_info.m_inerState) {
			// case 0 -> Searching for the enemy.
			case 0 -> {
				m_robot.setTurnRadarRight(360);				
			}
			// case 1 -> Enemy found, calculating corner and direction.
			case 1 -> {
				out.println("Stoping the scanning and getting furthes corner location:");
				m_robot.stop();
				
				double absBearing = Math.toRadians(m_robot.getHeading() + m_info.m_enemyBearing);

				m_info.m_enemyX = m_robot.getX() + Math.sin(absBearing) * m_info.m_enemyDistance;
				m_info.m_enemyY = m_robot.getY() + Math.cos(absBearing) * m_info.m_enemyDistance;

				double[] corner = getFurthesCorner(m_info.m_enemyX, m_info.m_enemyY);
				m_info.m_coordX = (int)corner[0];
				m_info.m_coordY = (int)corner[1];
				
				out.println("Corner coords -> " + m_info.m_coordX + ", " + m_info.m_coordY);
				
				m_info.m_fi = true;	
			}
		}
	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		out.println("A robot has been scanned !!!");
		
		m_info.m_enemyBearing = e.getBearing();
		m_info.m_enemyDistance = e.getDistance();
		
		m_info.m_inerState = 1;
	}
	
	// Pre: true
	// Post: returns an array[2] with the furthest corner coords(x,y).
	private double[] getFurthesCorner(double enemicX, double enemicY) 
	{
		double width = m_robot.getBattleFieldWidth() - MARGIN;
		double height = m_robot.getBattleFieldHeight() - MARGIN;

		double[][] cantonades = {
		    {MARGIN, MARGIN},
		    {width, MARGIN},
		    {MARGIN, height},
		    {width, height}
		};

		double[] mostDistantciaCantonada = cantonades[0];
		double maxDistancia = distance(enemicX, enemicY, cantonades[0][0], cantonades[0][1]);

		for (double[] cantonada : cantonades) {
			double dist = distance(enemicX, enemicY, cantonada[0], cantonada[1]);
			
			if (dist > maxDistancia) {
				maxDistancia = dist;
				mostDistantciaCantonada = cantonada;
			}
		}

		return mostDistantciaCantonada;
	}
	
		private double distance(double x1, double y1, double x2, double y2) 
	{
		return Math.sqrt(pow((x2 - x1),2) + pow((y2 - y1),2));
	}

	@Override
	public void onHitWall(HitWallEvent event) {
	
	}

	@Override
	public void onHitRobot(HitRobotEvent event) {
		
	}
}
