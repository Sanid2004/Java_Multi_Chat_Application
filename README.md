# Java_Multi_Chat_Application

                                  <-----Project Description----->
   Tools used : Socket Programming, Concepts of Multi-Threading and Oops

   Here , I have tried to establish a multi - chat Application in Java.
   ->Multiple Clients can enter the chat at a given time.
   ->Whenever a client gets added server notifies about it , to other clients
   ->Client can send messages to other clients.
   ->Whenever a client leaves the chat , server notifies about it to other clients

   Working : Each Server has a server socket which waits for the client's request and
             creates it's socket.
             Each client already have it's own socket.
             The server and client needs to work on same port (1234 here).
                            <--SERVER-->
              Here a server socket is created on the port 1234
              public static void main(String[] args) throws IOException {
                     ServerSocket serverSocket = new ServerSocket(1234);
                     Server server = new Server(serverSocket);
                     server.start_server();
                 }


              Here if a client wants to establish a connection , a socket is created.
              "A new Client get added" gets printed on Server's console.
              A client Handler object is created for that client, which will handle all the request from client to server and the messages that need to be delivered to other clients
              Since , for each client we are having a seperate thread , thus the Client Handler class implements Runnable
              Then a new thread is created and start method is called which internally calls the run() method
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
                                <--Client Handler-->
              Each socket has a bufferedReader(to read the input from other clients or server) and
              bufferedWriter(to write the input to other clients and to server)
              Each Client will have it's own socket
              Each Client will have a unique name
              To maintain the list of all clients we maintain an ArrayList of all clients.
              public static ArrayList<ClientHandler> clientHandler = new ArrayList<>();
                  private Socket socket;
                  private BufferedReader bufferedReader;
                  private BufferedWriter bufferedWriter;
                  private String name;


              The values are initialized by the constructor
              Then the message : "SERVER : "+name+" has just entered the chat !" is broadcasted using broadcast function
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

              // Broadcast Function
              Assumption  : Each Client will have a unique name
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

               Suppose in any case if bufferedReader or BufferedWriter or Socket fails
               then firstly we remove that client from ArrayList of clients and then close all of them
               We need to check that if they are null or not otherwise it gives null pointer exception

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

                   public void RemoveClientHandler(){
                          clientHandler.remove(this);
                          broadcast("SERVER : "+name+"has left the chat!");
                      }

                      <--Client-->


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

                  Here we create the socket for Client .Since I am working on my computer that's why "localhost"
                  I have created separate thread to listen the message and main thread for sending the message
                  public static void main(String[] args) throws IOException {
                          Scanner sc = new Scanner(System.in);
                          System.out.println("Enter your name for the group chat : ");
                          String username = sc.nextLine();
                          Socket socket = new Socket("localhost",1234);
                          Client client = new Client(socket,username);
                          client.listenforMessage();
                          client.sendMessage();
                      }

                   Here firstly the name of client is printed and then the message is sended using BufferedWriter
                   flush function is used to clear the bufferedWriter
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

                    A separate thread used for listening the messages from other client and printing on it's console
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
