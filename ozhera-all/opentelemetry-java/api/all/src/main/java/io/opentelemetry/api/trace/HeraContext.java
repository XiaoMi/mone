package io.opentelemetry.api.trace;

import java.util.HashMap;
import java.util.Map;

public class HeraContext {
  public static final int LIMIT_LENGTH = 8;
  public static final String HERA_CONTEXT_PROPAGATOR_KEY = "heracontext";
  public static final String ENTRY_SPLIT = ";";
  public static final String KEY_VALUE_SPLIT = ":";

  private HeraContext(){

  }

  public static Map<String,String> getInvalid() {
    return wrap(null);
  }

  public static Map<String,String> getDefault() {
    return wrap(null);
  }

  public static boolean isValid(Map<String,String> heraContext){
    if(heraContext != null && heraContext.size() > 0){
      String heraContextString = heraContext.get(HERA_CONTEXT_PROPAGATOR_KEY);
      if(heraContextString != null && !heraContextString.isEmpty()){
        return heraContextString.split(ENTRY_SPLIT).length <= LIMIT_LENGTH;
      }
    }
    return false;
  }

  public static Map<String,String> wrap(String heraContext){
    Map<String, String> result = new HashMap<>();
    result.put(HERA_CONTEXT_PROPAGATOR_KEY, heraContext);
    return result;
  }
}
