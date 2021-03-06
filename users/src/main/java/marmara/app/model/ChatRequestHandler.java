package marmara.app.model;

import marmara.app.StartApp;
import marmara.app.service.RegistryHandlings;
import marmara.app.service.impl.RegistryHandlingsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ChatRequestHandler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatRequestHandler.class);


    private static int numberOfPeers = 0;
    public boolean isChatting;
    private Socket socket;
    private DataInputStream in;
    private ServerSocket multiThreadSocket;


    @Override
    public void run() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));


        isChatting = false;
        // server is listening on port 8080
        try {
            multiThreadSocket = new ServerSocket(StartApp.portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Your Server started. Listening at port {}", multiThreadSocket.getLocalPort());

        LOGGER.info("Waiting for peer ...");

        Socket newRequestSocket = null;

        DataInputStream inputStream = null;
        DataOutputStream dos = null;
        // running infinite loop for getting
        // client request

        while (!isChatting) {

            try {
                // Accept the incoming request
                newRequestSocket = multiThreadSocket.accept();
                LOGGER.info("New client request received : {}", newRequestSocket);

                // obtain input and output streams
                inputStream = new DataInputStream(newRequestSocket.getInputStream());
                dos = new DataOutputStream(newRequestSocket.getOutputStream());
                RegistryConnection.isChatting = true;
                String userName = inputStream.readUTF();
                StringTokenizer st = new StringTokenizer(userName, "#");
                userName = st.nextToken();
                System.out.println("Enter 'accept-peer' to handle connection request");
                System.out.print(" > ");

                System.out.println(userName + "wants to chat yes or no?");
                System.out.print(" > ");

                String yerOrNo = bufferedReader.readLine();
                if ("yes".equalsIgnoreCase(yerOrNo)) {
                    dos.writeUTF("ACCEPT");
                    LOGGER.info("Creating a new handler for this peer ...");
                    Peer newPeer = Peer.builder().username(userName).build();
                    PeerHandler newPeerHandler = PeerHandler.builder().peer(newPeer).dis(inputStream).dos(dos).scn(new Scanner(System.in)).name(userName).socket(newRequestSocket).build();
                    PeerHandler.peerHandlerMap.put(userName, newPeerHandler);
                    ClientThread clientThread = new ClientThread(newPeerHandler);
                    ServerThread serverThread = new ServerThread();
                    Thread tClient = new Thread(clientThread);
                    Thread tServer = new Thread(serverThread);
                    tServer.start();
                    tClient.start();
                    LOGGER.info("Creating a new handler for this client ...");


                }else  if ("yes-chat".equalsIgnoreCase(yerOrNo)) {
                    dos.writeUTF("ACCEPT");
                    LOGGER.info("Creating a new handler for this peer ...");
                    Peer newPeer = Peer.builder().username(userName).build();
                    PeerHandler newPeerHandler = PeerHandler.builder().peer(newPeer).dis(inputStream).dos(dos).scn(new Scanner(System.in)).name(userName).socket(newRequestSocket).build();
                    PeerHandler.peerHandlerMap.put(userName, newPeerHandler);
                    ClientThread clientThread = new ClientThread(newPeerHandler);
                    Thread tClient = new Thread(clientThread);
                    tClient.start();

                    LOGGER.info("Creating a new handler for this client ...");

                } else {
                    dos.writeUTF("REJECT");
                    RegistryConnection registryConnection = new RegistryConnection();
                    RegistryHandlings registryHandlings = new RegistryHandlingsImpl();
                    registryHandlings.connectRegistry(registryConnection, StartApp.name, false);
                }


            } catch (IOException e) {
                LOGGER.error("IO error in newHandler socket", e);
            }


        }
    }

}
