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
    private static final String IP = "localhost"; //192.168.0.106
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
    String pass, letra, clientTGS, primero, segundo, ticket, ticket1, ticket2, idcliente, idcliente2, TGSdecif, TGSdecifCod, clave_ser, client_serverSK, client_serverSK2, client_serverSK3, idCServer, idCServer2, CS_H, CS_H2, CS_H3;
    
    
    public Cliente(){
        
           
        try {
            _socket = new Socket( InetAddress.getByName( IP ), PORT );
            out=new DataOutputStream(_socket.getOutputStream());
            in=new DataInputStream(_socket.getInputStream());
            BufferedReader leer= new BufferedReader(new InputStreamReader(System.in));
            

            System.out.println("Ingrese nombre de usuario: ");
            nombre= leer.readLine();
            out.writeUTF(nombre);
            System.out.println("Ingrese Password: ");
            pass = leer.readLine();
            //primero= this.codePass(pass);
            //System.out.println("Se codifico en: "+primero);
            //segundo= this.encrypt(primero, pass);
            //System.out.println("Password encryptada: "+segundo);
           
            
         archivo = new File ("hash.txt");
         fr = new FileReader (archivo);
         br = new BufferedReader(fr);

         String linea;
         while((linea=br.readLine())!=null){
           // System.out.println(linea);
            for (int i = 0; i < linea.length(); i++) {
                hashalf.add(linea.charAt(i));
            }
         }
          for (int i = 33; i < 126; i++){ 
                alf.add((char) i);
            }
           
            
            System.out.println("Solicitud de servicio");

    //Mensaje A, descifrar la clave
            System.out.println("");
            System.out.println("----------------------------**Primer mensaje recibido Client/TGS session key**");
            
            clientTGS = in.readUTF();
            System.out.println("Mensaje A recibido es: "+clientTGS);
            TGSdecif =this.desencrypt(clientTGS, pass);
            TGSdecifCod = this.decodePass(TGSdecif);
            System.out.println("La clave desifrada es: "+TGSdecifCod);
   //Mensaje B
        System.out.println("");
        System.out.println("----------------------------**   Segundo mensaje recibido Ticket-Granting Ticket**");
        ticket = in.readUTF();    
        System.out.println("Mensaje B: Ticket-Granting Ticket: "+ticket);
   //Mensaje c
        System.out.println("");
        System.out.println("----------------------------**   Primer mensaje enviado: Ticket-Granting Ticket**");
        out.writeUTF(ticket);
        System.out.println("Enviado mensaje compuesto por Ticket-Granting Ticket y solicitud del servicio: "+ticket);
               
   //Mensaje d
        System.out.println("");
        System.out.println("----------------------------**   Segundo mensaje enviado: Id del cliente **");
        idcliente = this.codePass(nombre);
        idcliente2 = this.encrypt(idcliente, TGSdecifCod);
        out.writeUTF(idcliente2);
            System.out.println("ID cliente encriptado: "+idcliente2);
        
    //Mensaje F
        System.out.println("----------------------------**   Tercer mensaje recibido Client/server session key**");
        System.out.println("");
        client_serverSK= in.readUTF();
        System.out.println("Mensaje F recibido es: "+client_serverSK);
        client_serverSK2=this.desencrypt(client_serverSK, TGSdecifCod);
        client_serverSK3= this.decodePass(client_serverSK2);
         System.out.println("La clave desifrada Client/server session key es: "+client_serverSK3);
        
        
   //Mensaje E
        System.out.println("");
        System.out.println("----------------------------**   Cuarto mensaje recibido Client-to-server ticket**");
       clave_ser=  in.readUTF();
            System.out.println("Client-to-server ticket recibido: "+clave_ser);
   //Mensaje envia E
   System.out.println("----------------------------**   Tercer mensaje enviadodo Client-to-server ticket**");
        out.writeUTF(clave_ser);
            System.out.println("Client-to-server enviado: "+clave_ser);
   //Mensaje G
            System.out.println("");
            System.out.println("----------------------------**   Cuarto mensaje enviado ID cliente encriptado con client/server session key**");
           idCServer = this.codePass(nombre);
           idCServer2 = this.encrypt(idCServer, client_serverSK3);
           out.writeUTF(idCServer2);
           System.out.println("ID cliente encriptado con client/server session key: "+idCServer2);
   
   //Mensaje H
            System.out.println("");
            CS_H= in.readUTF();
            CS_H2=this.desencrypt(CS_H, client_serverSK3);
            CS_H3= this.decodePass(CS_H2);
            System.out.println("Confirmacion desifrada usando client/server session key: "+CS_H3);
  
        
        
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
  

public static void main(String[] args) {
    Cliente obj= new Cliente();
   
}
    
    }
