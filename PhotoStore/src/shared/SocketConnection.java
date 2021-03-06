/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import client.ClientConnector;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Igor
 */
public class SocketConnection {

    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    SecretKeyFactory keyFactory;
    SecretKey secretKey;
    Cipher encrypter, decrypter;

    public SocketConnection(Socket s) {
        this.socket = s;
        establishEncryption();
    }

    private void establishEncryption() {
        try {
            this.keyFactory = SecretKeyFactory.getInstance("DESede");
            byte[] key = "S(aHz&Cc}RP$A4=R,k]7bg_`".getBytes();
            secretKey = new SecretKeySpec(key, 0, key.length, "DESede");
            encrypter = Cipher.getInstance("DESede");
            encrypter.init(Cipher.ENCRYPT_MODE, secretKey);
            decrypter = Cipher.getInstance("DESede");
            decrypter.init(Cipher.DECRYPT_MODE, secretKey);

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, null, ex);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            SealedObject sealedObj = (SealedObject) in.readObject();
            Object unsealedObj;
            unsealedObj = sealedObj.getObject(decrypter);
            return unsealedObj;
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, null, ex);

            return null;
        }
    }

    public void writeObject(Object obj) throws IOException {
        SealedObject sealedObj;
        try {
            sealedObj = (new SealedObject((Serializable) obj, encrypter));
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(sealedObj);
        } catch (IllegalBlockSizeException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, null, ex);
        }
    }

    public void writeFile(File in) {
        try {
            byte[] byteArray = new byte[(int) in.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(in));
            bis.read(byteArray, 0, byteArray.length);
            writeObject(byteArray);
            } catch (IOException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, null, ex);
        }
    }

    public void readFile(File out) {
        try {
            byte[] byteArray = (byte[]) readObject();
            FileOutputStream fos = new FileOutputStream(out);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(byteArray, 0, byteArray.length);
            bos.close();
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
