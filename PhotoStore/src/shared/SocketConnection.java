/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 *
 * @author IGOR
 */
public class SocketConnection {
    protected Socket socket = null;
    protected ObjectOutputStream out = null;
    protected ObjectInputStream in = null;
    protected static final Logger LOG = Logger.getLogger(SocketConnection.class.getName());
    
    protected void newIn() throws IOException {
        in = new ObjectInputStream(socket.getInputStream());
    }

    protected void newOut() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
    }
}