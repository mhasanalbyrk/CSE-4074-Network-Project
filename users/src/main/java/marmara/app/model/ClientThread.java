package marmara.app.model;

import marmara.app.StartApp;
import marmara.app.service.RegistryHandlings;
import marmara.app.service.impl.RegistryHandlingsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.StringTokenizer;

public class ClientThread implements Runnable {

    private final PeerHandler peerHandler;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientThread.class);


    private boolean isLast;
    public static boolean peerLoggedOut;

    private boolean  workFlag;

    public ClientThread(PeerHandler peerHandler) {
        this.peerHandler = peerHandler;
    }

    public void stopThread() {
        workFlag = false;
    }
    public void startThread(){
        workFlag = true;
    }

    @Override
    public void run() {
        peerLoggedOut = false;
        workFlag = true;
        while (true) {

            try {
                if (!peerHandler.getSocket().isClosed()) {
                    String msg = peerHandler.getDis().readUTF();
                    LOGGER.info("Got a message from a peer => {}", msg);
                    StringTokenizer st = new StringTokenizer(msg, "#");
                    String msgToRead = st.nextToken();
                    String peerName = st.nextToken();
                    if ("logout".equalsIgnoreCase(msgToRead)) {
                        System.out.println(peerHandler.getName() + " logged out");
                        System.out.println("Peer logged out enter 'logout-safe' to safely logout.");
                        System.out.print(" > ");
                        isLast = false;
                        break;
                    }
                    if ("logout-safe".equalsIgnoreCase(msgToRead)) {
                        isLast = true;
                        break;
                    }
                    System.out.println(peerName + " > " + msgToRead);
                    System.out.print(" > ");

                }
                } catch (IOException e) {
                    LOGGER.error("Error while reading message from a peer => ", e);
                }
        }

        LOGGER.info("Closing client thread {} logged out", peerHandler.getName());
        RegistryConnection.isChatting = false;
        if (isLast) {
            try {
                peerHandler.getDis().close();
                peerHandler.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            RegistryConnection registryConnection = new RegistryConnection();
            RegistryHandlings registryHandlings = new RegistryHandlingsImpl();
            registryHandlings.connectRegistry(registryConnection, StartApp.name, false);
        }
    }
}



