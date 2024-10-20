package dreadnoughtTeam;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import java.io.IOException;
import static java.lang.System.out;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.util.Utils;

public class Following extends StateTeam {

        private boolean readyToRead;

        public Following(StateTeamInfo stateInfo, Dreadnoughts robot) 
        {
                super(stateInfo, robot);
                readyToRead = true;
        }

        @Override
        public void turn() 
        {
                sendPosition();
                switch (stateInfo.innerState) {
                        case 0 -> {
                                messageReader();
                                stateInfo.innerState = 1;
                        }
                        case 1 -> {
                                followMyLeader();
                                readyToRead = true;
                        }
                }

                @Override
                public void onScannedRobot(ScannedRobotEvent e) 
                {
                        /*
                           switch (stateInfo.innerState) {
                           case 1, 2 -> {
                           stateInfo.enemyDistance = e.getDistance();
                           stateInfo.enemyBearing = e.getBearing();

                           if (!e.getName().equals(stateInfo.following)) {  
                           double firePower = Math.min(500 / e.getDistance(), 3);
                           robot.fire(firePower);
                           }
                           }
                           }

                        //Disparo si no es el líder o algun bot del equipo
                        if (!robot.isTeammate(e.getName())) {  
                        double firePower = Math.min(500 / e.getDistance(), 3);
                        robot.fire(firePower);
                        } else {
                        out.println("No disparar, es un compañero de equipo: " + e.getName());
                        }*/

                }

                @Override
                public void onMessageReceived(MessageEvent msg) 
                {
                        if (!readyToRead) 
                                return;

                        readyToRead = false;

                        stateInfo.msgObj = msg.getMessage();
                        stateInfo.msgSender = msg.getSender();
                        stateInfo.innerState = 0;
                }

                private void followMyLeader() 
                {
                        Double angleToLeader = getAngleToPoint(
                                        stateInfo.followCoordX, stateInfo.followCoordY);

                        Double distanceToLeader = getDistanceToPoint(
                                        stateInfo.followCoordX, stateInfo.followCoordY) * Math.cos(angleToLeader);

                        robot.setTurnRightRadians(angleToLeader);

                        /*
                         * This prevents the robot from hitting the followed robot and 
                         * going backwards. 
                         */
                        if (distanceToLeader >= 50)  
                                robot.setAhead(distanceToLeader - 50);
                }

                private double getDistanceToPoint(double x, double y) 
                {
                        x = x - robot.getX();
                        y = y - robot.getY();

                        return Math.hypot(x, y);
                }

                private double getAngleToPoint(double x, double y)
                {
                        x = x - robot.getX();
                        y = y - robot.getY();

                        return Utils.normalRelativeAngle(
                                        Math.atan2(x, y) - robot.getHeadingRadians()
                        );
                }


                private void sendPosition()
                {
                        out.println("sending current position...");
                        if (stateInfo.followed == -1) {
                                return;
                        }

                        try {
                                robot.sendMessage(FIRST_NAME +" (" + stateInfo.followed + ")",
                                                "My coords are: " + robot.getX() + "/" + robot.getY());
                                out.println(FIRST_NAME +" (" + stateInfo.followed + ") My coords are: " + robot.getX() + "/" + robot.getY());
                        } 
                        catch (IOException ex) {
                                Logger.getLogger(Following.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }

                private void messageReader()
                {
                        if (stateInfo.msgObj == null || !(stateInfo.msgObj instanceof String)) {
                                return;
                        }

                        String msg = (String) stateInfo.msgObj;
                        out.println("message: " + msg + " RECEIVED");

                        if (msg.startsWith("My coords are:")) {
                                String[] coords = (msg.split(":")[1].trim()).split("/");

                                stateInfo.followCoordX = Double.parseDouble(coords[0]);
                                stateInfo.followCoordY = Double.parseDouble(coords[1]);
                        }
                }
        }
