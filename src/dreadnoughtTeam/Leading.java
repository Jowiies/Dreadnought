
package dreadnoughtTeam;

import static java.lang.System.out;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class Leading extends StateTeam {

        final double corners[][];
	private boolean turnRadarRight = true;
	private boolean readyToScan = false;
	byte counter;

        public Leading(StateTeamInfo stateInfo, Dreadnoughts robot) 
        {
                super(stateInfo, robot);
		double width = robot.getBattleFieldWidth();
                double height = robot.getBattleFieldHeight();

                corners = new double[][]{
                        {width*0.1, height*0.1},	//botom-left
                        {width*0.9, height*0.1},	//botom-right
                        {width*0.9, height*0.9},	//top-rigth
                        {width*0.1, height*0.9}		//top-left
                };
		
		counter = 0;
        }

        @Override
        public void turn() 
        {
                sendPosition();
                robot.setAdjustRadarForRobotTurn(true);
		robot.setAdjustRadarForGunTurn(true);
		robot.setAdjustGunForRobotTurn(false);

		switch (stateInfo.innerState) {

			//case 0 -> Pointint to the corner ...
			case 0 -> {
				readyToScan = false;
				if (turnToPoint(corners[counter][0], corners[counter][1])) {
					robot.stop();
					stateInfo.innerState = 1;
					readyToScan = true;
				}
			}

			//case 1 -> Moving to the corner ...
			case 1 -> {
				oscillate();
				out.println("moving to the corner...");
				if (goTo(corners[counter][0], corners[counter][1])) {
					robot.stop();
					counter = (byte) (counter < 3 ? counter + 1 : 0);
					stateInfo.innerState = 0;
				}
			}
			//case 2 -> Evading the enemy ...

			case 2 -> {
				readyToScan = false;
				
				robot.setAdjustRadarForRobotTurn(false);

				evade();

				readyToScan = true;

				if (stateInfo.enemyDistance < 5) {
					robot.back(100);
				} else {
					robot.ahead(50);
				}

				stateInfo.innerState = 0;
			}
		}
        }

        @Override
        public void onMessageReceived(MessageEvent msg) 
        {
                /*NOTHING*/
        }

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		if (stateInfo.fi) {
			return;
		}

		if (!readyToScan) {
			return;
		}

		stateInfo.enemyBearing = e.getBearing();
		stateInfo.enemyDistance = e.getDistance();
		getEnemyCoords();

		if (stateInfo.enemyDistance > 200) {
			return;
		}

		if (stateInfo.innerState == 1) {
			robot.stop();
			stateInfo.innerState = 2;
		}

		if (stateInfo.innerState == 2 && stateInfo.enemyDistance <= 200) {
			robot.stop();
		}

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

       // private double[] getNearCorner() {

       //         double[] mostProperaCantonada = corners[0];
       //         double minDistancia = getDistanceToPoint(corners[0][0], corners[0][1]);

       //         for (double[] corner : corners) {
       //                 double dist = getDistanceToPoint(corner[0], corner[1]);

       //                 if (dist < minDistancia) {
       //                         minDistancia = dist;
       //                         mostProperaCantonada = corner;
       //                 }
       //         }

       //         return mostProperaCantonada;
       // }
	
	private double getDistanceToPoint(double x, double y) {
		x = x - robot.getX();
		y = y - robot.getY();

		return Math.hypot(x, y);
	}

	private double getAngleToPoint(double x, double y) {
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

		if (Math.abs(distance) > 0.01) {
			robot.setAhead(distance);
			return false;

		}
		return true;
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
	
	
	@Override
        public void onHitRobot(HitRobotEvent event) {
                stateInfo.innerState = 2;
        }
}


