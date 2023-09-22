#Encapsulation of prometheus-related operations

Encapsulate the data types in prometheus such as counter, gauge, and histogram, which is convenient for users to use.

Provide metricsTime annotations, which can automatically record information such as time consumption and access volume of functions

After quoting the package, a simple httpServer will be opened. The default listening port is 4444. You can use localhost:4444 to view the exposed indicator data locally.

For example:

     Metrics.getInstance().newCounter("testCounter2", "age", "city").with("99", "china").add(1,"18","beijing"); //counter type encapsulation
     Metrics.getInstance().newGauge("testGauge", "name").with("zxw").set(128,"zxw"); //gauge type encapsulation
     Metrics.getInstance().newHistogram("testNotMatchLabelNameAndLabelValueExceptionHistogram", null, "b", "c").with("1").observe(1); //histogram type encapsulation