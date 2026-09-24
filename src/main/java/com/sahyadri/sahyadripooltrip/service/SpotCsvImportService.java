package com.sahyadri.sahyadripooltrip.service;

import com.sahyadri.sahyadripooltrip.entity.Spot;
import com.sahyadri.sahyadripooltrip.repository.SpotRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpotCsvImportService {
    private static final String CSV_FILE = "data/MAHARASHTRA_SPOTS_MASTER.csv";
    private static final String EXPECTED_HEADER = "spot_id,name,category,district,locality,latitude,longitude,elevation_m,height_m,best_season,difficulty,popularity_tier,highlights,primary_source,verification_status,last_verified,record_status";
    private final SpotRepository repository;
    public SpotCsvImportService(SpotRepository repository){this.repository=repository;}

    @Transactional
    public ImportResult importSpots() throws Exception {
        ClassPathResource resource = new ClassPathResource(CSV_FILE);
        if(!resource.exists()) throw new IllegalStateException("Master spots CSV not found: "+CSV_FILE);
        int count=0;
        try(BufferedReader reader=new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))){
            String header=reader.readLine();
            if(header==null || header.isBlank()) throw new IllegalStateException("Master spots CSV is empty or header is missing.");
            header=removeBom(header).trim();
            if(!parseCsvLine(header).equals(parseCsvLine(EXPECTED_HEADER))) throw new IllegalStateException("Master spots CSV header does not match the expected schema.");
            String line; int row=1;
            while((line=reader.readLine())!=null){
                row++; if(line.isBlank()) continue;
                List<String> c=parseCsvLine(line);
                if(c.size()!=17) throw new IllegalStateException("Invalid spots CSV row at line "+row+". Expected 17 columns but found "+c.size());
                String id=value(c,0), name=value(c,1);
                if(id==null || id.isBlank()) throw new IllegalStateException("Missing spot_id at CSV line "+row);
                if(name==null || name.isBlank()) throw new IllegalStateException("Missing name for spot_id "+id+" at CSV line "+row);
                Spot s=repository.findById(id).orElseGet(Spot::new);
                s.setSpotId(id); s.setName(name); s.setCategory(value(c,2)); s.setDistrict(value(c,3)); s.setLocality(value(c,4));
                s.setLatitude(parseDouble(value(c,5))); s.setLongitude(parseDouble(value(c,6))); s.setElevationM(parseDouble(value(c,7))); s.setHeightM(parseDouble(value(c,8)));
                s.setBestSeason(value(c,9)); s.setDifficulty(value(c,10)); s.setPopularityTier(value(c,11)); s.setHighlights(value(c,12)); s.setPrimarySource(value(c,13)); s.setVerificationStatus(value(c,14)); s.setLastVerified(value(c,15)); s.setRecordStatus(value(c,16));
                repository.save(s); count++;
            }
        }
        return new ImportResult(count);
    }
    public record ImportResult(int spotCount) {}
    private String value(List<String> c,int i){if(i>=c.size())return null; String v=c.get(i); if(v==null)return null; v=v.trim(); return v.isEmpty()?null:v;}
    private Double parseDouble(String v){if(v==null||v.isBlank())return null; String n=v.trim(); if(n.startsWith("~")) n=n.substring(1).trim(); try{return Double.parseDouble(n);}catch(NumberFormatException e){throw new IllegalStateException("Invalid numeric value in spots CSV: "+v,e);}}
    private String removeBom(String v){return v!=null&&!v.isEmpty()&&v.charAt(0)=='\uFEFF'?v.substring(1):v;}
    private List<String> parseCsvLine(String line){
        List<String> columns=new ArrayList<>(); StringBuilder current=new StringBuilder(); boolean quoted=false;
        for(int i=0;i<line.length();i++){char ch=line.charAt(i); if(ch=='"'){if(quoted&&i+1<line.length()&&line.charAt(i+1)=='"'){current.append('"');i++;}else quoted=!quoted;}else if(ch==','&&!quoted){columns.add(current.toString());current.setLength(0);}else current.append(ch);} columns.add(current.toString()); return columns;
    }
}
