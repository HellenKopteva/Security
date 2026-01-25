package org.example.Security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Qualifier("PersonApiService")//т.к. мы сделали систему гибче указав в качестве сериса-интерфейс, нужно указать его реализацию  виде класса
//таким образом  другом контроллере с тем же функционалом, но для другой платформы нужно лишь изменить qualifаir и написать новую реализацию
@RestController
@RequestMapping("api/home")
public class HomeController {
    private PersonApiInterface personApiInterface;
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    public HomeController(PersonApiInterface personApiInterface, GlobalExceptionHandler globalExceptionHandler) {
        this.personApiInterface = personApiInterface;
        this.globalExceptionHandler = globalExceptionHandler;
    }


    @PostMapping("/create")
    public ResponseEntity<PersonDTO> create(
            @Valid @RequestBody PersonDTO personDTO//@Valid проверка чтоб данные проверялись по валидации описаной в классе который ожидаем
    ){
        return personApiInterface.create(personDTO);
    }
    @PostMapping("/createMultiply")
    public ResponseEntity<List<PersonDTO>> createMultiple(
            @Valid @NotEmpty @RequestBody List<PersonDTO> personList//для коолекций не использовать NotBlank
    ){
        return personApiInterface.createMultiple(personList);
    }
//--------------------------------------------------------------------
    @GetMapping("/getAll")//read операции 1,4,5,6
    public ResponseEntity<List<PersonDTO>> getAll(//по заданию ResponseEntity<PersonDTO> getAll, но как можно All в 1 PersonDTO-в результате сделал списком
            @RequestParam(name = "sortByYear",defaultValue = "false") boolean sortByYear,
            @RequestParam(name = "sortByTitle",defaultValue = "false") boolean sortByTitle,
            @RequestParam(name = "isReverse",defaultValue = "false") boolean isReverse,
            @RequestParam(name="page",defaultValue = "1") Integer page,
            @RequestParam(name = "size",defaultValue = "0")Integer size
    ){
        return personApiInterface.getAll(sortByYear,sortByTitle,isReverse,page,size);
    }
    @GetMapping("/getById")
    public ResponseEntity<PersonDTO> getById(
            @RequestParam(name="id") Long id
    ){
        return personApiInterface.getById(id);
    }
    @GetMapping("/getByTitle")
    public ResponseEntity<PersonDTO> getByTitle(
            @RequestParam(name="title")String title
    ){
        return personApiInterface.getByTitle(title);
    }
//------------------------------------------------------------------
    @PutMapping("/update")
    public ResponseEntity<PersonDTO> updatePerson(
            @RequestParam(name="id")Long id,
            @Valid @RequestBody PersonDTO personDTO
    ){
        return personApiInterface.updatePerson(id,personDTO);
    }
    @PatchMapping("/patch")//update 2,3
    public ResponseEntity<PersonDTO> updatePersonTitleOrYear(
            @RequestParam(name = "id")Long id,
            @RequestParam(name = "title",defaultValue = "")String title,
            @RequestParam(name="year",defaultValue = "0")Integer year
    ){
        return personApiInterface.updatePersonTitleOrYear(id,title,year);
    }
//---------------------------------------------------------------
    @DeleteMapping("/deleteById")
    public ResponseEntity<Void>deleteById(
            @RequestParam(name = "id") Long id
    ){
        return personApiInterface.deleteById(id);
    }
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void>deleteAll(){
        return personApiInterface.deleteAll();
    }
    @DeleteMapping("/deleteByTitle")
    public ResponseEntity<Void>deleteByTitle(
            @RequestParam(name = "title")String title
            ){
        return personApiInterface.deleteByTitle(title);
    }
    @DeleteMapping("/deleteByYearRange")
    public ResponseEntity<Void>deleteByIdYearRange(
            @RequestParam(name = "start")int start,
            @RequestParam(name = "end")int end
    ){
        return personApiInterface.deleteByYearRange(start,end);
    }
    @DeleteMapping("/deleteAllOlderThan")
    public ResponseEntity<Void>deleteAllOlderThan(
            @RequestParam(name = "year")int year
    ){
        return personApiInterface.deleteAllOlderThan(year);
    }
    @DeleteMapping("/deleteAllYoungerThan")
    public ResponseEntity<Void>deleteAllYoungerThan(
            @RequestParam(name = "year")int year
    ){
        return personApiInterface.deleteAllYoungerThan(year);
    }
    //--------------------------------------------------------------------
    @GetMapping("/findAllByYears")
    public ResponseEntity<List<PersonDTO>>findAllByYears(
            @RequestParam(name="minYear")int minYear,
            @RequestParam(name="maxYear")int maxYear
    ){
        return personApiInterface.findAllByYears(minYear,maxYear);
    }
    @GetMapping("/findAllByTitle")//тоже самое что getByTitle, но приказ есть приказ
    public ResponseEntity<List<PersonDTO>>findAllByTitle(
            @RequestParam(name="title")String title
    ){
        return personApiInterface.findAllByTitle(title);
    }
    @GetMapping("/findAllByAge")
    public ResponseEntity<List<PersonDTO>>findAllByAge(
            @RequestParam(name="age")int age
    ){
        return personApiInterface.findAllByAge(age);
    }
    @GetMapping("/findAllMinYearAndTitle")
    public ResponseEntity<List<PersonDTO>>findAllMinYearAndTitle(
            @RequestParam(name="year")int minYear,
            @RequestParam(name="title")String title
    ){
        return personApiInterface.findAllMinYearAndTitle(minYear,title);
    }
    //----------------------------------------------------------
    @GetMapping("/getTotalCount")
    public ResponseEntity<Long>getTotalCount(){
        return personApiInterface.getTotalCount();
    }
    @GetMapping("/getCountByYearRange")
    public ResponseEntity<Long>getCountByYearRange(
            @RequestParam(name="start")int minYear,
            @RequestParam(name="end")int maxYear
    ){
        return personApiInterface.getCountByYearRange(minYear,maxYear);
    }
    @GetMapping("/existsById")
    public ResponseEntity<Boolean>existsById(
            @RequestParam(name="id")long id
    ){
        return personApiInterface.existsById(id);
    }
    @GetMapping("/existsByTitle")
    public ResponseEntity<Boolean>existsByTitle(
            @RequestParam(name="title")String title
    ){
        return personApiInterface.existsByTitle(title);
    }
}
