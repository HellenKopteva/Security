package org.example.Security;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PersonApiInterface {//интерфейс укажет если в какойто из реализациии мы забыли переопределить метод
    ResponseEntity<PersonDTO> create(PersonDTO personDTO);
    ResponseEntity<List<PersonDTO>> createMultiple(List<PersonDTO> personList);
//--------------------------------------------------------------------
    ResponseEntity<List<PersonDTO>> getAll(boolean sortByYear,boolean sortByTitle,boolean isReverse,Integer page,Integer size);
    ResponseEntity<PersonDTO> getById(Long id);
    ResponseEntity<PersonDTO> getByTitle(String title);
//------------------------------------------------------------------
    ResponseEntity<PersonDTO> updatePerson(Long id,PersonDTO personDTO);
    ResponseEntity<PersonDTO> updatePersonTitleOrYear(Long id,String title,Integer year);
//---------------------------------------------------------------
   ResponseEntity<Void>deleteById(Long id);
   ResponseEntity<Void>deleteAll();
   ResponseEntity<Void>deleteByTitle(String title);
   ResponseEntity<Void>deleteByYearRange(int start,int end);
   ResponseEntity<Void>deleteAllOlderThan(int year);
   ResponseEntity<Void>deleteAllYoungerThan(int year);
    //--------------------------------------------------------------------
   ResponseEntity<List<PersonDTO>>findAllByYears(int minYear,int maxYear);
   ResponseEntity<List<PersonDTO>>findAllByTitle(String title);
   ResponseEntity<List<PersonDTO>>findAllByAge(int age);
   ResponseEntity<List<PersonDTO>>findAllMinYearAndTitle(int minYear,String title);
   //----------------------------------------------------------
   ResponseEntity<Long>getTotalCount();
   ResponseEntity<Long>getCountByYearRange(int minYear,int maxYear);
   ResponseEntity<Boolean>existsById(Long id);
   ResponseEntity<Boolean>existsByTitle(String title);
}
