package org.seasar.caching.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class CallDescription implements Serializable {
    private int targetObject;
    private Class declaredClass;
    private String methodName;
    private Class[] methodArguments;
    private Object[] argument;
    
    public CallDescription( MethodInvocation invocation ) {
        Method method = invocation.getMethod();
        argument = invocation.getArguments();
        targetObject = System.identityHashCode(invocation.getThis());
        declaredClass = method.getDeclaringClass();
        methodName = method.getName();
        methodArguments = method.getParameterTypes();
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
        if (!(object instanceof CallDescription)) {
            return false;
        }
        CallDescription rhs = (CallDescription) object;
        return new EqualsBuilder().append(this.methodArguments, rhs.methodArguments).append(
                this.declaredClass, rhs.declaredClass).append(this.argument, rhs.argument).append(
                this.methodName, rhs.methodName).append(targetObject, rhs.targetObject).isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(661437325, -495862237).append(
                this.methodArguments).append(this.declaredClass).append(this.argument)
                .append(this.methodName).append(targetObject).toHashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new ToStringBuilder(this).append(this.methodName).append(this.methodArguments)
                .append(this.argument).append(this.targetObject).toString();
    }
}
