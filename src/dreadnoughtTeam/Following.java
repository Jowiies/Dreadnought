package dreadnoughtTeam;

import static java.lang.System.out;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import robocode.DeathEvent;
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
                readyToScan = true;
                turnRadarRight = true;
        }

        @Override
        public void turn() 
        {
		if (robot.getEnergy() <= 0.1)
			sendOnDeathMessage();
		
                sendPosition();
                
                oscillate();
               
                robot.setAdjustRadarForRobotTurn(true);
                robot.setAdjustRadarForGunTurn(true);
                robot.setAdjustGunForRobotTurn(false);
                
                switch (stateInfo.innerState) {
                        //case 0 -> reading possible messages ...
                        case 0 -> {
                                messageReader();
                                stateInfo.innerState = 1;
                        }
                        //case 1 -> heading to my leader ... 
                        case 1 -> {
                                readyToRead = false;
                                followMyLeader();
                                readyToRead = true;
                        }
                        
                        //case 2 -> Evading the enemy ...
                        case 2 -> {

                                readyToScan = false;

                                robot.setAdjustRadarForRobotTurn(false);

                                evade();

                                if (stateInfo.enemyDistance < 15) {
                                        robot.back(100);
                                } else {
                                        robot.ahead(50);
                                }

                                
                                stateInfo.innerState = 0;
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

                stateInfo.enemyBearing = e.getBearing();
                stateInfo.enemyDistance = e.getDistance();
                getEnemyCoords();

                if (stateInfo.enemyDistance > 200) {
                        return;
                }

                readyToRead = false;
               
                robot.stop();
                stateInfo.innerState = 2;

        }

        @Override
        public void onMessageReceived(MessageEvent msg) 
        {
		
                if (!readyToRead && !isOnDeathMessage(msg.getMessage())) 
                        return;
		out.println(isOnDeathMessage(msg.getMessage()));
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

        private void evade() {
                double turnAngle;

                if (stateInfo.enemyDistance >= 150) turnAngle = 30;
                else if (stateInfo.enemyDistance >= 125) {
                        turnAngle = 45;
                } 
                else if (stateInfo.enemyDistance >= 100) {
                        turnAngle = 60;
                } 
                else {
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
                else if (msg.startsWith("Followed by:")) {
                        stateInfo.followed = (byte)Integer.parseInt(msg.split(":")[1].trim());
                }
                else if (msg.startsWith("Follow:")) {
                        stateInfo.following = (byte)Integer.parseInt(msg.split(":")[1].trim());
                }
                else if (msg.startsWith("For now on you are the leader")) {
                        //TODO
                }
                
        }

       private void sendOnDeathMessage()
       {
                try {
                        robot.sendMessage(FIRST_NAME +" (" + stateInfo.following + ")",
                                        "Followed by: " + stateInfo.followed);
                        out.println(FIRST_NAME +" (" + stateInfo.following + ") For now on you're being followed by: " + stateInfo.followed);
                       
                        if (stateInfo.followed == -1)
                                return;

                        robot.sendMessage(FIRST_NAME +" (" + stateInfo.followed + ")",
                                        "Follow: " + stateInfo.following);
                        out.println(FIRST_NAME +" (" + stateInfo.followed + ") For now on you follow: " + stateInfo.following);
                } 
                catch (IOException ex) {
                        Logger.getLogger(Following.class.getName()).log(Level.SEVERE, null, ex);
                }

       };
       
        private boolean isOnDeathMessage(Object msgObj)
	{
                if (msgObj == null || !(msgObj instanceof String)) {
                        return false;
                }

                String msg = (String) msgObj;
		if (msg.startsWith("Followed by:")  || msg.startsWith("Follow:"))
			out.println(msg);
                return ((msg.startsWith("Followed by:") || msg.startsWith("Follow:")));
	}


        @Override
        public void onHitRobot(HitRobotEvent event) {

                stateInfo.innerState = 2;
        }

        @Override
        public void onDeath(DeathEvent e)
        {
                //NOTHING
		
        }
}
