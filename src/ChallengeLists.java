import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class ChallengeLists {
	
	
	// All RobinHoods use this list
	private static ArrayList<Queue<Challenge>> listOfChallengeLists;
	//Array of disqualified (aka bankrupt) RobinHoods
	private static ArrayList<Integer> listOfBankruptRobinHoods;
	private static int numberOfActiveRobinHoods;
	
	public static void initializeChallengeLists(){
		
		listOfChallengeLists 		= new ArrayList<Queue<Challenge>>(GameProperties.thread_count);
		listOfBankruptRobinHoods 	= new ArrayList<Integer>();
		numberOfActiveRobinHoods 	= GameProperties.thread_count;
		
		//Initialize the challangeLists for each "thread"
		int i = 0;
		while (i < GameProperties.thread_count){
			Queue<Challenge> challengeList = new LinkedList<Challenge>();
			listOfChallengeLists.add(challengeList);
			i++;
		}
	}
	
	/*
	 * Returns the next Challenge in this RobinHood's queue
	 */
	public synchronized static Challenge getChallengeForRobinHood(int robinHoodId){
		//returns null if not challenge
		if (listOfChallengeLists.get(robinHoodId) == null) return null;
		return listOfChallengeLists.get(robinHoodId).poll();
	}
	public synchronized static boolean robinHoodHasChallengeWaiting(int id){
		if (listOfChallengeLists.get(id) == null || listOfChallengeLists.get(id).peek() == null){
			return false;
		}
		else {
			return true;
		}
	}
	
	/*
	 * Adds a challenge to RobinHood's queue
	 */
	public synchronized static boolean addChallengeToRobinHood(int robinHoodId, Challenge challenge){
		//verify that RobinHood wasn't removed from the game
		if (isRobinHoodStillInTheGame(robinHoodId)){
			Queue<Challenge> queueOfChallenges 	= listOfChallengeLists.get(robinHoodId);
			queueOfChallenges.add(challenge);
			return true;
		}
		else {
			return false;
		}
	}
	
	public synchronized static void removeRobinHoodFromTheGame(int id){
		// if Robin Hood already removed by a different thread, just do nothing
		if  (isRobinHoodStillInTheGame(id)){
			System.out.println("Robin-" + id + " is now bankrupt!");
			listOfChallengeLists.set(id, null);
			listOfBankruptRobinHoods.add(id);
			numberOfActiveRobinHoods--;
		}
	}
	public synchronized static boolean isRobinHoodStillInTheGame(int id){
		if (listOfBankruptRobinHoods.contains(id)){
			return false;
		}
		else{
			return true;
		}
	}
	public synchronized static int getNumberOfActiveRobinHoods(){
		return numberOfActiveRobinHoods;
	}
	public static ArrayList<Integer> getListOfBankruptRobinHoods(){
		return listOfBankruptRobinHoods;
	}
	
	
}