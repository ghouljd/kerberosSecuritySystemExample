package kerberos;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final String passSSpub = "14m55";
    private final String passSSpri = "55m41";
    
    public Kerberos(){
        int i=0;
        System.out.println("Hola, soy el servidor kerberisado");
        generate_hash();
        try {    
            connect = new Socket[NM];
            server= new ServerSocket(PORT);
            System.out.println("Estoy esperando por clientes.");
            thread hilo[]= new thread[NM];
            while(i<NM){
                connect[i]=server.accept();
                System.out.println("Conectado cliente "+connect[i].getInetAddress());
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
            int usernumber=0;
            try {
                String message= in.readUTF();
                System.out.println(message);
                usernumber=verify_user(message);
                if(usernumber!=-1){
                    message=encrypt(codePass(passTGSpub),pass.get(usernumber));
                    out.writeUTF(message);
                    System.out.println("Se envio el mensaje con la clave publica del TGS, encriptado asi: "+message);
                    message=user.get(usernumber)+","+_socket.getInetAddress()+","+passTGSpub;
                    message=encrypt(codePass(message),passTGSpri);
                    out.writeUTF(message);
                    System.out.println("Se envio el ticket cifrado con la clave privada del TGS, encriptado asi: "+message);
                    message=in.readUTF();
                    System.out.println("El cliente envia: "+message);
                    message=desencrypt(message, passTGSpri);
                    System.out.println("Desencriptando con la clave privada del TGS: "+message);
                    message=decodePass(message);
                    System.out.println("Decodificando: "+message);
                    String aux[]=message.split(",");
                    message=in.readUTF();
                    message=desencrypt(message, passTGSpub);
                    message=decodePass(message);
                    if(message.compareTo(aux[0])==0 && aux[2].compareTo(passTGSpub)==0){
                        System.out.println("Ticket valido.");
                        String auxs="";
                        for (int i = 0; i < aux[0].length() && i < passTGSpub.length(); i++) {
                            auxs+=aux[0].charAt(i);
                            auxs+=passTGSpub.charAt(i);
                        }
                        message=encrypt(codePass(auxs), passTGSpub);
                        System.out.println("Clave cliente/servidor generada: "+auxs+". Se encripto: "+message);
                        out.writeUTF(message);
                        message=user.get(usernumber)+","+_socket.getInetAddress()+","+auxs;
                        message=encrypt(codePass(message),passSSpri);
                        out.writeUTF(message);
                        System.out.println("Se envia el ticket para autenticarse frente al SS encriptado: "+message);
                        message=in.readUTF();
                        System.out.println("El cliente envia: "+message);
                        message=desencrypt(message, passSSpri);
                        System.out.println("Desencriptando con la clave privada del SS: "+message);
                        message=decodePass(message);
                        System.out.println("Decodificando: "+message);
                        aux=message.split(",");
                        message=in.readUTF();
                        message=desencrypt(message, auxs);
                        message=decodePass(message);
                        if(message.compareTo(aux[0])==0 && aux[2].compareTo(auxs)==0){
                            System.out.println("Ticket SS valido");
                            message=encrypt(codePass("Soy el SS y te estoy prestando el servicio."), auxs);
                            out.writeUTF(message);
                            System.out.println("Se esta prestando el servicio.");
                        }else{
                            System.out.println("Ticket no valido.");
                            _socket.close();
                        }
                    }else{
                        System.out.println("Ticket no valido.");
                        _socket.close();
                    }
                }else{
                    System.out.println("Usuario no valido: "+message + ". Se rechazo la solicitud");
                    _socket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Kerberos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private int verify_user(String nombre) {
        for (int i = 0; i < user.size(); i++)
                    if(nombre.compareTo(user.get(i))==0){
                        System.out.println("Usuario valido: "+nombre);
                        return i;
                    }
        return -1;
    }

    private String encrypt(String message,String pass){
        int aux=0;String enc="";
        for (int i = 0; i < pass.length(); i++) 
            aux+=pass.codePointAt(i);
        aux/=100;
        for (int i = 0; i < message.length(); i++) {
            int c=message.charAt(i);
            enc+=(char)(c+aux);
        }
        return enc;
    }
    
    private String desencrypt(String codePass,String passUser){
        int aux=0;String enc="";
        for (int i = 0; i < passUser.length(); i++) 
            aux+=passUser.codePointAt(i);
        aux/=100;
        for (int i = 0; i < codePass.length(); i++) {
            int c=codePass.charAt(i);
            enc+=(char)(c-aux);
        }
        return enc;
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
        System.out.println("El mensaje es: " +pass+". Se codifico a: "+hash);
        return hash;
    }
    
    private String decodePass(String pass) {
        String hash="";
        for (int i = 0; i < pass.length(); i++) {
            char c=pass.charAt(i);
            for (int j = 0; j < alf.size(); j++) 
                if(hashalf.get(j).compareTo(c)==0){
                    hash+=alf.get(j);
                    break;
                }
        }
        System.out.println("El mensaje es: " +pass+". Se decodifico a: "+hash);
        return hash;
    }
    
    private void generate_hash() { 
        FileWriter fichero = null;
        PrintWriter pw = null;
        
        alf = new ArrayList<>();
        hashalf = new ArrayList<>();
        for (int i = 33; i < 126; i++)
            alf.add((char) i);
        FileReader fr = null;
        try {
            hashalf = new ArrayList<>();
            File archivo = new File ("hash.txt");
            fr = new FileReader (archivo);
            BufferedReader br = new BufferedReader(fr);
            String linea;
            while((linea=br.readLine())!=null){
                for (int i = 0; i < linea.length(); i++) 
                    hashalf.add(linea.charAt(i));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Kerberos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Kerberos.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Kerberos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
        Kerberos k =new Kerberos();
    }
}
