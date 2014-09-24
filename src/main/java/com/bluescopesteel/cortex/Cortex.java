/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bluescopesteel.cortex;

import com.bluescopesteel.cortex.interfaces.CortexInterface;
import com.bluescopesteel.cortex.translators.DefaultTranslator;
import com.bluescopesteel.cortex.translators.Translator;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.reflections.Reflections;

/**
 *
 * @author Mitchell Just (Mitchell.Just@BlueScopeSteel.com)
 */
public class Cortex {

    private static Cortex theInstance;

    private CortexInterface cortexInterface;

    private final Map<String, Object> services;
    private final Map<String, InternalVariable> internalVariables;
    private final Map<Class, Translator> translators;
    private Translator defaultTranslator;

    public static synchronized Cortex getInstance() {
        if (theInstance == null) {
            theInstance = new Cortex();
            theInstance.registerAsService(theInstance);
        }

        return theInstance;
    }

    private Cortex() {
        services = Collections.synchronizedMap(new HashMap<String, Object>());
        internalVariables = Collections.synchronizedMap(new HashMap<String, InternalVariable>());
        translators = Collections.synchronizedMap(new HashMap<Class, Translator>());
        addAllTranslators();
        defaultTranslator = new DefaultTranslator();
    }

    private void addAllTranslators() {
        Reflections reflections = new Reflections("com.bluescopesteel.cortex.translators");
        Set<Class<? extends Translator>> subTypesOf = reflections.getSubTypesOf(Translator.class);

        for (Class<? extends Translator> translatorClass : subTypesOf) {

            Type[] genericInterfaces = translatorClass.getGenericInterfaces();

            ParameterizedType t = (ParameterizedType) genericInterfaces[0];

            Type[] actualTypeArguments = t.getActualTypeArguments();
            Class genericParameter = (Class) actualTypeArguments[0];

            try {
                Translator instance = translatorClass.newInstance();
                System.out.println("Registering Translator: [" + genericParameter.getSimpleName() + " " + translatorClass.getSimpleName() + "]");
                translators.put(genericParameter, instance);

            } catch (InstantiationException | IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(Cortex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void registerInterface(CortexInterface cortexInterface) {
        this.cortexInterface = cortexInterface;
        this.cortexInterface.registerCortex(this);
        this.cortexInterface.initialize();
        this.cortexInterface.output(getCortexWelcome());

    }

    private String getCortexWelcome() {
        StringBuilder sb = new StringBuilder();
        sb.append("______________________________________________\n");
        sb.append(" _________                __                 \n"
                + " \\_   ___ \\  ____________/  |_  ____ ___  ___\n"
                + " /    \\  \\/ /  _ \\_  __ \\   __\\/ __ \\\\  \\/  /\n"
                + " \\     \\___(  <_> )  | \\/|  | \\  ___/ >    < \n"
                + "  \\______  /\\____/|__|   |__|  \\___  >__/\\_ \\\n"
                + "         \\/                        \\/      \\/");
        sb.append("\n______________________________________________\n");
        return sb.toString();
    }

    public void registerAsService(Object object) {
        registerAsService(object.getClass().getSimpleName(), object);
    }

    public void registerAsService(String name, Object object) {
        services.put(name, object);
        Logger.getLogger(Cortex.class.getName()).info("Registered Cortex Service: " + name);
    }

    public List<String> getServices() {
        List<String> serviceNames = new ArrayList<>();
        for (Entry<String, Object> entry : services.entrySet()) {
            serviceNames.add(entry.getKey());
        }
        Collections.sort(serviceNames);
        return serviceNames;
    }

    public Object getService(String name) {

        return services.get(name);
    }

    public Translator getDefaultTranslator() {
        return defaultTranslator;
    }

    public void setDefaultTranslator(Translator defaultTranslator) {
        this.defaultTranslator = defaultTranslator;
    }

    public void addTranslator(Class clazz, Translator translator) {
        translators.put(clazz, translator);
    }

    public String translate(Object o) {
        if (o == null) {
            return "null";
        }
        Class objectClass = o.getClass();

        Translator translator = translators.containsKey(objectClass) ? translators.get(objectClass) : defaultTranslator;

        return translator.translate(o);
    }

    public InternalVariable getVariable(String objectName) {
        return internalVariables.get(objectName);
    }

    public InternalVariable addVariable(String objectName, Object newVariable) {

        //Check for existing variable
        InternalVariable variable = internalVariables.containsKey(objectName) ? getVariable(objectName) : new InternalVariable(objectName, newVariable);

        internalVariables.put(objectName, variable);

        return variable;
    }

}