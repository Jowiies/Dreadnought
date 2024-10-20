
package dreadnoughtTeam;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import static java.lang.Math.pow;
import static java.lang.System.out;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.util.Utils;

public class Leading extends StateTeam {
    
    //double corners[][];
    private static final double MARGIN = 25;

    public Leading(StateTeamInfo stateInfo, Dreadnoughts robot) {
        super(stateInfo, robot);
    }

    @Override
    public void turn() {
        switch (stateInfo.innerState) {
            case 0 -> {  //Leader found, calculating corner and direction.
                double absBearing = Math.toRadians(robot.getHeading() + stateInfo.enemyBearing);
                
                   
                int myIdx = Character.getNumericValue(robot.getName().charAt(robot.getName().length() - 2));
			
		if (stateInfo.leaderId == myIdx) {
			this.stateInfo.isLeader = true;

                        stateInfo.leaderCoordX = robot.getX() + Math.sin(absBearing) * stateInfo.enemyDistance;
                        stateInfo.leaderCoordY = robot.getY() + Math.cos(absBearing) * stateInfo.enemyDistance;

                        double[] corner = getNearCorner(stateInfo.leaderCoordX, stateInfo.leaderCoordY);
                        stateInfo.leaderGoCoordX = (int)corner[0];
                        stateInfo.leaderGoCoordY = (int)corner[1];

                        out.println("Corner coords -> " + stateInfo.leaderGoCoordX + ", " + stateInfo.leaderGoCoordY);
                        stateInfo.innerState = 1;
		} 
		else {
                    this.stateInfo.isLeader = false;
                    stateInfo.innerState = 0;
		}
                
            }
            
            case 1 -> { //Apuntamos a la primera esquina
                turnToPoint(stateInfo.leaderGoCoordX, stateInfo.leaderGoCoordY);
                stateInfo.innerState = 2;
            }
            
            case 2 -> { //vamos a la primera esquina
                goTo(stateInfo.leaderGoCoordX, stateInfo.leaderGoCoordY);
                stateInfo.innerState = 3;

            }

            case 3 -> {  //Prepararse para ir a la siguiente esquina

            }
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // ...
    }

    @Override
    public void onMessageReceived(MessageEvent msg) {
        // ...
    }

    private boolean goTo(double x, double y) 
    {

            double distance = getDistanceToPoint(x,y);

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

    private boolean turnToPoint(double x, double y)
    {


            double radarAngle = robot.getHeadingRadians() - 
                    robot.getRadarHeadingRadians();


            if (Math.abs(getAngleToPoint(x,y)) > 0.1 || Math.abs(radarAngle) > 0.1) {
                    robot.setTurnRightRadians(getAngleToPoint(x,y));
                    robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(radarAngle));
                    return false;
            }
            return true;
    }

    private double[] getNearCorner(double leaderX, double leaderY) {
        double width = robot.getBattleFieldWidth();
        double height = robot.getBattleFieldHeight();

        double[][] cantonades = {
            {(width*0.1)-MARGIN, (height*0.1)-MARGIN},
            {(width*0.9)-MARGIN, (height*0.1)-MARGIN},
            {(width*0.1)-MARGIN, (height*0.9)-MARGIN},
            {(width*0.9)-MARGIN, (height*0.9)-MARGIN}
        };

        double[] mostProperaCantonada = cantonades[0];
        double minDistancia = distance(leaderX, leaderY, cantonades[0][0], cantonades[0][1]);

        for (double[] cantonada : cantonades) {
            double dist = distance(leaderX, leaderY, cantonada[0], cantonada[1]);

            if (dist < minDistancia) {
                minDistancia = dist;
                mostProperaCantonada = cantonada;
            }
        }

        return mostProperaCantonada;
    }

    
    private double distance(double x1, double y1, double x2, double y2) 
    {
        return Math.sqrt(pow((x2 - x1),2) + pow((y2 - y1),2));
    }


    //Calcula el ángulo que el robot debe girar para apuntar al punto (x, y)
    private double getAngleToPoint(double x, double y) {
        double dx = x - robot.getX();
        double dy = y - robot.getY();
        double angleToTarget = Math.toDegrees(Math.atan2(dx, dy));
        double robotHeading = robot.getHeading();
        double angle = angleToTarget - robotHeading;

        // Ajustar el ángulo entre -180 y 180 grados
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;

        return angle;
    }

    //Método para enviar la posición del líder a los seguidores
    private void broadcastPosition() {
        try {
            robot.broadcastMessage(new Point2D.Double(robot.getX(), robot.getY()));
            out.println("Posición enviada: (" + robot.getX() + ", " + robot.getY() + ")");
        } catch (Exception e) {
            out.println("Error enviando posición: " + e.getMessage());
        }
    }
}


