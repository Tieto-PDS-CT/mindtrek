package com.tieto.mindtrek.balloon;

import javax.swing.*; 
import javax.json.*;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainClass extends JFrame
    implements KeyListener, ActionListener, ITietoMqttListener {

    BalloonController balloonController = null;
    Boolean autopilotOn = false;
    JButton autopilotButton = null;

    JTextArea debug1DisplayArea; // Navigation commands
    JTextArea debug2DisplayArea; // MQTT responses
    JTextArea debug3DisplayArea; // Compass data
    JTextArea debug4DisplayArea; // Beacon data
    JTextField typingArea;
    static final String newline = System.getProperty("line.separator");
    
    private final Map<String,Integer> beaconStrengthMap = new HashMap<>();

    // For filtering
    boolean keyDown = false;
    
    public MainClass(String name) {
        super(name);
        this.balloonController = new BalloonController();
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
    	MainClass frame = new MainClass("Tieto Balloon Control");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        frame.addComponentsToPane();
       
        //Display the window.
        frame.pack();
        frame.setVisible(true);    
    }
    
    private void updatAutopilotText(){
        if (autopilotOn){
            autopilotButton.setText("Turn Autopilot OFF");
        } else {
            autopilotButton.setText("Turn Autopilot ON");
        }
    }
    
    private void addComponentsToPane() {
        
        JButton button1 = new JButton("Clear");
        button1.setActionCommand("clear");
        button1.addActionListener(this);        
 
        JButton button2 = new JButton();
        button2.addActionListener(this);
        button2.setActionCommand("autopilot");
        autopilotButton = button2;
        updatAutopilotText();
        
        JButton button3 = new JButton("Disconnect");
        button3.setActionCommand("disconnect");
        button3.addActionListener(this);   
         
        typingArea = new JTextField(20);
        typingArea.addKeyListener(this);
         
        //typingArea.setFocusTraversalKeysEnabled(false);
         
        debug1DisplayArea = new JTextArea();
        debug1DisplayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(debug1DisplayArea);
        scrollPane.setPreferredSize(new Dimension(300, 125));
        
        debug2DisplayArea = new JTextArea();
        debug2DisplayArea.setEditable(false);
        JScrollPane scrollPane2 = new JScrollPane(debug2DisplayArea);
        scrollPane2.setPreferredSize(new Dimension(800, 125));
        
        debug3DisplayArea = new JTextArea();
        debug3DisplayArea.setEditable(false);
        JScrollPane scrollPane3 = new JScrollPane(debug3DisplayArea);
        scrollPane3.setPreferredSize(new Dimension(300, 125));
        
        debug4DisplayArea = new JTextArea();
        debug4DisplayArea.setEditable(false);
        JScrollPane scrollPane4 = new JScrollPane(debug4DisplayArea);
        scrollPane4.setPreferredSize(new Dimension(300, 125));

        GridLayout layout = new GridLayout(0,2);
        
        getContentPane().setLayout(layout);
        
        getContentPane().add(typingArea);
        getContentPane().add(scrollPane);
        getContentPane().add(scrollPane2);
        getContentPane().add(scrollPane3);
        getContentPane().add(scrollPane4);
        getContentPane().add(button1);
        getContentPane().add(button2);
        getContentPane().add(button3);
        
        balloonController.getMqttConnector().setListener(this);
    }
    
    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
        // displayInfo(e, "KEY TYPED: ");
    }
     
    /**
     * Prints to the first debug window.
     */
    public void debugPrint1(String message){
        System.out.print(message + "\n");
        debug1DisplayArea.append(message + newline);
    }
    
    /**
     * Prints to the second debug window.
     */
    public void debugPrint2(String message){
        System.out.print(message + "\n");
        debug2DisplayArea.append(message + newline);
    }
    
    public void debugPrint3(String message){
        System.out.print(message + "\n");
        debug3DisplayArea.append(message + newline);
    }
    
    public void debugPrint4(String message){
        System.out.print(message + "\n");
        debug4DisplayArea.append(message + newline);
    }
    
    /** Handle the key pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
        if (!keyDown){
            keyDown = true;
    
            if (e.getKeyCode() == KeyEvent.VK_UP){
                this.balloonController.goForward();
                debugPrint1("Forward!");
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN){
                this.balloonController.goBackward();
                debugPrint1("Backward!");
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT){
                this.balloonController.turnLeft();
                debugPrint1("Turn left!");
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
                this.balloonController.turnRight();
                debugPrint1("Turn right!");
            } else if (e.getKeyCode() == KeyEvent.VK_1){
                this.balloonController.goUp(1);
                debugPrint1("1 Step more height!");
            } else if (e.getKeyCode() == KeyEvent.VK_2){
                this.balloonController.goUp(2);
                debugPrint1("2 Steps more height!");
            } else if (e.getKeyCode() == KeyEvent.VK_3){
                this.balloonController.goUp(3);
                debugPrint1("3 Steps more height!");
            } else if (e.getKeyCode() == KeyEvent.VK_4){
                this.balloonController.goUp(4);
                debugPrint1("4 Steps more height!");
            } else if (e.getKeyCode() == KeyEvent.VK_D){
                // For debugging off-line: Pressing 'D' generates some received data.
                String tmp = "{\"baddr\" : \"10:5C:1E:6C:69:73\", \"rssi\" : \"-89\", \"time_to_go\" : \"0\", \"command_id\" : \"0\", \"x\" : \"114\", \"y\" : \"15\", \"z\" : \"-86\"}";
                mqttDataReceived(tmp);
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE){
                this.balloonController.goUp(3);
                debugPrint1("3 Steps more height!");
            }
        }
     }
    
     
    /** Handle the key released event from the text field. */
    public void keyReleased(KeyEvent e) {
        keyDown = false;
        // displayInfo(e, "KEY RELEASED: ");
    }
     
    /** Handle the button click. */
    public void actionPerformed(ActionEvent e) {
        
        String command  = e.getActionCommand();
                
        if (command == "clear"){
            //Clear the text components.
            debug1DisplayArea.setText("");
            debug2DisplayArea.setText("");
            debug3DisplayArea.setText("");
            debug4DisplayArea.setText("");
            typingArea.setText("");
             
            //Return the focus to the typing area.
            typingArea.requestFocusInWindow();
            
        } else if (command == "autopilot"){
            autopilotOn = !autopilotOn;
            updatAutopilotText();
            
            // To-Do: Start and stop autopilot here
        } else if (command == "disconnect"){
            balloonController.getMqttConnector().disconnect();
        }
    }
    
    private void displayInfo(KeyEvent e, String keyStatus){
        
        //You should only rely on the key char if the event
        //is a key typed event.
        int id = e.getID();
        String keyString;
        if (id == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            keyString = "key character = '" + c + "'";
        } else {
            int keyCode = e.getKeyCode();
            keyString = "key code = " + keyCode
                    + " ("
                    + KeyEvent.getKeyText(keyCode)
                    + ")";
        }
         
        int modifiersEx = e.getModifiersEx();
        String modString = "extended modifiers = " + modifiersEx;
        String tmpString = KeyEvent.getModifiersExText(modifiersEx);
        if (tmpString.length() > 0) {
            modString += " (" + tmpString + ")";
        } else {
            modString += " (no extended modifiers)";
        }
         
        String actionString = "action key? ";
        if (e.isActionKey()) {
            actionString += "YES";
        } else {
            actionString += "NO";
        }
         
        String locationString = "key location: ";
        int location = e.getKeyLocation();
        if (location == KeyEvent.KEY_LOCATION_STANDARD) {
            locationString += "standard";
        } else if (location == KeyEvent.KEY_LOCATION_LEFT) {
            locationString += "left";
        } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
            locationString += "right";
        } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
            locationString += "numpad";
        } else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
            locationString += "unknown";
        }
         
        debug1DisplayArea.append(keyStatus + newline
                + "    " + keyString + newline
                + "    " + modString + newline
                + "    " + actionString + newline
                + "    " + locationString + newline);
        debug1DisplayArea.setCaretPosition(debug1DisplayArea.getDocument().getLength());
    }
 
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    @Override
    public void mqttDataReceived(String data) {
        debugPrint2(data);
 
        JsonReader jsonReader = Json.createReader(new StringReader(data));

        JsonObject object = jsonReader.readObject();

        String btId = object.getString("baddr");
        int btRssi = Integer.parseInt(object.getString("rssi"));
        
        setBeaconData(btId, btRssi);

        int compassX = Integer.parseInt(object.getString("x"));
        int compassY = Integer.parseInt(object.getString("y"));
        int compassZ = Integer.parseInt(object.getString("z"));

        handleCompassData(compassX, compassY, compassZ);

        jsonReader.close();
    }
    
    public void setBeaconData(String btId, int btRssi) {
        beaconStrengthMap.put(btId, btRssi);
        printBeaconData();
    }
    
    public int getBeaconStrength(String btId) {
        return beaconStrengthMap.get(btId);
    }
    
    private void printBeaconData() {
        debug3DisplayArea.setText("");

        for (Map.Entry<String,Integer> beacon : beaconStrengthMap.entrySet()) {
            debugPrint3("Bt id: " + beacon.getKey() + " has signal strength: " + beacon.getValue() + "dB");
        }
    }
 
    private void handleCompassData(int x, int y, int z){
        double heading = 0.0f;
        
        float xFloat = x;
        float yFloat = y;
                
        heading  = Math.toDegrees(Math.atan2(yFloat, xFloat));
        
        while (heading < 0){
            heading += 360;
        }
        
        debug4DisplayArea.setText("");
        debugPrint4("Compass heading is " + Math.round(heading));
    }
}


