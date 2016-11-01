package kerberos;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Kerberos {

    public static void main(String[] args) {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fichero = null;
        PrintWriter pw = null;

        ArrayList<Character> alf = new ArrayList<>();

        try {
            fichero = new FileWriter("hash.txt");
            pw = new PrintWriter(fichero);
            for (int i = 33; i < 126; i++) 
                alf.add((char) i);
            Collections.shuffle(alf);
            for (int i = 0; i < alf.size(); i++) 
                pw.write(" " + alf.get(i));
            
            if (null != fichero) 
                fichero.close();
        } catch (IOException ex) {
            Logger.getLogger(Kerberos.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
