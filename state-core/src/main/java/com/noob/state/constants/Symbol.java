package com.noob.state.constants;

public interface Symbol {

	String EMPTY = "";
	String COLON = ":";
	String COMMA = ",";
	String SEMICOLON = ";";
	String MIDDLE_LINE = "-";

	String PLACEHOLDER = "%s";
	String DELIMITER_PART = "@";
	String DELIMITER = String.join(MIDDLE_LINE, DELIMITER_PART, DELIMITER_PART);

	String LOG_TEMPLETE = "From: [%s] To [%s]";

}
