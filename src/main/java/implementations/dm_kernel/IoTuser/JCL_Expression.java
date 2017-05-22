package implementations.dm_kernel.IoTuser;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JCL_Expression {

    private String expression;   
    private Vector<String> operators;
    private Vector<Float> values;
    private Vector<Integer> indexes;
    
    public JCL_Expression(String expression) {
        this.expression = expression;
        operators = new Vector<>();
        values = new Vector<>();
        indexes=new Vector<>();
        getOperatorsAndValues();
    }

    private void getOperatorsAndValues() {
        String[] expressions = expression.split(";");
        for (int i = 0; i < expressions.length; i++) {
            try {

                Pattern timePattern = Pattern.compile("(?<=S)[0-9]+");
                Matcher m = timePattern.matcher(expressions[i]);
                if (m.find()) {
                    String match = m.group(0);
                    String[] matchSplit = match.split(":");
                    indexes.add(Integer.parseInt(matchSplit[0]));
                    timePattern = Pattern.compile(">=|<=|<|>|=|~");
                    m = timePattern.matcher(expressions[i]);
                    if (m.find()) {
                        match = m.group(0);
                        matchSplit = match.split(":");
                        operators.add(matchSplit[0]);
                        timePattern = Pattern.compile("(?<=" + matchSplit[0] + ")( )*([+-]?(\\d+\\.)?\\d+)");
                        m = timePattern.matcher(expressions[i]);
                        if (m.find()) {
                            match = m.group(0);
                            matchSplit = match.split(":");
                            values.add(Float.parseFloat(matchSplit[0].trim()));
                        } else {
                            System.err.println("Error in JCL_Expression: Invalid expression");
                        }

                    } else {
                        System.err.println("Error in JCL_Expression: Invalid expression");

                    }
                } else
                    System.err.println("Error in JCL_Expression: Invalid expression");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean check(float[] sensorValue, float[] lastValue) {
        for (int i = 0; i < operators.size(); i++) {
            try{
                if (!matchWith(operators.get(i), sensorValue[indexes.get(i)], values.get(i), lastValue[indexes.get(i)]))
                    return false;
            }catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error in JCL_Expression: Invalid Sensor index");
                return false;
            }
        }
        return true;
    }

    private boolean matchWith(String operator, float sensorValue, float thresholdValue, float lastValue) {
        if (operator.equals(">") && sensorValue > thresholdValue)
            return true;
        else if (operator.equals("<") && sensorValue < thresholdValue)
            return true;
        else if (operator.equals(">=") && sensorValue >= thresholdValue)
            return true;
        else if (operator.equals("<=") && sensorValue <= thresholdValue)
            return true;
        else if (operator.equals("=") && sensorValue == thresholdValue)
            return true;
        else if (operator.equals("~") && Math.abs(sensorValue - lastValue) >= thresholdValue)
        	return true;
        else
            return false;
    }

    public String toString() {
        return expression;
    }

	public String getExpression() {
		return expression;
	}

	public Vector<String> getOperators() {
		return operators;
	}
}