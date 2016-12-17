package Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SettingsParser {
    private HashMap<String, String> map;
    private Path path;

    public SettingsParser(Path path) throws IOException {
        this.path = path;
        List<String> lines = Files.readAllLines(path);
        map = new HashMap<>();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                writeSettings();
            }
        });
        parseLines(lines);
    }

    private void parseLines(List<String> lines){
        for(String line: lines){
            String[]temp = line.split(":");
            map.put(temp[0],temp[1]);
        }
    }

    public HashMap<String,String> getMap(){
        return map;
    }

    public void writeSettings(){
        try {
            List<String> tempMap = new ArrayList<>();
            for( String tempKey: map.keySet() ) {
                tempMap.add(tempKey+":"+map.get(tempKey));
            }

            Files.write(path,tempMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
