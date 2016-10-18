package com.tieto.mindtrek.balloon;

import javax.swing.*; 
import javax.json.*;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainClass extends JFrame
    implements KeyListener, ActionListener, ITietoMqttListener {

    BalloonController balloonController = null;
    Boolean autopilotOn = false;
    JButton autopilotButton = null;
    
    int FILTERSIZE = 30;
    
    int[] compassXElements = new int[FILTERSIZE];
    int[] compassYElements = new int[FILTERSIZE];
    int compassIndex = 0;
    
    int minX = 1000;
    int minY = 1000;
    int maxX = -1000;
    int maxY = -1000;
    
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
    
    private double calculateHeading(float x, float y){
        double heading = 0.0f;
        heading  = Math.toDegrees(Math.atan2(y, x));
        
        while (heading < 0){
            heading += 360;
        }
        
        return heading;
    }
 
    private void handleCompassData(int x, int y, int z){
        
        y=y+12;
        x=x-8;
        
        // #1 First method: Straighforward calculation based on latest x, y values.
        double heading  = calculateHeading(x, y);
        
        debug4DisplayArea.setText("");
        debugPrint4("Compass heading is " + Math.round(heading) + ", X = " + x + ", Y= " + y);
        
        // #2 Second method: Storing of x and y values and using median ones.
        compassXElements[compassIndex] = x;
        compassYElements[compassIndex] = y;
        
        compassIndex = (compassIndex + 1) % 5;
        
        int[] tmpArray = new int[FILTERSIZE];
        System.arraycopy(compassXElements, 0, tmpArray, 0, FILTERSIZE);
        Arrays.sort(tmpArray);
        int medianX = tmpArray[FILTERSIZE/2];
        
        System.arraycopy(compassYElements, 0, tmpArray, 0, FILTERSIZE);
        Arrays.sort(tmpArray);
        int medianY = tmpArray[FILTERSIZE/2];
        
        double medianHeading = calculateHeading(x, y);
        
        debugPrint4("Median compass heading is " + Math.round(medianHeading) + " Median X = " + medianX + ", medianY = " + medianY);
        
        // #3 Third method: Calculating an average of all stored values.
        int xTotal = 0;
        int yTotal = 0;
        for (int i=0; i < FILTERSIZE; i++){
            xTotal += compassXElements[i];
            yTotal += compassYElements[i];
        }
        
        int averageX = xTotal / FILTERSIZE;
        int averageY = yTotal / FILTERSIZE;
        
        double averageHeading = calculateHeading(averageX, averageY);
        debugPrint4("Average compass heading is " + Math.round(averageHeading) + " Average X = " + averageX + ", Average Y = " + averageY);
        
        // For calibration: Store and display largest and smallest x and Y values.
        if (averageX < minX)
            minX = averageX;
        if (averageX > maxX)
            maxX = averageX;
        if (averageY > maxY)
            maxY = averageY;
        if (averageY < minY)
            minY = averageY;
        
        debugPrint4("MinX: " + minX + ", maxX: " + maxX + " minY: " + minY + " maxY: " + maxY);
    }
}


