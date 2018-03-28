package ch.mahdavi.AzurIO;

/*
    File: MessageNotifier.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    MessageNotifier is simple class that extends Observable
    It is designed as a singleton to be instantiated only once
    It is used by other classes to easily communicate information
    (text format) to the GUI (AzurIO_GUI) in an asynchronous manner
    In order to send a message to an Observer use the static method
    notifyMessage, for example:
    MessageNotifier.notifyMessage(messageToSendToObservers);
    In order to add an object to the list of observers, use the static
    method registerObservers, for example:
    MessageNotifier.registerObserver(objectToAddToListOfObservers);
 */

import java.util.Observable;
import java.util.Observer;

public class MessageNotifier extends Observable {
    private static MessageNotifier ourInstance = new MessageNotifier();

    public static MessageNotifier getInstance() {
        return ourInstance;
    }

    private MessageNotifier() {
    }

    private void notify(String message){
        setChanged();
        String formattedMessage = HelperMethods.formattedMessage(message, 40);
        notifyObservers(formattedMessage);
        System.out.println(formattedMessage);
    }

    public static void notifyMessage(String message){
        MessageNotifier.getInstance().notify(message);
    }

    public static void registerObserver(Observer o){
        MessageNotifier.getInstance().addObserver(o);
    }

}
