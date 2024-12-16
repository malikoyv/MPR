package pjwstk.edu.pl.mpr.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pjwstk.edu.pl.mpr.exceptions.CatNotFoundException;
import pjwstk.edu.pl.mpr.exceptions.EmptyString;
import pjwstk.edu.pl.mpr.models.Cat;
import pjwstk.edu.pl.mpr.repositories.CatRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.TIMES_ROMAN;

@Service
public class CatService {
    @Autowired
    public CatRepository catRepository;

    @Autowired
    public CatService(CatRepository catRepository) {
        this.catRepository = catRepository;
    }

    public List<Cat> getAll(){
        List<Cat> allCats = new ArrayList<>();
        catRepository.findAll().forEach(allCats::add);

        if (allCats.isEmpty()){
            throw new CatNotFoundException("There is no cat in the database");
        }

        return allCats;
    }

    public List<Cat> getByName(final String name) {
        List<Cat> cats = catRepository.findByNameContainingIgnoreCase(name);

        if (cats == null || cats.isEmpty()){
            throw new CatNotFoundException("Cat not found");
        }

        return cats;
    }

    public Cat addCat(Cat cat) {
        cat.setIdentificator();

        return catRepository.save(cat);
    }

    public List<Cat> getByAge(int age) {
        List<Cat> cats = catRepository.findByAge(age);
        if (cats == null || cats.isEmpty()){
            throw new CatNotFoundException("Cat not found");
        }

        return cats;
    }

    public List<Cat> deleteByName(final String name) {
        List<Cat> catToDelete = catRepository.findByName(name);

        if (name.isEmpty()){
            throw new EmptyString("Cat's name is empty");
        }
        catRepository.deleteAll(catToDelete);

        return catToDelete;
    }

    public List<Cat> changeName(final String oldName, final String newName) {
        if (oldName.isEmpty() || newName.isEmpty()){
            throw new EmptyString("Your name is empty. Please write cat's name!");
        }

        List<Cat> cats = catRepository.findByName(oldName);

        if (cats == null || cats.isEmpty()){
            throw new CatNotFoundException("Cat not found");
        }

        for (Cat cat : cats) {
            if (!cat.getName().equals(newName)) {
                cat.setName(newName);
                catRepository.save(cat);
                cat.setIdentificator();
            }
        }

        return cats;
    }

    public Cat getById(Long id){
        if (id < 0){
            throw new EmptyString("ID is not valid");
        }

        Optional<Cat> cat = catRepository.findById(id);

        if (cat.isEmpty()) {
            throw new CatNotFoundException("Cat was not found");
        }

        return cat.get();
    }

    public Cat updateCat(Long id){
        Optional<Cat> cat = catRepository.findById(id);
        if (cat.get().getId() != null) {
            catRepository.save(cat.get());
        } else {
            throw new IllegalArgumentException("Cat ID is required for updating a record.");
        }

        return cat.get();
    }

    public Cat addCatWithUpperName(String name, int age){
        if (name.isEmpty()){
            throw new EmptyString("Your name is empty. Please write cat's name!");
        }

        name = name.toUpperCase();
        Cat cat = new Cat(name, age);
        cat.setIdentificator();
        catRepository.save(cat);

        return cat;
    }

    public List<Cat> changeAllUpperToLowerByName(String name) {
        if (name.isEmpty()){
            throw new EmptyString("Your name is empty. Please write cat's name!");
        }

        List<Cat> cats = catRepository.findByName(name.toUpperCase());

        if (cats.isEmpty()) {
            throw new CatNotFoundException("Cat not found");
        }

        List<Cat> newCats = new ArrayList<>();

        for (Cat cat : cats) {
            cat.setName(cat.getName().toLowerCase());
            cat.setAge(cat.getAge());
            cat.setIdentificator();
            catRepository.save(cat);
            newCats.add(cat);
        }

        return newCats;
    }

    public byte[] getCatPdf(Long id) throws IOException {
        Optional<Cat> cat = catRepository.findById(id);

        if (cat.isEmpty()) {
            throw new CatNotFoundException("Cat not found!");
        }

        Cat catObj = cat.get();

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        writeContent(contentStream, catObj);
        document.addPage(page);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    protected void writeContent(PDPageContentStream contentStream, Cat cat) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(TIMES_ROMAN), 12);
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Name: " + cat.getName());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Age: " + cat.getAge());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Identificator: " + cat.getIdentificator());
        contentStream.newLineAtOffset(0, -15);
        contentStream.endText();
        contentStream.close();
    }

}
