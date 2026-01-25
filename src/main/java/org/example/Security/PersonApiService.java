package org.example.Security;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.http.ResponseEntity.status;

@Service
public class PersonApiService implements PersonApiInterface {
    private static final Pattern TITLE_PATTERN = Pattern.compile("^\\s*[a-zA-Zа-яА-ЯёЁ'][a-zA-Zа-яА-ЯёЁ' -]*[a-zA-Zа-яА-ЯёЁ']?[.!?,;:]?\\s*$");//разрешает предложения только из слов и пробелов и знаков препинания
    private PersonRepository personRepository;
    private Faker faker;

    @Autowired
    public PersonApiService(PersonRepository personRepository, Faker faker) {
        this.personRepository = personRepository;
        this.faker = faker;
    }

    @Override
    public ResponseEntity<PersonDTO> create(PersonDTO personDTO) {
        generationData();
        if (personDTO == null) return ResponseEntity.badRequest().build();
        try {
            Person person = getPerson(personDTO);
            person = personRepository.save(person);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath()// Берет базовый URL: http://localhost
                    .path("/api/home/getById")// Добавляет путь → /api/home/getById
                    .queryParam("id", person.getId())
                    .build()
                    .toUri();
            return ResponseEntity.created(location).body(person.getDTO());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public ResponseEntity<List<PersonDTO>> createMultiple(List<PersonDTO> personDTOList) {
        generationData();
        if (personDTOList == null || personDTOList.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        try {
            List<Person> list = new ArrayList<>();
            for (PersonDTO dto : personDTOList) {
                if (dto == null) continue;
                list.add(getPerson(dto));
            }
            personDTOList = personToDtoList(personRepository.saveAll(list));//как работает saveAll?
            return ResponseEntity.status(HttpStatus.CREATED).body(personDTOList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    //--------------------------------------------------------------------
    @Override
    public ResponseEntity<List<PersonDTO>> getAll(boolean sortByYear, boolean sortByTitle, boolean isReverse, Integer page, Integer size) {//read операции 1,4,5,6
        //похже переделать под праильную пагинацию с примера на уроке
        generationData();
        if (page < 1 || size < 0 || (sortByTitle && sortByYear)) return ResponseEntity.badRequest().build();
        List<PersonDTO> list = personToDtoList(personRepository.findAll());
        if (list.isEmpty() || (page - 1) * size > list.size()) return status(404).body(new ArrayList<>());
        if (sortByTitle) {
            list.sort(Comparator.comparing(PersonDTO::title));
        } else if (sortByYear) {
            list.sort(Comparator.comparing(PersonDTO::year));
        }
        if (isReverse) {
            list.reversed();
        } else if (size != 0)
            return ResponseEntity.ok(list.subList((page - 1) * size, (page * size > list.size()) ? list.size() - 1 : page * size));
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<PersonDTO> getById(Long id) {
        generationData();
        if (id < 1) return ResponseEntity.badRequest().build();
        try {
            Person person = personRepository.findById(id).orElse(null);
            if (person == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(person.getDTO());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<PersonDTO> getByTitle(String title) {//хз в задании сказано вернуть Person по title, при том что оно не уникально, так что как то так
        generationData();
        if (title.isBlank() || !TITLE_PATTERN.matcher(title).matches()) return ResponseEntity.badRequest().build();
        try {
            Person person = personRepository.getPersonByTitle(title);
            if (person == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(person.getDTO());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //------------------------------------------------------------------
    @Override
    public ResponseEntity<PersonDTO> updatePerson(Long id, PersonDTO personDTO) {
        generationData();
        if (id < 1 || personDTO == null) return ResponseEntity.badRequest().build();
        try {
            Person person = personRepository.findById(id).orElse(null);
            if (person == null) return ResponseEntity.notFound().build();
            person.setTitle(personDTO.title());
            person.setYear(personDTO.year());
            return ResponseEntity.ok(personRepository.save(person).getDTO());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public ResponseEntity<PersonDTO> updatePersonTitleOrYear(Long id, String title, Integer year) {//update 2,3
        generationData();
        if (title.isBlank() && year == 0 ||
                (!title.isBlank() && !TITLE_PATTERN.matcher(title).matches()) ||
                id < 1 ||
                year < 0 || year > LocalDateTime.now().getYear())
            return ResponseEntity.badRequest().build();
        try {
            Person person = personRepository.findById(id).orElse(null);
            if (person == null) return ResponseEntity.notFound().build();
            if (title.isEmpty() && year > 0){
                person.setYear(year);
                return ResponseEntity.ok(personRepository.save(person).getDTO());
            }else if (year == 0){
                person.setTitle(title);
                return ResponseEntity.ok(personRepository.save(person).getDTO());
            }else return updatePerson(id, new PersonDTO(title, year));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    //---------------------------------------------------------------
    @Override
    public ResponseEntity<Void> deleteById(Long id) {
        generationData();
        if (id < 1) return ResponseEntity.badRequest().build();
        try {
            if (personRepository.existsById(id)) {
                personRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAll() {
        generationData();
        if (personRepository.count() > 0) {
            personRepository.deleteAll();
            return ResponseEntity.noContent().build();
        } else return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> deleteByTitle(String title) {
        generationData();
        if (title.isBlank() || !TITLE_PATTERN.matcher(title).matches()) return ResponseEntity.badRequest().build();
        if (personRepository.existsPersonByTitle(title)) {
            if (personRepository.deletePersonByTitle(title) > 0) return ResponseEntity.noContent().build();
            else return status(409).build();
        } else return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> deleteByYearRange(int start, int end) {
        generationData();
        if (personRepository.existsPersonByYearBetween(start, end)) {
            if (personRepository.deletePersonByYearBetween(start, end) > 0) return ResponseEntity.noContent().build();
            else return status(409).build();
        } else return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> deleteAllOlderThan(int year) {
        generationData();
        if (personRepository.existsPersonByYearBefore(year)) {
            if (personRepository.deletePersonByYearBefore(year) > 0) return ResponseEntity.noContent().build();
            else return status(409).build();
        } else return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> deleteAllYoungerThan(int year) {
        generationData();
        if (personRepository.existsPersonByYearAfter(year)) {
            if (personRepository.deletePersonByYearAfter(year) > 0) return ResponseEntity.noContent().build();
            else return status(409).build();
        } else return ResponseEntity.notFound().build();
    }

    //--------------------------------------------------------------------
    @Override
    public ResponseEntity<List<PersonDTO>> findAllByYears(int minYear, int maxYear) {
        generationData();
        if (minYear < 0 || minYear > maxYear || maxYear > LocalDateTime.now().getYear() || minYear > LocalDateTime.now().getYear())
            return ResponseEntity.badRequest().build();
        try {
            List<PersonDTO> personDTOList = personToDtoList(personRepository.findAllByYearBetween(minYear, maxYear));
            if (personDTOList.isEmpty()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(personDTOList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public ResponseEntity<List<PersonDTO>> findAllByTitle(String title) {
        generationData();
        if (title.isBlank() || !TITLE_PATTERN.matcher(title).matches()) return ResponseEntity.badRequest().build();
        try {
            List<PersonDTO> personDTOList = personToDtoList(personRepository.findAllByTitleContainsIgnoreCase(title));
            if (personDTOList.isEmpty()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(personDTOList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public ResponseEntity<List<PersonDTO>> findAllByAge(int age) {
        generationData();
        int maxAge = LocalDateTime.now().getYear();//Старше Христа не ищем)
        if (age > maxAge) return ResponseEntity.badRequest().build();
        try {
            List<PersonDTO> personDTOList = personToDtoList(personRepository.findAllByYear(maxAge - age));
            if (personDTOList.isEmpty()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(personDTOList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public ResponseEntity<List<PersonDTO>> findAllMinYearAndTitle(int minYear, String title) {
        generationData();
        if (minYear < 0 || title.isBlank() || !TITLE_PATTERN.matcher(title).matches())
            return ResponseEntity.badRequest().build();
        try {
            List<PersonDTO> personDTOList = personToDtoList(personRepository.findAllByTitleAndYearAfter(title, minYear));
            if (personDTOList.isEmpty()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(personDTOList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    //----------------------------------------------------------
    public ResponseEntity<Long> getTotalCount() {
        generationData();
        try {
            return ResponseEntity.ok(personRepository.count());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public ResponseEntity<Long> getCountByYearRange(int minYear, int maxYear) {
        generationData();
        if (minYear < 0 || minYear > maxYear || maxYear > LocalDateTime.now().getYear() || minYear > LocalDateTime.now().getYear())
            return ResponseEntity.badRequest().build();
        try {
            return ResponseEntity.ok(personRepository.countPersonByYearBetween(minYear, maxYear));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public ResponseEntity<Boolean> existsById(Long id) {
        generationData();
        if (id < 1) return ResponseEntity.badRequest().build();
        try {
            return ResponseEntity.ok(personRepository.existsById(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public ResponseEntity<Boolean> existsByTitle(String title) {
        generationData();
        if (title.isBlank() || !TITLE_PATTERN.matcher(title).matches()) return ResponseEntity.badRequest().build();
        try {
            return ResponseEntity.ok(personRepository.existsByTitle(title));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }


    public List<PersonDTO> personToDtoList(List<Person> list) {
        List<PersonDTO> personDTOList = new ArrayList<>();
        for (Person person : list) {
            personDTOList.add(person.getDTO());
        }
        return personDTOList;
    }

    public void generationData(){
        if(personRepository.count()==0){
            for (int i = 0; i < 100; i++) {
                String title= faker.twinPeaks().character();
                int year=faker.number().numberBetween(1900,2025);
                Person person=new Person();
                person.setYear(year);
                person.setTitle(title);
                personRepository.save(person);
            }
        }
    }
    public Person getPerson(PersonDTO personDTO){
        Person person=new Person();
        person.setYear(personDTO.year());
        person.setTitle(personDTO.title());
        return person;
    }
}