import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;


public class Main {


	public static void main(String[] args) {
		// TimeKeeper
		long startTime = System.currentTimeMillis();

		// Validate args input
		try {
			if (Integer.parseInt(args[0]) < 2 || Integer.parseInt(args[0]) > 20) throw new Exception();
			GameProperties.thread_count 	= Integer.parseInt(args[0]);
			if (Integer.parseInt(args[1]) < 1 || Integer.parseInt(args[1]) > 1000000) throw new Exception();
			GameProperties.iteration_count	= Integer.parseInt(args[1]);
			if (!(Integer.parseInt(args[2]) == 1 || Integer.parseInt(args[2]) == 0)) throw new Exception();
			GameProperties.skew_odds 		= Integer.parseInt(args[2]) == 1 ? true : false;
			if (!(Integer.parseInt(args[3]) == 1 || Integer.parseInt(args[3]) == 0)) throw new Exception();
			GameProperties.donations	 	= Integer.parseInt(args[3]) == 1 ? true : false;
		}
		catch (Exception e){
			System.out.println("ERROR: There was a problem with your input. Please input four integers as arguments and obey the ranges");
			System.exit(-1);
		}

		// REPORT
		System.out.println("** The Robin Fest **\n--------------------\n");
		System.out.println("Total Competitors: " + GameProperties.thread_count);
		System.out.println("Total challenge iterations: " + GameProperties.iteration_count);
		System.out.println("Skew the odds: " + (GameProperties.skew_odds ? "Yes" : "No"));
		System.out.println("Donations Expected: " + (GameProperties.donations ? "Yes" : "No"));
		System.out.println("Total Coins Available: " + GameProperties.thread_count + " * " + GameProperties.iteration_count + " = " + (GameProperties.thread_count * GameProperties.iteration_count));
		System.out.println("\nStart the contest!\n");


		// Initialize the Challenge Lists which will keep track of challenges for all Robin Hoods
		ChallengeLists.initializeChallengeLists();
		// Initialize GoldAccounts
		GoldAccounts.initializeGoldAccounts(GameProperties.thread_count, GameProperties.iteration_count);
		// Initialize Stats
		RobinHoodStatsLists.initializeStatsList(GameProperties.thread_count);

		// Initialize the ArrayList of RobinHoods
		ArrayList<RobinHood> robinHoodsRunnable = new ArrayList<RobinHood>(GameProperties.thread_count); 
		ArrayList<Thread> robinHoodThreads = new ArrayList<Thread>(GameProperties.thread_count); 

		Random random = new Random();
		// Create RobinHood threads and add them to array of threads
		// start loop from 1, since there should not be a RobinHood of ID zero or he will have zero soldiers and no chance of winning 
		for (int i = 0; i < GameProperties.thread_count; i++){
			int COMPENSATE_FOR_MINIMUM_SOLDIERS_BEING_1 = 2;
			RobinHood robinHood = new RobinHood(
					i, 
					GameProperties.skew_odds ? ((i == 0) ? 1 : random.nextInt(i) + COMPENSATE_FOR_MINIMUM_SOLDIERS_BEING_1) : random.nextInt(GameProperties.thread_count)+1); //random integer between 1 and i or 1 and thread_count

			Thread thread = new Thread(robinHood, "Robin-" + i);
			//add to runnables 	array list
			robinHoodsRunnable.add(i, robinHood);
			//add to threads 	array list
			robinHoodThreads.add(i, thread); 
		}

		// Run every thread
		for (int i = 0; i < GameProperties.thread_count; i++){
			robinHoodThreads.get(i).start();
		}

		try {
			// Wait for every thread to finish
			for (int i = 0; i < GameProperties.thread_count; i++){
				robinHoodThreads.get(i).join();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		long endTime   = System.currentTimeMillis();

		// Print out results
		System.out.println("\nCompetition Summary");
		System.out.println("-------------------\n");
		System.out.println("Coins donated:");
		// Sort the winners (and count totals)
		int totalDonated 	= 0;
		int totalRemaining 	= 0;
		Map<Integer, ArrayList<String>> resultsMap = new TreeMap<Integer, ArrayList<String>>(Collections.reverseOrder());
		for (int i = 0; i < GameProperties.thread_count; i++){
			
			//if this amount of donations already exists
			if (resultsMap.containsKey(robinHoodsRunnable.get(i).getDonations()) == false){
				ArrayList<String> arrayList = new ArrayList<String>();
				arrayList.add(new String("Robin-" + i + ": " + robinHoodsRunnable.get(i).getDonations() + " (" + robinHoodsRunnable.get(i).getGold() + " remaining)"));
				resultsMap.put(robinHoodsRunnable.get(i).getDonations(), arrayList);				
			}
			else { // this key already exists. Add result to existing array
				ArrayList<String> arrayOfRobinHoodsWithSameDonationAmount = resultsMap.get(robinHoodsRunnable.get(i).getDonations());
				arrayOfRobinHoodsWithSameDonationAmount.add(new String("Robin-" + i + ": " + robinHoodsRunnable.get(i).getDonations() + " (" + robinHoodsRunnable.get(i).getGold() + " remaining)"));
			}
			//Sum Totals
			totalDonated	= totalDonated + robinHoodsRunnable.get(i).getDonations();
			totalRemaining 	= totalRemaining + robinHoodsRunnable.get(i).getGold();
		}
		for (Map.Entry<Integer, ArrayList<String>> entry : resultsMap.entrySet()) { //Print the sorted version
			for (String string : entry.getValue()){
				System.out.println(string);
			}
		}
		System.out.println("\nTotal coins in circulation: " + totalDonated + " donated + " + totalRemaining + " remaining = " + (totalDonated+totalRemaining));

		/*
		System.out.println("Removed From Game:" );
		for(int i = 0; i < ChallengeLists.getListOfBankruptRobinHoods().size(); i++){
			System.out.println("Robin-Hood-" + ChallengeLists.getListOfBankruptRobinHoods().get(i));			
		}
		 */
		/*
		System.out.println("Game stats:" );
		for(int i = 0; i < GameProperties.thread_count; i++){
			System.out.println("Robin-" + i + " WINS: " + RobinHoodStatsLists.getNumberOfChallengesWon(i) +
					", LOSSES: " + RobinHoodStatsLists.getNumberOfChallengesLost(i) +
					", TIES: " + RobinHoodStatsLists.getNumberOfChallengesTied(i)
					);			
		}
		*/

		System.out.println("\nTotal elapsed time for competition: " + (((endTime - startTime) / 1000.0) % 60));
		//Exit
		System.exit(0);
	}
}
