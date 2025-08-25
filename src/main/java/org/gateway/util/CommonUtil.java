package org.gateway.util;

import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class CommonUtil {
	
	public boolean isNotNullOrEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    public boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    
    public boolean isNotNullOrEmpty(Object obj) {
        return obj != null;
    }

    public boolean isNullOrEmpty(Object obj) {
        return obj == null;
    }

	public boolean isNotNullOrEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }
	
	public boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || (collection.size()==0 && collection.isEmpty());
    }
	
	public boolean isNullOrEmpty(String str) {
        return str == null || (str.length()==0 && str.isEmpty()) || str.isBlank();
    }
	
	public boolean isNotNullOrEmpty(String str) {
        return str != null && !str.isEmpty() && !str.isBlank();
    }
}
