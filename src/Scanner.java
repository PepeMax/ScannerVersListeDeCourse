import javax.comm.*;

import org.json.simple.parser.ParseException;

import com.sun.comm.Win32Driver;
import java.io.*;
import java.util.*;

public class Scanner extends Thread implements SerialPortEventListener {

    private CommPortIdentifier portId;
    private SerialPort serialPort;
    private BufferedReader fluxLecture;
    private boolean running;
    private FentreListeDeCourse window;

    /**
     * Constructeur qui récupère l'identifiant du port et lance l'ouverture.
     */
    public Scanner(String port) {
    	this.window = new FentreListeDeCourse();
    	this.window.setVisible(true);
        //initialisation du driver
        Win32Driver w32Driver = new Win32Driver();
        w32Driver.initialize();
        //récupération de l'identifiant du port
        try {
            portId = CommPortIdentifier.getPortIdentifier(port);
        } catch (NoSuchPortException e) {
        }
        
        //ouverture du port
        try {
            serialPort = (SerialPort) portId.open("ModeEvenement", 2000);
        } catch (PortInUseException e) {
        }
        //récupération du flux
        try {
            fluxLecture =
                new BufferedReader(
                    new InputStreamReader(serialPort.getInputStream()));
        } catch (IOException e) {
        }
        //ajout du listener
        try {
            serialPort.addEventListener(this);
        } catch (TooManyListenersException e) {
        }
        //paramétrage du port
        serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.setSerialPortParams(
                9600,
                SerialPort.DATABITS_7,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_EVEN);
        } catch (UnsupportedCommOperationException e) {
        }
    }
    public void run() {
        running = true;
        while (running) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
        //fermeture du flux et port
        try {
            fluxLecture.close();
        } catch (IOException e) {
        }
        serialPort.close();
    }
    /**
     * Méthode de gestion des événements.
     */
    public void serialEvent(SerialPortEvent event) {
        //gestion des événements sur le port :
        //on ne fait rien sauf quand les données sont disponibles
        switch (event.getEventType()) {
            case SerialPortEvent.BI :
            case SerialPortEvent.OE :
            case SerialPortEvent.FE :
            case SerialPortEvent.PE :
            case SerialPortEvent.CD :
            case SerialPortEvent.CTS :
            case SerialPortEvent.DSR :
            case SerialPortEvent.RI :
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY :
                break;
            case SerialPortEvent.DATA_AVAILABLE :
                String codeBarre = new String(); 
                try {
                    //lecture du buffer et affichage
                    codeBarre = (String) fluxLecture.readLine();
                    
                    try {
						JsonClass.readInBDD(codeBarre,this.window);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    
                    
                } catch (IOException e) {
                }
                break;
        }
    }
    /**
     * Permet l'arrêt du thread
     */
    public void stopThread() {
        running = false;
    }
    /**
     * Méthode principale de l'exemple.
     */
    public static void main(String[] args) {
    	
        //Récuperation du port en argument
        String port = "COM7";
        //lancement de l'appli
        Scanner modeEve = new Scanner(port);
        modeEve.start();
    }
}