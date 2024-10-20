
package dreadnoughtTeam;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import java.awt.geom.Point2D;
import static java.lang.System.out;

public class Leading extends StateTeam {
    
    double corners[][];
    
    private int currentCornerIndex;

    public Leading(StateTeamInfo stateInfo, Dreadnoughts robot) {
        super(stateInfo, robot);
        
        //Inicialización de las esquinas del rectángulo
        this.corners = new double[][] {
            {robot.getBattleFieldWidth() * 0.1, robot.getBattleFieldHeight() * 0.1}, 
            {robot.getBattleFieldWidth() * 0.9, robot.getBattleFieldHeight() * 0.1}, 
            {robot.getBattleFieldWidth() * 0.1, robot.getBattleFieldHeight() * 0.9}, 
            {robot.getBattleFieldWidth() * 0.9, robot.getBattleFieldHeight() * 0.9}
        };
        
        this.currentCornerIndex = 0;  //Índice de la esquina inicial
    }

    @Override
    public void turn() {
        switch (stateInfo.innerState) {
            case 0 -> {  //Calcular la esquina más cercana y comenzar el movimiento
                currentCornerIndex = findClosestCorner();
                stateInfo.innerState = 1;
            }

            case 1 -> {  //Moverse hacia la esquina más cercana
                double targetX = corners[currentCornerIndex][0];
                double targetY = corners[currentCornerIndex][1];
                
                if (goTo(targetX, targetY)) {
                    out.println("Llegado a la esquina " + currentCornerIndex);
                    broadcastPosition();  //Enviar la posición a los seguidores
                    stateInfo.innerState = 2;
                }
            }

            case 2 -> {  //Prepararse para ir a la siguiente esquina
                currentCornerIndex = (currentCornerIndex + 1) % corners.length;  //Sentido horario
                stateInfo.innerState = 3; 
            }

            case 3 -> {  //Moverse hacia la siguiente esquina
                double targetX = corners[currentCornerIndex][0];
                double targetY = corners[currentCornerIndex][1];
                
                if (goTo(targetX, targetY)) {  //Si alcanzamos la esquina
                    out.println("Llegado a la esquina " + currentCornerIndex);
                    broadcastPosition();  //Enviar la posición a los seguidores
                    stateInfo.innerState = 2;
                }
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

    //Método que mueve el robot hacia una posición (x, y) específica
    private boolean goTo(double x, double y) {
        double distance = getDistanceToPoint(x, y);
        double angle = getAngleToPoint(x, y);
        
        robot.turnRight(angle);
        robot.setAhead(distance);
        robot.execute();

        //Si el robot está cerca de la esquina, consideramos que ha llegado
        return (distance < 25);
    }

    //Encuentra la esquina más cercana al robot al inicio
    private int findClosestCorner() {
        double minDistance = Double.MAX_VALUE;
        int closestIndex = 0;

        for (int i = 0; i < corners.length; i++) {
            double distance = getDistanceToPoint(corners[i][0], corners[i][1]);
            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    //Calcula la distancia entre el robot y el punto (x, y)
    private double getDistanceToPoint(double x, double y) {
        x = x - robot.getX();
        y = y - robot.getY();
        return Math.hypot(x, y);
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


