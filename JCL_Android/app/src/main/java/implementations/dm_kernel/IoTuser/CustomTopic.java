package implementations.dm_kernel.IoTuser;

public class CustomTopic {
	private JCL_Expression expression;
	private String topicName;
	private boolean triggered = false;
	
	public JCL_Expression getExpression() {
		return expression;
	}
	public void setExpression(JCL_Expression expression) {
		this.expression = expression;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public boolean isTriggered() {
		return triggered;
	}
	public void setTriggered(boolean triggered) {
		this.triggered = triggered;
	}
}
