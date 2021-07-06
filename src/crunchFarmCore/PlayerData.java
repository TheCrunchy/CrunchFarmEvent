package crunchFarmCore;

import java.util.HashMap;

import org.bukkit.Material;

public class PlayerData {

	public HashMap<String, Integer> depositedTier1 = new HashMap<String, Integer>();
	public HashMap<String, Integer> depositedTier2 = new HashMap<String, Integer>();
	public HashMap<String, Integer> depositedTier3 = new HashMap<String, Integer>();
	public int Tier1 = 0;
	public int Tier2 = 0;
	public int Tier3 = 0;
	public boolean BoughtTitle = false;
	public boolean BoughtHoe = false;
	public int increasedChance = 0;
	public void AddToDepositCounts(Material mat, int amount, int tier) {
		switch (tier) {
		case 1:
			if (depositedTier1.containsKey(mat.toString())) {
				depositedTier1.put(mat.toString(), depositedTier1.get(mat.toString()) + amount);
				Tier1 += amount;
			}
			else {
				depositedTier1.put(mat.toString(), amount);
				Tier1 += amount;
			}
			break;
		case 2:
			if (depositedTier2.containsKey(mat.toString())) {
				depositedTier2.put(mat.toString(), depositedTier2.get(mat.toString()) + amount);
				Tier2 += amount;
			}
			else {
				depositedTier2.put(mat.toString(), amount);
				Tier2 += amount;
			}
			break;
		case 3: 
			if (depositedTier3.containsKey(mat.toString())) {
				depositedTier3.put(mat.toString(), depositedTier3.get(mat.toString()) + amount);
				Tier3 += amount;
			}
			else {
				depositedTier3.put(mat.toString(), amount);
				Tier3 += amount;
			}
			break;
		}
	}
	
	public int GetTierCount(Material mat, int tier) {
		switch (tier) {
		case 1:
			if (depositedTier1.containsKey(mat.toString())) {
				return depositedTier1.get(mat.toString());
				
			}	
			break;
		case 2:
			if (depositedTier2.containsKey(mat.toString())) {
				return depositedTier2.get(mat.toString());
			}	
			break;
		case 3: 
			if (depositedTier3.containsKey(mat.toString())) {
				return depositedTier3.get(mat.toString());
			}
			break;
			
		}
		return 0;
	}
}
