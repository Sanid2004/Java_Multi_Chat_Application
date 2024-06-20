import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandler = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    @Override
    public void run() {
        String messagefromclient;
        while (socket.isConnected()){
            try {
                messagefromclient = bufferedReader.readLine();
                broadcast(messagefromclient);
            }catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }
    ClientHandler(Socket socket){
        try{
            this.socket=socket;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.name = bufferedReader.readLine();
            clientHandler.add(this);
            broadcast("SERVER : "+name+" has just entered the chat !");
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void broadcast(String messagefromclient)  {
        for(ClientHandler client : clientHandler){
            if(!client.name.equals(name)){
                try {
                    client.bufferedWriter.write(messagefromclient);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }catch (IOException e){
                    closeEverything(socket,bufferedReader,bufferedWriter);
                }
            }
        }
    }

    public void RemoveClientHandler(){
        clientHandler.remove(this);
        broadcast("SERVER : "+name+"has left the chat!");
    }

    public void closeEverything(Socket socket,BufferedReader bufferedReader , BufferedWriter bufferedWriter)  {
        RemoveClientHandler();
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
