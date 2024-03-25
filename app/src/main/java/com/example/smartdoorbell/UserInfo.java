package com.example.smartdoorbell;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class UserInfo {

    /*
    this class contain several methods to write, read or initialize user ID.
     */

    private Context context;
    private File file;

    public UserInfo(Context context){
        this.context = context;
        this.file = new File(context.getFilesDir(), "ID");
    }

    public boolean fileExist(){
        return file.exists();
    }


    public boolean initInfo(){
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

    public boolean writeInfo(String firstName, String lastName, String email){
        if(!file.exists()){
            return false;
        }
        else{
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);

                try {
                    FileWriter writer = new FileWriter(fos.getFD());
                    writer.write(firstName + '\n');
                    writer.write(lastName  + '\n');
                    writer.write(email  + '\n');
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

    String[] readFile(){
        String[] userInfo = new String[3];
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);

            Scanner scanner = new Scanner(fis);
            if(scanner.hasNext()){
                userInfo[0] = scanner.next();
                userInfo[1] = scanner.next();
                userInfo[2] = scanner.next();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return userInfo;
    }

    public boolean deleteFile(){
        return file.delete();
    }

}
