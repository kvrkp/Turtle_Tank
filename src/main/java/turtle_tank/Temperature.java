package turtle_tank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;

public class Temperature implements Runnable {

    private static volatile String PATH2W1 = "/sys/bus/w1/devices";
    private static volatile File dir = new File(PATH2W1);
    private static volatile File[] dirA = dir.listFiles(new DirFilter()); // array of 28- directories

    private static volatile float water, air, basking;

    @Override
    public void run() {
        if(dirA != null) {
            while(true) {
                int locIdx = 0;
                float[] tempArray = new float[3]; // hold temperature read in from file

                for(File file : dirA) { // each file within the Directory Array
                    String filePath = PATH2W1 + "/" + file.getName() + "/w1_slave";
                    File f = new File(filePath);
                    try(BufferedReader br = new BufferedReader(new FileReader(f))) {
                        String curLine;
                        while((curLine = br.readLine()) != null) {
                            int idx = curLine.indexOf("t=");
                            if(idx > -1) {
                                float tempC = Float.parseFloat(curLine.substring(curLine.indexOf("t=") + 2));
                                tempC /= 1000;
                                tempArray[locIdx] = tempC * 9 / 5 + 32;
                                locIdx++;
                            }
                        }
                    } catch(Exception ex) {
                        System.out.println("Error reading temperatures");
                    }
                }

                water = tempArray[0];
                air = tempArray[1];
                basking = tempArray[2];

                try {
                    Thread.sleep(5000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static float getWater() {
        return water;
    }

    static float getAir() {
        return air;
    }

    static float getBasking() {
        return basking;
    }

    static float getTooHotDay() {
        float tooHotDay = 92f;
        return tooHotDay;
    }

    static float getTooHotNight() {
        float tooHotNight = 81f;
        return tooHotNight;
    }

    static float getTooHotWater() {
        float tooHotWater = 76f;
        return tooHotWater; }
}

// This FileFilter selects subdirs with name beginning with 28-
class DirFilter implements FileFilter {
    public boolean accept(File file) {
        String dirName = file.getName();
        String startOfName = dirName.substring(0, 3);
        return (file.isDirectory() && startOfName.equals("28-"));
    }
}