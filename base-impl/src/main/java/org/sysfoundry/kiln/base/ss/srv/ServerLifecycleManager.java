package org.sysfoundry.kiln.base.ss.srv;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.LifecycleException;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.srv.ServerSet;
import org.sysfoundry.kiln.base.evt.Event;
import org.sysfoundry.kiln.base.evt.EventBus;
import org.sysfoundry.kiln.base.sys.Args;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static org.sysfoundry.kiln.base.sys.Sys.*;

@Slf4j
class ServerLifecycleManager {

    private EventBus eventBus;
    private Set<Server> servers;
    private Map<Integer,List<Server>> serverGroupMap;
    private Object lockObject = new Object(); //object used for synchronizing access during initialization
    private Optional<List<Server>> unknownLevelListOptional;
    private Optional<List<Server>> orphanLevelListOptional;
    private Optional<List<List<Server>>> orderedServersListOption;
    private String[] args;

    @Inject
    ServerLifecycleManager(EventBus eventBus, @ServerSet Set<Server> servers, @Args String[] args){
        this.eventBus = eventBus;
        this.servers = servers;
        this.args = args;
        this.eventBus.register(this);
    }


    private boolean isInitialized(){
        boolean retVal = false;

        if(serverGroupMap != null){
            retVal = true;
        }

        return retVal;
    }

    @Subscribe
    public void onEvent(Event event){
        String eventName = event.getName();

        log.trace("Received Event {}",eventName);

        switch(eventName){
            case INITIALIZING_EVENT:
                initializeServerGroup(event);
                break;
            case STARTING_EVENT:
                startServers(event);
                break;
            case STOPPING_EVENT:
                stopServers(event);
        }
    }

    private void initializeServerGroup(Event event) {
        log.trace("Initializing the ServerLifecycleManager...");
        if(!isInitialized()){
            synchronized (lockObject){
                if(!isInitialized()){
                    List<Server> sortedServerList = sort(new ArrayList<Server>(servers));
                    Map<Integer, List<Server>> groupedServerMap = group(sortedServerList);
                    this.serverGroupMap = groupedServerMap;

                    this.unknownLevelListOptional = getUnknownLevelList(this.serverGroupMap);
                    this.orphanLevelListOptional = getOrphanLevelList(this.serverGroupMap);
                    this.orderedServersListOption = getOrderedServerMetaList(this.serverGroupMap);

                    log.trace("Server Group Map {}",serverGroupMap);
                }
            }
        }
    }

    private Optional<List<List<Server>>> getOrderedServerMetaList(Map<Integer, List<Server>> serverGroupMap) {
        Set<Integer> levels = serverGroupMap.keySet();
        List<Integer> levelList = new ArrayList<>(levels);
        Collections.sort(levelList);
        List<Integer> finalListOfLevels = levelList.stream().filter(v -> {
            return ((v == Server.ORPHAN_LEVEL) || (v == Server.UNKNOWN_LEVEL)) ? false : true;
        }).collect(Collectors.toList());

        List<List<Server>> finalList = new ArrayList<>();
        for (Integer level : finalListOfLevels) {
            finalList.add(serverGroupMap.get(level));
        }

        return Optional.of(finalList);
    }

    private Optional<List<Server>> getOrphanLevelList(Map<Integer, List<Server>> serverGroupMap) {
        Optional<List<Server>> orphanServerListOptional = Optional.empty();
        if(serverGroupMap.containsKey(Server.ORPHAN_LEVEL)){
            orphanServerListOptional = Optional.ofNullable(serverGroupMap.get(Server.ORPHAN_LEVEL));
        }
        return orphanServerListOptional;
    }

    private Optional<List<Server>> getUnknownLevelList(Map<Integer, List<Server>> serverGroupMap) {
        Optional<List<Server>> unKnownServerListOptional = Optional.empty();
        if(serverGroupMap.containsKey(Server.UNKNOWN_LEVEL)){
            unKnownServerListOptional = Optional.ofNullable(serverGroupMap.get(Server.UNKNOWN_LEVEL));
        }
        return unKnownServerListOptional;
    }

    private void stopServers(Event event) {
        if(!isInitialized()){
            throw new RuntimeException("Server Lifecycle Manager has not been initialized!");
        }
        log.trace("Stopping All Servers in order...");

        this.orderedServersListOption.ifPresent(serverListOfList->{
            ArrayList<List<Server>> newServerListOfList = new ArrayList<>();
            newServerListOfList.addAll(serverListOfList);

            //reverse the list
            Collections.reverse(newServerListOfList);

            newServerListOfList.forEach(serverList->{
                serverList.forEach(server -> {
                    try {
                        server.stop();
                    } catch (LifecycleException e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }

    private void startServers(Event event) {
        if(!isInitialized()){
            throw new RuntimeException("Server Lifecycle Manager has not been initialized!");
        }

        log.trace("Starting all Servers in order...");

        this.orderedServersListOption.ifPresent(serverListOfList->{
            log.trace("Servers to be started {} count {}",serverListOfList,serverListOfList.size());
            serverListOfList.forEach(serverList->{
                serverList.forEach(server -> {
                    try {
                        server.start(args);
                    } catch (LifecycleException e) {
                        e.printStackTrace();
                    }
                });
            });
        });

        this.unknownLevelListOptional.ifPresent(serverList->{
            serverList.forEach(server -> {
                log.warn("Server - {} is in an unknown state and cannot be started!",server.getName());
            });
        });

        this.orphanLevelListOptional.ifPresent(serverList->{
            serverList.forEach(server -> {
                log.warn("Server - {} is in an orphaned state and cannot be started. " +
                        "Please verify the requirements of this server!",server.getName());
            });
        });

    }

    public static Map<Integer,List<Server>> group(List<Server> serverListToBeGrouped){
        Map<Integer,List<Server>> groupMap = new HashMap<>();
        for (Server server : serverListToBeGrouped) {
            int level = server.getStartLevel();
            List<Server> serverList = null;
            boolean newList = false;
            if(groupMap.containsKey(level)){
                serverList = groupMap.get(level);
            }else{
                serverList = new ArrayList<>();
                newList = true;
            }
            serverList.add(server);
            if(newList){
                groupMap.put(level,serverList);
            }
        }
        return groupMap;
    }

    private  List<Server> sort(List<Server> serverList){
        ArrayList<Server> sortedList = new ArrayList<>();

        ArrayList<String> availableCapabilityList = new ArrayList<>();
        ArrayList<Server> unsortedList = new ArrayList<>(serverList);
        List<Server> currentSortedList = new ArrayList<>();

        currentSortedList = sort_internal(availableCapabilityList,unsortedList,currentSortedList,0);


        sortedList.addAll(currentSortedList);

        return sortedList;
    }

    private  List<Server> sort_internal(List<String> availableCapabilityList,
                                                  List<Server> unsortedList,
                                                  List<Server> currentSortedList,int level) {
        List<Server> newServerList = new ArrayList<>();

        for (Server server : unsortedList) {

            if(server.areRequirementsSatisfied(availableCapabilityList)){
                server.setStartLevel(level+1);
                newServerList.add(server);
            }
        }

        List<Server> newUnsortedList = new ArrayList<>();

        unsortedList.forEach(server -> {
            if(!newServerList.contains(server)){
                newUnsortedList.add(server);
            }
        });

        List<String> newAvailableCapabilityList = new ArrayList<>();
        newAvailableCapabilityList.addAll(availableCapabilityList);

        List<Server> newCurrentSortedList = new ArrayList<>();
        newCurrentSortedList.addAll(currentSortedList);

        for (Server server : newServerList) {
            //add the new capabilities to the newAvailabilityList
            if(server.getProvidedCapabilities().isPresent()){
                String[] newCapabilities = server.getProvidedCapabilities().get();

                for (String newCapability : newCapabilities) {
                    if(!newAvailableCapabilityList.contains(newCapability)){
                        //add it to the new list
                        newAvailableCapabilityList.add(newCapability);
                    }
                }

            }
            //add to the newCurrentSortedList
            newCurrentSortedList.add(server);
        }

        if(newServerList.size() > 0){
            //recursively loop only if new servers are identified in the list
            return sort_internal(newAvailableCapabilityList,newUnsortedList,newCurrentSortedList,level+1);
        }else{

            if(unsortedList.size()>0){
                List<String> newAvailableCapabilityListForPending = new ArrayList<>();
                newAvailableCapabilityListForPending.addAll(newAvailableCapabilityList);
                List<Server> orphanList = new ArrayList<>();

                //add the pending items capability list
                for (Server server : unsortedList) {
                    if(server.getProvidedCapabilities().isPresent()) {
                        String[] capabilities = server.getProvidedCapabilities().get();
                        for (String capability : capabilities) {
                            if(!newAvailableCapabilityListForPending.contains(capability)){
                                newAvailableCapabilityListForPending.add(capability);
                            }
                        }

                    }
                }

                //now iterate and check if the pending items are satisfied
                for (Server server : unsortedList) {
                    if(server.areRequirementsSatisfied(newAvailableCapabilityListForPending)){
                        server.setStartLevel(level+1); //jump to the next level
                        newServerList.add(server);
                    }else{
                        //these are orphans
                        orphanList.add(server);
                    }
                }


                //finally add the rest of the orphans with the appropriate level
                for (Server server : orphanList) {
                    if(server.hasRequirement("*")){
                        server.setStartLevel(Integer.MAX_VALUE); //should be the last to be started
                    }else {
                        server.setStartLevel(Server.ORPHAN_LEVEL); //-2 represents orphans
                    }
                    newServerList.add(server);
                }

                //finally add them to the sorted list
                for (Server server : newServerList) {
                    newCurrentSortedList.add(server);
                }

            }

            return newCurrentSortedList;
        }


    }


}
