package com.github.afarentino.getfit.core;
import java.util.StringTokenizer;

public class TimerExp extends Component {
    private Integer minutes = 0;
    private Integer seconds = 0;

    private static Integer getInteger(String sec) {
       return Integer.parseInt(sec);
    }

    private void setSeconds(String val) {
        this.seconds = getInteger(val);
    }

    private void setMinutes(String val) {
        this.minutes = getInteger(val);
    }

    @Override
    public Type getType() { return Type.TOTALTIME; }

    @Override
    public String toString() {
        if (minutes > 0) {
            if (seconds > 0) { return minutes + " min " + seconds + " sec"; }
            return minutes + " min";
        }
        return seconds + " sec";
    }

    void parse(String text) throws ParseException {
        int startIndex = firstDigit(text);
        if (startIndex == -1) {
            throw new ParseException("Unparseable Exp: \"" + text + "\" does not contain a digit");
        }
        if (text.contains("min")) {
            text = text.substring(startIndex, text.indexOf("min")-1);
        }
        int colonIndex = firstColon(text);
        if (colonIndex == -1) {
            setSeconds("0");
            setMinutes(text);
        } else {
            try {
                StringTokenizer st = new StringTokenizer(text.substring(startIndex), ":");
                setMinutes(st.nextToken());
                while (st.hasMoreTokens()) {
                    String candidate = st.nextToken();
                    int lastDigit = lastDigit(candidate);
                    setSeconds(candidate.substring(0, lastDigit));
                    break;
                }
            } catch (NumberFormatException e) {
                throw new ParseException("Invalid TimerExp " + text, e);
            }
        }
    }
}