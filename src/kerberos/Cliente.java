/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kerberos;

import java.io.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {
    private static final int PORT = 20005;
    private static final String IP = "192.168.0.106"; //192.168.0.106
    Socket _socket;
    DataOutputStream out;
    DataInputStream in;
    String  resultado="";
    File archivo = null;
    FileReader fr = null;
    BufferedReader br = null;
    FileWriter fichero = null;
    PrintWriter pw = null;
    ArrayList<Character> hashalf = new ArrayList<>();
    ArrayList<Character> alf = new ArrayList<>();
    String nombre;
    String pass, letra, clientTGS, primero, segundo, ticket, ticket1, ticket2, idcliente, idcliente2, TGSdecif, TGSdecifCod;
    
    
    public Cliente(){
        
           
        try {
            _socket = new Socket( InetAddress.getByName( IP ), PORT );
            out=new DataOutputStream(_socket.getOutputStream());
            in=new DataInputStream(_socket.getInputStream());
            BufferedReader leer= new BufferedReader(new InputStreamReader(System.in));
            
//Parte 1,2 y 3
            System.out.println("Ingrese nombre de usuario: ");
            nombre= leer.readLine();
            out.writeUTF(nombre);
            System.out.println("Ingrese Password: ");
            pass = leer.readLine();
            primero= this.codePass(pass);
            //System.out.println("Se codifico en: "+primero);
            segundo= this.encrypt(primero, pass);
            System.out.println("Password encryptada: "+segundo);
           
            
// guardar en el arraylist hashalf el archivo
         archivo = new File ("hash.txt");
         fr = new FileReader (archivo);
         br = new BufferedReader(fr);

         String linea;
         while((linea=br.readLine())!=null){
            System.out.println(linea);
            for (int i = 0; i < linea.length(); i++) {
                hashalf.add(linea.charAt(i));
            }
         }
          for (int i = 33; i < 126; i++){ 
                alf.add((char) i);
            }
            for (int i = 0; i < alf.size(); i++) {
                System.out.println(alf.get(i));
            }
            
            System.out.println("Solicitud de servicio");

    //Mensaje A, descifrar la clave
        clientTGS = in.readUTF();
            System.out.println("Mensaje A recibido es: "+clientTGS);
            TGSdecif =this.desencrypt(clientTGS, pass);
            TGSdecifCod = this.decodePass(TGSdecif);
            System.out.println("La clave desifrada es: "+TGSdecifCod);
   //Mensaje B
        ticket = in.readUTF();    
        System.out.println("Mensaje B: Ticket-Granting Ticket: "+ticket);
   //Mensaje c
        out.writeUTF(ticket);
        System.out.println("Enviado mensaje compuesto por Ticket-Granting Ticket y solicitud del servicio: "+ticket);
               
   //Mensaje d
        idcliente = this.codePass(nombre);
        idcliente2 = this.encrypt(idcliente, nombre);
        out.writeUTF(idcliente2);
            System.out.println("ID cliente codificado: "+idcliente2);
   
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    
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
        System.out.println("El pass es: " +pass+". Se codifico a: "+hash);
        return hash;
    } 
    
    
    private String InvcodePass(String pass) {
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
        System.out.println("El mensaje es: " +pass+". Se codifico a: "+hash);
        return hash;
    }
  /* private String leer_arch(String a){
        
      File archivo = null;
      FileReader fr = null;
      BufferedReader br = null;
       int i=0;
      try {
         archivo = new File ("hash.txt");
         fr = new FileReader (archivo);
         br = new BufferedReader(fr);
         String linea;
         while((linea=br.readLine())!=null){
             //-----
             
             
             System.out.println(linea)   ;
         
         }
      }
      catch(Exception e){
         e.printStackTrace();
      }finally{
         // En el finally cerramos el fichero, para asegurarnos
         // que se cierra tanto si todo va bien como si salta 
         // una excepcion.
         try{                    
            if( null != fr ){   
               fr.close();     
            }                  
         }catch (Exception e2){ 
            e2.printStackTrace();
         }
      }
        return n;
    }*/
public static void main(String[] args) {
    Cliente obj= new Cliente();
   
}
    
    }
