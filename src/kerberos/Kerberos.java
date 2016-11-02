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
    private ArrayList<String> pass=new ArrayList<>(Arrays.asList("012356", "567890"));
    private final String passTGSpub = "14m7G5";
    private final String passTGSpri = "5G7m41";
    
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
                System.out.println(message);
                for (int i = 0; i < user.size(); i++)
                    if(message.compareTo(user.get(i))==0){
                        flag=true;
                        usernumber=i;
                        System.out.println("Usuario valido: "+message);
                        break;
                    }
                if(flag){
                    message=encrypt(codePass(passTGSpub),pass.get(usernumber));
                    out.writeUTF(message);
                    System.out.println("Se envio el mensaje con la clave publica del TGS, encriptado asi: "+message);
                    message=user.get(usernumber)+","+_socket.getInetAddress()+","+passTGSpub;
                    message=encrypt(codePass(message),passTGSpri);
                    out.writeUTF(message);
                    System.out.println("Se envio el ticket cifrado con la clave privada del TGS, encriptado asi: "+message);
                }else
                    System.out.println("Usuario no valido: "+message + ". Se rechazo la solicitud");
            } catch (IOException ex) {
                Logger.getLogger(Kerberos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String encrypt(String codePass,String passUser){
        byte[] binpass= codePass.getBytes();
        byte[] binuser= passUser.getBytes(),res= new byte[binpass.length];
        for (int i = 0; i < binpass.length; i++){
            binpass[i]=(byte) ~binpass[i];
            if(i<binpass.length && i<binuser.length)
                res[i]=(byte) (binpass[i]^binuser[i]);
            else{
                int j=binuser.length-1;
                res[i]=(byte) (binpass[i]^binuser[j]);
            }
        }
        System.out.println("Se encripto en: "+res.toString());
        return res.toString();
    }
    
    private String codePass(String pass) {
        String hash="";
        for (int i = 0; i < pass.length(); i++) {
            char c=pass.charAt(i);
            for (int j = 0; j < alf.size(); j++) 
                if(alf.get(j).compareTo(c)==0){
                    hash+=hashalf.get(j);
                    break;
                }
        }
        System.out.println("El pass es: " +pass+". Se codifico a: "+hash);
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
