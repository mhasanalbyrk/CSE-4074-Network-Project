package marmara.marmara.model;

import marmara.marmara.StartApp;
import marmara.marmara.service.impl.PeerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class ServerThread implements Runnable {

    private static Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);
    private BufferedReader reader;
    private boolean workFlag;
    public static boolean  peerLoggedOut;

    public void stopThread() {
        workFlag = false;
    }

    public void startThread() {
        workFlag = true;
    }

    @Override
    public void run() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        peerLoggedOut = false;

        while (!peerLoggedOut) {
            if (workFlag) {
                try {
                    // read the message to deliver.
                    System.out.println(" > ");
                    String msg = reader.readLine();
                    if ("logout".equalsIgnoreCase(msg)){
                        PeerHandler.peerHandlerMap.forEach((s, peerHandler) -> {
                            if (Objects.nonNull(s) && Objects.nonNull(peerHandler)) {
                                try {
                                    peerHandler.getDos().writeUTF("logout#400");
                                    peerHandler.getDos().close();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        ClientThread.peerLoggedOut = true;
                        break;
                    }
                    msg += "#" + StartApp.name;
                    String finalMsg = msg;

                    PeerHandler.peerHandlerMap.forEach((s, peerHandler) -> {
                        if (Objects.nonNull(s) && Objects.nonNull(peerHandler)) {
                            try {
                                peerHandler.getDos().writeUTF(finalMsg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        LOGGER.info("Closing server thread");
        System.out.println("Closing server thread ...");
        RegistryConnection.isChatting = false;
    }

}

