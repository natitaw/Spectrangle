package Connection;

public class CommandReader {

    public enum State {
        CLIENT, SERVER
    }

    public State readerState;

    public CommandReader(State stateInput){
        this.readerState = stateInput;
    }

    public void read(String inputString) {
        // seperate by spaces
        // get first command
        String[] seperateWords = inputString.split("\\s+");
        String command = seperateWords[0];

        if (this.readerState==State.CLIENT){
            switch (command) {
                //TODO Make case for 4 or 8 whitespaces
                case "waiting":
                    //
                    break;
                case "start":
                    //
                    break;
                case "order":
                    //
                    break;

                case "replace":
                    //
                    break;
                case "move":
                    //
                    break;
                case "chat":
                    //S
                    break;
                default:
                    System.out.println("Server sent a command that was ignored:");
                    System.out.println(inputString);
                    break;
            }
        } else if (this.readerState==State.SERVER) {
            switch (command) {
                //TODO Make case for 4 or 8 whitespaces
                case "connect":
                    //
                    break;
                case "request":
                    //
                    break;
                case "place":
                    //
                    break;
                case "skip":
                    //
                    break;
                case "exchange":
                    //
                    break;
                case "move":
                    //
                    break;
                case "chat":
                    //
                    break;
                default:
                    System.out.println("Client sent a command that was ignored:");
                    System.out.println(inputString);
                    break;
            }
        }
    }
}


