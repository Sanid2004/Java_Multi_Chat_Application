
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    ServerSocket serversocket ;
    public Server(ServerSocket serversocket){
        this.serversocket=serversocket;
    }

    public void start_server(){
        try{

            while(!serversocket.isClosed()){
                Socket socket = serversocket.accept();
                System.out.println("A new Client get added");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        }catch (IOException e){
                close_server();
        }
    }

    public void close_server(){
        try {
            if(serversocket!=null){
                serversocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.start_server();
    }
}
