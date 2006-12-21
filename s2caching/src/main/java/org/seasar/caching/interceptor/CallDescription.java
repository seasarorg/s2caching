package org.seasar.caching.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class CallDescription implements Serializable {
    /*
     * �Ăяo���Ώۂ̃I�u�W�F�N�g�́A�Ăяo���L���b�V�����邩�ǂ����ɂ��ďd�v�ȏ��ł���B
     * ���̂��߁A�L���b�V���̃L�[�̈ꕔ���\������v�f�Ƃ��ĕK�v�ł���B
     * �������A�Ăяo���Ώۂ̃I�u�W�F�N�g�������Ǝ��ʂ��邱�Ƃ��ł���V���A���C�Y�\�ȏ�񂪂Ȃ��B
     * �Ώ�Object���̂��́A�ł̓V���A���C�Y�����Ƃ��ɂނ��남�����Ȃ��ƂɂȂ邵�ASoftRefenrece
     * �Ȃǂ̓V���A���C�Y�\�ł͂Ȃ��B
     * hashCode()���g�����Ƃɂ������AhashCode()�ɂ��Ă� �������Ƃ��ɂ�equal�ł���A�Ƃ�����`
     * �͂���Ă��Ȃ� (equals()��false��2�I�u�W�F�N�g�ɂ���hashCode()���قȂ�\��������)
     * ���[��... orz
     */
    private Object placeHolder = new Object(); // unserializable �Ȃ��̂�������Ă����Η�O���ł�
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
                this.declaredClass, rhs.declaredClass).append(this.argument, rhs.argument).append(this.methodName, rhs.methodName).isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(661437325, -495862237).append(
                this.methodArguments).append(this.declaredClass).append(this.argument)
                .append(this.methodName).toHashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new ToStringBuilder(this).append(this.methodName).append(this.methodArguments).append(this.argument).toString();
    }
}
