import java.util.Scanner;

@SuppressWarnings("serial")
class InvalidMoveException extends Exception {
}


public class CLIReversi {
    @SuppressWarnings("resource")
    public static void main(String args[]) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Welcome message
        System.out.println("Welcome to Reversi-Tiny!");

        // Specify which player is human
        String player = "";
        do {
            System.out.println("Do you want to play as " + "player 1 or player 2? [Enter 1 or 2]");
            player = scanner.nextLine();
        } while (!player.equals("1") && !player.equals("2"));
        String computer = (player.equals("1")) ? "2" : "1";

        // Specify who play first
        Boolean human_first = null; // note: nullable boolean
        do {
            System.out.println("Do you want to play first? [Y/n]");
            String line = "";
            line = scanner.nextLine();
            if (line.equalsIgnoreCase("yes") || line.equalsIgnoreCase("y")) {
                human_first = true;
            } else if (line.equalsIgnoreCase("no") || line.equalsIgnoreCase("n")) {
                human_first = false;
            }
        } while (human_first == null);

        // Init board
        char[] board = new char[16];
        for (int i = 0; i < 16; i++) {
            if (i == 5 || i == 10) {
                board[i] = '2';
            } else if (i == 6 || i == 9) {
                board[i] = '1';
            } else {
                board[i] = '0';
            }
        }
        State curr_state = new State(board);
        System.out.println("");

        if (!human_first) {
            System.out.println(String.format("Player %s's turn", computer));
            curr_state = getOptimalMove(curr_state, computer.charAt(0));
            printPrettyBoard(curr_state);
            System.out.println("");
        } else
            printPrettyBoard(curr_state);

        do {
            // Human's turn
            if (curr_state.isTerminal())
                break;
            if (curr_state.getSuccessors(player.charAt(0)).length == 0) {
                System.out.println(String.format("Player %s can't move", player));
            } else {
                System.out.println(String.format("Player %s's turn", player));
                String move;
                do {
                    move = "";
                    System.out.println("Enter the board position to put your new piece [1-16]:");
                    move = scanner.nextLine();
                    boolean isEmpty = false;
                    try {
                        isEmpty = isPosEmpty(move, curr_state);
                        if (isEmpty) {
                            int move_pos = Integer.parseInt(move) - 1;
                            State temp = updateState(curr_state, move_pos, player.charAt(0));
                            if (temp == null)
                                throw new Exception();
                            else {
                                curr_state = temp;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                } while (true);

                System.out.println("");
                printPrettyBoard(curr_state);;
                System.out.println("");
            }

            // Computer's turn
            if (curr_state.isTerminal())
                break;
            if (curr_state.getSuccessors(computer.charAt(0)).length == 0) {
                System.out.println(String.format("Player %s can't move", computer));
            } else {
                System.out.println(String.format("Player %s's turn", computer));
                curr_state = getOptimalMove(curr_state, computer.charAt(0));
                printPrettyBoard(curr_state);
                System.out.println("");
            }
        } while (true);

        int final_score = curr_state.getScore();
        if (final_score == 0)
            System.out.println("Tied Game!");
        else if (final_score == 1)
            System.out.println("Player 1 Wins!");
        else if (final_score == -1)
            System.out.println("Player 2 Wins!");
        else
            throw new Exception();

        scanner.close(); // close scanner
    }

    private static State updateState(State curr_state, int move_pos, char player) {
        for (State s : curr_state.getSuccessors(player)) {
            if (s.getBoard().charAt(move_pos) != '0')
                return s;
        }
        return null;
    }

    private static boolean isPosEmpty(String move, State curr_state) throws InvalidMoveException {
        String board = curr_state.getBoard();
        try {
            int move_in_int = Integer.parseInt(move);
            if (move_in_int < 1 || move_in_int > 16)
                return false;
            char char_at_pos = board.charAt(move_in_int - 1);
            if (char_at_pos != '0')
                return false;
            else
                return true;
        } catch (Exception e) {
            throw new InvalidMoveException();
        }
    }

    private static State getOptimalMove(State curr_state, char player) {

        if (curr_state.isTerminal())
            return null;

        State[] successors = curr_state.getSuccessors(player);
        if (successors.length == 0)
            return null;
        else {
            State opt_state = null;
            int opt_val;
            if (player == '1')
                opt_val = Integer.MIN_VALUE;
            else
                opt_val = Integer.MAX_VALUE;
            for (State s : curr_state.getSuccessors(player)) {
                if (player == '1') {
                    int curr_val = 0; // default - harmless
                    curr_val = Minimax.run_with_pruning(s, '2');

                    if (curr_val > opt_val) {
                        opt_val = curr_val;
                        opt_state = new State(s.getBoard().toCharArray());
                    }
                } else {
                    int curr_val = 0;
                    curr_val = Minimax.run_with_pruning(s, '1');

                    if (curr_val < opt_val) {
                        opt_val = curr_val;
                        opt_state = new State(s.getBoard().toCharArray());
                    }
                }
            }
            return opt_state;
        }
    }

    private static void printPrettyBoard(State s) {
        String b = s.getBoard();
        System.out.println("-----------------");
        System.out.printf("| %s | %s | %s | %s |\n", b.charAt(0), b.charAt(1), b.charAt(2),
            b.charAt(3));
        System.out.println("-----------------");
        System.out.printf("| %s | %s | %s | %s |\n", b.charAt(4), b.charAt(5), b.charAt(6),
            b.charAt(7));
        System.out.println("-----------------");
        System.out.printf("| %s | %s | %s | %s |\n", b.charAt(8), b.charAt(9), b.charAt(10),
            b.charAt(11));
        System.out.println("-----------------");
        System.out.printf("| %s | %s | %s | %s |\n", b.charAt(12), b.charAt(13), b.charAt(14),
            b.charAt(15));
        System.out.println("-----------------");
    }
}
