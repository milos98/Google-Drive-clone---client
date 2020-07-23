package client;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
 
//Ovde, za razliku od serverske niti, koristimo interface RUNNABLE
public class Client implements Runnable {
   
    //Definisemo ulazni i izlazni tok, soket za komunikaciju i ulaz sa tastature
    static Socket soketZaKomunikaciju = null;
    static BufferedReader serverInput = null;
    static PrintStream serverOutput = null;
    static BufferedReader unosSaTastature = null;
   
    // U okviru MAIN metode definisemo prikaz poruka koje dolaze od serverske niti (od servera)
    public static void main(String[] args) {
       
        try {
           
            // Kada pokrenemo klijenta gadjamo localhost i port 9000 definisan na serverskoj strani
            soketZaKomunikaciju = new Socket("localhost", 9000);
           
            // Inicijalizujemo tokove i unos sa tastature
            serverInput = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
            serverOutput = new PrintStream(soketZaKomunikaciju.getOutputStream());
            unosSaTastature = new BufferedReader(new InputStreamReader(System.in));
           
            // Ovde pokrecemo metodu RUN koja je definisana nize
            new Thread(new Client()).start();
           
            String input;
           
            // Dokle god stizu poruke, iste se ispisuju na strani klijenta
            // Ako dodje poruka koja pocinje sa >>> Dovidjenja, a to je u slucaju da smo mi uneli ***quit, zatvara se
            // soket za komunikaciju
            while(true) {
                input = serverInput.readLine();
                System.out.println(input);
               
                if(input.startsWith(">>> Dovidjenja")) {
                    break;
                }
            }
           
            // Zatvaranje soketa u slucaju kada napustamo chat
           
            soketZaKomunikaciju.close();
           
        // Obradjena su dva izuzetka:
        // Prvi u slucacu da je nepoznat host tj. server na koji se kacimo
        // Drugi u slucaju da server iznenada prestane sa radom npr.           
           
        } catch (UnknownHostException e) {
            System.out.println("UNKNOWN HOST!");
        } catch (IOException e) {
            System.out.println("SERVER IS DOWN!!!");
        }
       
    }
   
 
    // U okviru RUN metode saljemo poruke koje klijent otkuca ka serveru
 
    @Override
    public void run() {
 
       
            try {
               
                String message;
                String pom;
               
                while(true) {
                   
                    message = unosSaTastature.readLine();
                    serverOutput.println(message);
                   
                    // Ako otkucamo ***quit napustamo server
                   
                    if(message.equals("***quit")) {
                    	break;
                    }
                    
                    if(message.equals("2")) {
                    	
                    	message = unosSaTastature.readLine();
        				serverOutput.println(message);
        				
        				pom = message;
        				File file = new File(pom);
	        			
        				message = unosSaTastature.readLine();
        				serverOutput.println(message);
        				
        				if(file.getName().endsWith(".txt")) {
        					BufferedReader br = null;
            				try {
    							br = new BufferedReader(new FileReader(pom));
    							
    							boolean kraj = false;
    							
    							while(!kraj) {
    								pom = br.readLine();
    								if(pom == null)
    									kraj = true;
    								else
    									serverOutput.println(pom);
    							}
    								serverOutput.println("***kraj");
    							
    						} catch (Exception e) {
    							e.printStackTrace();
    						}
        				} else {
        					message = null;
        					try {
        						FileInputStream fIs = new FileInputStream(file);
        						byte[] b = new byte[(int) file.length()];
        						fIs.read(b);
        						message = new String(Base64.getEncoder().encode(b), "UTF-8");
        						serverOutput.println(message);
        						fIs.close();
        					} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
        				}
                    }
                }
               
            } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }