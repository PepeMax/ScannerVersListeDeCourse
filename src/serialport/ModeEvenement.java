package serialport;

import javax.comm.*;
import com.sun.comm.Win32Driver;
import java.io.*;
import java.util.*;

//https://christophej.developpez.com/tutoriel/java/javacomm/

public class ModeEvenement extends Thread implements SerialPortEventListener {

    private CommPortIdentifier portId;
    private SerialPort serialPort;
    private BufferedReader fluxLecture;
    private boolean running;

    /**
     * Constructeur qui récupère l'identifiant du port et lance l'ouverture.
     */
    public ModeEvenement(String port) {
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
        System.out.println("port ouvert, attente de lecture");
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
                    System.out.println(codeBarre);
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
        String port = "COM1";
        //lancement de l'appli
        ModeEvenement modeEve=new ModeEvenement(port);
        modeEve.start();
        //"interface utilisateur"
        System.out.println("taper q pour quitter");
        //construction flux lecture
        BufferedReader clavier =
                new BufferedReader(new InputStreamReader(System.in));
        //lecture sur le flux entrée.
        try {
            String lu = clavier.readLine();
            while (!lu.equals("q")) {
            }
        } catch (IOException e) {
        }
        modeEve.stopThread();
    }
}