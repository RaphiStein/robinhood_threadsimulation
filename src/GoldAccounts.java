import java.util.ArrayList;


public class GoldAccounts {
	
	private static int[] balances;
	private static int[] donations;
	
	public static void initializeGoldAccounts(int numberOfAccounts, int startingBalance){
		balances = new int[numberOfAccounts];
		donations = new int[numberOfAccounts];
		
		for (int i = 0 ; i < numberOfAccounts; i++){
			balances[i] = startingBalance;
		}
		for (int j = 0 ; j < numberOfAccounts; j++){
			donations[j] = 0;
		}
	}
	
	private synchronized static void deductFromRobinHood(int id, int amount){
		balances[id] = balances[id] - amount;
	}
	public synchronized static int getRobinHoodBalance(int id){
		return balances[id];
	}
	public synchronized static int getRobinHoodDonationBalance(int id){
		return donations[id];
	}
	public synchronized static void depositIntoRobinHood(int id, int amount){
		balances[id] = balances[id] + amount;
	}
	private synchronized static void depositDonation(int id, int donationAmount){
		donations[id] = donations[id] + donationAmount;
	}
	public synchronized static void transferFromAtoB(int robinHood_A_id, int robinHood_B_id, int amount){
		if (getRobinHoodBalance(robinHood_A_id) < amount){
			// Do not transfer
		}
		else {
			deductFromRobinHood(robinHood_A_id, amount);
			depositIntoRobinHood(robinHood_B_id, amount);
			
		}
	}
	public synchronized static void makeDonation(int id){
		//calculate 10 percent of balance
		int tenPercent = (int) (Math.round(getRobinHoodBalance(id)*(0.10)));
		deductFromRobinHood(id, tenPercent);
		depositDonation(id, tenPercent);
	}
}