package Game;

public enum ColorDefinition {
	
	/**
	 * Color definition for the pieces
	 * Joker is represented by WHITE
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
	}; //joker

}
