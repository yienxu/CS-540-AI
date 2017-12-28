
// CS 540 HW 06
// Yien Xu
// yxu322@wisc.edu

import java.util.*;

class State {
    char[] board;

    public int value;
    public int alpha;
    public int beta;

    public State(char[] arr) {
        this.board = Arrays.copyOf(arr, arr.length);
    }

    public int getScore() {
        if (!isTerminal()) {
            throw new UnsupportedOperationException("No score for non-terminal state.");
        }

        int darkCount = 0;
        int lightCount = 0;

        for (char tile : board) {
            if (tile == Reversi.DARK) {
                darkCount++;
            } else if (tile == Reversi.LIGHT) {
                lightCount++;
            }
        }

        return darkCount > lightCount ? 1 : (darkCount < lightCount ? -1 : 0);
    }

    public boolean isTerminal() {
        return getSuccessors(Reversi.DARK).length == 0 && getSuccessors(Reversi.LIGHT).length == 0;
    }

    private char[][] charToBoard(char[] myBoard) {
        int dim = (int) Math.sqrt(myBoard.length);
        char[][] board = new char[dim][dim];
        int iter = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = myBoard[iter++];
            }
        }
        return board;
    }

    private char[] boardToChar(char[][] board) {
        char[] myBoard = new char[(int) Math.pow(board.length, 2)];
        int iter = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                myBoard[iter++] = board[i][j];
            }
        }
        return myBoard;
    }

    public State[] getSuccessors(char player) {
        List<State> list = new ArrayList<>();

        char[][] board = charToBoard(this.board);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                // make a temporary 2D char array
                char[][] curr = new char[board.length][board[i].length];
                // copy all values to the temp char array
                for (int m = 0; m < board.length; m++) {
                    for (int n = 0; n < board[m].length; n++) {
                        curr[m][n] = board[m][n];
                    }
                }
                // Only proceed if there's an empty spot
                if (curr[i][j] != Reversi.EMPTY) {
                    continue;
                }
                // place the new piece
                curr[i][j] = player;
                boolean isFlipped = false;

                // horizontally forward
                int flipIndex = -1;
                for (int k = j + 1; k < curr[i].length; k++) {
                    if (curr[i][k] == Reversi.EMPTY) {
                        break;
                    } else if (curr[i][k] == player) {
                        flipIndex = k;
                        break;
                    }
                }
                // flip all tiles in-between
                for (int k = j + 1; k < flipIndex; k++) {
                    curr[i][k] = player;
                    isFlipped = true;
                }

                // horizontally backward
                flipIndex = curr[i].length;
                for (int k = j - 1; k >= 0; k--) {
                    if (curr[i][k] == Reversi.EMPTY) {
                        break;
                    } else if (curr[i][k] == player) {
                        flipIndex = k;
                        break;
                    }
                }
                // flip all tiles in-between
                for (int k = j - 1; k > flipIndex; k--) {
                    curr[i][k] = player;
                    isFlipped = true;
                }

                // vertically downward
                flipIndex = -1;
                for (int k = i + 1; k < curr.length; k++) {
                    if (curr[k][j] == Reversi.EMPTY) {
                        break;
                    } else if (curr[k][j] == player) {
                        flipIndex = k;
                        break;
                    }
                }
                // flip all tiles in-between
                for (int k = i + 1; k < flipIndex; k++) {
                    curr[k][j] = player;
                    isFlipped = true;
                }

                // vertically upward
                flipIndex = curr.length;
                for (int k = i - 1; k >= 0; k--) {
                    if (curr[k][j] == Reversi.EMPTY) {
                        break;
                    } else if (curr[k][j] == player) {
                        flipIndex = k;
                        break;
                    }
                }
                // flip all tiles in-between
                for (int k = i - 1; k > flipIndex; k--) {
                    curr[k][j] = player;
                    isFlipped = true;
                }

                // north-east
                flipIndex = curr.length;
                for (int m = i - 1, n = j + 1; m >= 0 && n < curr[j].length; m--, n++) {
                    if (curr[m][n] == Reversi.EMPTY) {
                        break;
                    } else if (curr[m][n] == player) {
                        flipIndex = m;
                        break;
                    }
                }
                // flip all tiles in-between
                for (int m = i - 1, n = j + 1; m > flipIndex; m--, n++) {
                    curr[m][n] = player;
                    isFlipped = true;
                }

                // south-east
                flipIndex = -1;
                for (int m = i + 1, n = j + 1; m < curr.length && n < curr[i].length; m++, n++) {
                    if (curr[m][n] == Reversi.EMPTY) {
                        break;
                    } else if (curr[m][n] == player) {
                        flipIndex = m;
                        break;
                    }
                }
                // flip all tiles in-between
                for (int m = i + 1, n = j + 1; m < flipIndex; m++, n++) {
                    curr[m][n] = player;
                    isFlipped = true;
                }

                // north-west
                flipIndex = curr.length;
                for (int m = i - 1, n = j - 1; m >= 0 && n >= 0; m--, n--) {
                    if (curr[m][n] == Reversi.EMPTY) {
                        break;
                    } else if (curr[m][n] == player) {
                        flipIndex = m;
                        break;
                    }
                }
                // flip all tiles in-between
                for (int m = i - 1, n = j - 1; m > flipIndex; m--, n--) {
                    curr[m][n] = player;
                    isFlipped = true;
                }
                // south-west
                flipIndex = -1;
                for (int m = i + 1, n = j - 1; m < curr.length && n >= 0; m++, n--) {
                    if (curr[m][n] == Reversi.EMPTY) {
                        break;
                    } else if (curr[m][n] == player) {
                        flipIndex = m;
                        break;
                    }
                }
                // flip all tiles in-between
                for (int m = i + 1, n = j - 1; m < flipIndex; m++, n--) {
                    curr[m][n] = player;
                    isFlipped = true;
                }

                // if any tile could be flipped, then consider it to be a
                // successor
                if (isFlipped) {
                    list.add(new State(boardToChar(curr)));
                }
            }
        }

        return list.toArray(new State[0]);
    }

    public void printState(int option, char player) {
        switch (option) {
        case 1:
            State[] succs = getSuccessors(player);
            for (State s : succs) {
                System.out.println(s);
            }
            if (succs.length == 0 && !isTerminal()) {
                System.out.println(this);
            }
            break;
        case 2:
            if (isTerminal()) {
                System.out.println(getScore());
            } else {
                System.out.println("non-terminal");
            }
            break;
        case 3:
            System.out.println(Minimax.run(this, player));
            System.out.println(Minimax.numCalls);
            break;
        case 4:
            succs = getSuccessors(player);
            if (succs.length == 0 && !isTerminal()) {
                System.out.println(this);
                break;
            }
            for (State s : succs) {
                s.value = Minimax.run(s, player);
            }
            int bestIndex = -1;
            if (player == Reversi.DARK) {
                int bestVal = Integer.MIN_VALUE;
                for (int i = 0; i < succs.length; i++) {
                    if (succs[i].value > bestVal) {
                        bestVal = succs[i].value;
                        bestIndex = i;
                    }
                }
            } else {
                int bestVal = Integer.MAX_VALUE;
                for (int i = 0; i < succs.length; i++) {
                    if (succs[i].value < bestVal) {
                        bestVal = succs[i].value;
                        bestIndex = i;
                    }
                }
            }
            if (bestIndex != -1) {
                System.out.println(succs[bestIndex]);
            }
            break;
        case 5:
            System.out.println(Minimax.run_with_pruning(this, player));
            System.out.println(Minimax.numCalls);
            break;
        case 6:
            succs = getSuccessors(player);
            if (succs.length == 0 && !isTerminal()) {
                System.out.println(this);
                break;
            }
            for (State s : succs) {
                s.value = Minimax.run_with_pruning(s, player);
            }
            bestIndex = -1;
            if (player == Reversi.DARK) {
                int bestVal = Integer.MIN_VALUE;
                for (int i = 0; i < succs.length; i++) {
                    if (succs[i].value > bestVal) {
                        bestVal = succs[i].value;
                        bestIndex = i;
                    }
                }
            } else {
                int bestVal = Integer.MAX_VALUE;
                for (int i = 0; i < succs.length; i++) {
                    if (succs[i].value < bestVal) {
                        bestVal = succs[i].value;
                        bestIndex = i;
                    }
                }
            }
            if (bestIndex != -1) {
                System.out.println(succs[bestIndex]);
            }
            break;

        default:
            throw new UnsupportedOperationException("Option " + option + " is not supported.");
        }
    }

    public String getBoard() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            builder.append(this.board[i]);
        }
        return builder.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof State)) {
            return false;
        }
        State src = (State) o;

        if (this == o) {
            return true;
        }

        for (int i = 0; i < 16; i++) {
            if (this.board[i] != src.board[i])
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getBoard();
    }
}


class Minimax {
    public static int numCalls = 0;

    private static int max_value(State curr) {
        numCalls++;
        if (curr.isTerminal()) {
            return curr.getScore();
        }

        curr.value = Integer.MIN_VALUE;
        State[] succs = curr.getSuccessors(Reversi.DARK);
        if (succs.length == 0) {
            succs = new State[] {curr};
        }
        for (State succ : succs) {
            curr.value = Math.max(curr.value, min_value(succ));
        }
        return curr.value;

    }

    private static int min_value(State curr) {
        numCalls++;
        if (curr.isTerminal()) {
            return curr.getScore();
        }

        curr.value = Integer.MAX_VALUE;
        State[] succs = curr.getSuccessors(Reversi.LIGHT);
        if (succs.length == 0) {
            succs = new State[] {curr};
        }
        for (State succ : succs) {
            curr.value = Math.min(curr.value, max_value(succ));
        }
        return curr.value;
    }

    private static int max_value_with_pruning(State curr, int alpha, int beta) {
        numCalls++;
        if (curr.isTerminal()) {
            return curr.getScore();
        }

        curr.value = Integer.MIN_VALUE;
        State[] succs = curr.getSuccessors(Reversi.DARK);
        if (succs.length == 0) {
            succs = new State[] {curr};
        }
        for (State succ : succs) {
            int minValue = min_value_with_pruning(succ, alpha, beta);
            alpha = Math.max(alpha, minValue);
            curr.value = Math.max(curr.value, minValue);
            curr.alpha = alpha;
            curr.beta = beta;
            if (alpha >= beta) {
                return beta;
            }
        }
        return alpha;
    }

    private static int min_value_with_pruning(State curr, int alpha, int beta) {
        numCalls++;
        if (curr.isTerminal()) {
            return curr.getScore();
        }

        curr.value = Integer.MAX_VALUE;
        State[] succs = curr.getSuccessors(Reversi.LIGHT);
        if (succs.length == 0) {
            succs = new State[] {curr};
        }
        for (State succ : succs) {
            int maxValue = max_value_with_pruning(succ, alpha, beta);
            beta = Math.min(beta, maxValue);
            curr.value = Math.min(curr.value, maxValue);
            curr.alpha = alpha;
            curr.beta = beta;
            if (alpha >= beta) {
                return alpha;
            }
        }
        return beta;
    }

    public static int run(State curr, char player) {
        numCalls = 0;
        return player == Reversi.DARK ? max_value(curr) : min_value(curr);
    }

    public static int run_with_pruning(State curr, char player) {
        numCalls = 0;
        return player == Reversi.DARK
            ? max_value_with_pruning(curr, Integer.MIN_VALUE, Integer.MAX_VALUE)
            : min_value_with_pruning(curr, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}


public class Reversi {
    public static final char EMPTY = '0';
    public static final char DARK = '1';
    public static final char LIGHT = '2';

    public static void main(String args[]) {
        if (args.length != 3) {
            System.out.println("Invalid Number of Input Arguments");
            return;
        }
        int flag = Integer.valueOf(args[0]);
        char[] board = new char[16];
        for (int i = 0; i < 16; i++) {
            board[i] = args[2].charAt(i);
        }
        int option = flag / 100;
        char player = args[1].charAt(0);
        if ((player != '1' && player != '2') || args[1].length() != 1) {
            System.out.println("Invalid Player Input");
            return;
        }
        State init = new State(board);
        init.printState(option, player);
    }
}
