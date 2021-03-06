/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.user;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Databasemanager;
import server.Filesystem;
import shared.ClientType;
import shared.SocketConnection;
import shared.user.PictureModifies;
import shared.user.UserCall;

/**
 *
 * @author Igor
 */
public class UserServerRunnable implements Observer, Runnable {

    private SocketConnection socket;
    private Databasemanager dbm;
    private Filesystem fs;
    private String uid;
    
    public UserServerRunnable(SocketConnection socket) {
        this.socket = socket;
        dbm = new Databasemanager();
        fs = new Filesystem(socket, dbm);
    }

    @Override
    public void update(Observable o, Object arg) {
        Logger.getAnonymousLogger().log(Level.INFO, "Oberservable");
    }

    @Override
    public void run() {
        String[] args;
        boolean result;
        try {
            while (!socket.isClosed()) {
                UserCall call = (UserCall) socket.readObject();
                switch (call) {
                    case test: {
                        testConnection();
                        break;
                    }
                    case register: {
                        args = (String[]) socket.readObject();
                        result = dbm.registerUser(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
                        socket.writeObject(result);
                        break;
                    }
                    case login: {
                        args = (String[]) socket.readObject();
                        uid = args[0];
                        result = dbm.login(ClientType.user, args[0], args[1]);
                        socket.writeObject(result);
                        break;
                    }
                    case logout: {
                        //TODO: logout statements
                        break;
                    }
                    case attachCodeToAccount: {
                        String s = (String) socket.readObject();
                        int i = (int) socket.readObject();
                        dbm.attachCodeToAccount(s, i);
                        break;
                    }
                    case download: {
                        fs.download(uid);
                        break;
                    }
                    case upload: {
                        ArrayList<PictureModifies> pmList = (ArrayList<PictureModifies>) socket.readObject();
                        fs.uploadModifiedPicture(pmList);
                        break;
                    }
                    
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getAnonymousLogger().log(Level.INFO, "User client disconnected: {0}", socket.getInetAddress());
        } catch (SQLException ex) {
            Logger.getLogger(UserServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testConnection() {
        try {
            boolean receive = (boolean) socket.readObject();
            Logger.getAnonymousLogger().log(Level.INFO, "Message received: {0}", receive);
            boolean send = true;
            socket.writeObject(send);
            Logger.getAnonymousLogger().log(Level.INFO, "Message sent: {0}", send);
        } catch (ClassNotFoundException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, null,  ex);
        } catch (IOException ex) {
            Logger.getAnonymousLogger().log(Level.INFO, "User client disconnected: {0}", socket.getInetAddress());
        }
    }
}
