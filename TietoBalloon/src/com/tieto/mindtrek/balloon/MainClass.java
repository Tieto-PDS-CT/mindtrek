package com.tieto.mindtrek.balloon;

import javax.swing.*; 
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;

public class MainClass extends JFrame
    implements KeyListener, ActionListener {

	BalloonController balloonController = null;
	
    JTextArea debug1DisplayArea;
    JTextArea debug2DisplayArea;
    JTextField typingArea;
    static final String newline = System.getProperty("line.separator");

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
    
    private void addComponentsToPane() {
        
        JButton button = new JButton("Clear");
        button.addActionListener(this);
         
        typingArea = new JTextField(20);
        typingArea.addKeyListener(this);
         
        //Uncomment this if you wish to turn off focus
        //traversal.  The focus subsystem consumes
        //focus traversal keys, such as Tab and Shift Tab.
        //If you uncomment the following line of code, this
        //disables focus traversal and the Tab events will
        //become available to the key event listener.
        //typingArea.setFocusTraversalKeysEnabled(false);
         
        debug1DisplayArea = new JTextArea();
        debug1DisplayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(debug1DisplayArea);
        scrollPane.setPreferredSize(new Dimension(300, 125));
        
        debug2DisplayArea = new JTextArea();
        debug2DisplayArea.setEditable(false);
        JScrollPane scrollPane2 = new JScrollPane(debug2DisplayArea);
        scrollPane2.setPreferredSize(new Dimension(800, 125));

        getContentPane().add(typingArea, BorderLayout.PAGE_START);
        getContentPane().add(scrollPane, BorderLayout.LINE_START);
        getContentPane().add(scrollPane2, BorderLayout.LINE_END);
        getContentPane().add(button, BorderLayout.PAGE_END);
    }
    
    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
        // displayInfo(e, "KEY TYPED: ");
    }
     
    /**
     * Prints to the left debug pane on application window, and to console output.
     * Meant for input keys and other "slow" debug output.
     */
    public void debugPrint1(String message){
        System.out.print(message);
        debug1DisplayArea.append(message + newline);
    }
    
    /**
     * Prints to the rigth (wider) debug pane on the application window, and to console output.
     * Meant for MQTT traffic and other "fast" debug output.
     */
    public void debugPrint2(String message){
        System.out.print(message);
        debug2DisplayArea.append(message + newline);
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
        //Clear the text components.
        debug1DisplayArea.setText("");
        debug2DisplayArea.setText("");
        typingArea.setText("");
         
        //Return the focus to the typing area.
        typingArea.requestFocusInWindow();
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
}


