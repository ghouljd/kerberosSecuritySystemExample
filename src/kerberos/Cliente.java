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
    private static final String IP = "192.168.0.106";
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
    String pass, letra;
    
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
            //Pso 2     -----metodo de encriptacion 
           /* for(int i=0; i<pass.length();i++){
               letra =""+pass.charAt(i);
                for (int j = 0; j < alf.size(); j++) {
                    if(letra.equals(alf.get(j))){
                        
                    }
                    
                }
            }*/
               fichero = new FileWriter("hash.txt");
            pw = new PrintWriter(fichero);
            for (int i = 33; i < 126; i++) 
                alf.add((char) i);
            Collections.shuffle(alf);
            for (int i = 0; i < alf.size(); i++) 
                pw.write(" " + alf.get(i));
            
            if (null != fichero) 
                fichero.close();

         
            System.out.println("Solicitud de servicio");
//Parte 4
        
            
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    private String codePass(String pass) {
        String hash="";
        for (int i = 0; i < pass.length(); i++) {
            char c=pass.charAt(i);
            for (int j = 0; j < alf.size(); j++) {
                if(c==alf.get(i)){
                    hash+=hashalf.get(i);
                    break;
                }
            }
        } 
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
