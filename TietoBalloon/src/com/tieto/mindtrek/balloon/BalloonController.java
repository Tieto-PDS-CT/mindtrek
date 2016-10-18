package com.tieto.mindtrek.balloon;

public class BalloonController 
{
	private MqttConnector mqttConnector = null;
	
	private static final String BALLOON_COMMAND = "{\"m1\" : \"%s\", \"m2\" : \"%s\", \"m_up\" : \"%s\", \"time\" : \"%s\", \"command_id\" : \"%s\"}";
	
	private static final int SIDE_MOTOR_STOP = 0;
	private static final int SIDE_MOTOR_FORWARD = 1;
	private static final int SIDE_MOTOR_BACKWARD = 2;
	
	private static final int UP_MOTOR_STANDARD_SPEED = 1;
	
	private static final int TIME = 50;
	
	private int commandId = 0;
	
	private int getCommandId()
	{
		return commandId++;
	}
	
	public BalloonController()
	{
		this.mqttConnector = new MqttConnector();
	}
	
    public MqttConnector getMqttConnector(){
        return this.mqttConnector;
    }

	
	private String getBalloonCommand(int motor1, int motor2, int upMotor, int time)
	{
		int commandId = this.getCommandId();
		String command = String.format(BalloonController.BALLOON_COMMAND, motor1, motor2, upMotor, time, commandId);
		return command;
	} 
	
	public boolean goUp(int speed)
	{
		boolean result = true;
		String command = this.getBalloonCommand(BalloonController.SIDE_MOTOR_STOP, BalloonController.SIDE_MOTOR_STOP, speed, BalloonController.TIME);
		result = this.mqttConnector.write(command);
		return result;
	}
	
	public boolean goForward()
	{
		boolean result = true;
		String command = this.getBalloonCommand(BalloonController.SIDE_MOTOR_FORWARD, BalloonController.SIDE_MOTOR_FORWARD, BalloonController.UP_MOTOR_STANDARD_SPEED, BalloonController.TIME);
		result = this.mqttConnector.write(command);
		return result;
	}
	
	public boolean goBackward()
	{
		boolean result = true;
		String command = this.getBalloonCommand(BalloonController.SIDE_MOTOR_BACKWARD, BalloonController.SIDE_MOTOR_BACKWARD, BalloonController.UP_MOTOR_STANDARD_SPEED, BalloonController.TIME);
		result = this.mqttConnector.write(command);
		return result;
	}
	
	public boolean turnRight()
	{
		boolean result = true;
		String command = this.getBalloonCommand(BalloonController.SIDE_MOTOR_BACKWARD, BalloonController.SIDE_MOTOR_FORWARD, BalloonController.UP_MOTOR_STANDARD_SPEED, BalloonController.TIME);
		result = this.mqttConnector.write(command);
		return result;		
	}
	
	public boolean turnLeft()
	{
		boolean result = true;
		String command = this.getBalloonCommand(BalloonController.SIDE_MOTOR_FORWARD, BalloonController.SIDE_MOTOR_BACKWARD, BalloonController.UP_MOTOR_STANDARD_SPEED, BalloonController.TIME);
		result = this.mqttConnector.write(command);
		return result;		
	}
}
