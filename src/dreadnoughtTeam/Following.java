package dreadnoughtTeam;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import java.awt.geom.Point2D;
import static java.lang.System.out;

public class Following extends StateTeam {

    public Following(StateTeamInfo stateInfo, Dreadnoughts robot) {
        super(stateInfo, robot);
    }

    @Override
    public void turn() {
        switch (stateInfo.innerState) {
            case 0 -> {
                out.println("Buscando coordenadas del líder...");
                stateInfo.innerState = 1;
            }
            
            case 1 -> {
                if (stateInfo.followCoordX != 0 || stateInfo.followCoordY != 0) {
                    out.println("Recibidas coordenadas del líder, moviéndose...");
                    
                    double distanceToTarget = Point2D.distance(
                        robot.getX(), 
                        robot.getY(), 
                        stateInfo.followCoordX, 
                        stateInfo.followCoordY
                    );
                    
                    if (distanceToTarget > 50) {
                        goTo(stateInfo.followCoordX, stateInfo.followCoordY);
                    }
                    
                    if (distanceToTarget <= 50) {
                        out.println("Líder alcanzado, esperando nuevas coordenadas...");
                        stateInfo.innerState = 2;
                    }
                }
            }
            
            case 2 -> {
                out.println("Esperando nuevas coordenadas del líder...");
            }
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        switch (stateInfo.innerState) {
            case 1, 2 -> {
                stateInfo.enemyDistance = e.getDistance();
                stateInfo.enemyBearing = e.getBearing();

                if (!e.getName().equals(stateInfo.myLeaderId)) {  
                    double firePower = Math.min(500 / e.getDistance(), 3);
                    robot.fire(firePower);
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent msg) {
        switch (stateInfo.innerState) {
            case 0, 1, 2 -> {
                if (msg.getMessage() instanceof Point2D.Double) {
                    Point2D.Double coords = (Point2D.Double) msg.getMessage();
                    stateInfo.followCoordX = coords.getX();
                    stateInfo.followCoordY = coords.getY();
                    
                    out.println("Nuevas coordenadas recibidas: (" + stateInfo.followCoordX + ", " + stateInfo.followCoordY + ")");
                    
                    stateInfo.innerState = 1;
                }
            }
        }
    }
    
    private boolean goTo(double x, double y) {
        double distance = getDistanceToPoint(x, y);
        
        if (Math.abs(distance) > 0.01) {
            robot.setAhead(distance);
            return false;
        }
        return true;
    }

	private double getDistanceToPoint(double x, double y) 
	{
		x = x - robot.getX();
		y = y - robot.getY();

		return Math.hypot(x, y);
	}
}
