package yorickbm.towerdefence.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.TreeMap;

/**
 * Source: https://www.spigotmc.org/threads/tutorial-a-guide-on-json-configurations.181365/
 * Modified By: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class JsonConfig {

    private File file;
    private JSONObject json; // org.json.simple
    private JSONParser parser = new JSONParser();

    public JsonConfig(File file) {
        this.file = file;
        reload();
    }

    @SuppressWarnings("unchecked")
    public void reload() {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                PrintWriter pw = new PrintWriter(file, "UTF-8");
                pw.print("{");
                pw.print("}");
                pw.flush();
                pw.close();
            }
            json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean save(JSONObject data) {
        try {
            TreeMap<String, Object> treeMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
            treeMap.putAll(data);

            Gson g = new GsonBuilder().setPrettyPrinting().create();
            String prettyJsonString = g.toJson(treeMap);

            FileWriter fw = new FileWriter(file);
            fw.write(prettyJsonString);
            fw.flush();
            fw.close();

            reload();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public String getRawData(String key) {
        return json.containsKey(key) ? json.get(key).toString() : key;
    }

    public String getString(String key) {
        return ChatColor.translateAlternateColorCodes('&', getRawData(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.valueOf(getRawData(key));
    }

    public double getDouble(String key) {
        try {
            return Double.parseDouble(getRawData(key));
        } catch (Exception ex) { }
        return -1;
    }

    public double getInteger(String key) {
        try {
            return Integer.parseInt(getRawData(key));
        } catch (Exception ex) { }
        return -1;
    }

    public JSONObject getObject(String key) {
        return json.containsKey(key) ? (JSONObject) json.get(key) : new JSONObject();
    }

    public JSONArray getArray(String key) {
        return json.containsKey(key) ? (JSONArray) json.get(key) : new JSONArray();
    }

}
