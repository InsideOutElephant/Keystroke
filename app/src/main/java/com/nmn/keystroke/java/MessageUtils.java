package com.nmn.keystroke.java;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageUtils {
    Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public Message getMessage(InputStream inputStream) {
        BufferedInputStream bufferedInputStream = null;
        ObjectInputStream objectInputStream = null;
        Message message = null;
        try {
            bufferedInputStream = new BufferedInputStream(inputStream);
            objectInputStream = new ObjectInputStream(bufferedInputStream);
            message = (Message) objectInputStream.readObject();
            LOG.log(Level.INFO, "Received from client:" + message.toString());
            checkStream(inputStream);
            objectInputStream.close();
            bufferedInputStream.close();
            inputStream.close();
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {

        }
        return message;
    }

    private void checkStream(InputStream inputStream) {
        try {
            while (inputStream.available() > 0) {
                LOG.log(Level.INFO, new Scanner(inputStream).nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Command getCommand(Message message) {
        return message.getCommand();
    }

    public int getMessageLength(Message message) {
        return message.toString().length(); // TODO: Get actual length - more than likely not needed at all as objects are using java serialisation and not json
    }

    public MessageType getMessageType(Message message) {
        return message.getType();
    }

    public List<Command> getCommandList(Message message) {
        return message.getCommandList();
    }


}
