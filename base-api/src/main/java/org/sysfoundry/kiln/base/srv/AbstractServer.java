/*
 * Copyright 2019 Sysfoundry (www.sysfoundry.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sysfoundry.kiln.base.srv;

import org.sysfoundry.kiln.base.Constants;

import java.util.Optional;

/**
 * The AbstractServer provides a convenient and easy to use abstract class for Server implementations to extend from
 * It provides the below capabilities for subclasses to reuse
 * <ol>
 *     <li>name - Ability to specify a name for the server</li>
 *     <li>providedCapabilities - Ability to specify a list of capabilities which are provided by the server</li>
 *     <li>requiredCapabilities - Ability to specify a list of capabilities which are required by the server in order to start successfully</li>
 *     <li>startLevel - Ability for kiln to set and retrieve the startlevel of the server</li>
 * </ol>
 */
public abstract class AbstractServer implements Server{

    private String name;
    private Optional<String[]> providesOptional = Optional.empty();
    private Optional<String[]> requiresOptional = Optional.empty();
    private String documentation;
    private Integer startLevel = Server.UNKNOWN_LEVEL;

    public AbstractServer(){

    }

    public AbstractServer(String name,String providesList,String requiresList){
        this.name = name;
        if(providesList!= null && !providesList.trim().isEmpty()){
            this.providesOptional = Optional.ofNullable(providesList.split(","));
        }
        if(requiresList!= null && !requiresList.trim().isEmpty()){
            this.requiresOptional = Optional.ofNullable(requiresList.split(","));
        }

    }

    public AbstractServer(String name,String[] provides,String[] requires){
        this.name = name;
        this.providesOptional = Optional.ofNullable(provides);
        this.requiresOptional = Optional.ofNullable(requires);
    }

    public AbstractServer(String name){
        this.name = name;
    }

    @Override
    public String getDocumentation() {
        if(documentation == null){
            AboutServer aboutServerAnnotation = getAboutAnnotation();
            if(aboutServerAnnotation == null){
                String message = getAboutServerAnnotationMissingMessage();
                throw new IllegalStateException(message);
            }
            documentation = aboutServerAnnotation.doc();
        }
        return documentation;
    }

    @Override
    public String getName() {
        if(name != null){
            return name;
        }else{
            AboutServer aboutServerAnnotation = getAboutAnnotation();
            if(aboutServerAnnotation == null){
                String message = getAboutServerAnnotationMissingMessage();
                throw new IllegalStateException(message);
            }
            String tempName = aboutServerAnnotation.name();
            if(Constants.TYPE_NAME.equalsIgnoreCase(tempName)){
                //this means we have to assign the type name of the server as the name itself
                tempName = getClass().getName();
            }
            name = tempName;
        }
        return name;
    }

    private String getAboutServerAnnotationMissingMessage() {
        String message = String.format("Server %s is in an invalid state since it does not provide the annotation %s. " +
                        "It is good practice to annotate the server with the mentioned annotation",
                getClass(),AboutServer.class);

        return message;
    }

    @Override
    public Integer getStartLevel() {
        return startLevel;
    }

    @Override
    public void setStartLevel(Integer startLevel) {
        this.startLevel = startLevel;
    }

    @Override
    public Optional<String[]> getRequiredCapabilities() {
        if(!requiresOptional.isPresent()){
            AboutServer aboutServerAnnotation = getAboutAnnotation();
            if(aboutServerAnnotation == null){
                String message = getAboutServerAnnotationMissingMessage();
                throw new IllegalStateException(message);
            }
            requiresOptional = Optional.ofNullable(aboutServerAnnotation.requires());

        }
        return requiresOptional;
    }

    @Override
    public Optional<String[]> getProvidedCapabilities() {
        if(!providesOptional.isPresent()){
            AboutServer aboutServerAnnotation = getAboutAnnotation();
            if(aboutServerAnnotation == null){
                String message = getAboutServerAnnotationMissingMessage();
                throw new IllegalStateException(message);
            }
            providesOptional = Optional.ofNullable(aboutServerAnnotation.provides());

        }

        return providesOptional;
    }

    private AboutServer getAboutAnnotation(){
        //first check if the current class provides this annotation
        AboutServer aboutServer = getClass().getAnnotation(AboutServer.class);

        if(aboutServer == null){
            //then lets try one level up (to accommodate class proxies etc)
            Class superClass = getClass().getSuperclass();
            aboutServer = (AboutServer)superClass.getAnnotation(AboutServer.class);
        }
        return aboutServer;
    }
}
