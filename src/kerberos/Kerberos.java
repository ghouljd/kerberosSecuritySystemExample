package kerberos;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Kerberos {
    private static final int PORT = 20005;
    private static final int NM = 10;
    ServerSocket server;
    Socket connect[];
    ArrayList<Character> alf,hashalf;
    private ArrayList<String> user=new ArrayList<>(Arrays.asList("jesus", "angelica"));
    private ArrayList<String> pass=new ArrayList<>(Arrays.asList("1234", "5678"));
    private final String passTGS = "14m7G5";
    
    public Kerberos(){
        int i=0;
        System.out.println("Hola, soy el servidor kerberisado");
        generate_hash();
        try {    
            connect = new Socket[NM];
            server= new ServerSocket(PORT);
            System.out.println("servidor esperando");
            thread hilo[]= new thread[NM];
            while(i<NM){
                connect[i]=server.accept();
                System.out.println("conectado cliente "+connect[i].getInetAddress());
                hilo[i]=new thread(connect[i]);
                hilo[i].start();
                i++;
            }
        } catch (IOException ex) {
            Logger.getLogger(Kerberos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }       

    class thread extends Thread{
        Socket _socket; 
        DataInputStream in;
        DataOutputStream out;
        thread(Socket sockaux){
            try {
                _socket=sockaux;
                in=new DataInputStream(_socket.getInputStream());
                out= new DataOutputStream(_socket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(Kerberos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        @Override
        public void run(){
            boolean flag=false;
            int usernumber=0;
            try {
                String message= in.readUTF();
                for (int i = 0; i < user.size(); i++)
                    if(message==user.get(i)){
                        flag=true;
                        usernumber=i;
                        System.out.println("Usuario valido: "+message);
                    }
                if(flag){
                    encrypt(codePass(passTGS),pass.get(usernumber));
                }else
                    System.out.println("Usuario no valido: "+message + ". Se rechazo la solicitud");
            } catch (IOException ex) {
                Logger.getLogger(Kerberos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void encrypt(String codePass,String passUser) {
        byte[] binpass= codePass.getBytes();
        for (int i = 0; i < binpass.length; i++) 
            System.out.print(binpass[i]);
        System.out.println("");
        for (int i = 0; i < binpass.length; i++)
            binpass[i]=(byte) ~binpass[i];
        for (int i = 0; i < binpass.length; i++) 
            System.out.print(binpass[i]);
        String a=binpass.toString();
        System.out.println("Se encripto en: "+a);
    }
    
    private String codePass(String pass) {
        String hash="";
        for (int i = 0; i < pass.length(); i++) {
            char c=pass.charAt(i);
            for (int j = 0; j < alf.size(); j++) 
                if(c==alf.get(i)){
                    hash+=hashalf.get(i);
                    break;
                }
        }
        System.out.println("El pass del usuario es: " +pass+". Se codifico a: "+hash);
        return hash;
    }
    
    private void generate_hash() { 
        FileWriter fichero = null;
        PrintWriter pw = null;
        
        alf = new ArrayList<>();
        hashalf = new ArrayList<>();
        try {
            fichero = new FileWriter("hash.txt");
            pw = new PrintWriter(fichero);
            for (int i = 33; i < 126; i++){ 
                alf.add((char) i);
                hashalf.add((char) i);
            }
            Collections.shuffle(hashalf);
            for (int i = 0; i < hashalf.size(); i++) 
                pw.write(" " + hashalf.get(i));
            
            if (null != fichero) 
                fichero.close();
        } catch (IOException ex) {
            Logger.getLogger(Kerberos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        Kerberos k =new Kerberos();
    }
}
