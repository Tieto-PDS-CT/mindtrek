package com.tieto.mindtrek.balloon;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttConnector implements MqttCallback
{
	private static final String		BROKER			= "tcp://54.93.150.126:1883";
    private static final int		QOS             = 2;
    private static final String		WRITE_TOPIC		= "team8_write";
    private static final String		READ_TOPIC		= "team8_read";

    private MqttClient sampleClient = null;
    private ITietoMqttListener mqttListener = null;


    public MqttConnector()
    {
        this.connect();
    }

    public void setListener(ITietoMqttListener listener){
        mqttListener = listener;
	}
	
	public boolean connect()
	{
		boolean result 		=	true;
	    String clientId     =	"Tieto Balloon Client";
	    MemoryPersistence persistence = new MemoryPersistence();
	    try 
	    {
	    	this.sampleClient = new MqttClient(MqttConnector.BROKER, clientId, persistence);
	        MqttConnectOptions connOpts = new MqttConnectOptions();
	        connOpts.setCleanSession(true);
	        
	        System.out.println("Connecting to broker: " + MqttConnector.BROKER);
	        sampleClient.connect(connOpts);
	        System.out.println("Connected");
	        sampleClient.subscribe(MqttConnector.READ_TOPIC);
	        sampleClient.setCallback(this);
	    } 
	    catch(MqttException me) 
	    {
	    	result = false;
	        System.out.println("reason "+me.getReasonCode());
	        System.out.println("msg "+me.getMessage());
	        System.out.println("loc "+me.getLocalizedMessage());
	        System.out.println("cause "+me.getCause());
	        System.out.println("excep "+me);
	        me.printStackTrace();
	    }
	    
	    return result;
	}
	
	public boolean write(String content)
	{
		boolean result = true;
		
	    try 
	    {
	        System.out.println("Publishing message: " + content);
	        MqttMessage message = new MqttMessage(content.getBytes());
	        message.setQos(MqttConnector.QOS);
	        sampleClient.publish(MqttConnector.WRITE_TOPIC, message);
	        System.out.println("Message published");
	    } 
	    catch(MqttException me) 
	    {
	    	result = false;
	        System.out.println("reason "+me.getReasonCode());
	        System.out.println("msg "+me.getMessage());
	        System.out.println("loc "+me.getLocalizedMessage());
	        System.out.println("cause "+me.getCause());
	        System.out.println("excep "+me);
	        me.printStackTrace();
	    }
	    
	    return result;
	}
	
	public boolean disconnect()
	{
		boolean result = true;
		
	    try 
	    {
	        this.sampleClient.disconnect();
	        System.out.println("Disconnected");
	    }
	    catch(MqttException me) 
	    {
	    	result = false;
	        System.out.println("reason "+me.getReasonCode());
	        System.out.println("msg "+me.getMessage());
	        System.out.println("loc "+me.getLocalizedMessage());
	        System.out.println("cause "+me.getCause());
	        System.out.println("excep "+me);
	        me.printStackTrace();
	    }
	    
	    return result;
	}

    @Override
    public void connectionLost(Throwable throwable) 
    {
        System.out.println("msg " + throwable.getMessage());
        System.out.println("loc " + throwable.getLocalizedMessage());
        System.out.println("cause " + throwable.getCause());
        System.out.println("excep " + throwable);
        throwable.printStackTrace();
        connect();

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Delivery complete.");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (mqttListener != null){
            mqttListener.mqttDataReceived(message.toString()); 
        }

    }
}
