
package crunchFarmCore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;


public class StorageManager {

	private static File folder;
	private static File storage;
	private static Gson gson = new Gson();
	private static Plugin plugin;
	public static void setup(String path, Plugin pluginInput) {
		plugin = pluginInput;
		folder = new File(path);
		if (!folder.exists()) folder.mkdir();
	}

	//put this stuff in a try catch so i dont need a try catch everytime i call the methods
	public static PlayerData load(UUID uuid) {
		storage = new File(folder, uuid + ".json");
		FileReader reader;
		PlayerData data = null;
		if (!storage.exists()) {
			try {
				storage.createNewFile();
				FileWriter writer = new FileWriter(storage, false);
				writer.write(gson.toJson(new PlayerData()));
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			reader = new FileReader(storage);
			JsonReader test = new JsonReader(reader);
			test.setLenient(true);
			Type temp = new TypeToken<PlayerData>() {}.getType();
			data = gson.fromJson(test, temp);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public static void saveTHREAD(UUID uuid, PlayerData playerData) {

		storage = new File(folder, uuid + ".json");
		FileWriter writer;
		try {
			writer = new FileWriter(storage, false);
			writer.write(gson.toJson(playerData));
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	}
	public static void save(UUID uuid, PlayerData playerData) {
	    new BukkitRunnable() {
	        @Override
	        public void run() {
		storage = new File(folder, uuid + ".json");
		FileWriter writer;
		try {
			writer = new FileWriter(storage, false);
			writer.write(gson.toJson(playerData));
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	        }

			    }.runTaskAsynchronously(plugin);
	}
}

