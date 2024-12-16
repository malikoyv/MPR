package pjwstk.edu.pl.mpr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pjwstk.edu.pl.mpr.models.Cat;
import pjwstk.edu.pl.mpr.repositories.CatRepository;
import pjwstk.edu.pl.mpr.services.CatService;

import java.io.IOException;
import java.util.List;

@Controller
public class CatController {
    @Autowired
    private CatRepository repository;

    @Autowired
    private CatService catService = new CatService(repository);

    @GetMapping("/getAllCats")
    public ResponseEntity<List<Cat>> getAllCats(){
        return new ResponseEntity<>(catService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Cat> getById(@PathVariable long id){return new ResponseEntity<>(catService.getById(id), HttpStatus.OK);}

    @GetMapping("/getByName/{name}")
    public ResponseEntity<List<Cat>> getByName(@PathVariable String name){
        return new ResponseEntity<>(catService.getByName(name), HttpStatus.OK);
    }

    @PostMapping("/addCat")
    public ResponseEntity<Cat> addCat(@RequestBody Cat cat){
        return new ResponseEntity<>(catService.addCat(cat), HttpStatus.OK);
    }

    @DeleteMapping("/deleteCat/{name}")
    public ResponseEntity<List<Cat>> deleteCat(@PathVariable String name){
        return new ResponseEntity<>(catService.deleteByName(name), HttpStatus.OK);
    }

    @GetMapping("/getByAge/{age}")
    public ResponseEntity<List<Cat>> getByAge(@PathVariable int age){
        return new ResponseEntity<>(catService.getByAge(age), HttpStatus.OK);
    }

    @PatchMapping("/changeName/{oldName}/{newName}")
    public ResponseEntity<List<Cat>> changeName(@PathVariable String oldName, @PathVariable String newName){
        return new ResponseEntity<>(catService.changeName(oldName, newName), HttpStatus.OK);
    }

    @PostMapping("/addCatWithUpperName/{name}/{age}")
    public ResponseEntity<Cat> addCatUppercase(@PathVariable String name, @PathVariable int age) {
        return new ResponseEntity<>(catService.addCatWithUpperName(name, age), HttpStatus.OK);
    }

    @GetMapping("/getCatsLower/{name}")
    public ResponseEntity<List<Cat>> getCatsLower(@PathVariable String name) {
        return new ResponseEntity<>(catService.changeAllUpperToLowerByName(name), HttpStatus.OK);
    }

    @GetMapping("/getCatPdf/{id}")
    public ResponseEntity<byte[]> getCatPdf(@PathVariable Long id) throws IOException {
        byte[] pdf = catService.getCatPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("catInfo.pdf").build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("/updateCat/{id}")
    public ResponseEntity<Cat> updateCat(@PathVariable long id){
        return new ResponseEntity<>(catService.updateCat(id), HttpStatus.OK);
    }
}
