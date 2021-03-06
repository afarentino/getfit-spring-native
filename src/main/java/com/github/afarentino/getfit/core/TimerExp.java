package com.github.afarentino.getfit.core;

import java.math.RoundingMode;
import java.util.StringTokenizer;
import java.text.DecimalFormat;

/**
 * @see https://mkyong.com/java/how-to-round-double-float-value-to-2-decimal-points-in-java/
 */
public class TimerExp extends Component {
    private Integer minutes = 0;
    private Integer seconds = 0;

    private DistanceExp delegate = new DistanceExp();
    private String clockTime;

    private String text;
    private Double decMinutes;

    protected Type t;
    private boolean hasInZone = false;

    public boolean hasInZone() {
        return this.hasInZone;
    }

    public boolean hasClockTime() {
        return this.clockTime != null;
    }

    public String getClockTime() {
        return this.clockTime;
    }

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
    String getValue() {
        return this.decMinutes.toString();
    }

    @Override
    void setValue(Object value) {
        try {
            String newText = (String) value;
            this.parse(newText);
        } catch (ParseException ex) {
            throw new IllegalStateException( ex );
        }
    }

    @Override
    public Type getType() { return t; }

    /**
     * Override the display type for this TimerExp
     * @param t
     */
    public void setType(Component.Type t) {
        this.t = t;
    }

    @Override
    public String toString() {
        if (this.getType() == Type.INZONE) {
            DecimalFormat df = new DecimalFormat( "0.00");
            df.setRoundingMode(RoundingMode.UP);
            return df.format(decMinutes);
        } else {
            // Use Math.round to convert to int it will round the double up
            int minutes = (int) Math.round(decMinutes);
            return Integer.toString(minutes);
        }
    }

    void parse(String text) throws ParseException {
        try {
            delegate.parse(text);
        } catch (ParseException ex) {
            logger.debug("Text is not a DistanceExp: Continuing");
        }
        if (text.toUpperCase().contains("AM") || text.toUpperCase().contains("PM")) {
            if (text.toUpperCase().contains("AT") == false) {
                throw new ParseException("Unparseable TimerExp: \"" + text + "\" contains only AM or PM");
            }
            // Try to extract clockTime String from the remaining text
            if (text.length() > 2) {
                int start = text.toUpperCase().lastIndexOf("AT") + 2;
                String candidate = text.substring(start).trim();
                this.clockTime = firstIn(candidate, TIME_PATTERN);
            }
        }
        if (delegate.toString().isEmpty() == false) {
            throw new ParseException("Invalid TimerExp " + text);
        }

        int startIndex = firstDigit(text);
        boolean hasMin = false;
        if (startIndex == -1) {
            throw new ParseException("Unparseable Exp: \"" + text + "\" does not contain a digit");
        }
        if (text.contains("min")) {
            text = text.substring(startIndex, text.indexOf("min")-1);
            hasMin = true;
        }
        int colonIndex = firstColon(text);
        if (colonIndex == -1) {
            if ((hasMin == false) && !text.contains("sec") ) {
                throw new ParseException("Invalid TimeExp: " + text);
            }
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
        // Wrap up parsing work here by setting state
        this.text = text;
        double secs = seconds * (1.0/60.0);
        double decMinutesVal = minutes + secs;
        this.decMinutes = decMinutesVal;
        if (text.contains("in zone")) {
            hasInZone = true;
            this.setType(Component.Type.INZONE);
        } else {
            this.setType(Component.Type.TOTALTIME);
        }
    }
}
