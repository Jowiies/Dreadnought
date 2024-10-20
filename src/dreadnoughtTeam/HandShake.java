package dreadnoughtTeam;

import java.io.IOException;
import static java.lang.System.out;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;

public class HandShake extends StateTeam 
{

	private boolean isChosen;
	private byte counter;

	private final List<Map.Entry<String, Double>> tankDistances;
	private final List<MessageEvent> msgQueue;
	private final boolean isSelector;

	public HandShake(StateTeamInfo stateInfo, Dreadnoughts robot) 
	{
		super(stateInfo, robot);
		
		this.tankDistances = new ArrayList<>();
		this.msgQueue = new ArrayList<>();
		isSelector = robot.getName().endsWith("(1)");
		counter = 0;
		isChosen = false;
		
		initializeStateInfo(stateInfo);
	}

	private void initializeStateInfo(StateTeamInfo stateInfo) 
	{
		stateInfo.clockWise = true;
		stateInfo.isLeader = false;
		stateInfo.following = -1;
		stateInfo.followed = -1;
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
				checkResults();
			}
		}
	}

	private void checkResults() 
	{
		if (stateInfo.isLeader && counter == 4) {
			sendResults();
			robot.stop();
			stateInfo.fi = true;
		} else if (!stateInfo.isLeader && stateInfo.following != -1) {
			robot.stop();
			stateInfo.fi = true;
		}
	}

	@Override
	public void onMessageReceived(MessageEvent message) 
	{
		if (stateInfo.fi) {
			return;
		}
		if (stateInfo.isLeader && counter < 4) {
			msgQueue.add(message);
			counter++;
		}
		stateInfo.msgObj = message.getMessage();
		stateInfo.msgSender = message.getSender();
		stateInfo.innerState = 1;
	}

	private void leaderSelection() 
	{
		if (isSelector && !isChosen) {
			int leader = new Random().nextInt(1, 6);
			try {
				robot.broadcastMessage("The leader is :" + leader);
				out.println("The leader is :" + leader);
				amITheLeader(leader);
				isChosen = true;
			} catch (IOException ex) {
				Logger.getLogger(HandShake.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private void messageReader() 
	{
		if (!(stateInfo.msgObj instanceof String)) {
			return;
		}

		String msg = (String) stateInfo.msgObj;
		out.println("message: " + msg + " RECEIVED");

		if (msg.startsWith("The leader is :")) {
			readTheLeaderIs(msg);
		} 
		else if (msg.startsWith("My position is:")) {
			readMyPositionIs(msg);
		} 
		else if (msg.startsWith("Following/Followed :")) {
			readFollowingFollowed(msg);
		} 
		else if (msg.startsWith("STOP")) {
			stateInfo.innerState = 2;
		}
	}

	private void readMyPositionIs(String msg) {
		String coordinatesPart = msg.substring(msg.indexOf("(") + 1, msg.indexOf(")"));
		String[] coordinates = coordinatesPart.split(", ");

		double x = Double.parseDouble(coordinates[0]);
		double y = Double.parseDouble(coordinates[1]);
		double distance = Math.hypot(x - robot.getX(), y - robot.getY());

		out.println("My distance is : " + distance);
		sendMessageWithDistance(distance);
	}

	private void sendMessageWithDistance(double distance) {
		try {
			robot.sendMessage(stateInfo.msgSender, "My distance is : " + distance);
		} catch (IOException ex) {
			Logger.getLogger(HandShake.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void readFollowingFollowed(String msg) {
		String[] idxs = msg.split(":")[1].trim().split("/");
		stateInfo.following = (byte) Integer.parseInt(idxs[0]);
		stateInfo.followed = (byte) Integer.parseInt(idxs[1]);
		stateInfo.followed = (stateInfo.followed == getIdFromName(robot.getName())) ? -1 : stateInfo.followed;
	}
	
	private void readTheLeaderIs(String msg) {
		int idx = Integer.parseInt(msg.split(":")[1].trim());
		amITheLeader(idx);
		stateInfo.leaderId = (byte) idx;
	}

	private void amITheLeader(int idx) {
		int myIdx = getIdFromName(robot.getName());

		this.stateInfo.isLeader = (idx == myIdx);
		out.println(this.stateInfo.isLeader ? "I am the leader." : "I am not the leader.");

		if (this.stateInfo.isLeader) {
			sendMyPosition();
		}
	}

	private void sendMyPosition() 
	{
		try {
			robot.broadcastMessage("My position is: (" + robot.getX() + ", " + robot.getY() + ")");
		} catch (IOException ex) {
			Logger.getLogger(HandShake.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private byte getIdFromName(String name) 
	{
		return (byte) Character.getNumericValue(name.charAt(name.length() - 2));
	}

	private void sendResults() 
	{
		if (!msgQueue.isEmpty()) {
			for (var msg : msgQueue) {
				double distance = Double.parseDouble(((String)msg.getMessage()).split(":")[1].trim());
				tankDistances.add(new AbstractMap.SimpleEntry<>(msg.getSender(), distance));
			}
			msgQueue.clear();
		}

		tankDistances.sort(Comparator.comparing(Map.Entry::getValue));

		if (tankDistances.size() < 2) {
			return;
		}

		String followingName = tankDistances.get(0).getKey();
		String followedName = tankDistances.get(1).getKey();

		stateInfo.followed = getIdFromName(followingName);

		sendFollowingFollowed(followingName, robot.getName(), followedName);

		for (int i = 1; i < tankDistances.size() - 1; ++i) {
			followingName = tankDistances.get(i - 1).getKey();
			followedName = tankDistances.get(i + 1).getKey();
			sendFollowingFollowed(tankDistances.get(i).getKey(), followingName, followedName);
		}

		sendFollowingFollowed(followedName, tankDistances.get(tankDistances.size() - 2).getKey(), followedName);
	}

	private void sendFollowingFollowed(String receiver, String following, String followed) 
	{
		try {
			robot.sendMessage(receiver, "Following/Followed: " + getIdFromName(following) + "/" + getIdFromName(followed));
			out.println("Following/Followed: " + getIdFromName(following) + "/" + getIdFromName(followed));
		} catch (IOException ex) {
			Logger.getLogger(HandShake.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {/* DO NOTHING */}
}
