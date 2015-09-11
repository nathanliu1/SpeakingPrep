package ca.uwaterloo.speakingprep;

import android.os.Environment;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Nathan on 9/11/2015.
 */
public class FileNameManager  {
    private HashMap<String,String> fileManager =  new HashMap<String,String>();

    public FileNameManager() {

    }

    public int getTrialNumber(String question) {
        if (!fileManager.containsKey(question)) {
            fileManager.put(question, "1");
            return 1;
        } else {
            fileManager.put(question,Integer.parseInt(fileManager.get(question))+1 + "");
            return Integer.parseInt(fileManager.get(question));
        }
    }

}
