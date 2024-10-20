
package dreadnoughtTeam;

import java.io.IOException;
import static java.lang.System.out;
import java.util.logging.Level;
import java.util.logging.Logger;
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

        public Leading(StateTeamInfo stateInfo, Dreadnoughts robot) 
        {
                super(stateInfo, robot);
        }
        
        @Override
        public void turn() 
        {
                sendPosition();
                out.println("Im the leader, so i'm standing doing nothing");	
        }

        @Override
        public void onMessageReceived(MessageEvent msg) 
        {
                /*TODO*/
        }

        @Override
        public void onScannedRobot(ScannedRobotEvent e)
        {
                /*TODO*/
        }

        public Leading(StateTeamInfo stateInfo, Dreadnoughts robot) {
                super(stateInfo,robot);

                this.corners = new double[][]{
                        {robot.getBattleFieldWidth() * 0.1, robot.getBattleFieldHeight() * 0.1}, 
                                {robot.getBattleFieldWidth() * 0.9, robot.getBattleFieldHeight() * 0.1}, 
                                {robot.getBattleFieldWidth() * 0.1, robot.getBattleFieldHeight() * 0.9}, 
                                {robot.getBattleFieldWidth() * 0.9, robot.getBattleFieldHeight() * 0.9}
                };

        }



        private void sendPosition()
        {
                out.println("sending current position...");
                try {
                        robot.sendMessage(FIRST_NAME +" (" + stateInfo.followed + ")",
                                        "My coords are: " + robot.getX() + "/" + robot.getY());
                        out.println(FIRST_NAME +" (" + stateInfo.followed + ") My coords are: " + robot.getX() + "/" + robot.getY());
                } 
                catch (IOException ex) {
                        Logger.getLogger(Following.class.getName()).log(Level.SEVERE, null, ex);
                }
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
}


