package com.iot.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.iot.activity.MessageHandler;
 
public class ProcessHandler extends Thread {
    InputStream is;
    public boolean done = false;
    private int userID;
    MessageHandler mh;
    
    
    public ProcessHandler(InputStream is, int userID) {
        this.is = is;
        this.userID = userID;
        mh = new MessageHandler();
    }
     
    public void run() {
        BufferedReader br = null;
        try {
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = null;
            while(!done) {
                if((line = br.readLine()) != null) {
                	if (line.contains("R,")) {
    					int predictedClass = Integer.parseInt(line.split(",")[2]);
    					mh.collectResults(this.userID, predictedClass);
    				}
    				System.out.println(userID+":"+line);
                }
            }
             
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
 
}


