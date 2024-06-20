import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;

    public Client(Socket socket,String username){
        try {
            this.socket = socket;
            this.username = username;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner sc = new Scanner(System.in);
            while(socket.isConnected()){
                String  messagetosend = sc.nextLine();
                bufferedWriter.write(username + " : " + messagetosend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void listenforMessage(){
        new Thread(() -> {
            String msgfromgroupchat;
            while (socket.isConnected()){
                try{
                    msgfromgroupchat = bufferedReader.readLine();
                    System.out.println(msgfromgroupchat);
                }catch (IOException e){
                    closeEverything(socket,bufferedReader,bufferedWriter);
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
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

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your name for the group chat : ");
        String username = sc.nextLine();
        Socket socket = new Socket("localhost",1234);
        Client client = new Client(socket,username);
        client.listenforMessage();
        client.sendMessage();
    }

}
