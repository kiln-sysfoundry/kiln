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

package org.sysfoundry.kiln.base.sys;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.spi.Message;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.sysfoundry.kiln.base.Constants;
import org.sysfoundry.kiln.base.cfg.ConfigurationProviderFactory;
import org.sysfoundry.kiln.base.cfg.ConfigurationSource;
import org.sysfoundry.kiln.base.cfg.TypesafeConfigurationSource;
import org.sysfoundry.kiln.base.health.Log;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.srv.ServerSet;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.sysfoundry.kiln.base.Constants.VALIDATE_CONFIG;
import static org.sysfoundry.kiln.base.util.CollectionUtils.KV;
import static org.sysfoundry.kiln.base.util.CollectionUtils.MAP;

/**
 * The Subsys represents an Abstraction of the Subsys in Kiln.
 * The Subsys provides the below capabilities for any Sub system extending from this Abstract Guice Module.
 * <ul>
 *     <li>Subsys Configuration support - Ability to load subsys configuration from the Subsys's package</li>
 *     <li>SubsysInfo support - Ability to register SubsysInfo provided by the concreted child subsystem</li>
 * </ul>
 */
public abstract class Subsys extends AbstractModule {

    public static final String DEFAULT_CONFIG_NAME = "defaults.conf";

    public static final String BASE_SUBSYS_NAME = "base";

    private SubsysInfo subsysInfo;

    private static final Logger log = Log.get(BASE_SUBSYS_NAME);


    /**
     * Creates a Subsys instance.
     */
    public Subsys(){
        subsysInfo = null; //will be populated later from annotation if available
    }

    /**
     * Creates a Subsys instance using the {@link SubsysInfo}
     * @param subsysInfo The subsys info for the subsys
     */
    public Subsys(SubsysInfo subsysInfo){
        this.subsysInfo = subsysInfo;
    }

    @Override
    protected void configure() {
        init();
        registerSubsysInfo();
        registerSubsysConfigSource();
        registerSubsysConfigProvider();
        registerServers();
        //registerProvisions();
    }

    /*private void registerProvisions() {
        Map<String,Object> subsysAttributes = subsysInfo.getAttributes();
        if(subsysAttributes.containsKey(Constants.PROVISIONS)){
            List<Key> keysList = (List<Key>)subsysAttributes.get(Constants.PROVISIONS);

            for (Key key : keysList) {
                Class keyType = key.type();
                Class<? extends Annotation> scopeAnnotationClass = key.scope();
                Class<? extends Annotation> annotationClass = key.annotation();
                String name = key.name();

                //if annotation is available it is given priority over name
                if(annotationClass != null && !annotationClass.isAssignableFrom(None.class)){
                    if(!(Strings.isNullOrEmpty(name) && name.equalsIgnoreCase(Constants.NONE))){
                        log.info("Annotation {} binding is prioritized over name {} which has been ignored. " +
                                "To avoid ambiguity in documentation it is advisable to use either one not both!",annotationClass,name);
                    }
                    registerClasswithAnnotation(keyType,annotationClass,scopeAnnotationClass);
                }

            }

        }
    }

    private void registerClasswithAnnotation(Class keyType, Class<? extends Annotation> annotationClass, Class<? extends Annotation> scopeAnnotationClass) {
        bind(keyType).annotatedWith(annotationClass).in(scopeAnnotationClass);
    }*/

    protected void registerSubsysConfigProvider() {
        Map<String,Object> subsysAttributes = subsysInfo.getAttributes();
        if(subsysAttributes.containsKey(Constants.CONFIG_CLASS)){
            Class configClass = (Class)subsysAttributes.get(Constants.CONFIG_CLASS);
            if(!Void.class.isAssignableFrom(configClass)){
                String configPrefix = (String)subsysAttributes.get(Constants.CONFIG_PREFIX);
                if(!Strings.isNullOrEmpty(configPrefix)) {
                    log.trace("About to register config provider for {} with prefix {}", configClass,configPrefix);
                    ConfigurationProviderFactory configurationProviderFactory = new ConfigurationProviderFactory();
                    requestInjection(configurationProviderFactory);
                    if(subsysAttributes.containsKey(VALIDATE_CONFIG)){
                        Boolean validationReqState = (Boolean)subsysAttributes.get(VALIDATE_CONFIG);
                        configurationProviderFactory.setValidationEnabled(validationReqState);
                    }
                    Provider configurationProvider = configurationProviderFactory.getConfigurationProvider(configPrefix, configClass);
                    bind(configClass).toProvider(configurationProvider).in(Singleton.class);
                    log.trace("Registered provider {} for class {}",configurationProvider,configClass);

                }
            }
        }
    }

    protected void registerServers(){
        Map<String,Object> subsysAttributes = subsysInfo.getAttributes();
        if(subsysAttributes.containsKey(Constants.SERVER_CLASSES)){
            List<Class<? extends Server>> serverClasses = (List<Class<? extends Server>>)subsysAttributes.get(Constants.SERVER_CLASSES);

            if(serverClasses != null && serverClasses.size() > 0){
                //automatically register these servers from the declaration
                bindServers(serverClasses.toArray(new Class[0]));
                log.trace("Successfully bound server classes {}",serverClasses);

            }
        }
    }

    protected void init() {
        Class<? extends Subsys> subsysClass = this.getClass();

        AboutSubsys aboutSubsysAnnotation = subsysClass.getAnnotation(AboutSubsys.class);

        if(aboutSubsysAnnotation == null){
            String message = String.format("Subsys %s has not been annotated with %s. " +
                    "It is advised to annotate for self documentation of subsystems.",subsysClass, AboutSubsys.class);
            Message msgObj = new Message(message);
            List<Message> messages = new ArrayList<>();
            messages.add(msgObj);
            throw new ConfigurationException(messages);
        }

        SubsysInfo tempSubsysInfo = getSubsysInfo(subsysClass, aboutSubsysAnnotation,this.subsysInfo);
        this.subsysInfo = tempSubsysInfo;
    }

    private SubsysInfo getSubsysInfo(Class<? extends Subsys> subsysClass, AboutSubsys aboutSubsysAnnotation, SubsysInfo providedSubsysInfo) {
        String id = aboutSubsysAnnotation.id();
        if(id.equalsIgnoreCase(Constants.TYPE_NAME)){
            id = subsysClass.getName();
        }
        Class configClass = aboutSubsysAnnotation.configType();
        String configPrefix = aboutSubsysAnnotation.configPrefix();
        Class<? extends Server>[] serverClasses = aboutSubsysAnnotation.servers();
        Key[] provisionKeys = aboutSubsysAnnotation.provisions();
        Key[] requirementKeys = aboutSubsysAnnotation.requirements();
        String documentation = aboutSubsysAnnotation.doc();
        String[] authors = aboutSubsysAnnotation.authors();
        String provider = aboutSubsysAnnotation.provider();
        String[] emitsEvents = aboutSubsysAnnotation.emits();
        String[] reactsToEvents = aboutSubsysAnnotation.reactsTo();
        boolean validateConfig = aboutSubsysAnnotation.validateConfig();

        if(providedSubsysInfo!=null){
            id = providedSubsysInfo.getID();
        }


        Map<String, Object> subsysAttributes = MAP(KV(Constants.ID, id),
                KV(Constants.CONFIG_CLASS, configClass),
                KV(Constants.CONFIG_PREFIX, configPrefix),
                KV(Constants.SERVER_CLASSES, Arrays.asList(serverClasses)),
                KV(Constants.PROVISIONS, Arrays.asList(provisionKeys)),
                KV(Constants.REQUIREMENTS, Arrays.asList(requirementKeys)),
                KV(Constants.DOCUMENTATION, documentation),
                KV(Constants.AUTHORS, Arrays.asList(authors)),
                KV(Constants.PROVIDER, provider),
                KV(Constants.EMITS_EVENTS, Arrays.asList(emitsEvents)),
                KV(Constants.REACTS_TO_EVENTS, Arrays.asList(reactsToEvents)),
                KV(VALIDATE_CONFIG,validateConfig));

        if(providedSubsysInfo != null){
            Map<String, Object> providedSubsysInfoAttributes = providedSubsysInfo.getAttributes();

            if(!providedSubsysInfoAttributes.isEmpty()){
                providedSubsysInfoAttributes.forEach((k,v)->{
                    if(subsysAttributes.containsKey(k)){
                        log.info("Overwriting Subsys Attribute {} with provided value {}",k,v);
                    }
                    subsysAttributes.put(k,v);
                });
            }
        }

        SubsysInfo subsysInfo = new SubsysInfo(id,subsysAttributes);



        return subsysInfo;
    }

    /**
     * Looks up and registers the configuration source from the default location.
     * The default location is the package name of the subsystem instance appended with '/defaults.conf'
     */
    protected void registerSubsysConfigSource() {
        try(InputStream resourceAsStream = getClass().getResourceAsStream(DEFAULT_CONFIG_NAME)){
            if(resourceAsStream != null){
                //InputStreamConfigurationSource inputStreamConfigurationSource =
                //        new InputStreamConfigurationSource(false,true,resourceAsStream);
                Config config = ConfigFactory.parseReader(new InputStreamReader(resourceAsStream));
                TypesafeConfigurationSource typesafeConfigurationSource = new TypesafeConfigurationSource(config);
                //registerConfigSource(inputStreamConfigurationSource);
                registerConfigSource(typesafeConfigurationSource);
            }else{
                log.trace("Unable to find Subsys config {} for {} in classpath!",DEFAULT_CONFIG_NAME,subsysInfo.getID());
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("Failed to read {}, so ignoring config file..",DEFAULT_CONFIG_NAME);
        }

    }

    /**
     * Registers the given configuration source instance to the SubsysConfigSourceSet annotation.
     * @param configurationSource - The configuration source instance to register
     */
    protected void registerConfigSource(ConfigurationSource configurationSource) {
        Multibinder<ConfigurationSource> configSources = Multibinder.newSetBinder(binder(),ConfigurationSource.class,
                SubsysConfigSourceSet.class);
        configSources.addBinding().toInstance(configurationSource);
        log.trace("Registered Subsys config for {}",subsysInfo.getID());

    }

    /**
     * Registers or binds the SubsysInfo to the SubsysSet annotation @see #SubsysSet
     */
    protected void registerSubsysInfo(){

        //bind the SubSystem` information
        Multibinder<SubsysInfo> subsysInfoMultibinder = Multibinder.newSetBinder(binder(), SubsysInfo.class, SubsysSet.class);
        subsysInfoMultibinder.addBinding().toInstance(subsysInfo);
        log.trace("Registering SubsysInfo - {}",subsysInfo);
    }

    /**
     * Binds the given set of server classes to the ServerSet annotation. @See #ServerSet
     * @param servers - The list of server classes to bind
     */
    protected void bindServers(Class<? extends Server>... servers){
        if(servers != null && servers.length>0) {
            Multibinder<Server> serverMultibinder = Multibinder.newSetBinder(binder(), Server.class, ServerSet.class);
            for (Class<? extends Server> server : servers) {
                serverMultibinder.addBinding().to(server).in(Singleton.class);
                log.trace("Bound Class {} to annotation {} set",server, ServerSet.class);
            }

        }
    }
}
