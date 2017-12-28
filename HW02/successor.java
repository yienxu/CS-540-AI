// CS 540 Lec 001
// HW 2
// Author: Yien Xu
// Email: yxu322@wisc.edu

import java.util.ArrayList;

public class successor {
    public static class JugState {
        int[] Capacity = new int[]{0,0,0};
        int[] Content = new int[]{0,0,0};
        public JugState() {

        }

        // Constructor: make a copy of the current JugState and update two
        // contents. If input of contentToUpdate contains numbers 
        // other than 0, 1, 2, then no updates will be made.
        public JugState(JugState curr, int contentToUpdate1, int content1,
                        int contentToUpdate2, int content2) {
            this.Capacity[0] = curr.Capacity[0];
            this.Capacity[1] = curr.Capacity[1];
            this.Capacity[2] = curr.Capacity[2];
            this.Content[0] = curr.Content[0];
            this.Content[1] = curr.Content[1];
            this.Content[2] = curr.Content[2];
            if (contentToUpdate1 >= 0 && contentToUpdate1 < Content.length) {
                Content[contentToUpdate1] = content1;
            }
            if (contentToUpdate2 >= 0 && contentToUpdate2 < Content.length) {
                Content[contentToUpdate2] = content2;
            }
        }
        
        public void printContent() {
            System.out.println(Content[0] + " " + Content[1] + " " + Content[2]);
        }

        public ArrayList<JugState> getNextStates() {
            ArrayList<JugState> successors = new ArrayList<>();
            // iterate through all jugs
            for (int i = 0; i < Content.length; i++) {
                //Empty a jug
                if (Content[i] != 0) {
                    successors.add(new JugState(this, i, 0, -1, -1));
                }

                // Fill a jug
                if (Content[i] < Capacity[i]) {
                    successors.add(new JugState(this, i, Capacity[i], -1, -1));
                }

                // Pour water from one jug to another until either the former
                // is empty or the latter is full.
                for (int j = 0; j < Content.length; j++) {
                    // if same jug, i is empty or j is full, skip
                    if (i == j || Content[i] == 0 || Content[j] == Capacity[j]) {
                        continue;
                    }
                    // pouring entire content of i to j
                    // if i is poured out completely
                    if (Content[j] + Content[i] <= Capacity[j]) {
                        successors.add(new JugState(this, i, 0, 
                        	j, Content[j] + Content[i]));
                    } else { 
                    // if j is completely filled
                        successors.add(new JugState(this, i, 
                            Content[j] + Content[i] - Capacity[j], j, Capacity[j]));
                    }
                }
            }
            return successors;
        }
    }

    public static void main(String[] args) {
        if (args.length != 6) {
            System.out.println("Usage: java successor [A] [B] [C] [a] [b] [c]");
            return;
        }

        // parse command line arguments
        JugState a = new JugState();
        a.Capacity[0] = Integer.parseInt(args[0]);
        a.Capacity[1] = Integer.parseInt(args[1]);
        a.Capacity[2] = Integer.parseInt(args[2]);
        a.Content[0] = Integer.parseInt(args[3]);
        a.Content[1] = Integer.parseInt(args[4]);
        a.Content[2] = Integer.parseInt(args[5]);

        // Implement this function
        ArrayList<JugState> asist = a.getNextStates();

        // Print out generated successors
        for (int i=0;i< asist.size(); i++) {
            asist.get(i).printContent();
        }
    }
}
