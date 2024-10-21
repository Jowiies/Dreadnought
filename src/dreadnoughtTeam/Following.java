package dreadnoughtTeam;

import static java.lang.System.out;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class Following extends StateTeam {

        private boolean readyToRead;
        private boolean turnRadarRight;
        private boolean readyToScan;

        public Following(StateTeamInfo stateInfo, Dreadnoughts robot) 
        {
                super(stateInfo, robot);
                readyToRead = true;
                readyToScan = false;
                turnRadarRight = true;
        }

        @Override
        public void turn() 
        {
                sendPosition();
                robot.setAdjustRadarForRobotTurn(true);
                robot.setAdjustRadarForGunTurn(true);
                robot.setAdjustGunForRobotTurn(false);
                oscillate();
		out.println("reading = " + readyToRead);
		
		out.println("scanning = " + readyToScan);
		
		out.println("state = " + stateInfo.innerState);
                switch (stateInfo.innerState) {
                        case 0 -> {
                                messageReader();
                                stateInfo.innerState = 1;
                        }

                        case 1 -> {
                                //readyToScan = false;
                                //	if (turnToPoint(stateInfo.followCoordX, stateInfo.followCoordY)) {
                                //		stateInfo.innerState = 2;
                                //		readyToScan = true;
                                //	}
                                readyToRead = false;
                                //oscillate();
                                followMyLeader();
                                readyToRead = true;

                        }

                        //case 2 -> Moving to the corner ...
                        case 2 -> {
                                oscillate();

                                if (goTo(stateInfo.followCoordX, stateInfo.followCoordY)) {
                                        stateInfo.innerState = 0;
                                        readyToRead = true;

                                }
                                //readyToRead = true;
                        }
                        //case 3 -> Evading the enemy ...

                        case 3 -> {

                                readyToScan = false;

                                robot.setAdjustRadarForRobotTurn(false);

                                evade();

                                if (stateInfo.enemyDistance < 5) {
                                        robot.back(100);
                                } else {
                                        robot.ahead(50);
                                }

                                //stateInfo.innerState = 1;
                                stateInfo.innerState = 1;
				readyToScan = true;
                        }
                }
        }

        @Override
        public void onScannedRobot(ScannedRobotEvent e) {
                if (stateInfo.fi) {
                        return;
                }

                if (!readyToScan) {
                        return;
                }

                String name = FIRST_NAME + " (" + stateInfo.following + ")";
                if (name.equals(e.getName()))
                        return;
                //		if (e.getName().contains(FIRST_NAME))
                //			return;

                stateInfo.enemyBearing = e.getBearing();
                stateInfo.enemyDistance = e.getDistance();
                getEnemyCoords();

                if (stateInfo.enemyDistance > 200) {
                        return;
                }

                readyToRead = false;

                // if (stateInfo.innerState == 2) {
                //     robot.stop();
                //     stateInfo.innerState = 3;
                // }

                // if (stateInfo.innerState == 3 && stateInfo.enemyDistance <= 200) {
                //     robot.stop();
                // }
               
                robot.stop();
                stateInfo.innerState = 3;

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

        private void getEnemyCoords() {
                double absoluteBearing = Math.toRadians(robot.getHeading() + stateInfo.enemyBearing);

                stateInfo.enemyX = robot.getX() + stateInfo.enemyDistance * Math.sin(absoluteBearing);
                stateInfo.enemyY = robot.getY() + stateInfo.enemyDistance * Math.cos(absoluteBearing);
        }

        private boolean turnToPoint(double x, double y) {
                double radarAngle = robot.getHeadingRadians()
                        - robot.getRadarHeadingRadians();

                if (Math.abs(getAngleToPoint(x, y)) > 0.1 || Math.abs(radarAngle) > 0.1) {
                        robot.setTurnRightRadians(getAngleToPoint(x, y));
                        robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(radarAngle));
                        return false;
                }
                return true;
        }

        private boolean goTo(double x, double y) {

                double distance = getDistanceToPoint(x, y);

                if (Math.abs(distance) <= 50)
                        return true;

                if (distance >= 50)
                        robot.setAhead(distance);


                return false;

        }

        private void evade() {
                double turnAngle;

                if (stateInfo.enemyDistance >= 150) {
                        turnAngle = 30;
                } else if (stateInfo.enemyDistance >= 125) {
                        turnAngle = 45;
                } else if (stateInfo.enemyDistance >= 100) {
                        turnAngle = 60;
                } else {
                        turnAngle = 90;
                }

                if (stateInfo.enemyBearing < 0) {
                        robot.turnRight(turnAngle);
                } else {
                        robot.turnLeft(turnAngle);
                }
        }

        private void oscillate() {
                double radarAngle = robot.getHeadingRadians()
                        - robot.getRadarHeadingRadians();

                robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(radarAngle));

                double oscillation = Math.toRadians(13);

                if (Math.abs(radarAngle) < 0.01) {
                        if (turnRadarRight) {
                                robot.setTurnRadarRightRadians(oscillation);
                        } else {
                                robot.setTurnRadarLeftRadians(oscillation);
                        }
                        turnRadarRight = !turnRadarRight;
                }

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

        private byte getIdFromName(String name) {
                return (byte) Character.getNumericValue(name.charAt(name.length() - 2));
        }


        @Override
        public void onHitRobot(HitRobotEvent event) {

                stateInfo.innerState = 3;
        }

}
