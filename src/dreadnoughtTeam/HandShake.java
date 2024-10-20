
package dreadnoughtTeam;

import java.io.IOException;
import static java.lang.System.out;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;


public class HandShake extends StateTeam
{
	boolean isSelector;
	boolean isChosen;
	
	public HandShake(StateTeamInfo stateInfo, Dreadnoughts robot) 
	{
		super(stateInfo, robot);
		isSelector = robot.getName().endsWith("(1)");
		out.println(robot.getName());
		isChosen = false;
	}

	@Override
	public void turn() 
	{
		switch (stateInfo.innerState) {
			case 0 -> {
				leaderSelection();
			}
			
			case 1 -> {
				messageReader();
				stateInfo.innerState++;
			}
			
			case 2 -> {
                            stateInfo.fi = true;
			}
		}
		
	}

	@Override
	public void onMessageReceived(MessageEvent message) 
	{
		stateInfo.msgObj = message.getMessage();
		stateInfo.msgSender = message.getSender();
		
		if (stateInfo.innerState != 2) 
			stateInfo.innerState = 1;
	}
	
	
	private void leaderSelection()
	{
		if (isSelector && !isChosen) {
			Random random = new Random();
			int leader = random.nextInt(1,6);
			try {
				robot.broadcastMessage("The leader is :" + leader);
				out.println("The leader is :" + leader);
				amITheLeader(leader);
                                stateInfo.leaderId = leader;
				isChosen = true;
			} 
			catch (IOException ex) {
				Logger.getLogger(HandShake.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		if (!stateInfo.isLeader) stateInfo.innerState = 2;
	}
	
	private void messageReader() 
	{
		if (!(stateInfo.msgObj instanceof String)) return;
		
		String msg = (String) stateInfo.msgObj;
		out.println("message: " + msg + " RECIVED");
		
		if (msg.startsWith("The leader is :")) {
			int idx = Integer.parseInt(msg.split(":")[1].trim());
			amITheLeader(idx);
			//stateInfo.leaderId = (byte)idx;
		}
		else if (msg.startsWith("My position is:")) {
			String coordinatesPart = msg.substring(msg.indexOf("(") + 1, msg.indexOf(")"));
			String[] coordinates = coordinatesPart.split(", ");

			double x = Double.parseDouble(coordinates[0]);
			double y = Double.parseDouble(coordinates[1]);
			
			try {
				robot.sendMessage(stateInfo.msgSender, "My distance is : " + Math.hypot(x, y));
			} 
			catch (IOException ex) {
				Logger.getLogger(HandShake.class.getName()).log(Level.SEVERE, null, ex);
			}
			
		}
		else if (msg.startsWith("My distance is :")) {
			
		}
	}
	
	private void amITheLeader(int idx)
	{
		int myIdx = Character.getNumericValue(robot.getName()
				.charAt(robot.getName().length() - 2));
			
		if (idx == myIdx) {
			this.stateInfo.isLeader = true;
			out.println("I am the leader.");
			try {
				robot.broadcastMessage("My position is: (" + robot.getX() + ", " + robot.getY() + ")");
			} 
			catch (IOException ex) {
				Logger.getLogger(HandShake.class.getName()).log(Level.SEVERE, null, ex);
			}
		} 
		else {
			this.stateInfo.isLeader = false;
			out.println("I am not the leader.");
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {/* NOTHING */}
}
