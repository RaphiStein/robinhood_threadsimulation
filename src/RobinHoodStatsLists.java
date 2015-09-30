import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class RobinHoodStatsLists {

	private static ArrayList<RobinHoodStats> robinHoodStatsList;

	public static void initializeStatsList(int thread_count){

		robinHoodStatsList	= new ArrayList<RobinHoodStats>(GameProperties.thread_count);

		//Initialize the challangeLists for each "thread"
		int i = 0;
		while (i < GameProperties.thread_count){
			RobinHoodStats robinHoodStats = new RobinHoodStats(i);
			robinHoodStatsList.add(robinHoodStats);
			i++;
		}

	}
	
	public synchronized static void addChallengeWon(int id){
		robinHoodStatsList.get(id).addChallengesWon();
	}
	public synchronized static void addChallengeLost(int id){
		robinHoodStatsList.get(id).addChallengesLost();
	}
	public synchronized static int getNumberOfChallengesWon(int id){
		return robinHoodStatsList.get(id).getChallengesWon();
	}
	public synchronized static int getNumberOfChallengesLost(int id){
		return robinHoodStatsList.get(id).getChallengesLost();
	}
	public synchronized static int getNumberOfChallengesTied(int id){
		return robinHoodStatsList.get(id).getChallengesTied();
	}
	public synchronized static void addChallengeTied(int id){
		robinHoodStatsList.get(id).addChallengesTied();
	}
}
