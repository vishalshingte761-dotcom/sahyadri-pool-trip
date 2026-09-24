package com.sahyadri.sahyadripooltrip.repository;
import com.sahyadri.sahyadripooltrip.entity.NatureDestination; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface NatureDestinationRepository extends JpaRepository<NatureDestination,String>{ List<NatureDestination> findBySectionIgnoreCase(String s); List<NatureDestination> findByStateIgnoreCase(String s); List<NatureDestination> findByCategoryIgnoreCase(String s); List<NatureDestination> findBySectionIgnoreCaseAndStateIgnoreCase(String a,String b); }
