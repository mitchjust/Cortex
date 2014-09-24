/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bluescopesteel.cortex.translators;

import com.bluescopesteel.cortex.Cortex;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Mitchell Just (Mitchell.Just@BlueScopeSteel.com)
 */
public class InvocationTargetExceptionTranslator implements Translator<InvocationTargetException>{

    @Override
    public String translate(InvocationTargetException object) {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception In Reflection Method Invocation: \n");
        sb.append(Cortex.getInstance().getDefaultTranslator().translate(object.getCause()));
        return sb.toString();
    }

}
