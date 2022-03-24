import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {

            Socket socket = new Socket("localhost",8100);
            ObjectOutputStream outputToServer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputFromServer = new ObjectInputStream(socket.getInputStream());
            outputToServer.writeObject("client");
            while (true) {
                String username = readFromUser("Enter username :");
                String password = readFromUser("Enter password :");
                outputToServer.writeObject(username);
                outputToServer.writeObject(password);
                if ((boolean)inputFromServer.readObject())
                    break;
                System.out.println(inputFromServer.readObject().toString());
            }
            int port = (int)inputFromServer.readObject();
            socket.close();
            socket = new Socket("localhost",port);
            outputToServer = new ObjectOutputStream(socket.getOutputStream());
            inputFromServer = new ObjectInputStream(socket.getInputStream());
            String inputFromNode;
            while (true){
                inputFromNode = (String)inputFromServer.readObject();
                System.out.print(inputFromNode);
                if(inputFromNode.contains("Enter") || inputFromNode.contains("Do"))
                    outputToServer.writeObject(readFromUser(""));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static String readFromUser(String message) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(message);
        return bufferedReader.readLine().trim();
    }
}
