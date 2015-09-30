public class Challenge {
	private int challengerId;
	private int challengersSoldiers;
	private int wager;
	
	public Challenge(int challengerId, int challengersSoldiers, int wager) {
		this.challengerId = challengerId;
		this.challengersSoldiers = challengersSoldiers;
		this.wager = wager;
	}
	public int getChallengerId() {
		return challengerId;
	}
	public void setChallengerId(int challengerId) {
		this.challengerId = challengerId;
	}
	public int getChallengersSoldiers() {
		return challengersSoldiers;
	}
	public void setChallengersSoldiers(int challengersSoldiers) {
		this.challengersSoldiers = challengersSoldiers;
	}
	public int getWager() {
		return wager;
	}
	public void setWager(int wager) {
		this.wager = wager;
	}
}
