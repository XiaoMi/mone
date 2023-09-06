# Provide functions such as scheduled tasks.
# For example：

CronExpression expression = new CronExpression("0 47 11 ? * *");
System.out.println(expression.getNextValidTimeAfter(new Date()));