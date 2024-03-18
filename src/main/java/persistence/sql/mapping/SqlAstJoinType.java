package persistence.sql.mapping;

public enum SqlAstJoinType {
   	INNER(""),
   	LEFT("left "),
   	RIGHT("right "),
   	CROSS("cross "),
   	FULL("full ");

   	private final String text;

    SqlAstJoinType(final String text) {
   		this.text = text;
   	}

    @Override
   	public String toString() {
        return this.text;
   	}
}
