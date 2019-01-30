package model;

public enum ColorDefinition {

    /**
     * Color definition for the Pieces Joker is represented by WHITE
     */

    /**
     * Each Definition has its own toString() method
     */

    RED {
        @Override
        public String toString() {
            return "RED";
        }
    },
    BLUE {
        @Override
        public String toString() {
            return "BLUE";
        }
    },
    GREEN {
        @Override
        public String toString() {
            return "GREEN";
        }
    },
    YELLOW {
        @Override
        public String toString() {
            return "YELLOW";
        }
    },
    PURPLE {
        @Override
        public String toString() {
            return "PURPLE";
        }
    },
    WHITE {
        @Override
        public String toString() {
            return "WHITE";
        }
    }

}
