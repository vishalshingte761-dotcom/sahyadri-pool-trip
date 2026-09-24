package com.sahyadri.sahyadripooltrip.service;

import com.sahyadri.sahyadripooltrip.entity.Spot;
import com.sahyadri.sahyadripooltrip.repository.SpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SpotService {
    private final SpotRepository repository;
    public SpotService(SpotRepository repository){this.repository=repository;}
    public List<Spot> getAllSpots(){return repository.findAll();}
    public Optional<Spot> getSpotById(String id){return repository.findById(id);}
    public Optional<Spot> getSpotByName(String name){return repository.findByNameIgnoreCase(name);}
    public List<Spot> getByCategory(String category){return repository.findByCategoryIgnoreCase(category);}
    public List<Spot> getByDistrict(String district){return repository.findByDistrictIgnoreCase(district);}
    public List<Spot> getByRecordStatus(String status){return repository.findByRecordStatusIgnoreCase(status);}
    public List<Spot> getByVerificationStatus(String status){return repository.findByVerificationStatusIgnoreCase(status);}
    public long count(){return repository.count();}
}
