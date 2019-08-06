package org.sysfoundry.kiln.base.ss.evt;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.evt.Event;
import org.sysfoundry.kiln.base.evt.OnEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
class SubscriberMeta {

    private Class subscriberType;
    private Optional<String[]> interestedEventsOptional = Optional.empty();
    private Optional<List<String>> interestedEventsOptionalList = Optional.empty();
    private Optional<Method> targetMethodOptional = Optional.empty();
    private Optional<List<Method>> targetMethodListOptional = Optional.empty();
    private boolean hasMultipleOnEventMethods = false;

    SubscriberMeta(@NonNull Class subscriberType){
        this.subscriberType = subscriberType;
        this.targetMethodListOptional = _findTargetMethods(this.subscriberType);
        log.trace("targetMethodListOptional {}",targetMethodListOptional);
        this.hasMultipleOnEventMethods = _hasMultipleOnEventMethods(this.targetMethodListOptional);
        this.targetMethodOptional = _chooseTargetMethod(this.targetMethodListOptional);

        targetMethodOptional.ifPresent(method->{
            interestedEventsOptional = _findInterestedEvents(method);
            interestedEventsOptional.ifPresent(events->{
                interestedEventsOptionalList = Optional.ofNullable(Arrays.asList(events));
            });
        });

    }

    boolean isValidEventTarget(){
        return targetMethodOptional.isPresent();
    }

    Optional<List<String>> getInterestedEventsList(){
        return interestedEventsOptionalList;
    }

    boolean hasMultipleOnEventMethods(){
        return hasMultipleOnEventMethods;
    }

    Optional<List<Method>> getTargetMethods(){
        return targetMethodListOptional;
    }

    Optional<Method> getTargetMethod(){
        return targetMethodOptional;
    }

    Class getSubscriberType(){
        return subscriberType;
    }

    private Optional<String[]> _findInterestedEvents(Method method) {
        Optional<String[]> interestedEvents = Optional.empty();
        OnEvent onEventAnnotation = method.getAnnotation(OnEvent.class);
        if(onEventAnnotation != null){
            String[] events = onEventAnnotation.value();
            interestedEvents = Optional.ofNullable(events);
        }
        return interestedEvents;
    }

    private Optional<Method> _chooseTargetMethod(Optional<List<Method>> targetMethodListOptional) {
        if(targetMethodListOptional.isPresent()){
            List<Method> targetMethods = targetMethodListOptional.get();
            if(targetMethods.isEmpty()){
                return Optional.empty();
            }else if(targetMethods.size() == 1){
                return Optional.ofNullable(targetMethods.get(0));
            }else{
                //there are more than one method annotated with OnEvent
                //so pick the first one with the Event parameter
                List<Method> methodWithEventParams = targetMethods.stream().filter(method -> {
                    boolean retVal = false;
                    if(method.getParameterCount() == 1){
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if(Event.class.isAssignableFrom(parameterTypes[0])){
                            retVal = true;
                        }
                    }
                    return retVal;
                }).collect(Collectors.toList());

                if(methodWithEventParams.isEmpty()){
                    //in this case just pick the first one in the list and return back
                    return Optional.ofNullable(targetMethods.get(0));
                }else{
                    return Optional.ofNullable(methodWithEventParams.get(0));
                }
            }

        }else{
            return Optional.empty();
        }

    }

    private boolean _hasMultipleOnEventMethods(Optional<List<Method>> targetMethodListOptional) {
        boolean retVal = false;

        if(targetMethodListOptional.isPresent()){
            List<Method> methods = targetMethodListOptional.get();
            if(methods.size() > 1){
                retVal = true;
            }
        }
        return retVal;
    }

    private Optional<List<Method>> _findTargetMethods(Class targetClass) {
        Method[] methods = targetClass.getDeclaredMethods();

        ArrayList<Method> targetMethods = new ArrayList<>();

        for (Method method : methods) {

            if(isValidTargetMethod(method)){
                targetMethods.add(method);
            }
        }

        return Optional.ofNullable(targetMethods);
    }

    private boolean isValidTargetMethod(Method declaredMethod) {
        boolean retVal = false;
        OnEvent onEventAnnotation = declaredMethod.getAnnotation(OnEvent.class);
        if(onEventAnnotation != null && declaredMethod.getParameterCount() < 2){
            if(declaredMethod.getParameterCount() > 0){
                Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                if(Event.class.isAssignableFrom(parameterTypes[0])){
                    retVal = true;
                }
            }else{
                retVal = true;
            }
        }

        return retVal;
    }

}