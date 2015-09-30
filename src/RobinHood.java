import java.util.Random;


public class RobinHood implements Runnable {

	// NOTE: ids start at 1, so this must be adjusted when it comes to arrays and arraylists for each Robin Hood
	private int id;
	private int maxArmySize;


	Random random;

	public RobinHood(int id, int maxArmySize){
		this.id 			= id;
		this.maxArmySize	= maxArmySize;

		//System.out.println("Robin-Hood-" + id + " created. Max Army Size: " + maxArmySize);
	}
	@Override
	public void run() {
		// Run iterations
		for (int i = 0; i < GameProperties.iteration_count; i++){
			synchronized (ChallengeLists.class) {
				// An if/else statement wrap both of the two critical functions:
				// 1) checkForChallenges
				// 2) issueChallenges
				if (ChallengeLists.isRobinHoodStillInTheGame(this.id)){
					checkForChallenges();
				}
				else {
					break; //stop the run loop if this Robin Hood ran out of money
				}
				// check again, since Robin Hood can get removed inside checkForChallenges
				// also check that there are other Robin Hoods to issue the challenge to
				if (ChallengeLists.isRobinHoodStillInTheGame(this.id) || ChallengeLists.getNumberOfActiveRobinHoods() > 1){ 
					issueChallenge(i);
				}
				else {
					break; //stop the run loop if this Robin Hood ran out of money
				}
			}
		}


		// After running all its iterations, run a loop to check if there are still any challenges outstanding for it
		while(ChallengeLists.robinHoodHasChallengeWaiting(this.id)){
			synchronized (this) {
				if (ChallengeLists.isRobinHoodStillInTheGame(this.id)){
					checkForChallenges();
				}
				else {
					break; // break out of this loop and leave
				}
			}
		}
	}

	private void checkForChallenges(){
		random = new Random();
		// Get challenge
		Challenge challenge = ChallengeLists.getChallengeForRobinHood(id);
		int soldiersToSend;
		while (challenge != null){

			// disregard this challenge is the challenger is removed from the game or can't afford his wager anymore
			int challengerId = challenge.getChallengerId();
			if (ChallengeLists.isRobinHoodStillInTheGame(challengerId) && 
					GoldAccounts.getRobinHoodBalance(challengerId) >= challenge.getWager()){

				if (GoldAccounts.getRobinHoodBalance(id) < challenge.getWager()){
					// Not enough money to match the wager. Give challenger the money and Leave the game.
					int balance = GoldAccounts.getRobinHoodBalance(this.id);
					GoldAccounts.transferFromAtoB(this.id, challenge.getChallengerId(), balance);
					ChallengeLists.removeRobinHoodFromTheGame(id);
					// Update Stats
					RobinHoodStatsLists.addChallengeWon(challengerId);
					RobinHoodStatsLists.addChallengeLost(this.id);
				}
				else { // aka if GoldAccounts.getRobinHoodBalance(id) >= challenge.getWager()
					//pick an army size
					soldiersToSend	= random.nextInt(maxArmySize);

					// Challenge Report
					//					System.out.println("----->");
					//					System.out.println("     --------------------------------------");
					//					System.out.println("    Challenger: " + challenge.getChallengerId() + " Challengee: " + id);		
					//					System.out.println("    Challenger Balance: " + GoldAccounts.getRobinHoodBalance(challenge.getChallengerId()) + ", Challengee Balance: " + GoldAccounts.getRobinHoodBalance(this.id));				
					//					System.out.println("    Challenger Soldiers: " + challenge.getChallengersSoldiers() + " Challengee Soldiers: " + soldiersToSend);				
					//					System.out.println("     --------------------------------------");
					//					System.out.println("<-----");


					//compare army size to challengers army size
					if (soldiersToSend > challenge.getChallengersSoldiers()){
						// this RobinHood wins!
						// take gold from the other RobinHood and add to our Robin Hood
						GoldAccounts.transferFromAtoB(challenge.getChallengerId(), id, challenge.getWager());

						if (isRobinHoodBankrupt(challenge.getChallengerId())){
							removeRobinHoodFromGame(challenge.getChallengerId());
						}
						if (isRobinHoodBankrupt(id)){
							removeRobinHoodFromGame(id);
						}

						// If turned on, Make Donation every tenth win
						if (GameProperties.donations){
							if (RobinHoodStatsLists.getNumberOfChallengesWon(this.id) % 10 == 0){
								GoldAccounts.makeDonation(this.id);
							}
						}

						// Update Stats
						RobinHoodStatsLists.addChallengeWon(this.id);
						RobinHoodStatsLists.addChallengeLost(challengerId);
					}
					else if(soldiersToSend < challenge.getChallengersSoldiers()){
						// this RobinHood loses!
						// take gold from the other RobinHood and add to our Robin Hood
						GoldAccounts.transferFromAtoB(id, challenge.getChallengerId(), challenge.getWager());

						// Update Stats
						RobinHoodStatsLists.addChallengeWon(challengerId);
						RobinHoodStatsLists.addChallengeLost(this.id);

						// Bankruptcy check
						if (isRobinHoodBankrupt(challenge.getChallengerId())){
							removeRobinHoodFromGame(challenge.getChallengerId());
						}
						if (isRobinHoodBankrupt(id)){
							removeRobinHoodFromGame(id);
						}
					}
					else {
						// Tie
						// Update Stats
						RobinHoodStatsLists.addChallengeTied(challengerId);
						RobinHoodStatsLists.addChallengeTied(this.id);
					}
				}
			}
			challenge = ChallengeLists.getChallengeForRobinHood(id);
		}
	}
	private void issueChallenge(int challengeNumber){
		random = new Random();

		int soldiersToSend	= random.nextInt(maxArmySize);
		// Calculate Wager
		double wagerPercentage	= (random.nextInt(GameProperties.MAX_WAGER_PERCENTAGE) + 1) / 100.0;
		double wagerUnrounded	= wagerPercentage * GoldAccounts.getRobinHoodBalance(this.id);
		int wager 				= (int) Math.round(wagerUnrounded);

		// Create the challenge
		Challenge challenge = new Challenge(this.id, soldiersToSend, wager);

		// Add challenge to a random RobinHoods challenge queue
		// Verify that challengee is still in the game
		int challengeeId;
		boolean challengeIssued = false;
		do {
			//Check if this is the last Robin Hood standing
			if (ChallengeLists.getNumberOfActiveRobinHoods() <= 1) break;

			challengeeId = random.nextInt(GameProperties.thread_count);
			if (challengeeId != this.id){ //proceed only if we didn't pick ourselves to challenge. 
				if (ChallengeLists.isRobinHoodStillInTheGame(challengeeId)){
					boolean didSucceed = ChallengeLists.addChallengeToRobinHood(challengeeId, challenge);
					if (didSucceed){
						//System.out.println("Robin-Hood-" + this.id + " issued challenge " + challengeNumber);
						challengeIssued = true; //leave loop					
					}
				}
			}
		} while (!challengeIssued);
	}

	public int getGold() {
		return GoldAccounts.getRobinHoodBalance(id);
	}
	public int getDonations() {
		return GoldAccounts.getRobinHoodDonationBalance(id);
	}
	private boolean isRobinHoodBankrupt(int id){
		if (GoldAccounts.getRobinHoodBalance(id) <= 0 ){
			return true;
		}
		else{
			return false;
		}
	}
	private void removeRobinHoodFromGame(int id){
		ChallengeLists.removeRobinHoodFromTheGame(id);
	}
}
