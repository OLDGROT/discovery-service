package org.oldgrot.discoveryservice.controller;

import lombok.AllArgsConstructor;
import org.oldgrot.discoveryservice.dto.ServiceInstance;
import org.oldgrot.discoveryservice.service.RegistryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController()
@RequestMapping()
public class RegistryController {
    private final RegistryService registryService;

    @PostMapping("/register")
    public ResponseEntity<String> registerService(@RequestBody ServiceInstance service) {
        registryService.registry(service);
        return ResponseEntity.status(201).body("Сервер зарегистрирован");
    }

    @GetMapping("/get-services")
    public ResponseEntity< Map<String,List<ServiceInstance>>> getServices() {
        return ResponseEntity.status(200).body(registryService.getServices());
    }

    @GetMapping("/get-service/{name}")
    public ResponseEntity<List<ServiceInstance>> getService(@PathVariable String name) {
        Map< String,List<ServiceInstance>> instances = registryService.getServices();
        if(instances.containsKey(name) && !instances.get(name).isEmpty()) {
            return ResponseEntity.status(200).body(instances.get(name));
        }else return ResponseEntity.status(404).body(instances.get(name));
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat(@RequestBody ServiceInstance service) {
        registryService.updateHeartbeat(service);
        return ResponseEntity.ok().build();
    }
}
