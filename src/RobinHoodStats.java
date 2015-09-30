
public class RobinHoodStats {
	
	private int robinHoodId;
	private int challengesWon;
	private int challengesLost;
	private int challengesTied;
	
	public RobinHoodStats(int id) {
		this.robinHoodId = id;
		this.challengesWon = 0;
		this.challengesLost = 0;
	}
	
	public synchronized int robinHoodId(){
		return robinHoodId;
	}
	public synchronized int getChallengesWon() {
		return challengesWon;
	}
	public synchronized void addChallengesWon() {
		this.challengesWon++;
	}
	public synchronized int getChallengesLost() {
		return challengesLost;
	}
	public synchronized void addChallengesLost() {
		this.challengesLost++;
	}
	public synchronized int getChallengesTied() {
		return challengesTied;
	}
	public synchronized void addChallengesTied() {
		this.challengesTied++;
	}
}
