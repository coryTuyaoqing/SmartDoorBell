package com.example.smartdoorbell;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class UserID {

    /*
    this class contain several methods to write, read or initialize user ID.
     */

    private Context context;
    private File file;

    public UserID(Context context){
        this.context = context;
        this.file = new File(context.getFilesDir(), "ID");
    }


    public boolean initID(){
        if(!file.exists()){
            System.out.println("file doesn't exist");
            try{
                System.out.println("file already exists");
                return file.createNewFile();
            }
            catch(IOException e){
                e.printStackTrace();
                return false;
            }
        }
        else return false;

    }

    public boolean writeID(int ID){
        if(!file.exists()){
            return false;
        }
        else{
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);

                try {
                    FileWriter writer = new FileWriter(fos.getFD());
                    writer.write(String.valueOf(ID));
                    writer.close();
                    return true;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int readID(){
        int ID = -1;
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);

            Scanner scanner = new Scanner(fis);
            if(scanner.hasNext()){
                ID = scanner.nextInt();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return ID;
    }
}
