package org.lafabriquedigitowl.controller;

import com.lafabriquedigitowl.Owl;
import org.lafabriquedigitowl.service.OwlProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/owl")
public class OwlProducerController {

    private final OwlProducerService producerService;

    public OwlProducerController(OwlProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping(value = "/send")
    public ResponseEntity<?> postOwl(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "species", required = false) String species,
            @RequestParam(name = "age") Integer age,
            @RequestParam(name = "haveRing") boolean haveRing
    ) {
        producerService.sendMessage(
                Owl.newBuilder().setId(id).setName(name).setSpecies(species).setAge(age).setHaveRing(haveRing).build());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/status")
    public String status() {
        return producerService.status();
    }

}
