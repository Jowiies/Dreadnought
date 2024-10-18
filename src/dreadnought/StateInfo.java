package dreadnought;

public class StateInfo 
{

	public StateInfo() {
		this.m_id = -1;
		this.m_inerState = 0;
		this.m_fi = false;
		this.m_coordX = -1;
		this.m_coordY = -1;
		this.m_directionAngle = -1;
		this.m_enemyDistance = -1;
		this.m_enemyBearing = -1;
		this.m_enemyX = -1;
		this.m_enemyY = -1;
                this.m_enemyLastSeenTime = 0;
	}
	
	byte m_id;
	
	byte m_inerState;
	
	boolean m_fi;
	
	int m_coordX, m_coordY;
	
	double m_directionAngle;
	
	double m_enemyDistance;
	double m_enemyBearing;
	
	double m_enemyX, m_enemyY;
        
        long m_enemyLastSeenTime = 0;
}
