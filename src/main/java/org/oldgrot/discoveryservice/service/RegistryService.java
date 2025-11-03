package org.oldgrot.discoveryservice.service;

import org.oldgrot.discoveryservice.dto.ServiceInstance;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RegistryService {

    ConcurrentHashMap<String, List<ServiceInstance>> services = new ConcurrentHashMap<>();
    private static final long duration = 20_000;

    public void registry(ServiceInstance service){
        services.compute(service.getServiceName(),(name, instances)  -> {
            if(instances ==null ) instances = new CopyOnWriteArrayList<ServiceInstance>();
            boolean exists = instances.stream()
                    .anyMatch(i -> i.getHost().equals(service.getHost()) && i.getPort().equals(service.getPort()));
            if (!exists) {
                service.setLastHeartbeat(System.currentTimeMillis());
                instances.add(service);
            }
            return instances;
        });
    }

    public void updateHeartbeat(ServiceInstance service){
        List<ServiceInstance> instances = services.get(service.getServiceName());
        if(instances != null && !instances.isEmpty()){
            instances.stream()
                    .filter(i -> i.getHost().equals(service.getHost()) && i.getPort().equals(service.getPort()))
                    .forEach(i -> i.setLastHeartbeat(System.currentTimeMillis()));
        }
    }

    public Map<String, List<ServiceInstance>> getServices(){
        return services;
    }

    public List<ServiceInstance> getService(String serviceName){
        return services.get(serviceName);
    }

    @Scheduled(fixedRate = 5000)
    private void removeServices(){
        long now = System.currentTimeMillis();
        services.values()
                .forEach(i -> i.removeIf(j ->now - j.getLastHeartbeat() > duration));
    }
}
