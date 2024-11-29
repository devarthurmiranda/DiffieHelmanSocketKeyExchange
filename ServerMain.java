import java.io.IOException;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        Alice ctrl = new Alice();
        try {
            ctrl.createServerSocket(1234);
            Socket socket = ctrl.acceptConnection();
            ctrl.dataTransfer(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
